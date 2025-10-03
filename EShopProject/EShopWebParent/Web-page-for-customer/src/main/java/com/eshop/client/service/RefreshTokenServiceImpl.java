package com.eshop.client.service;

import com.eshop.client.repository.RefreshTokenRepository;
import com.eshop.client.service.interfaceS.RefreshTokenService;
import com.eshop.common.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private String newOpaqueToken() {
        return RandomString.make(64);
    }

    @Override
    @Transactional
    public RefreshToken issue(Integer customerId, int ttlDays, String ip, String ua) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCustomerId(customerId);
        refreshToken.setToken(newOpaqueToken());
        refreshToken.setExpiresAt(Instant.now().plus(ttlDays, ChronoUnit.DAYS));
        refreshToken.setCreatedIp(ip);
        refreshToken.setUserAgent(ua);
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findActive(String token) {
        return refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()));
    }

    @Override
    @Transactional
    public void revoke(String token) {
        refreshTokenRepository.findByTokenAndRevokedFalse(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Override
    @Transactional
    public void revokeAllForUser(Integer customerId) {
        refreshTokenRepository.findAll().stream()
                .filter(rt -> rt.getCustomerId().equals(customerId) && !rt.isRevoked())
                .forEach(rt -> { rt.setRevoked(true);
                    refreshTokenRepository.save(rt); });
    }
}

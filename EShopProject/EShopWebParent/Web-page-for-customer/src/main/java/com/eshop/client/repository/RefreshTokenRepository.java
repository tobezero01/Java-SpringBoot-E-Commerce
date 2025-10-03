package com.eshop.client.repository;

import com.eshop.common.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);
    long deleteByCustomerIdAndRevokedTrue(Long userId);
    long deleteByExpiresAtBefore(Instant time);
}

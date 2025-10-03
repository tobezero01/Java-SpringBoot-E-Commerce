package com.eshop.client.service.interfaceS;

import com.eshop.common.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken issue(Integer customerId, int ttlDays, String ip, String ua);
     Optional<RefreshToken> findActive(String token);
    void revoke(String token);
    void revokeAllForUser(Integer customerId);
}

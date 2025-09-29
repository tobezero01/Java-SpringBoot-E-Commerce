package com.eshop.client.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtTokenService {
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails user);
    long getAccessTokenTtlSeconds();
    String generateAccessToken(UserDetails user, Map<String, Object> extraClaims);

    //UserDetails loadUserByUsername(String username);
}

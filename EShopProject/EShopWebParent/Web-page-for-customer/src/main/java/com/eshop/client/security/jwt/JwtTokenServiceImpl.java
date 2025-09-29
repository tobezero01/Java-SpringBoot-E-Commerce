package com.eshop.client.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService{

    @Value("${spring.security.jwt.secret}") private String secret;
    @Value("${spring.security.jwt.issuer}") private String issuer;
    @Value("${spring.security.jwt.access-ttl-seconds}") private long ttlSeconds;

    private Key signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(UserDetails user, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);

        return Jwts.builder()
                .setClaims(new HashMap<>(extraClaims))
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS256, signingKey())
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey()).build()
                .parseClaimsJws(token).getBody().getSubject();

    }

    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        try {
            var claims = Jwts.parser()
                    .setSigningKey(signingKey()).build()
                    .parseClaimsJws(token).getBody();

            return user.getUsername().equals(claims.getSubject())
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public long getAccessTokenTtlSeconds() {
        return ttlSeconds ;
    }



}

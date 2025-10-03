package com.eshop.client.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.*;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtProperties props;
    private final Key signingKey;

    public JwtTokenServiceImpl(JwtProperties props) {
        this.props = props;
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(props.getSecret());
        } catch (IllegalArgumentException ex) {
            keyBytes = props.getSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }


    @Override
    public String generateAccessToken(UserDetails user, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getAccessTtlSeconds());

        return Jwts.builder()
                .setClaims(new HashMap<>(extraClaims))
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .setIssuer(props.getIssuer())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return parseAndValidate(token).getSubject();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        try {
            Claims claims = parseAndValidate(token);
            return user.getUsername().equals(claims.getSubject())
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims parseAndValidate(String token) {
        return Jwts.parser()
                .requireIssuer(props.getIssuer())
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public long getAccessTokenTtlSeconds() {
        return props.getAccessTtlSeconds() ;
    }

}

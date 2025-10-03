package com.eshop.client.security.jwt;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.security.jwt")
// Log xác nhận trong ứng dụng (không lộ secret)
//@Slf4j
public class JwtStartupLogger {
    private static final Logger log = LoggerFactory.getLogger(JwtStartupLogger.class);

    private String issuer;
    private String secret;
    private long accessTtlSeconds;

    public void setIssuer(String issuer) { this.issuer = issuer; }
    public void setSecret(String secret) { this.secret = secret; }
    public void setAccessTtlSeconds(long accessTtlSeconds) { this.accessTtlSeconds = accessTtlSeconds; }

    @PostConstruct
    public void logAtStart() {
        int len = (secret == null ? 0 : secret.length());
        log.info("JWT config loaded: issuer='{}', secret.length={}, accessTtlSeconds={}",
                issuer, len, accessTtlSeconds);
    }
}

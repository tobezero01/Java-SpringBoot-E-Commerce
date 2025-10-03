package com.eshop.client.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.security.auth")
public class AuthProperties {
    private int refreshTtlDaysRemember = 30;
    private int refreshTtlDaysSession = 1;

    private Cookie cookie = new Cookie();

    public int getRefreshTtlDaysRemember() {
        return refreshTtlDaysRemember;
    }

    public void setRefreshTtlDaysRemember(int refreshTtlDaysRemember) {
        this.refreshTtlDaysRemember = refreshTtlDaysRemember;
    }

    public int getRefreshTtlDaysSession() {
        return refreshTtlDaysSession;
    }

    public void setRefreshTtlDaysSession(int refreshTtlDaysSession) {
        this.refreshTtlDaysSession = refreshTtlDaysSession;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }
}

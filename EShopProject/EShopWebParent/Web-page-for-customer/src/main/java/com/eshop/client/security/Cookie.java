package com.eshop.client.security;


public class Cookie {
    private boolean secure = false;
    private String domain = null;

    public boolean isSecure() { return secure; }
    public void setSecure(boolean secure) { this.secure = secure; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
}

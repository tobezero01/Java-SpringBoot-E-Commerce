package com.eshop.client.helper;

import com.eshop.client.security.AuthProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class RefreshCookie {
    public static final String NAME = "eshop_refresh";
    private final AuthProperties props;

    public RefreshCookie(AuthProperties props) {
        this.props = props;
    }

    public void write(HttpServletResponse response, String token, int maxAgeSeconds, String contextPath) {
        Cookie cookie = new Cookie(NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(props.getCookie().isSecure());
        cookie.setPath((contextPath == null || contextPath.isBlank()) ? "/" : contextPath + "/");
        if (props.getCookie().getDomain() != null && !props.getCookie().getDomain().isBlank()) {
            cookie.setDomain(props.getCookie().getDomain());
        }
        cookie.setMaxAge(maxAgeSeconds);
        if (props.getCookie().isSecure()) {
            cookie.setAttribute("SameSite", "None");
        } else {
            cookie.setAttribute("SameSite", "Lax");
        }
        response.addCookie(cookie);
    }

    public void clear(HttpServletResponse response, String contextPath) {
        Cookie cookie = new Cookie(NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(props.getCookie().isSecure());
        cookie.setPath((contextPath == null || contextPath.isBlank()) ? "/" : contextPath + "/");
        if (props.getCookie().getDomain() != null && !props.getCookie().getDomain().isBlank()) {
            cookie.setDomain(props.getCookie().getDomain());
        }
        cookie.setMaxAge(0);
        if (props.getCookie().isSecure()) {
            cookie.setAttribute("SameSite", "None");
        } else {
            cookie.setAttribute("SameSite", "Lax");
        }
        response.addCookie(cookie);
    }
}

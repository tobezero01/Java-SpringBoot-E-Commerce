package com.eshop.client.dto.request;

public record LoginRequest(
        String email, String password,
        Boolean rememberMe
) {
}

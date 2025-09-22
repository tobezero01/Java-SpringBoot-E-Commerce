package com.eshop.client.dto.response;

public record JwtResponse(String tokenType, String accessToken, long expiresInSeconds, String fullName) {}


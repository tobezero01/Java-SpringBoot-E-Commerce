package com.eshop.client.dto.response;

public record ReturnCheckResponse(
        boolean allowed,
        String reason
) {}
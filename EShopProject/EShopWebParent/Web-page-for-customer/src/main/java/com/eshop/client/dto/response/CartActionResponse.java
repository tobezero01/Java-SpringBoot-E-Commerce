package com.eshop.client.dto.response;

public record CartActionResponse(
        Integer productId,
        Integer quantity,
        Float subtotal,
        String message
) {}

package com.eshop.client.dto.request;

public record AddToCartRequest(
        Integer productId,
        Integer quantity
) {}

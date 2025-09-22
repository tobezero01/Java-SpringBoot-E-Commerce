package com.eshop.client.dto;

public record CheckoutItemDTO(
        Integer productId,
        String name,
        String alias,
        String image,
        Float unitPrice,       // đơn giá bán (đã discount)
        Integer quantity,
        Float subtotal
) {}

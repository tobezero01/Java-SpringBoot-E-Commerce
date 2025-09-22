package com.eshop.client.dto;

public record OrderItemDTO(
        Integer productId,
        String name,
        String alias,
        String image,
        float unitPrice,
        int quantity,
        float subtotal,
        boolean reviewedByCustomer,
        boolean canCustomerReview
) {}

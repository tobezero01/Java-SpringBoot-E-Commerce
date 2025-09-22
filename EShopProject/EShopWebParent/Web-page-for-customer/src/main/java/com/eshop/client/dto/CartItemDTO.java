package com.eshop.client.dto;


public record CartItemDTO(
        Integer productId,
        String name,
        String alias,
        String image,
        Float price,
        Float discountPrice,
        Integer quantity,
        Float subtotal
) {}

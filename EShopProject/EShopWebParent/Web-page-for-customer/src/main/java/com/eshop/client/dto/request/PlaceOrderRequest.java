package com.eshop.client.dto.request;

public record PlaceOrderRequest(
        Integer addressId,      // null -> dùng default
        String paymentMethod,   // "COD" | "PAYPAL"
        String note
) {}

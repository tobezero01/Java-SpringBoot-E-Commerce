package com.eshop.client.dto.paypalDTO;

public record PaypalCreateRequest(
        Integer addressId,
        String note
) {}

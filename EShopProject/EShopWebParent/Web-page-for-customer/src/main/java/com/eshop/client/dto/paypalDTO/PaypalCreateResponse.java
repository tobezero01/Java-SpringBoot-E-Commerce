package com.eshop.client.dto.paypalDTO;

public record PaypalCreateResponse(
        String paypalOrderId,
        String approveUrl,
        String localOrderNumber
) {}

package com.eshop.client.dto.paypalDTO;

public record PaypalValidateResponse(
        boolean valid,
        String reason,
        String paypalOrderId,
        String status,
        String intent,
        String currency,
        Float amount,
        String payerEmail,
        String payerId
) {}

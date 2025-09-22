package com.eshop.client.dto.paypalDTO;

public record PaypalCaptureRequest(
        String paypalOrderId,
        String localOrderNumber
) {}

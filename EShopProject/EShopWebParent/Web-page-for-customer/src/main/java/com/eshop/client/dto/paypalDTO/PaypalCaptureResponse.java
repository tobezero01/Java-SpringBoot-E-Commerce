package com.eshop.client.dto.paypalDTO;

import java.time.Instant;

public record PaypalCaptureResponse(
        boolean success,
        String status,
        String captureId,
        String localOrderNumber,
        Float amount,
        String currency,
        Instant capturedAt
) {}

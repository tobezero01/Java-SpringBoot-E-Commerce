package com.eshop.client.dto.paypalDTO;

public record PaypalOrderValidation(
        String paypalOrderId,
        String status,          // APPROVED | COMPLETED | PAYER_ACTION_REQUIRED | ...
        String intent,          // CAPTURE | AUTHORIZE
        String currency,        // e.g. USD
        Float amount,           // total of purchase_units[0]
        String payerEmail,
        String payerId,
        boolean valid,          // true nếu hợp lệ theo tiêu chí ở service
        String reason           // nếu invalid -> lý do
) {}

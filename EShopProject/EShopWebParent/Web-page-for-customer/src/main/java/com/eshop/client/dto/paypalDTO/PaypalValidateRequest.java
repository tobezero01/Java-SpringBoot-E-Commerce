package com.eshop.client.dto.paypalDTO;

public record PaypalValidateRequest(
        String paypalOrderId,
        Float expectedAmount,
        String expectedCurrency,
        Integer localOrderId,      // tùy chọn: nếu muốn buộc vào 1 đơn local
        String localOrderNumber    // tùy chọn: nếu dùng order_number
) {}

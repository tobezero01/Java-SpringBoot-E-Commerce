package com.eshop.client.dto.paypalDTO;

public record PaypalCreateResponse(
        String paypalOrderId,   // trả về để FE mở approveUrl
        String approveUrl,      // link PayPal UI cho người mua bấm Approve
        String localOrderNumber // đang thanh toán đơn local nào
) {}

package com.eshop.client.dto.request;

public record OrderReturnRequest (
         Integer orderId,
         String reason,
         String note
) {
}

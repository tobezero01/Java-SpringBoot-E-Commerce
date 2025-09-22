package com.eshop.client.dto.response;

public record OrderReturnResponse(
        Integer orderId,
        String status
) {
}

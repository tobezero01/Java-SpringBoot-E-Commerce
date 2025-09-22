package com.eshop.client.dto.response;

import java.time.Instant;

public record PlaceOrderResponse(
        Integer orderId,
        String orderNumber,
        String status,
        String paymentMethod,
        Float productTotal,
        Float shippingCost,
        Float grandTotal,
        Instant createdAt
) {}

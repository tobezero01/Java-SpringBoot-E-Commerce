package com.eshop.client.dto.response;


import java.util.Date;

public record PlaceOrderResponse(
        Integer orderId,
        String orderNumber,
        String status,
        String paymentMethod,
        Float productTotal,
        Float shippingCost,
        Float grandTotal,
        Date createdAt
) {}

package com.eshop.client.dto;

import java.time.Instant;
import java.util.List;

public record OrderDTO(
        Integer id,
        String orderNumber,
        String status,
        String paymentMethod,
        Float productTotal,
        Float shippingCost,
        Float grandTotal,
        Instant createdAt,
        List<OrderItemDTO> items
) {

}

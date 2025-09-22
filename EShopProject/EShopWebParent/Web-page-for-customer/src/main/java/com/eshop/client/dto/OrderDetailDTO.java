package com.eshop.client.dto;

import java.util.Date;
import java.util.List;

public record OrderDetailDTO(
        Integer id,
        String orderNumber,
        Date orderTime,
        String status,
        String paymentMethod,
        float productCost,
        float shippingCost,
        float total,
        List<OrderItemDTO> items,
        String firstName,
        String lastName,
        String phone,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country
) {}

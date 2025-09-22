package com.eshop.client.dto;

import java.util.Date;

public record OrderSummaryDTO(
        Integer id,
        String orderNumber,
        Date orderTime,
        String status,
        float total,
        int itemsCount
) {}

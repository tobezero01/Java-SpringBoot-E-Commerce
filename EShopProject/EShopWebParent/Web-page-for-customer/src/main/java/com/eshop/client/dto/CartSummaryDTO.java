package com.eshop.client.dto;

import java.util.List;

public record CartSummaryDTO(
        List<CartItemDTO> items,
        Float estimatedTotal,
        Boolean shippingSupported
) {}

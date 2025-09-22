package com.eshop.client.dto;

import java.util.List;

public record CheckoutSummaryDTO(
        List<CheckoutItemDTO> items,
        Float productTotal,        // tổng tiền hàng
        Float shippingCost,     // phí vận chuyển (0 nếu không hỗ trợ)
        Float paymentTotal,     // = productTotal + shippingCost
        boolean shippingSupported,
        Integer addressId,      // địa chỉ dùng để tính
        String addressLine      // hiển thị nhanh địa chỉ
) {}
package com.eshop.client.mapper;

import com.eshop.client.dto.CartItemDTO;
import com.eshop.common.entity.CartItem;

public final class CartMapper {
    private CartMapper(){}

    public static CartItemDTO toDto(CartItem item) {
        var p = item.getProduct();
        return new CartItemDTO(
                p.getId(),
                p.getName(),
                p.getAlias(),
                p.getMainImagePath(),
                p.getPrice(),
                p.getDiscountPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}

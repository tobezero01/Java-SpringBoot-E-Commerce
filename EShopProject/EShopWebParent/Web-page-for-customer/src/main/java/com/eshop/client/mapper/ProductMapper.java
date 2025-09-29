package com.eshop.client.mapper;

import com.eshop.client.dto.ProductDTO;
import com.eshop.common.entity.product.Product;

public final class ProductMapper {
    private ProductMapper(){}

    public static ProductDTO toDto(Product p) {
        return new ProductDTO(
                p.getId(),
                p.getName(),
                p.getAlias(),
                p.getShortDescription(),
                p.getFullDescription(),
                p.getPrice(),
                p.getDiscountPrice(),
                p.getMainImagePath(),
                p.getAverageRating(),
                p.getReviewCount(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.isInStock()
        );
    }
}

package com.eshop.client.dto;

public record ProductDTO(
        Integer id,
        String name,
        String alias,
        String shortDescription,
        String fullDescription,
        Float price,
        Float discountPrice,
        String mainImagePath,
        Float averageRating,
        Integer reviewCount,
        Integer categoryId,
        String categoryName,
        Boolean inStock
) {}

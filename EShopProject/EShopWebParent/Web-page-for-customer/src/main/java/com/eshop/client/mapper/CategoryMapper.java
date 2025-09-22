package com.eshop.client.mapper;

import com.eshop.client.dto.CategoryDTO;
import com.eshop.common.entity.Category;

public final class CategoryMapper {
    private CategoryMapper() {}

    public static CategoryDTO toDto(Category c) {
        return new CategoryDTO(
                c.getId(),
                c.getName(),
                c.getAlias(),
                c.getImagePath(),
                c.getChildren() != null && !c.getChildren().isEmpty()
        );
    }
}

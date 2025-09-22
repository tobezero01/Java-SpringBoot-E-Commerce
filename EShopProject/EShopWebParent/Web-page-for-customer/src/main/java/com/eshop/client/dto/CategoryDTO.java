package com.eshop.client.dto;

public record CategoryDTO(
        Integer id,
        String name,
        String alias,
        String imagePath,
        boolean hasChildren
) {}

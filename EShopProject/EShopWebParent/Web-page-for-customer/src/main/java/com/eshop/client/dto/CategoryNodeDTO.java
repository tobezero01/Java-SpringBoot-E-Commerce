package com.eshop.client.dto;

import java.util.ArrayList;
import java.util.List;

public record CategoryNodeDTO(
        Integer id,
        String  name,
        String  alias,
        String  image,
        List<CategoryNodeDTO> children
) {
    public static CategoryNodeDTO leaf(Integer id, String name, String alias, String image) {
        return new CategoryNodeDTO(id, name, alias, image, new ArrayList<>());
    }
}
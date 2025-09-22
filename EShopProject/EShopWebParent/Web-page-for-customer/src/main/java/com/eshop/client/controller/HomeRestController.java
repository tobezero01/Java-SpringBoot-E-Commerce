package com.eshop.client.controller;

import com.eshop.client.dto.CategoryDTO;
import com.eshop.client.mapper.CategoryMapper;
import com.eshop.client.service.CategoryService;
import com.eshop.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeRestController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public List<CategoryDTO> homepageCategories() {
        List<Category> leaves = categoryService.listNoChildrenCategories(); // nghiệp vụ giữ nguyên
        return leaves.stream().map( CategoryMapper::toDto).toList();
    }
}

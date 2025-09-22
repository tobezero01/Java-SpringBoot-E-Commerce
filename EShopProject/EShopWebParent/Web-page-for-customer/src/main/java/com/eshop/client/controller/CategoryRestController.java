package com.eshop.client.controller;

import com.eshop.client.dto.CategoryDTO;
import com.eshop.client.exception.CategoryNotFoundException;
import com.eshop.client.mapper.CategoryMapper;
import com.eshop.client.service.CategoryService;
import com.eshop.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryRestController {
    private final CategoryService categoryService;

    // Danh mục lá (cũng dùng được cho trang chủ nếu muốn)
    @GetMapping("/leaf")
    public List<CategoryDTO> leaves() {
        return categoryService.listNoChildrenCategories()
                .stream().map(CategoryMapper::toDto).toList();
    }

    // Chi tiết danh mục theo alias
    @GetMapping("/alias/{alias}")
    public CategoryDTO byAlias(@PathVariable String alias) throws CategoryNotFoundException {
        Category c = categoryService.getCategory(alias); // dùng lại service
        return CategoryMapper.toDto(c);
    }

    // Breadcrumb (cha -> ... -> con)
    @GetMapping("/{id}/parents")
    public List<CategoryDTO> parents(@PathVariable Integer id) throws CategoryNotFoundException {
        Category child = new Category(); child.setId(id);
        return categoryService.getCategoryParents(child)
                .stream().map(CategoryMapper::toDto).toList();
    }
}

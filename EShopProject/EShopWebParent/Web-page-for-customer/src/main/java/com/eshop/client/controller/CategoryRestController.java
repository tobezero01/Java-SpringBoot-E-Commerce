package com.eshop.client.controller;

import com.eshop.client.dto.CategoryDTO;
import com.eshop.client.dto.CategoryNodeDTO;
import com.eshop.client.exception.CategoryNotFoundException;
import com.eshop.client.mapper.CategoryMapper;
import com.eshop.client.service.interfaceS.CategoryService;
import com.eshop.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    /** 1) Cây đầy đủ (top-level -> children -> ...) */
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryNodeDTO>> tree() {
        return ResponseEntity.ok(categoryService.listCategoryTree());
    }

    // 2) Tất cả parent cấp cao nhất
    @GetMapping("/top-level")
    public ResponseEntity<List<CategoryDTO>> topLevel() {
        List<CategoryDTO> list = categoryService.listTopLevelParents()
                .stream().map(CategoryMapper::toDto).toList();
        return ResponseEntity.ok(list);
    }

    // 3) Danh mục lá
    @GetMapping("/leaf")
    public ResponseEntity<List<CategoryDTO>> leaves() {
        List<CategoryDTO> list = categoryService.listNoChildrenCategories()
                .stream().map(CategoryMapper::toDto).toList();
        return ResponseEntity.ok(list);
    }

    // 4) Chi tiết theo alias (giữ)
    @GetMapping("/alias/{alias}")
    public ResponseEntity<CategoryDTO> byAlias(@PathVariable String alias) throws CategoryNotFoundException {
        Category categoryDTO = categoryService.getCategory(alias);
        return ResponseEntity.ok(CategoryMapper.toDto(categoryDTO));
    }

    // 5) Breadcrumb (root -> ... -> child) cho id
    @GetMapping("/{id}/parents")
    public ResponseEntity<List<CategoryDTO>> parents(@PathVariable Integer id) throws CategoryNotFoundException {
        List<CategoryDTO> path = categoryService.getAncestorsPath(id)
                .stream().map(CategoryMapper::toDto).toList();
        return ResponseEntity.ok(path);
    }
}

package com.eshop.product;

import com.eshop.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/c/{category_alias}")
    public String viewCategory(@PathVariable("category_alias") String alias) {
        return "products_by_category";
    }
}

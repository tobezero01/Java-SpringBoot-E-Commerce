package com.eshop.product;

import com.eshop.category.CategoryService;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;


    @GetMapping("/c/{category_alias}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,
                                     Model model) {
        Category category = categoryService.getCategory(alias);
        if (category == null) {
            return "error/404";
        }
        List<Category> listCategoryParents = categoryService.getCategoryParents(category);

        List<Product> listProducts =productService.listByCategory(category.getId());

        model.addAttribute("pageTitle", category.getName());
        model.addAttribute("listCategoryParents", listCategoryParents);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("category", category);

        return "products_by_category";
    }

}

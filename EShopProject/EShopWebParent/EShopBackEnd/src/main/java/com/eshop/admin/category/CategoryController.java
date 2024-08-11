package com.eshop.admin.category;

import com.eshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private  CategoryService categoryService;

    @GetMapping("/categories")
    public String listAll(Model model) {
        List<Category> categoryList = categoryService.listAll();

        model.addAttribute("listCategories", categoryList);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Create new category");
        model.addAttribute("listCategories",listCategories);
        return "categories/category_form";
    }


}

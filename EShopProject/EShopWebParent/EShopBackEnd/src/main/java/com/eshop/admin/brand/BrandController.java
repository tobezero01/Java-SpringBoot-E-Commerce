package com.eshop.admin.brand;

import com.eshop.admin.category.CategoryService;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BrandController {
    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/brands")
    public String listAll(Model model) {
        List<Brand> listBrands = brandService.listAll();
        model.addAttribute("listBrands" , listBrands);
        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String newBrand (Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("pageTitle" , "Create new brand");
        model.addAttribute("listCategories" , listCategories);
        model.addAttribute("brand" , new Brand());


        return "brands/brand_form";
    }
}

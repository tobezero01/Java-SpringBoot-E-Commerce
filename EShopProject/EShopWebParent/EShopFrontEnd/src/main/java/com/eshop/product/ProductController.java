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
    public String viewCategoryByFirstPage(@PathVariable("category_alias") String alias ,
                                     Model model ) {
        return viewCategoryByPage(alias, model, 1);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias ,
                               Model model,
                               @PathVariable("pageNum") int pageNum) {
        Category category = categoryService.getCategory(alias);
        if( category == null) {
            return "error/404";
        }
        List<Category> listCategoryParents =categoryService.getCategoryParents(category);

        Page<Product> pageProducts = productService.listByCategory(1, category.getId());
        List<Product> listProducts = pageProducts.getContent();


        long startCount = (pageNum - 1) *ProductService.PRODUCT_PER_PAGE +1;
        long endCount = startCount + ProductService.PRODUCT_PER_PAGE -1;
        endCount = (endCount > pageProducts.getTotalElements()) ? pageProducts.getTotalElements() : endCount;

        model.addAttribute("currentPage" , pageNum);
        model.addAttribute("totalPages" , pageProducts.getTotalPages());
        model.addAttribute("startCount" , startCount);
        model.addAttribute("endCount" , endCount);
        model.addAttribute("totalItems" , pageProducts.getTotalElements());

        model.addAttribute("pageTitle", category.getName());
        model.addAttribute("listCategoryParents", listCategoryParents);
        model.addAttribute("listProducts", listProducts);

        return "products_by_category";
    }
}

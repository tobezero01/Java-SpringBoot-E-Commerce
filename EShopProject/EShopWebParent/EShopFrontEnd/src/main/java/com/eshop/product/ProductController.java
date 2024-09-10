package com.eshop.product;

import com.eshop.category.CategoryService;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Product;
import com.eshop.exception.CategoryNotFoundException;
import com.eshop.exception.ProductNotFoundException;
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
                                     Model model)  {
        try {
            Category category = categoryService.getCategory(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(category);

            List<Product> listProducts = productService.listByCategory(category.getId());

            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("listProducts", listProducts);
            model.addAttribute("category", category);

            return "products/products_by_category";
        } catch (CategoryNotFoundException e) {
            return "error/404";
        }
    }


    //detail product
    @GetMapping("/d/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {
        try {
            Product product = productService.getProduct(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", product.getShortName());
            return "products/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

}

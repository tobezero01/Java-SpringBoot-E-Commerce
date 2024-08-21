package com.eshop.admin.product;

import com.eshop.admin.brand.BrandService;
import com.eshop.admin.category.CategoryService;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @GetMapping("/products")
    public String listAll(Model model) {
        List<Product> listProducts = productService.listAll();
        model.addAttribute("listProducts" , listProducts);
        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> listBrands = brandService.listAll();
        //List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands",listBrands);
        model.addAttribute("pageTitle" , "Create new product");
        //model.addAttribute("listCategories", listCategories);

        return "products/product_form";
    }

}

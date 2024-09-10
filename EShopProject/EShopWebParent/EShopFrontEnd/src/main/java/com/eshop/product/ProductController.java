package com.eshop.product;

import com.eshop.category.CategoryService;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Product;
import com.eshop.exception.CategoryNotFoundException;
import com.eshop.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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

    @GetMapping("/search")
    public String searchFirstPage(@Param("keyWord") String keyWord, Model model) {
        return search(keyWord, model,1);
    }

    @GetMapping("/search/page/{pageNum}")
    public String search(@Param("keyWord") String keyWord, Model model,
                         @PathVariable("pageNum") int pageNum) {
        Page<Product> pageProducts = productService.search(keyWord, pageNum);
        List<Product> listResult = pageProducts.getContent();
        long startCount = (pageNum - 1) * ProductService.SEARCH_RESULT_PER_PAGE + 1;
        long endCount = startCount + ProductService.SEARCH_RESULT_PER_PAGE - 1;

        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("pageTitle", keyWord + " - Search result");
        model.addAttribute("listResult", listResult);

        return "products/search_result";
    }

}

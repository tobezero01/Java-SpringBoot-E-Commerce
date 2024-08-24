package com.eshop.admin.product;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.brand.BrandNotFoundException;
import com.eshop.admin.brand.BrandService;
import com.eshop.admin.category.CategoryService;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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

    @PostMapping("/products/save")
    public String saveProduct(Product product , RedirectAttributes redirectAttributes,
                              @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            product.setMainImage(fileName);
            Product savedProduct = productService.save(product);

            String uploadDir = "../product-images/" + savedProduct.getId();

            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            productService.save(product);
        }
        redirectAttributes.addFlashAttribute("message","The product has been saved successfully");
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "enable" : "disable";
        String message = "The product Id : " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String delete(@PathVariable(name = "id") Integer id ,
                              Model model , RedirectAttributes redirectAttributes) throws IOException {
        try{
            productService.delete(id);
            redirectAttributes.addFlashAttribute("message" , "The product ID : " + id + " has been deleted successfully!" );
        } catch (ProductNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }
        return "redirect:/products";
    }

}

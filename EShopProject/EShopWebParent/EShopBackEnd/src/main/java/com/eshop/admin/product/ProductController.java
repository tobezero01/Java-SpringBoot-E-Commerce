package com.eshop.admin.product;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.brand.BrandService;
import com.eshop.admin.category.CategoryService;
import com.eshop.admin.exception.ProductNotFoundException;
import com.eshop.admin.security.EShopUserDetails;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        return listByPage(1,"asc", model,null,"name" );
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> listBrands = brandService.listAll();
        //List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create new product");
        model.addAttribute("numberOfExistingExtraImages" , 0);

        //model.addAttribute("listCategories", listCategories);

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam(value = "fileImage") MultipartFile mainImageMultipart,
                              @RequestParam(value = "extraImage") MultipartFile[] extraImageMultipart,
                              @RequestParam(name = "detailNames", required = false ) String [] detailNames,
                              @RequestParam(name = "detailValues", required = false ) String [] detailValues,
                              @AuthenticationPrincipal EShopUserDetails loggerUser
                              ) throws IOException {

        if(loggerUser.hasRole("Salesperson")) {
            productService.saveProductPrice(product);
            redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
            return "redirect:/products";
        }

        setMainImageName(mainImageMultipart , product);
        setExtraImageNames(extraImageMultipart , product);
        setProductDetails(detailNames, detailValues, product);

        Product savedProduct = productService.save(product);
        saveUploadImages(mainImageMultipart , extraImageMultipart, savedProduct);

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
        return "redirect:/products";
    }

    private void setProductDetails(String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;
        for (int c = 0 ; c < detailNames.length; c++) {
            String name = detailNames[c];
            String value = detailValues[c];

            if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }

    private void saveUploadImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultipart, Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            String uploadDir = "../product-images/" + savedProduct.getId();

            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if(extraImageMultipart.length > 0) {
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultipart) {
                if (!multipartFile.isEmpty()) continue;
                String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
            }
        }

    }

    public void setExtraImageNames(MultipartFile[] extraImageMultipart , Product product) {
        if(extraImageMultipart.length > 0) {
            for (MultipartFile multipartFile : extraImageMultipart) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    product.addExtraImage(fileName);
                }
            }
        }
    }

    public void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id , Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();

            Integer numberOfExistingExtraImages = product.getImages().size();

            model.addAttribute("product" , product);
            model.addAttribute("listBrands" , listBrands);
            model.addAttribute("numberOfExistingExtraImages" , numberOfExistingExtraImages);
            model.addAttribute("pageTitle" , "Edit product ( Id = " + id + " )");

            return "products/product_form";
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message" , ex.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum ,
                             @Param("sortDir") String sortDir, Model model,
                             @Param("keyWord") String keyWord ,
                             @Param("sortField") String sortField) {
        if(sortDir == null) {
            sortDir = "asc";
        }
        Page<Product> page = productService.listByPage(pageNum, sortField,sortDir,keyWord);
        List<Product> listProducts = page.getContent();

        long startCount = (pageNum - 1) *ProductService.PRODUCTS_BY_PAGE +1;
        long endCount = startCount + ProductService.PRODUCTS_BY_PAGE -1;
        endCount = (endCount > page.getTotalElements()) ? page.getTotalElements() : endCount;
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage" , pageNum);
        model.addAttribute("totalPages" , page.getTotalPages());
        model.addAttribute("startCount" , startCount);
        model.addAttribute("endCount" , endCount);
        model.addAttribute("totalItems" , page.getTotalElements());
        model.addAttribute("sortDir" , sortDir);
        model.addAttribute("sortField" , sortField);
        model.addAttribute("keyWord" , keyWord);
        model.addAttribute("reverseSortDir" , reverseSortDir);
        model.addAttribute("listProducts" , listProducts);


        return "products/products";
    }

    @GetMapping("/products/detail/{id}")
    public String Product(@PathVariable("id") Integer id , Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);

            model.addAttribute("product" , product);

            return "products/product_detail_modal";
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message" , ex.getMessage());
            return "redirect:/products";
        }
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
    public String delete(@PathVariable(name = "id") Integer id,
                         Model model, RedirectAttributes redirectAttributes) throws IOException {
        try {
            productService.delete(id);
            String extraDir = "../product-images/" + id + "/extras";
            String mainImageDir = "../product-images/" + id ;
            FileUploadUtil.removeDir(extraDir);
            FileUploadUtil.removeDir(mainImageDir);
            redirectAttributes.addFlashAttribute("message", "The product ID : " + id + " has been deleted successfully!");
        } catch (ProductNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }
        return "redirect:/products";
    }



}

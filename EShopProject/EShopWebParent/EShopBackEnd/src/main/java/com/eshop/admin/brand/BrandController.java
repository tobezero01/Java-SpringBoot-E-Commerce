package com.eshop.admin.brand;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.category.CategoryService;
import com.eshop.admin.exception.BrandNotFoundException;
import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.admin.paging.PagingAndSortingParam;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
public class BrandController {
    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/brands")
    public String listAll() {
        return "redirect:/brands/page/1?sortField=name&sortDir=asc";
    }

    @GetMapping("/brands/new")
    public String newBrand (Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("pageTitle" , "Create new brand");
        model.addAttribute("listCategories" , listCategories);
        model.addAttribute("brand" , new Brand());

        return "brands/brand_form";
    }

    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum ,
                             @PagingAndSortingParam(listName = "listBrands") PagingAndSortingHelper helper
    ) {
         brandService.listByPage(pageNum,helper );

        return "brands/brands";
    }

    @PostMapping("/brands/save")
    public String saveBrand(Brand brand , @RequestParam("fileImage")MultipartFile multipartFile ,
                            RedirectAttributes redirectAttributes) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);

            Brand saveBrand = brandService.save(brand);
            String uploadDir = "../brand-logos/" + saveBrand.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }
        else {
            brandService.save(brand);
        }

        redirectAttributes.addFlashAttribute("message" , "The brand has been saved successfully!");

        return "redirect:/brands";
    }


    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id ,
                            Model model , RedirectAttributes redirectAttributes)  {
        try{
            Brand brand = brandService.get(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand" , brand);
            model.addAttribute("pageTitle" , "Edit the brand (ID : " + id +" )");
            model.addAttribute("listCategories" , listCategories);

            return "brands/brand_form";
        } catch (BrandNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/brands";
        }

    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id ,
                            Model model , RedirectAttributes redirectAttributes) throws IOException {
        try{
            brandService.delete(id);

            String brandDir = "../brand-logos/" + id;
            FileUploadUtil.removeDir(brandDir);

            redirectAttributes.addFlashAttribute("message" , "The brand ID : " + id + " has been deleted successfully!" );
        } catch (BrandNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }
        return "redirect:/brands";
    }
}

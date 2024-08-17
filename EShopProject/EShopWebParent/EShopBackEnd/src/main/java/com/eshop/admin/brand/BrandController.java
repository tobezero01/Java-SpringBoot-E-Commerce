package com.eshop.admin.brand;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.category.CategoryPageInfo;
import com.eshop.admin.category.CategoryService;
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
    public String listAll(Model model) {
        return listByPage(1,"asc", model,null,"name" );
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
                             @Param("sortDir") String sortDir, Model model,
                             @Param("keyWord") String keyWord ,
                             @Param("sortField") String sortField) {
        if(sortDir == null) {
            sortDir = "asc";
        }
        Page<Brand> page = brandService.listByPage(pageNum, sortField,sortDir,keyWord);
        List<Brand> listBrands = page.getContent();

        long startCount = (pageNum - 1) *BrandService.BRANDS_PER_PAGE +1;
        long endCount = startCount + BrandService.BRANDS_PER_PAGE -1;
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
        model.addAttribute("listBrands" , listBrands);


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

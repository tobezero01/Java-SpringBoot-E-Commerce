package com.eshop.admin.category;

import com.eshop.admin.FileUploadUtil;
import com.eshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private  CategoryService categoryService;

    @GetMapping("/categories")
    public String listAll(Model model) {
        List<Category> categoryList = categoryService.listAll();

        for(Category category : categoryList) {
            System.out.println(category.getName());
        }

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


    @PostMapping("/categories/save")
    public String saveCategory(Category category,
                               @RequestParam("fileImage")MultipartFile multipartFile ,
                               RedirectAttributes redirectAttributes) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        category.setImage(fileName);

        Category savedCategory = categoryService.save(category);
        String uploadDir = "../category-images/" + savedCategory.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        redirectAttributes.addFlashAttribute("message", "The Category had been save successful!");
        return "redirect:/categories";
    }
}

package com.eshop.admin.category.controller;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.brand.BrandService;
import com.eshop.admin.category.CategoryNotFoundException;
import com.eshop.admin.category.CategoryPageInfo;
import com.eshop.admin.category.CategoryService;
import com.eshop.admin.user.UserService;
import com.eshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
        return listByPage(1 , sortDir, model ,null,"name");
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum ,
                             @Param("sortDir") String sortDir, Model model,
                             @Param("keyWord") String keyWord,
                             @Param("sortField") String sortField) {
        if(sortDir == null || sortDir.isEmpty()) {
            sortDir = "asc";
        }
        long startCount = (pageNum - 1) *CategoryService.ROOT_CATEGORIES_PER_PAGE +1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE -1;
        CategoryPageInfo categoryPageInfo = new CategoryPageInfo();
        List<Category> categoryList = categoryService.listByPage(categoryPageInfo,pageNum, sortDir, keyWord);
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPages" , categoryPageInfo.getTotalPages());
        model.addAttribute("totalItems" , categoryPageInfo.getTotalElements());
        model.addAttribute("currentPage" , pageNum);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("listCategories", categoryList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

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
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = categoryService.save(category);
            String uploadDir = "../category-images/" + savedCategory.getId();

            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else{
            categoryService.save(category);
        }

        redirectAttributes.addFlashAttribute("message", "The Category had been save successful!");
        return "redirect:/categories";
    }


    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model ,
                               RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.get(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("listCategories" , listCategories);
            model.addAttribute("pageTitle", "Edit Category (ID : " + id +")");
            return "categories/category_form";
        } catch (CategoryNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message" , exception.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        categoryService.updateCategoryEnabledStatus(id, enabled);
        String status = enabled ? "enable" : "disable";
        String message = "The category Id : " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id ,
                                 Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            String categoryDir = "../category-images/" + id;
            FileUploadUtil.removeDir(categoryDir);
            redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been delete successful");
        } catch (CategoryNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/categories";
    }
}

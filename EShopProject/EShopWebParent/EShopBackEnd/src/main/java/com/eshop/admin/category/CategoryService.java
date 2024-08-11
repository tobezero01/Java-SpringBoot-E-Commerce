package com.eshop.admin.category;

import com.eshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listAll() {
        return categoryRepository.findAll();
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = categoryRepository.findAll();
        for (Category category : categoriesInDB) {
            if(category.getParent() == null) {
                categoriesUsedInForm.add(new Category(category.getName()));
                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInForm.add(new Category(name));

                    listChildren(categoriesUsedInForm,subCategory,1);
                }
            }
        }
        return categoriesUsedInForm;
    }
    public void listChildren(List<Category> categoriesUsedInForm, Category parent , int subLevel) {
        int newSubLevel = subLevel+1;
        Set<Category> children = parent.getChildren();
        for (Category subCategory : children) {
            String name = "";
            for (int i = 0 ; i < newSubLevel ; i++) {
                name+="--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(new Category(name));
            listChildren(categoriesUsedInForm,subCategory, newSubLevel);
        }
    }
}

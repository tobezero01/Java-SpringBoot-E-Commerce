package com.eshop.client.service;

import com.eshop.client.exception.CategoryNotFoundException;
import com.eshop.client.repository.CategoryRepository;
import com.eshop.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> listNoChildrenCategories() {
        List<Category> listNoChildrenCategories = new ArrayList<>();
        List<Category> listEnabledCategories = categoryRepository.findAllEnabled();
        listEnabledCategories.forEach(category -> {
            Set<Category> children = category.getChildren();
            if (children == null || children.size() == 0) {
                listNoChildrenCategories.add(category);
            }
        });
        return listNoChildrenCategories;
    }

    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category = categoryRepository.findByAliasEnabled(alias);
        if (category == null) {
            throw new CategoryNotFoundException("Category not found with alias " + alias);
        }
        return category;
    }

    public List<Category> getCategoryParents(Category child) {
        List<Category> listParents = categoryRepository.findAllParents();
        Category parent = child.getParent();

        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }
        listParents.add(child);

        return listParents;
    }
}

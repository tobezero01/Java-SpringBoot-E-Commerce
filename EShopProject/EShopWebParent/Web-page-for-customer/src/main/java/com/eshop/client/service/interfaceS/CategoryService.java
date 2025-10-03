package com.eshop.client.service.interfaceS;

import com.eshop.client.dto.CategoryNodeDTO;
import com.eshop.client.exception.CategoryNotFoundException;
import com.eshop.common.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> listTopLevelParents();
    List<Category> listNoChildrenCategories();
    Category getCategory(String alias) throws CategoryNotFoundException;
    List<CategoryNodeDTO> listCategoryTree();
    List<Category> getAncestorsPath(Integer id) throws CategoryNotFoundException;
}

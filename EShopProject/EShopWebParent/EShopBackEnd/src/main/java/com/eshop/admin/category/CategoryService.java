package com.eshop.admin.category;

import com.eshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listAll() {
        return categoryRepository.findAll();
    }
}

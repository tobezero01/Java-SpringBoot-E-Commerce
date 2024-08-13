package com.eshop.admin.category;

import com.eshop.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listAll(String sortDir) {
        Sort sort = Sort.by("name");
        if(sortDir.equals("asc")) {
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }
        List<Category> rootCategories = categoryRepository.findRootCategories(sort);
        return listHierarchicalCategories(rootCategories,sortDir);
    }

    //using copy method to avoid change the database
    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();
        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(),sortDir);

            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHierarchicalUsedInForm(hierarchicalCategories, subCategory, 1,sortDir);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalUsedInForm(List<Category> hierarchicalCategories,
                                               Category parent, int subLevel, String sortDir) {
        int newSubLevel = subLevel+1;
        Set<Category> children = sortSubCategories(parent.getChildren());

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0 ; i < newSubLevel ; i++) {
                name+="--";
            }
            name += subCategory.getName();

            hierarchicalCategories.add(Category.copyFull(subCategory, name));
            listSubHierarchicalUsedInForm(hierarchicalCategories, subCategory, newSubLevel ,sortDir);
        }
    }


    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categoriesInDB = categoryRepository.findRootCategories(Sort.by("name").ascending());
        for (Category category : categoriesInDB) {
            if(category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category));
                Set<Category> children = sortSubCategories(category.getChildren());

                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

                    listSubCategoryUsedInForm(categoriesUsedInForm,subCategory,1);
                }
            }
        }
        return categoriesUsedInForm;
    }

    public void listSubCategoryUsedInForm(List<Category> categoriesUsedInForm, Category parent , int subLevel) {
        int newSubLevel = subLevel+1;
        Set<Category> children = sortSubCategories(parent.getChildren());
        for (Category subCategory : children) {
            String name = "";
            for (int i = 0 ; i < newSubLevel ; i++) {
                name+="--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
            listSubCategoryUsedInForm(categoriesUsedInForm,subCategory, newSubLevel);
        }
    }


    public Category get(Integer id) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID = " + id);
        }
    }

    public String checkUniqueCategory(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0) ;
        Category categoryByName = categoryRepository.findByName(name);

        if(isCreatingNew) {
            if(categoryByName != null) {
                return "DuplicateName";
            }else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);
                if(categoryByAlias != null) {
                    return "DuplicateAlias";
                }
            }
        }
        // edit mod
        else {
            if(categoryByName != null && categoryByName.getId() != id) {
                return "DuplicateName";
            }
            Category categoryByAlias = categoryRepository.findByAlias(alias);
            if(categoryByAlias != null && categoryByAlias.getId() != id) {
                return "DuplicateAlias";
            }
        }

        return "OK";
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children , "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children ,String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category category1, Category category2) {
                if(sortDir.equals("asc")) {
                    return category1.getName().compareTo(category2.getName());
                }else {
                    return category2.getName().compareTo(category1.getName());
                }

            }
        });
        sortedChildren.addAll(children);
        return sortedChildren;
    }
}

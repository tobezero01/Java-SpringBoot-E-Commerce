package com.eshop.admin.category;

import com.eshop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.util.List;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreateRootCategory() {
        Category category = new Category("Electronic");
        Category savedCategory = categoryRepository.save(category);

        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateSubCategory() {
        Category parent = new Category(2);
        Category subCategory = new Category("Screen", parent);
        Category savedCategory = categoryRepository.save(subCategory);

        assertThat(savedCategory.getId()).isGreaterThan(0);

    }

    @Test
    public void testGetCategory() {
        Category category = categoryRepository.findById(1).get();
        System.out.println(category.getName());

        Set<Category> subCategory = category.getChildren();
        for (Category cate : subCategory) {
            System.out.println(cate.getName());
        }

        assertThat(subCategory.size()).isGreaterThan(0);
    }


    @Test
    public void testPrintHierarchicalCategories() {
        Iterable<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            if(category.getParent() == null) {
                System.out.println(category.getName());
                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    System.out.print("--");
                    System.out.println(subCategory.getName());
                    printChildren(subCategory,1);
                }
            }
        }
    }

    public void printChildren(Category parent , int subLevel) {
        int newSubLevel = subLevel+1;
        Set<Category> children = parent.getChildren();
        for (Category subCategory : children) {
            for (int i = 0 ; i < newSubLevel ; i++) {
                System.out.print("--");
            }
            System.out.println(subCategory.getName());

            printChildren(subCategory, newSubLevel);
        }
    }

    @Test
    public void testListRootCategories() {
        List<Category> categories = categoryRepository.findRootCategories();

        for(Category category : categories) {
            System.out.println(category.getName());
        }
    }
}

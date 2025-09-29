package com.eshop.client.repository;

import com.eshop.client.exception.CategoryNotFoundException;
import com.eshop.common.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("select c from Category c where c.enabled = true order by c.name asc")
    List<Category> findAllEnabled();

    @Query("select c from Category c where c.enabled = true and c.alias = ?1")
    Category findByAliasEnabled(String alias);

    @Query("select c from Category c where c.enabled = true and c.parent is null order by c.name asc")
    List<Category> findAllTopLevel();

    default Category getByIdOrThrow(Integer id) throws CategoryNotFoundException {
        return findById(id).orElseThrow(() -> new CategoryNotFoundException(
                "Category not found with id " + id));
    }

}

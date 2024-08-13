package com.eshop.admin.category;

import com.eshop.common.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("Select c From Category c WHERE c.parent.id is null")
    public List<Category> findRootCategories(Sort sort);

    public Category findByName(String name);

    public Category findByAlias(String alias);
}

package com.eshop.client.repository;

import com.eshop.common.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("Select c from Category c Where c.enabled = true Order by c.name ASC")
    public List<Category> findAllEnabled();

    @Query("Select c from Category c Where c.enabled = true and c.alias = ?1")
    public Category findByAliasEnabled(String alias);

    @Query("Select c from Category c Where c.enabled = true and c.parent IS NULL Order by c.name ASC")
    public List<Category> findAllParents();

}

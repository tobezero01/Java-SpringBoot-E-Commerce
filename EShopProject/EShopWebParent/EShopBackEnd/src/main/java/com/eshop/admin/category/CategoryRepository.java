package com.eshop.admin.category;

import com.eshop.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories(Sort sort);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    Page<Category> findRootCategories(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Category> search(String keyWord, Pageable pageable);

    Category findByName(String name);

    Category findByAlias(String alias);

    @Modifying
    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
    void updateEnabledStatus(Integer id, boolean enabled);

    public Long countById(Integer id);
}

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

    @Query("Select c From Category c WHERE c.parent.id is null")
    public List<Category> findRootCategories(Sort sort);

    @Query("Select c From Category c WHERE c.parent.id is null")
    public Page<Category> findRootCategories(Pageable pageable);

    @Query("Select c FROM Category c WHERE c.name LIKE %?1%")
    public Page<Category> search(String keyWord , Pageable pageable);

    public Category findByName(String name);

    public Category findByAlias(String alias);

    @Query("Update Category c SET c.enabled = ?2 WHERE c.id = ?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);

    public Long countById(Integer id);
}

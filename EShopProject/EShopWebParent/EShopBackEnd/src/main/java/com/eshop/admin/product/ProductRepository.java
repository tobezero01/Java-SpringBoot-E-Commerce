package com.eshop.admin.product;

import com.eshop.common.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    public Product findByName(String name);

    @Query("Update Product p Set p.enabled = ?2 Where p.id = ?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);

    public Long countById(Integer id);
}

package com.eshop.admin.product;
import com.eshop.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByName(String name);

    @Modifying
    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    void updateEnabledStatus(Integer id, boolean enabled);

    Long countById(Integer id);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(p.fullDescription) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(p.brand.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(p.category.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Product> findAll(String keyWord, Pageable pageable);

    @Query("SELECT p From Product p WHERE p.name LIKE LOWER(CONCAT('%', ?1, '%'))")
    public Page<Product> searchProductsByName(String keyWord, Pageable pageable);
}

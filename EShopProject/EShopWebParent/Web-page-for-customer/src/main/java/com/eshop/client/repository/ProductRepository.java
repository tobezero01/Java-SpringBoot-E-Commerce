package com.eshop.client.repository;

import com.eshop.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product , Integer> {

    @Query("Select p From Product p Where p.enabled = true " +
            "And (p.category.id = ?1 or p.category.allParentIDs Like %?1%) Order By p.name ASC")
    public List<Product> listByCategory(Integer categoryId);

    public Product findByAlias(String alias);

    @Query(value = "select * from Products p where p.enabled = true and match (name , short_description , full_description) " +
            "against (?1)" , nativeQuery = true)
    public Page<Product> search(String keyWord, Pageable pageable);

    @Query("UPDATE Product p " +
            "SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0), " +
            "p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id = ?1) " +
            "WHERE p.id = ?1")
    @Modifying
    public void updateReviewCountAndAverageRating(Integer productId);
}

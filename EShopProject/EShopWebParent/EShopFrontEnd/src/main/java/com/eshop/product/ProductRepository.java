package com.eshop.product;

import com.eshop.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product , Integer> {

    @Query("Select p From Product p Where p.enabled = true " +
            "And (p.category.id = ?1 or p.category.allParentIDs Like %?1%) Order By p.name ASC")
    public List<Product> listByCategory(Integer categoryId);

    public Product findByAlias(String alias);
}

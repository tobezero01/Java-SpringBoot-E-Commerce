package com.eshop.admin.product;
import com.eshop.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("Select p From Product p Where p.name like %?1% "
            + "Or p.shortDescription Like %?1% "
            + "Or p.fullDescription Like %?1% "
            + "or p.brand.name like %?1% "
            + "or p.category.name like %?1% ")
    public Page<Product> findAll(String keyWord , Pageable pageable);
}

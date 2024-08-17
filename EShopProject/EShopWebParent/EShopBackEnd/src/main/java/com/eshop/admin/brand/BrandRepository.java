package com.eshop.admin.brand;

import com.eshop.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository <Brand , Integer>{
    public Long countById(Integer id) ;

    public Brand findByName(String name);

    @Query("Select b from Brand b where b.name LIKE %?1%")
    public Page<Brand> findAll(String keyWord, Pageable pageable);
}

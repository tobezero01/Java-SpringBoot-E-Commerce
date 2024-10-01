package com.eshop.admin.shippingrate;

import com.eshop.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate , Integer> {

    @Query("Select sr From ShippingRate sr Where sr.country.id = ?1 And sr.state = ?2")
    public ShippingRate findByCountryAndState(Integer countryId , String state);

    @Query("Update ShippingRate sr Set sr.codSupported = ?2 Where sr.id = ?1")
    @Modifying
    public void updateCODSupport(Integer id, boolean enabled);

    @Query("Select sr From ShippingRate sr Where sr.country.name Like %?1% Or sr.state like %?1%")
    public Page<ShippingRate> findAll(String keyWord, Pageable pageable);

    public Long countById(Integer id);
}

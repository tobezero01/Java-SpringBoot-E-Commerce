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

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.id = ?1 AND sr.state = ?2")
    ShippingRate findByCountryAndState(Integer countryId, String state);

    @Modifying
    @Query("UPDATE ShippingRate sr SET sr.codSupported = ?2 WHERE sr.id = ?1")
    void updateCODSupport(Integer id, boolean enabled);

    @Query("SELECT sr FROM ShippingRate sr WHERE " +
            "LOWER(sr.country.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(sr.state) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<ShippingRate> findAll(String keyWord, Pageable pageable);

    Long countById(Integer id);
}

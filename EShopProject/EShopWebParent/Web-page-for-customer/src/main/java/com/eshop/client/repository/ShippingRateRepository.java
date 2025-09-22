package com.eshop.client.repository;

import com.eshop.common.entity.Country;
import com.eshop.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

    public ShippingRate findByCountryAndState(Country country, String state);
}

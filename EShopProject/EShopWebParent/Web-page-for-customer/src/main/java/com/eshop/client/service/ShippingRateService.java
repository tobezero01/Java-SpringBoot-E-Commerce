package com.eshop.client.service;

import com.eshop.client.repository.ShippingRateRepository;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShippingRateService {

    private final ShippingRateRepository shippingRateRepository;

    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();
        if (state == null || state.isEmpty()) {
            state = customer.getCity();
        }
        return shippingRateRepository.findByCountryAndState(customer.getCountry(), state);
    }

    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if (state == null || state.isEmpty()) {
            state = address.getCity();
        }
        return shippingRateRepository.findByCountryAndState(address.getCountry(), state);
    }
}

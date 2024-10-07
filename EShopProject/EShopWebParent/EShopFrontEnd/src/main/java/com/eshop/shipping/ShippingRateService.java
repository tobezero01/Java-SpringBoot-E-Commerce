package com.eshop.shipping;

import com.eshop.common.entity.Address;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShippingRateService {

    @Autowired private ShippingRateRepository shippingRateRepository;

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

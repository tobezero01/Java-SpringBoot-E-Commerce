package com.eshop.client.service.interfaceS;

import com.eshop.common.entity.Address;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;

public interface ShippingRateService {
    ShippingRate getShippingRateForCustomer(Customer customer);
    ShippingRate getShippingRateForAddress(Address address);
}

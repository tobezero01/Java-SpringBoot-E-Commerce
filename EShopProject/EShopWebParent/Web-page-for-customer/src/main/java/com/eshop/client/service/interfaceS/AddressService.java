package com.eshop.client.service.interfaceS;

import com.eshop.common.entity.Address;
import com.eshop.common.entity.Customer;

import java.util.List;

public interface AddressService {
    List<Address> listAddressBook(Customer customer);
    void save(Address address);
    Address get(Integer addressId, Integer customerId);
    void delete(Integer addressId, Integer customerId);
    void setDefaultAddress(Integer defaultAddressId, Integer customerId);
    Address getDefaultAddress(Customer customer);
}

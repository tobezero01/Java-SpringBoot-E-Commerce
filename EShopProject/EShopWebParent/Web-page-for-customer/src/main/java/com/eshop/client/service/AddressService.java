package com.eshop.client.service;

import com.eshop.client.repository.AddressRepository;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }

    public void save(Address address) {
        addressRepository.save(address);
    }

    public Address get(Integer addressId, Integer customerId) {
        return addressRepository.findByIdAndCustomer(addressId, customerId);
    }

    public void delete(Integer addressId, Integer customerId) {
        addressRepository.deleteByIdAndCustomer(addressId, customerId);
    }

    public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
        if (defaultAddressId > 0) {
            addressRepository.setDefaultAddress(defaultAddressId);
        }
        addressRepository.setNonDefaultAddressForOthers(defaultAddressId, customerId);
    }

    public Address getDefaultAddress(Customer customer) {
        return addressRepository.findDefaultByCustomer(customer.getId());
    }

}

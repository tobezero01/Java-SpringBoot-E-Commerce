package com.eshop.client.mapper;

import com.eshop.client.dto.AddressDTO;
import com.eshop.client.dto.request.CreateAddressRequest;
import com.eshop.client.dto.request.UpdateAddressRequest;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.Country;

public final class AddressMapper {
    private AddressMapper(){}

    public static AddressDTO toDto(Address a) {
        return new AddressDTO(
                a.getId(),
                a.getFirstName(),
                a.getLastName(),
                a.getPhoneNumber(),
                a.getAddressLine1(),
                a.getAddressLine2(),
                a.getCity(),
                a.getState(),
                a.getPostalCode(),
                a.getCountry() != null ? a.getCountry().getId() : null,
                a.getCountryName() != null ? a.getCountryName() : null,
                a.isDefaultForShipping()
        );
    }

    public static Address fromCreate(CreateAddressRequest request) {
        Address address = new Address();
        address.setFirstName(request.firstName());
        address.setLastName(request.lastName());
        address.setPhoneNumber(request.phoneNumber());
        address.setAddressLine1(request.addressLine1());
        address.setAddressLine2(request.addressLine2());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        if (request.countryId() != null) address.setCountry(new Country(request.countryId()));
        address.setDefaultForShipping(Boolean.TRUE.equals(request.defaultForShipping()));
        return address;
    }

    public static void applyUpdate(Address address, UpdateAddressRequest request) {
        if (request.firstName() != null) address.setFirstName(request.firstName());
        if (request.lastName() != null) address.setLastName(request.lastName());
        if (request.phoneNumber() != null) address.setPhoneNumber(request.phoneNumber());
        if (request.addressLine1() != null) address.setAddressLine1(request.addressLine1());
        if (request.addressLine2() != null) address.setAddressLine2(request.addressLine2());
        if (request.city() != null) address.setCity(request.city());
        if (request.state() != null) address.setState(request.state());
        if (request.postalCode() != null) address.setPostalCode(request.postalCode());
        if (request.countryId() != null) address.setCountry(new Country(request.countryId()));
        if (request.defaultForShipping() != null) address.setDefaultForShipping(request.defaultForShipping());
    }
}

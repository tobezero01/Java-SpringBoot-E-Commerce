package com.eshop.client.dto;

public record AddressDTO(
        Integer id,
        String firstName,
        String lastName,
        String phoneNumber,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        Integer countryId,
        String countryName,
        Boolean defaultForShipping
) {}

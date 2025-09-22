package com.eshop.client.dto.request;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String countryCode, // optional
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String phone
) {}

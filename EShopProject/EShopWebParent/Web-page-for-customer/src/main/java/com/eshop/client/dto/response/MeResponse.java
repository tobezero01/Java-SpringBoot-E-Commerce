package com.eshop.client.dto.response;

public record MeResponse(Integer id, String email, String firstName, String lastName,
                         String phoneNumber, String address) {}


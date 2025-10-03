package com.eshop.client.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {

    @Size(max = 45)
    private String firstName;

    @Size(max = 45)
    private String lastName;

    @Size(max = 15)
    private String phone;

    @Size(max = 64)
    private String addressLine1;

    @Size(max = 64)
    private String addressLine2;

    @Size(max = 45)
    private String city;

    @Size(max = 45)
    private String state;

    @Size(max = 10)
    private String postalCode;         // map -> postalCode

    private String countryCode;
    private Integer countryId;
}

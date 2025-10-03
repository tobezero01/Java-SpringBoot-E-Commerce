package com.eshop.client.service.interfaceS;

import com.eshop.client.dto.request.ProfileUpdateRequest;
import com.eshop.client.dto.response.ProfileResponse;
import com.eshop.common.entity.AuthenticationType;
import com.eshop.common.entity.Customer;

public interface CustomerService {
    void updateAuthenticationType(Customer customer, AuthenticationType type);
    void addCustomerUponOAuthLogin(String name, String email, String countryCode);
    Customer getCustomerByEmail(String email);

    ProfileResponse getCurrentProfile();
    ProfileResponse updateCurrentProfile(ProfileUpdateRequest req);
}

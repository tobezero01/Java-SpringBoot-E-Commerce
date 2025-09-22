package com.eshop.client.security.oauth;

import com.eshop.client.service.CustomerService;
import com.eshop.common.entity.AuthenticationType;
import com.eshop.common.entity.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired private CustomerService customerService;

    public OAuthLoginSuccessHandler(@Lazy CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
        String name = oAuth2User.getName();
        String email = oAuth2User.getEmail();
        String countryCode = request.getLocale().getCountry();

        Customer customer = customerService.getCustomerByEmail(email);
        if (customer == null) {
            customerService.addCustomerUponOAuthLogin(name , email,countryCode);
        }else {
            oAuth2User.setFullName(customer.getFullName());
            customerService.updateAuthenticationType(customer, AuthenticationType.GOOGLE);
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

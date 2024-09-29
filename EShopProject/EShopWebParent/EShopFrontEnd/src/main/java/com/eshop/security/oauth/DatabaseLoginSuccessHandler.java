package com.eshop.security.oauth;

import com.eshop.common.entity.AuthenticationType;
import com.eshop.common.entity.Customer;
import com.eshop.customer.CustomerService;
import com.eshop.security.CustomerUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private CustomerService customerService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
        Customer customer = userDetails.getCustomer();
        customerService.updateAuthenticationType(customer , AuthenticationType.DATABASE);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

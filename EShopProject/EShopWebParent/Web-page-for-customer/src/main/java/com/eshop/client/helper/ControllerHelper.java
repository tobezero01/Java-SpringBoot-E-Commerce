package com.eshop.client.helper;

import com.eshop.client.exception.CustomerNotFoundException;
import com.eshop.client.repository.CustomerRepository;

import com.eshop.common.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ControllerHelper {
    private final CustomerRepository customerRepository;

    // lays customer hien tai theo jwt
    public Customer requireAuthenticatedCustomer() throws CustomerNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("Unauthorized");
        }
        String email = auth.getName();
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) throw new CustomerNotFoundException("Customer not found");
        return customer;
    }
}

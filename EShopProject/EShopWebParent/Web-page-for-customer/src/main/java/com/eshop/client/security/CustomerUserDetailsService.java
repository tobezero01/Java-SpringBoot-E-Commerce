package com.eshop.client.security;

import com.eshop.client.repository.CustomerRepository;
import com.eshop.common.entity.Customer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email);

        if (customer == null) {
            throw new UsernameNotFoundException("No Customer Found with the Email : " + email);
        }

        return new CustomerUserDetails(customer);
    }

    @Transactional(readOnly = true)
    public CustomerUserDetails loadUserById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found by id: " + id));
        return new CustomerUserDetails(customer);
    }
}


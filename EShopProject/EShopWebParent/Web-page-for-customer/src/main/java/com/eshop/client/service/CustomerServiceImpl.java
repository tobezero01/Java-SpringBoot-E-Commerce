package com.eshop.client.service;

import com.eshop.client.dto.request.ProfileUpdateRequest;
import com.eshop.client.dto.response.ProfileResponse;
import com.eshop.client.repository.CountryRepository;
import com.eshop.client.repository.CustomerRepository;
import com.eshop.client.security.CustomerUserDetails;
import com.eshop.client.service.interfaceS.CustomerService;
import com.eshop.common.entity.AuthenticationType;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

     private final CustomerRepository customerRepository;
     private final CountryRepository countryRepository;

     private final PasswordEncoder passwordEncoder;
     @Override
    public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
        if (customer.getAuthenticationType() == null || !customer.getAuthenticationType().equals(authenticationType)) {
            customerRepository.updateAuthenticationType(customer.getId(), authenticationType);
        }
    }

    @Override
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getCurrentProfile() {
        Customer customer = getCurrentCustomerManaged();
        return toResponse(customer);
    }

    @Override
    @Transactional
    public ProfileResponse updateCurrentProfile(ProfileUpdateRequest request) {
        Customer customer = getCurrentCustomerManaged();
        if (notBlank(request.getFirstName())) customer.setFirstName(request.getFirstName().trim());
        if (notBlank(request.getLastName()))  customer.setLastName(request.getLastName().trim());

        if (request.getPhone() != null) customer.setPhoneNumber(request.getPhone().trim());

        if (request.getAddressLine1() != null) customer.setAddressLine1(request.getAddressLine1().trim());
        if (request.getAddressLine2() != null) customer.setAddressLine2(request.getAddressLine2().trim());

        if (request.getCity() != null)   customer.setCity(request.getCity().trim());
        if (request.getState() != null)  customer.setState(request.getState().trim());
        if (request.getPostalCode() != null) customer.setPostalCode(request.getPostalCode().trim());

        Country country = null;
        if (notBlank(request.getCountryCode())) {
            country = countryRepository.findByCode(request.getCountryCode().trim());
            if (country == null) throw new IllegalArgumentException("Invalid country code: " + request.getCountryCode());
        } else if (request.getCountryId() != null) {
            country = countryRepository.findById(request.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid country id: " + request.getCountryId()));
        }
        if (country != null) customer.setCountry(country);

        if (customer.getAddressLine2() == null) customer.setAddressLine2("");

        customerRepository.save(customer);
        return toResponse(customer);
    }

    @Override
    public void addCustomerUponOAuthLogin( String name, String email, String countryCode) {
        Customer customer = new Customer();
        customer.setEmail(email);
        setName(name, customer);
        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(AuthenticationType.GOOGLE);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setAddressLine2("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostalCode("");
        customer.setCountry(countryRepository.findByCode(countryCode));

        customerRepository.save(customer);
    }

    private void setName(String name, Customer customer) {
        String[] nameArray = name.split(" ");
        if (nameArray.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        } else {
            String firstName = nameArray[0];
            customer.setFirstName(firstName);

            String lastName = name.replaceFirst(firstName , "");
            customer.setLastName(lastName);
        }
    }

    private Customer getCurrentCustomerManaged() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal()))
            throw new RuntimeException("Unauthorized");
        CustomerUserDetails user = (CustomerUserDetails) auth.getPrincipal();
        Integer id = user.getCustomer().getId();
        // Lấy entity managed để update
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    private static boolean notBlank(String s) { return s != null && !s.isBlank(); }

    private static ProfileResponse toResponse(Customer customer) {
        String fullName = ((customer.getFirstName() == null ? "" : customer.getFirstName()) + " " +
                (customer.getLastName()  == null ? "" : customer.getLastName())).trim();
        String countryCode = customer.getCountry() != null ? customer.getCountry().getCode() : null;
        String countryName = customer.getCountry() != null ? customer.getCountry().getName() : null;

        return new ProfileResponse(
                customer.getEmail(),
                customer.getFirstName(), customer.getLastName(), fullName,
                customer.getPhoneNumber(),
                customer.getAddressLine1(), customer.getAddressLine2(),
                customer.getCity(), customer.getState(), customer.getPostalCode(),
                countryCode, countryName
        );
    }

}

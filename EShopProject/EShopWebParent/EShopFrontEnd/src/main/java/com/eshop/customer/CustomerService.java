package com.eshop.customer;

import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.setting.CountryRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
@Transactional
public class CustomerService {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private CountryRepository countryRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer == null;
    }

    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);
        System.out.println(1111);
        customerRepository.save(customer);
    }

    private void encodePassword(Customer customer) {
        String encode = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encode);
    }

    public boolean verify(String verificationCode) {
        Customer customer = customerRepository.findByVerificationCode(verificationCode);

        if (customer == null || customer.isEnabled()) {
            return false;
        } else  {
            customerRepository.enable(customer.getId());
            return true;
        }
    }

}

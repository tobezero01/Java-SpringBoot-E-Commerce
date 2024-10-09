package com.eshop.customer;

import com.eshop.common.entity.AuthenticationType;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.exception.CustomerNotFoundException;
import com.eshop.setting.repository.CountryRepository;
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

    @Autowired private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService( CustomerRepository customerRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer == null;
    }

    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);
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

    public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
        if (customer.getAuthenticationType() == null || !customer.getAuthenticationType().equals(authenticationType)) {
            customerRepository.updateAuthenticationType(customer.getId(), authenticationType);
        }
    }


    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

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

    public void update(Customer customerInForm) {
        Customer customerInDb = customerRepository.findById(customerInForm.getId()).get();
        if (customerInDb.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
            if(!customerInForm.getPassword().isEmpty()) {
                String encodePassword = passwordEncoder.encode(customerInForm.getPassword());
                customerInForm.setPassword(encodePassword);
            }
            else {
                customerInForm.setPassword(customerInDb.getPassword());
            }
        } else {
            customerInForm.setPassword(customerInDb.getPassword());
        }

        customerInForm.setEnabled(customerInDb.isEnabled());
        customerInForm.setVerificationCode(customerInDb.getVerificationCode());
        customerInForm.setCreatedTime(customerInDb.getCreatedTime());
        customerInForm.setAuthenticationType(customerInDb.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDb.getResetPasswordToken());

        customerRepository.save(customerInForm);
    }

    public String updateResetPasswordToTokenEmail(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email);
        if(customer != null) {
            String token = RandomString.make(30);
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);

            return token;
        } else {
            throw new CustomerNotFoundException("Could not find any customer with Email = " + email );
        }
    }

    public Customer getByResetPasswordToken(String token) {
        return customerRepository.findByResetPasswordToken(token);
    }

    public void updatePasswordToken(String token, String newPassword) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByResetPasswordToken(token);
        if (customer == null) {
            throw new CustomerNotFoundException("No customer found : invalid token");
        }
        customer.setPassword(newPassword);
        customer.setResetPasswordToken(null);
        encodePassword(customer);

        customerRepository.save(customer);
    }
}

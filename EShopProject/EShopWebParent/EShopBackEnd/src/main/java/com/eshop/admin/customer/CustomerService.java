package com.eshop.admin.customer;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.eshop.admin.exception.CustomerNotFoundException;
import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.admin.setting.country.CountryRepository;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CustomerService {
    public static final int CUSTOMERS_PER_PAGE = 10;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private CountryRepository countryRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public void listByPage(int pageNum , PagingAndSortingHelper helper) {
        Sort sort = Sort.by(helper.getSortField());
        sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum-1, CUSTOMERS_PER_PAGE, sort);
        Page<Customer> page = null;
        if(helper.getKeyWord() != null) {
            page = customerRepository.findAll(helper.getKeyWord() , pageable);
        } else {
            page = customerRepository.findAll(pageable);
        }
        helper.updateModelAttributes(pageNum, page);
    }

    public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
        customerRepository.updateEnabledStatus(id, enabled);
    }

    public Customer get(Integer id) throws CustomerNotFoundException {
        try {
            return customerRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new CustomerNotFoundException("Could not find any customer with ID = " + id);
        }
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }


    public boolean isEmailUnique(Integer id, String email) {
        Customer existCustomer = customerRepository.findByEmail(email);

        if (existCustomer != null && existCustomer.getId() != id) {
            return false;
        }
        return true;
    }

    public void save(Customer customerInForm) {
        Customer customerInDb = customerRepository.findById(customerInForm.getId()).get();
        if(!customerInForm.getPassword().isEmpty()) {
            String encodePassword = passwordEncoder.encode(customerInForm.getPassword());
            customerInForm.setPassword(encodePassword);
        }
        else {
            customerInForm.setPassword(customerInDb.getPassword());
        }
        customerInForm.setEnabled(customerInDb.isEnabled());
        customerInForm.setVerificationCode(customerInDb.getVerificationCode());
        customerInForm.setCreatedTime(customerInDb.getCreatedTime());
        customerInForm.setAuthenticationType(customerInDb.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDb.getResetPasswordToken());
        customerRepository.save(customerInForm);
    }

    public void delete(Integer id) throws CustomerNotFoundException {
        Long count = customerRepository.countById(id);
        if (count == null || count == 0) {
            throw new CustomerNotFoundException("Could not find any customer with ID = " + id);
        }
        customerRepository.deleteById(id);
    }
}

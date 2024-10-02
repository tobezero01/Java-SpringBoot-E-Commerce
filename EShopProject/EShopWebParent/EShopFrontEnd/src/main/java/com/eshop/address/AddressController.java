package com.eshop.address;

import com.eshop.Utility;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.Customer;
import com.eshop.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AddressController {

    @Autowired private AddressService addressService;
    @Autowired private CustomerService customerService;

    @GetMapping("/address_book")
    public String showAddressBook(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<Address> listAddresses = addressService.listAddressBook(customer);
        model.addAttribute("listAddresses" , listAddresses);
        model.addAttribute("customer", customer);
        return "address_book/addresses";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getMailOfAuthenticatedCustomer(request);

        return customerService.getCustomerByEmail(email);
    }

}

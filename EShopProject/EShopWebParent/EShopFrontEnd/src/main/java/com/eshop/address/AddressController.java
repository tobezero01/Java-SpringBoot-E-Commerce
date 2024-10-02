package com.eshop.address;

import com.eshop.Utility;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;
import com.eshop.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class AddressController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/address_book")
    public String showAddressBook(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<Address> listAddresses = addressService.listAddressBook(customer);

        boolean usePrimaryAddressAsDefault = true;
        for (Address address : listAddresses) {
            if (address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false;
                break;
            }
        }
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("customer", customer);
        return "address_book/addresses";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getMailOfAuthenticatedCustomer(request);

        return customerService.getCustomerByEmail(email);
    }

    @GetMapping("/address_book/new")
    public String newAddress(Model model) {
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Add new book");

        return "address_book/address_form";
    }

    @PostMapping("/address_book/save")
    public String save(Address address, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Customer customer = getAuthenticatedCustomer(request);

        address.setCustomer(customer);
        addressService.save(address);
        redirectAttributes.addFlashAttribute("message", "The address has been saved successfully");

        return "redirect:/address_book";
    }

    @GetMapping("/address_book/edit/{id}")
    public String editRate(@PathVariable(name = "id") Integer addressId,
                           Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<Country> listCountries = customerService.listAllCountries();
        Address address = addressService.get(addressId, customer.getId());

        model.addAttribute("address", address);
        model.addAttribute("pageTitle", "Edit the Address (ID : " + addressId + " )");
        model.addAttribute("listCountries", listCountries);

        return "address_book/address_form";
    }

    @GetMapping("/address_book/delete/{id}")
    public String deleteRate(@PathVariable(name = "id") Integer addressId,
                             HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {

        Customer customer = getAuthenticatedCustomer(request);
        addressService.delete(addressId, customer.getId());

        redirectAttributes.addFlashAttribute("message", "The address has been deleted successfully");
        return "redirect:/address_book";
    }


}

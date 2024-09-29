package com.eshop.shoppingCart;

import com.eshop.Utility;
import com.eshop.common.entity.Customer;
import com.eshop.customer.CustomerService;
import com.eshop.exception.CustomerNotFoundException;
import com.eshop.exception.ShoppingCartException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartRestController {
    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable(name = "productId") Integer productId ,
                                   @PathVariable("quantity") Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = cartService.addProductToCart(productId, quantity, customer);
            return updatedQuantity + " item(s) of this product were added to your shopping cart";
        } catch (CustomerNotFoundException ex) {
            return "You must login to add this product to Cart";
        } catch (ShoppingCartException e) {
            return e.getMessage();
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getMailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No Authenticated Customer");
        }

        return customerService.getCustomerByEmail(email);
    }
}

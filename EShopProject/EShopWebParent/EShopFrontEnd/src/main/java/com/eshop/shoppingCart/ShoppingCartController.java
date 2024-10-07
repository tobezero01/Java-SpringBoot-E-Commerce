package com.eshop.shoppingCart;

import com.eshop.Utility;
import com.eshop.address.AddressService;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;
import com.eshop.customer.CustomerService;
import com.eshop.shipping.ShippingRateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired private ShoppingCartService cartService;
    @Autowired private CustomerService customerService;

    @Autowired private AddressService addressService;

    @Autowired private ShippingRateService shippingRateService;

    @GetMapping("/cart")
    public String viewCart(Model model , HttpServletRequest request)  {
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartService.listCartItems(customer);

        float estimatedTotal = 0.0F;
        for (CartItem cartItem : cartItems) {
            estimatedTotal += cartItem.getSubtotal();
        }

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        if (defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else  {
            usePrimaryAddressAsDefault = true;
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate != null);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);

        return "cart/shopping_cart";
    }
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getMailOfAuthenticatedCustomer(request);

        return customerService.getCustomerByEmail(email);
    }

}

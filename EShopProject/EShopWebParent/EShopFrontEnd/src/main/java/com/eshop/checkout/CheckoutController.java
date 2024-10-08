package com.eshop.checkout;

import com.eshop.Utility;
import com.eshop.address.AddressService;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;
import com.eshop.common.entity.order.PaymentMethod;
import com.eshop.customer.CustomerService;
import com.eshop.order.OrderService;
import com.eshop.shipping.ShippingRateService;
import com.eshop.shoppingCart.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class CheckoutController {

    @Autowired private CheckoutService checkoutService;
    @Autowired private CustomerService customerService;
    @Autowired private AddressService addressService;
    @Autowired private ShippingRateService shippingRateService;
    @Autowired private ShoppingCartService shoppingCartService;

    @Autowired private OrderService orderService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model , HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        Address defaultAddress =addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            model.addAttribute("shippingAddress", customer.getAddress());
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }
        if (shippingRate == null) {
            return "redirect:/cart";
        }
        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems",cartItems);
        return "checkout/checkout";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getMailOfAuthenticatedCustomer(request);

        return customerService.getCustomerByEmail(email);
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) {
        String paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);
        Customer customer = getAuthenticatedCustomer(request);
        Address defaultAddress =addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        shoppingCartService.deleteByCustomer(customer);

        return "checkout/order_completed";
    }
}

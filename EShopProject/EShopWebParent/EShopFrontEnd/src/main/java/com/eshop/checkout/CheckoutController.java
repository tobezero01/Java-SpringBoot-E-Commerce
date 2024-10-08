package com.eshop.checkout;

import com.eshop.Utility;
import com.eshop.address.AddressService;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;
import com.eshop.common.entity.order.Order;
import com.eshop.common.entity.order.PaymentMethod;
import com.eshop.customer.CustomerService;
import com.eshop.order.OrderService;
import com.eshop.setting.CurrencySettingBag;
import com.eshop.setting.EmailSettingBag;
import com.eshop.setting.SettingService;
import com.eshop.shipping.ShippingRateService;
import com.eshop.shoppingCart.ShoppingCartService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired private CheckoutService checkoutService;
    @Autowired private CustomerService customerService;
    @Autowired private AddressService addressService;
    @Autowired private ShippingRateService shippingRateService;
    @Autowired private ShoppingCartService shoppingCartService;
    @Autowired private OrderService orderService;
    @Autowired private SettingService settingService;

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
    public String placeOrder(HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
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

        Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        shoppingCartService.deleteByCustomer(customer);

        sendOrderConfirmationEmail(request, createdOrder);

        return "checkout/order_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request, Order createdOrder)
            throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        mailSender.setDefaultEncoding("utf-8");
        String toAddress = createdOrder.getCustomer().getEmail();
        String subject = emailSettings.getOrderConfirmationSubject();
        String content = emailSettings.getOrderConfirmationContent();

        subject = subject.replace("[[orderId]]" , String.valueOf(createdOrder.getId()));
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getMailFrom() , emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss , dd MMM yyyy");
        String orderTime = dateFormat.format(createdOrder.getOrderTime());

        CurrencySettingBag currencySettings = settingService.getCurrencySettings();
        String totalAmount = Utility.formatCurrency(createdOrder.getTotal(), currencySettings);

        content = content.replace("[[name]]", createdOrder.getCustomer().getFullName());
        content = content.replace("[[orderId]]", String.valueOf(createdOrder.getId()));
        content = content.replace("[[orderTime]]", orderTime);
        content = content.replace("[[shippingAddress]]", createdOrder.getAddress());
        content = content.replace("[[total]]", totalAmount);
        content = content.replace("[[paymentMethod]]" , createdOrder.getPaymentMethod().toString());

        helper.setText(content, true);
        mailSender.send(message);
    }

}

package com.eshop.client.service.interfaceS;

import com.eshop.common.entity.Customer;
import com.eshop.common.entity.order.Order;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface OrderEmailService {
    void sendOrderConfirmation(Customer customer, Order order)
            throws MessagingException, UnsupportedEncodingException;
}

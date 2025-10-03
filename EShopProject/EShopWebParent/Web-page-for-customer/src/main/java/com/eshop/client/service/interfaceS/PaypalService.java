package com.eshop.client.service.interfaceS;

import com.eshop.client.dto.paypalDTO.PaypalOrderValidation;
import com.eshop.client.exception.PaypalAPIException;

import java.util.Map;

public interface PaypalService {
    Map<String, Object> createOrder(Float amount, String currency, String returnUrl, String cancelUrl) throws PaypalAPIException;
    Map<String, Object> captureOrder(String paypalOrderId) throws PaypalAPIException;
    PaypalOrderValidation validateOrder(String paypalOrderId, Float expectedAmount, String expectedCurrency) throws PaypalAPIException;
}

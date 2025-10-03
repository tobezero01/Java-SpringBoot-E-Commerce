package com.eshop.client.service.interfaceS;

import com.eshop.client.dto.CheckoutSummaryDTO;
import com.eshop.client.dto.request.PlaceOrderRequest;
import com.eshop.client.dto.response.PlaceOrderResponse;
import com.eshop.client.helper.CheckoutInfo;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;
import com.eshop.common.entity.order.Order;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

public interface CheckoutAppService {
    CheckoutSummaryDTO summarize(Customer customer, Integer addressId);
    PlaceOrderResponse placeOrderCod(Customer customer, PlaceOrderRequest request)
            throws MessagingException, UnsupportedEncodingException;
    Order createPendingPaypalOrder(Customer customer, Integer addressId, String note);
    Order finalizePaypalOrderAfterCapture(Customer customer, String localOrderNumber,
                                          String captureId, Date capturedAt,
                                          Float capturedAmount, String capturedCurrency);
    CheckoutInfo prepareCheckout(List<CartItem> items, ShippingRate rate);
}

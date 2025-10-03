package com.eshop.client.controller;

import com.eshop.client.dto.CheckoutSummaryDTO;
import com.eshop.client.dto.request.PlaceOrderRequest;
import com.eshop.client.dto.response.PlaceOrderResponse;
import com.eshop.client.exception.CustomerNotFoundException;
import com.eshop.client.helper.ControllerHelper;
import com.eshop.client.service.interfaceS.CheckoutAppService;
import com.eshop.common.entity.Customer;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutRestController {
    private final ControllerHelper helper;
    private final CheckoutAppService checkoutApp;

    /** GET /api/checkout/summary?addressId= */
    @GetMapping("/summary")
    public CheckoutSummaryDTO summary(@RequestParam(required = false) Integer addressId) throws CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();
        return checkoutApp.summarize(customer, addressId);
    }

    /** POST /api/checkout/place-order : tạo đơn */
    @PostMapping("/place-order")
    public ResponseEntity<PlaceOrderResponse> placeOrderCod(@RequestBody PlaceOrderRequest req) throws MessagingException, UnsupportedEncodingException, CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();
        // Buộc COD ở đây
        PlaceOrderRequest cod = new PlaceOrderRequest(req.addressId(), "COD", req.note());
        PlaceOrderResponse response = checkoutApp.placeOrderCod(customer, cod);
        return ResponseEntity.ok(response);
    }

}

package com.eshop.checkout.paypal;

import com.eshop.common.entity.setting.Setting;
import com.eshop.setting.SettingService;
import com.eshop.setting.settingBag.PaymentSettingBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class PaypalService {
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    @Autowired private SettingService settingService;

    public  boolean validateOrder(String orderId) throws PaypalAPIException {
        PaypalOrderResponse orderResponse = getOrderDetails(orderId);
        return orderResponse.validate(orderId);
    }

    private PaypalOrderResponse getOrderDetails(String orderId) throws PaypalAPIException {
        ResponseEntity<PaypalOrderResponse> response = makeRequest(orderId);

        HttpStatus statusCode = (HttpStatus) response.getStatusCode();
        if (!statusCode.equals(HttpStatus.OK)) {
            throwExceptionForNonOkResponse(statusCode);
        }
        PaypalOrderResponse orderResponse = response.getBody();
        return orderResponse;
    }

    private ResponseEntity<PaypalOrderResponse> makeRequest(String orderId) {
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String baseUrl = paymentSettings.getBaseUrl();
        String requestURL = baseUrl + GET_ORDER_API + orderId;
        String clientId = paymentSettings.getClientId();
        String clientSecret = paymentSettings.getClientSecret();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.add("Accept-Language", "en_US");
        httpHeaders.setBasicAuth(clientId, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PaypalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request, PaypalOrderResponse.class);
        return response;
    }

    private void throwExceptionForNonOkResponse(HttpStatus statusCode) throws PaypalAPIException {
        String message = null;
        switch (statusCode) {
            case NOT_FOUND -> {
                message = "Order ID not found";
            }
            case BAD_REQUEST -> {
                message = "Bad request to Paypal checkout API";
            }
            case INTERNAL_SERVER_ERROR -> {
                message = "Paypal Server Error";
            }
            default -> {
                message = "Paypal return non-OK status code";
            }
        }
        throw new PaypalAPIException(message);
    }
}

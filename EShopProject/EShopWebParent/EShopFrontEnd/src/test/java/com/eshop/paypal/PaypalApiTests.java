package com.eshop.paypal;

import com.eshop.checkout.paypal.PaypalOrderResponse;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


public class PaypalApiTests {
    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static final String CLIENT_ID ="ASaWrxkri96VT4Gr_4JV4ipjVKKLKqonlnDoYTGX2tqMkdMnrZY0iSV-4vsyd47TUVIWTbPj7fAB73Tp";
    private static final String CLIENT_SECRET ="EG2fzevEa5N35azt8XO0BZtr_o-nhyjT1YV4b-YMxnVVYp-_KMSG5JAPndv0I-T6pt9F2mPZCRvhdTA4";

    @Test
    public void testGetOrderDetails() {
        String orderId = "VALID_ORDER_ID"; // Thay VALID_ORDER_ID bằng orderId thực tế
        String requestURL = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.add("Accept-Language", "en_US");
        httpHeaders.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<PaypalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request, PaypalOrderResponse.class);
            PaypalOrderResponse orderResponse = response.getBody();
            System.out.println("Order ID = " + orderResponse.getId());
            System.out.println("Validated : " + orderResponse.validate(orderId));
        } catch (HttpClientErrorException e) {
            System.out.println("Error occurred: " + e.getResponseBodyAsString());
        }
    }

}

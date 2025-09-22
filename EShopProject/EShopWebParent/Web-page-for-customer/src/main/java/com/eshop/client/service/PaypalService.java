package com.eshop.client.service;

import com.eshop.client.dto.paypalDTO.PaypalOrderValidation;
import com.eshop.client.exception.PaypalAPIException;
import com.eshop.client.setting.PaymentSettingBag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaypalService {
    private final RestTemplate restTemplate;
    private final SettingService settingService;

    /** Lấy Access Token từ PayPal (OAuth2 Client Credentials) */
    private String getAccessToken(PaymentSettingBag bag) throws PaypalAPIException {
        String url = bag.getBaseUrl() + "/v1/oauth2/token";
        String credentials = bag.getClientId() + ":" + bag.getClientSecret();
        String basic = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", basic);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(form, headers), Map.class);
            Object token = response.getBody().get("access_token");
            if (token == null) throw new PaypalAPIException("No access_token returned from PayPal");
            return token.toString();
        } catch (HttpStatusCodeException e) {
            throw new PaypalAPIException("OAuth2 failed: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new PaypalAPIException("OAuth2 failed: " + ex.getMessage());
        }
    }

    /** Gọi PayPal Orders API để lấy chi tiết đơn theo orderId */
    private Map<String, Object> getOrder(String accessToken, PaymentSettingBag bag, String paypalOrderId) throws PaypalAPIException{
        String url = bag.getBaseUrl() + "/v2/checkout/orders/" + paypalOrderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        try {
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            throw new PaypalAPIException("Get order failed: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new PaypalAPIException("Get order failed: " + ex.getMessage());
        }
    }

    /* ===== Create Order ===== */
    public Map<String, Object> createOrder(Float amount, String currency) throws PaypalAPIException {
        PaymentSettingBag bag = settingService.getPaymentSettings();
        String accessToken = getAccessToken(bag);

        String url = baseUrl(bag) + "/v2/checkout/orders";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Body tối thiểu
        Map<String, Object> body = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                        "amount", Map.of(
                                "currency_code", currency,
                                "value", String.format(java.util.Locale.US, "%.2f", amount)
                        )
                )),
                "application_context", Map.of(
                        "shipping_preference", "NO_SHIPPING" // nếu không ship qua PayPal
                )
        );

        try {
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            throw new PaypalAPIException("Create order failed: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new PaypalAPIException("Create order failed: " + ex.getMessage());
        }
    }

    /* ===== Capture Order ===== */
    public Map<String, Object> captureOrder(String paypalOrderId) throws PaypalAPIException {
        PaymentSettingBag bag = settingService.getPaymentSettings();
        String accessToken = getAccessToken(bag);

        String url = baseUrl(bag) + "/v2/checkout/orders/" + paypalOrderId + "/capture";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(Map.of(), headers), Map.class);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            throw new PaypalAPIException("Capture order failed: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new PaypalAPIException("Capture order failed: " + ex.getMessage());
        }
    }

    public PaypalOrderValidation validateOrder(String paypalOrderId, Float expectedAmount, String expectedCurrency)
            throws PaypalAPIException {
        PaymentSettingBag bag = settingService.getPaymentSettings();
        String accessToken = getAccessToken(bag);
        Map<String, Object> order = getOrder(accessToken, bag, paypalOrderId);

        String status = str(order.get("status"));
        String intent = str(order.get("intent"));

        Map<String, Object> amount = null;
        try {
            Map<String, Object> purchase_units = (Map<String, Object>) order.get("purchase_units");
            if (purchase_units != null && !purchase_units.isEmpty()) {
                Map<String, Object> pu0 = (Map<String, Object>) purchase_units.get(0);
                amount = (Map<String, Object>) pu0.get("amount");
            }
        }catch (ClassCastException ignored) {}

        String currency = (amount != null) ? str(amount.get("currency_code")) : null;
        Float value = null;
        if (amount != null && amount.get("value") != null) {
            try { value = Float.parseFloat(amount.get("value").toString()); } catch (NumberFormatException ignored) {}
        }

        String payerEmail = null, payerId = null;
        try {
            Map<String, Object> payer = (Map<String, Object>) order.get("payer");
            if (payer != null) {
                payerEmail = str(payer.get("email_address"));
                payerId    = str(payer.get("payer_id"));
            }
        } catch (ClassCastException ignored) {}

        boolean statusOk = "APPROVED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status);

        boolean amountOk = true;
        if (expectedAmount != null && value != null) {
            amountOk = Math.abs(expectedAmount - value) < 0.01f;
        }
        boolean currencyOk = true;
        if (expectedCurrency != null && currency != null) {
            currencyOk = expectedCurrency.equalsIgnoreCase(currency);
        }

        boolean valid = statusOk && amountOk && currencyOk;
        String reason = valid ? null : ("Invalid: statusOk=" + statusOk + ", amountOk=" + amountOk + ", currencyOk=" + currencyOk);

        return new PaypalOrderValidation(
                paypalOrderId,
                status,
                intent,
                currency,
                value,
                payerEmail,
                payerId,
                valid,
                reason
        );
    }

    private static String str(Object o) { return o == null ? null : o.toString(); }

    private String baseUrl(PaymentSettingBag bag) { return bag.getBaseUrl(); }
}

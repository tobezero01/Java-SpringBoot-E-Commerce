package com.eshop.client.service;

import com.eshop.client.dto.paypalDTO.PaypalOrderValidation;
import com.eshop.client.exception.PaypalAPIException;
import com.eshop.client.service.interfaceS.PaypalService;
import com.eshop.client.service.interfaceS.SettingService;
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
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaypalServiceImpl implements PaypalService {
    private final RestTemplate restTemplate;
    private final SettingService settingService;

    /** Lấy Access Token từ PayPal (OAuth2 Client Credentials) */
    // server2server
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
    //để đọc trạng thái/amount/currency
    private Map<String, Object> getOrder(String accessToken, PaymentSettingBag bag, String paypalOrderId) throws PaypalAPIException{
        String url = bag.getBaseUrl() + "/v2/checkout/orders/" + paypalOrderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

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
    @Override
    public Map<String, Object> createOrder(Float amount, String currency,
                                           String returnUrl, String cancelUrl) throws PaypalAPIException {
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
                        "reference_id", "OD-PLACEHOLDER",
                        "amount", Map.of(
                                "currency_code", currency,
                                "value", String.format(Locale.US, "%.2f", amount)
                        )
                )),
                "application_context", Map.of(
                        "return_url", returnUrl,
                        "cancel_url", cancelUrl,
                        "user_action", "PAY_NOW",          // hiện nút Pay Now rõ ràng hơn
                        "brand_name", "EShop",
                        "landing_page", "LOGIN",
                        "shipping_preference", "NO_SHIPPING"
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
    @Override
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

    @Override
    public PaypalOrderValidation validateOrder(String paypalOrderId, Float expectedAmount, String expectedCurrency)
            throws PaypalAPIException {
        PaymentSettingBag bag = settingService.getPaymentSettings();
        String accessToken = getAccessToken(bag);
        Map<String, Object> order = getOrder(accessToken, bag, paypalOrderId);

        String status = str(order.get("status"));
        String intent = str(order.get("intent"));

        Map<String, Object> amount = null;
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> pus = (List<Map<String, Object>>) order.get("purchase_units");
            if (pus != null && !pus.isEmpty()) {
                Map<String, Object> pu0 = pus.get(0);
                amount = (Map<String, Object>) pu0.get("amount");
            }
        } catch (ClassCastException ignored) {}

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

        boolean statusOk   = "APPROVED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status);
        boolean amountOk   = (expectedAmount == null)   ? true : (value != null && Math.abs(expectedAmount - value) < 0.01f);
        boolean currencyOk = (expectedCurrency == null) ? true : (currency != null && expectedCurrency.equalsIgnoreCase(expectedCurrency));

        boolean valid = statusOk && amountOk && currencyOk;
        String reason = valid ? null : ("Invalid: statusOk=" + statusOk + ", amountOk=" + amountOk + ", currencyOk=" + currencyOk);

        return new PaypalOrderValidation(
                paypalOrderId, status, intent, currency, value, payerEmail, payerId, valid, reason
        );
    }

    private static String str(Object o) { return o == null ? null : o.toString(); }

    private String baseUrl(PaymentSettingBag bag) { return bag.getBaseUrl(); }


    /*
    id: mã Order trên PayPal (ví dụ 5O190127TN364715T).
    intent: cách xử lý tiền, thường CAPTURE (thu ngay) hoặc AUTHORIZE (giữ tiền, thu sau).
    status: trạng thái của order, hay gặp CREATED, APPROVED, COMPLETED.
    payment_source: thông tin nguồn thanh toán (paypal/venmo/card…), có thể chứa email, account id, địa chỉ rút gọn.
    payer: thông tin người trả tiền (tên, email_address, payer_id, address.country_code).
    purchase_units: mảng các đơn vị mua (thường một phần tử), mỗi phần tử có số tiền, shipping, payments… (xem tiếp bên dưới).
    create_time, update_time: thời điểm tạo/cập nhật (ISO-8601).
    links: các HATEOAS link (self, approve, up, refund…) để điều hướng gọi API tiếp theo.

    Bên trong purchase_units[]
        reference_id: tham chiếu do bạn đặt (tùy chọn).
        amount: tổng tiền của unit, gồm:
        currency_code (ví dụ USD),
        value (chuỗi tiền “230.00”),
        có thể có breakdown (chi tiết như item_total, shipping, tax_total, discount…).
        payee: tài khoản người bán (merchant) nhận tiền.
        shipping: địa chỉ giao (nếu bạn để PayPal thu thập), gồm name.full_name và address (address_line_1, admin_area_1/2, postal_code, country_code).
        payments: kết quả thanh toán sau khi capture/authorize, thường dùng:

        captures[]: mảng các capture, mỗi phần tử có:
            id: mã giao dịch capture (rất quan trọng để đối soát/hoàn tiền),
            status (thường COMPLETED),
            amount { currency_code, value },
            final_capture (bool),
            seller_protection, seller_receivable_breakdown (gross/paypal_fee/net),
            create_time, update_time,
            links (self, refund…).
    * */
}

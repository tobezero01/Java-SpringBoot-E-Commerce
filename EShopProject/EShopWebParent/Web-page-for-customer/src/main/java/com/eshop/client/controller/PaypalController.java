package com.eshop.client.controller;

import com.eshop.client.dto.CheckoutSummaryDTO;
import com.eshop.client.dto.paypalDTO.*;
import com.eshop.client.exception.CustomerNotFoundException;
import com.eshop.client.exception.PaypalAPIException;
import com.eshop.client.helper.ControllerHelper;
import com.eshop.client.service.CheckoutAppService;
import com.eshop.client.service.OrderEmailService;
import com.eshop.client.service.PaypalService;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/paypal")
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;
    private final ControllerHelper helper;
    private final CheckoutAppService checkoutApp;
    private final OrderEmailService orderEmailService;

    @PostMapping("/create")
    public ResponseEntity<PaypalCreateResponse> create(@RequestBody PaypalCreateRequest req) throws PaypalAPIException, CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();

        // Tính tổng từ server
        CheckoutSummaryDTO sum = checkoutApp.summarize(customer, req.addressId());

        // Tạo local order (NEW, chưa clear giỏ)
        Order local = checkoutApp.createPendingPaypalOrder(customer, req.addressId(), req.note());

        // Tạo PayPal order server-side (capture)
        Map<String, Object> created = paypalService.createOrder(sum.paymentTotal(), "USD"); // currency bạn có thể đọc từ setting
        String paypalOrderId = created.get("id").toString();

        // Lấy approve link
        String approveUrl = null;
        try {
            List<Map<String, Object>> links = (List<Map<String, Object>>) created.get("links");
            for (Map<String, Object> l : links) {
                if ("approve".equalsIgnoreCase(String.valueOf(l.get("rel")))) {
                    approveUrl = String.valueOf(l.get("href"));
                    break;
                }
            }
        } catch (ClassCastException ignored) {}

        return ResponseEntity.ok(new PaypalCreateResponse(paypalOrderId, approveUrl, local.getOrderNumber()));
    }

    @PostMapping("/process")
    public ResponseEntity<PaypalValidateResponse> process(@RequestBody PaypalValidateRequest request)
            throws PaypalAPIException, CustomerNotFoundException {

        Customer customer = helper.requireAuthenticatedCustomer();

        PaypalOrderValidation paypalOrderValidation = paypalService.validateOrder(
                request.paypalOrderId(),
                request.expectedAmount(),
                request.expectedCurrency()
        );

        PaypalValidateResponse body = new PaypalValidateResponse(
                paypalOrderValidation.valid(),
                paypalOrderValidation.reason(),
                paypalOrderValidation.paypalOrderId(),
                paypalOrderValidation.status(),
                paypalOrderValidation.intent(),
                paypalOrderValidation.currency(),
                paypalOrderValidation.amount(),
                paypalOrderValidation.payerEmail(),
                paypalOrderValidation.payerId()
        );
        return ResponseEntity.ok(body);
    }

    /**  thực hiện thu tiền và finalize đơn */
    @PostMapping("/capture")
    public ResponseEntity<PaypalCaptureResponse> capture(@RequestBody PaypalCaptureRequest req) throws PaypalAPIException, CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();

        // Capture từ PayPal
        Map<String, Object> cap = paypalService.captureOrder(req.paypalOrderId());

        // Parse kết quả cơ bản
        String status = String.valueOf(cap.get("status")); // COMPLETED ...
        String captureId = null;
        Float amount = null;
        String currency = null;
        Instant capturedAt = null;
        try {
            // capture id trong purchase_units[0].payments.captures[0]
            var pus = (List<Map<String, Object>>) cap.get("purchase_units");
            if (pus != null && !pus.isEmpty()) {
                Map<String, Object> pay = (Map<String, Object>) pus.get(0).get("payments");
                List<Map<String, Object>> captures = (List<Map<String, Object>>) pay.get("captures");
                if (captures != null && !captures.isEmpty()) {
                    Map<String, Object> c0 = captures.get(0);
                    captureId = String.valueOf(c0.get("id"));
                    Map<String, Object> amt = (Map<String, Object>) c0.get("amount");
                    if (amt != null) {
                        currency = String.valueOf(amt.get("currency_code"));
                        amount = Float.parseFloat(String.valueOf(amt.get("value")));
                    }
                    if (c0.get("create_time") != null) {
                        capturedAt = Instant.parse(String.valueOf(c0.get("create_time")));
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(new PaypalCaptureResponse(
                "COMPLETED".equalsIgnoreCase(status),
                status,
                captureId,
                req.localOrderNumber(),
                amount,
                currency,
                capturedAt
        ));
    }
}

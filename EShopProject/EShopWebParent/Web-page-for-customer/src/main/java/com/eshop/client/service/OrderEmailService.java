package com.eshop.client.service;

import com.eshop.client.setting.EmailSettingBag;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.order.Order;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class OrderEmailService {
    private final JavaMailSender mailSender;
    private final SettingService settingService;

    public void sendOrderConfirmation(Customer customer, Order order)
            throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setFrom(emailSettings.getMailFrom(), emailSettings.getSenderName());
        helper.setTo(customer.getEmail());

        String orderIdDisplay = (order.getOrderNumber() != null)
                ? order.getOrderNumber() : String.valueOf(order.getId());

        String subject = emailSettings.getOrderConfirmationSubject()
                .replace("[[orderId]]", orderIdDisplay);

        // link dùng tạm
        //String orderDetailsLink = "http://localhost:8686/EShop/api/orders/" + order.getId();

        String content = emailSettings.getOrderConfirmationContent()
                .replace("[[name]]", safe(customer.getFullName()))
                .replace("[[orderId]]", orderIdDisplay)
                .replace("[[shippingAddress]]", customer.getAddress())
                .replace("[[orderTime]]", order.getOrderTime().toString())
                .replace("[[paymentMethod]]", prettyPayment(order))
                .replace("[[total]]", String.format("%.2f", order.getTotal())
                //.replace("[[orderLink]]", orderDetailsLink)
                );


        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }
    private String prettyPayment(Order order) {
        if (order.getPaymentMethod() == null) return "";
        return switch (order.getPaymentMethod()) {
            case COD    -> "Cash on Delivery";
            case PAYPAL -> "PayPal";
            default     -> order.getPaymentMethod().name();
        };
    }
    private String safe(String s){ return s == null ? "" : s; }
}

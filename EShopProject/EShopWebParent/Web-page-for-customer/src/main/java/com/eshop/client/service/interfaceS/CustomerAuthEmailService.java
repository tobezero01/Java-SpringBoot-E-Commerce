package com.eshop.client.service.interfaceS;

import com.eshop.common.entity.Customer;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;

public interface CustomerAuthEmailService {
    void sendRegistrationVerification(HttpServletRequest req, Customer customer)
            throws MessagingException, UnsupportedEncodingException;

    void sendResetPassword(HttpServletRequest req, String email, String token)
            throws MessagingException, UnsupportedEncodingException;
}

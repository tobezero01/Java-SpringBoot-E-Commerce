package com.eshop.client.service.interfaceS;

import com.eshop.client.dto.request.ForgotPasswordRequest;
import com.eshop.client.dto.request.LoginRequest;
import com.eshop.client.dto.request.RegisterRequest;
import com.eshop.client.dto.request.ResetPasswordRequest;
import com.eshop.client.dto.response.JwtResponse;
import com.eshop.client.dto.response.MeResponse;
import com.eshop.client.dto.response.SimpleMessageResponse;
import com.eshop.client.dto.response.VerifyResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request,
                      HttpServletRequest httpRequest,
                      HttpServletResponse httpResponse);

    JwtResponse refresh(HttpServletRequest httpRequest,
                        HttpServletResponse httpResponse);

    void logout(HttpServletRequest httpRequest,
                HttpServletResponse httpResponse);

    MeResponse me();

    SimpleMessageResponse register(RegisterRequest request,
                                   HttpServletRequest httpRequest);

    VerifyResponse verify(String code);

    SimpleMessageResponse forgotPassword(ForgotPasswordRequest request,
                                         HttpServletRequest httpRequest);

    SimpleMessageResponse resetPassword(ResetPasswordRequest request);
}

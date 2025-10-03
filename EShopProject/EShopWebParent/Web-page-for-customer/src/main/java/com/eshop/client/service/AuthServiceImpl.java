package com.eshop.client.service;

import com.eshop.client.dto.request.ForgotPasswordRequest;
import com.eshop.client.dto.request.LoginRequest;
import com.eshop.client.dto.request.RegisterRequest;
import com.eshop.client.dto.request.ResetPasswordRequest;
import com.eshop.client.dto.response.JwtResponse;
import com.eshop.client.dto.response.MeResponse;
import com.eshop.client.dto.response.SimpleMessageResponse;
import com.eshop.client.dto.response.VerifyResponse;
import com.eshop.client.helper.RefreshCookie;
import com.eshop.client.repository.CustomerRepository;
import com.eshop.client.security.AuthProperties;
import com.eshop.client.security.CustomerUserDetails;
import com.eshop.client.security.CustomerUserDetailsService;
import com.eshop.client.security.jwt.JwtTokenService;
import com.eshop.client.service.interfaceS.AuthService;
import com.eshop.client.service.interfaceS.CustomerAuthEmailService;
import com.eshop.client.service.interfaceS.RefreshTokenService;
import com.eshop.common.entity.AuthenticationType;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.RefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final CustomerUserDetailsService userDetailsService;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerAuthEmailService customerAuthEmailService;
    private final RefreshTokenService refreshService;
    private final RefreshCookie refreshCookie;
    private final AuthProperties authProps;

    @Override
    public JwtResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        CustomerUserDetails user = (CustomerUserDetails) auth.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("fullName", user.getFullName() == null ? "" : user.getFullName());

        String accessToken = jwtTokenService.generateAccessToken(user, claims);

        // Issue refresh token + HttpOnly cookie
        Integer customerId = user.getCustomer().getId();
        int days = Boolean.TRUE.equals(request.rememberMe())
                ? authProps.getRefreshTtlDaysRemember()
                : authProps.getRefreshTtlDaysSession();
        int maxAgeSec = (int) Duration.ofDays(days).toSeconds();

        String ip = httpRequest.getRemoteAddr();
        String ua = httpRequest.getHeader("User-Agent");

        RefreshToken rt = refreshService.issue(customerId, days, ip, ua);
        refreshCookie.write(httpResponse, rt.getToken(), maxAgeSec, httpRequest.getContextPath());

        return new JwtResponse("Bearer ", accessToken,
                jwtTokenService.getAccessTokenTtlSeconds(), user.getFullName());
    }

    @Override
    public JwtResponse refresh(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String cookieVal = extractRefreshCookie(httpRequest);
        if (cookieVal == null || cookieVal.isBlank()) {
            throw new RuntimeException("Missing refresh cookie");
        }

        Optional<RefreshToken> opt = refreshService.findActive(cookieVal);
        if (opt.isEmpty()) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        RefreshToken refreshToken = opt.get();
        // (Optional) rotation: tạo mới + revoke cũ (hiện tạm giữ nguyên)
        CustomerUserDetails user = userDetailsService.loadUserById(refreshToken.getCustomerId());

        Map<String, Object> claims = Map.of(
                "fullName", user.getFullName() == null ? "" : user.getFullName()
        );
        String accessToken = jwtTokenService.generateAccessToken(user, claims);

        return new JwtResponse("Bearer ", accessToken,
                jwtTokenService.getAccessTokenTtlSeconds(), user.getFullName());
    }

    @Override
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String cookieVal = extractRefreshCookie(httpRequest);
        if (cookieVal != null && !cookieVal.isBlank()) {
            refreshService.revoke(cookieVal);
        }
        refreshCookie.clear(httpResponse, httpRequest.getContextPath());
    }

    @Override
    public MeResponse me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("Unauthorized");
        }
        CustomerUserDetails userDetails =
                (CustomerUserDetails) userDetailsService.loadUserByUsername(auth.getName());
        Customer customer = userDetails.getCustomer();
        return new MeResponse(customer.getId(), customer.getEmail(),
                customer.getFirstName(), customer.getLastName(),
                customer.getPhoneNumber(), customer.getAddress());
    }

    @Override
    public SimpleMessageResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        if (customerRepository.findByEmail(request.email()) != null) {
            throw new RuntimeException("Email already exists");
        }
        Customer customer = new Customer();
        customer.setEmail(request.email());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setPhoneNumber(request.phone());
        customer.setCountry(new Country(request.countryCode()));
        customer.setAddressLine1(request.addressLine1());
        customer.setAddressLine2(request.addressLine2() == null ? "" : request.addressLine2());
        customer.setCity(request.city());
        customer.setState(request.state());
        customer.setPostalCode(request.postalCode());
        customer.setPassword(passwordEncoder.encode(request.password()));
        customer.setEnabled(false);
        customer.setVerificationCode(UUID.randomUUID().toString());
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(AuthenticationType.DATABASE);
        customerRepository.save(customer);

        try {
            customerAuthEmailService.sendRegistrationVerification(httpRequest, customer);
            log.info("Verification Code : {}", customer.getVerificationCode());
        } catch (Exception e) {
            log.warn("sendRegistrationVerification error: {}", e.getMessage());
        }
        return new SimpleMessageResponse(true, "Please check your email to verify the account");
    }

    @Override
    public VerifyResponse verify(String code) {
        Customer customer = customerRepository.findByVerificationCode(code);
        if (customer == null) {
            throw new RuntimeException("Invalid code");
        }
        customer.setEnabled(true);
        customer.setVerificationCode(null);
        customerRepository.save(customer);
        return new VerifyResponse(true);
    }

    @Override
    public SimpleMessageResponse forgotPassword(ForgotPasswordRequest request, HttpServletRequest httpRequest) {
        Customer c = customerRepository.findByEmail(request.email());
        if (c != null && c.isEnabled()) {
            String token = UUID.randomUUID().toString();
            c.setResetPasswordToken(token);
            customerRepository.save(c);
            try {
                customerAuthEmailService.sendResetPassword(httpRequest, c.getEmail(), token);
                log.info("Reset Password Token : {}", token);
            } catch (Exception e) {
                log.warn("sendResetPassword error: {}", e.getMessage());
            }
        }
        return new SimpleMessageResponse(true, "If the email exists, we have sent instructions to reset password");
    }

    @Override
    public SimpleMessageResponse resetPassword(ResetPasswordRequest request) {
        Customer customer = customerRepository.findByResetPasswordToken(request.token());
        if (customer == null) {
            throw new RuntimeException("Invalid or expired token");
        }
        customer.setPassword(passwordEncoder.encode(request.newPassword()));
        customer.setResetPasswordToken(null);
        customerRepository.save(customer);
        return new SimpleMessageResponse(true, "Password has been reset");
    }

    private String extractRefreshCookie(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if (RefreshCookie.NAME.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}

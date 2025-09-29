package com.eshop.client.controller;


import com.eshop.client.dto.request.*;
import com.eshop.client.dto.response.*;
import com.eshop.client.helper.GoogleVerifier;
import com.eshop.client.repository.CustomerRepository;
import com.eshop.client.security.CustomerUserDetails;
import com.eshop.client.security.CustomerUserDetailsService;
import com.eshop.client.security.jwt.JwtTokenService;
import com.eshop.client.service.CustomerAuthEmailService;
import com.eshop.common.entity.AuthenticationType;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final CustomerUserDetailsService userDetailsService;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleVerifier googleVerifier;
    private final CustomerAuthEmailService customerAuthEmailService;

    // --- LOGIN (DATABASE) ---
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        CustomerUserDetails user = (CustomerUserDetails) auth.getPrincipal();

        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("fullName", user.getFullName() == null ? "" : user.getFullName());
        String token = jwtTokenService.generateAccessToken(user, claims);

        return ResponseEntity.ok(new JwtResponse("Bearer ", token, jwtTokenService.getAccessTokenTtlSeconds(), user.getFullName()));
    }

    // --- ME ---
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal()))
            return ResponseEntity.status(401).body(Map.of("code",401,"message","Unauthorized"));
        CustomerUserDetails userDetails = (CustomerUserDetails) userDetailsService.loadUserByUsername(auth.getName());
        Customer customer = userDetails.getCustomer();
        return ResponseEntity.ok(new MeResponse(customer.getId(), customer.getEmail(), customer.getFirstName(), customer.getLastName()
                , customer.getPhoneNumber(), customer.getAddress()));
    }


//    @PostMapping("/google")
//    public ResponseEntity<JwtResponse> loginGoogle(@RequestBody @Valid GoogleSignInRequest request) throws Exception {
//        GoogleIdToken.Payload payload = googleVerifier.verify(request.idToken());
//        String email = payload.getEmail();
//        Customer customer = customerRepository.findByEmail(email);
//        if (customer == null){
//            Customer ncustomer = new Customer();
//            ncustomer.setEmail(email);
//            ncustomer.setFirstName((String) payload.get("given_name"));
//            ncustomer.setLastName((String) payload.get("family_name"));
//            ncustomer.setAuthenticationType(AuthenticationType.GOOGLE);
//            ncustomer.setCreatedTime(new Date());
//            ncustomer.setAddressLine1("Vietnam");
//            ncustomer.setAddressLine2("Vietnam");
//            ncustomer.setEnabled(true);
//            customer = customerRepository.save(ncustomer);
//        }
//        CustomerUserDetails principal = new CustomerUserDetails(customer);
//
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("fullName", principal.getFullName() == null ? "" : principal.getFullName());
//        String token = jwtTokenService.generateAccessToken(principal, claims);
//        return ResponseEntity.ok(new JwtResponse("Bearer ", token, jwtTokenService.getAccessTokenTtlSeconds(), principal.getFullName()));
//    }

    // --- REGISTER ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request, HttpServletRequest httpRequest ) {
        if (customerRepository.findByEmail(request.email()) != null) {
            return ResponseEntity.status(409).body(Map.of("code",409,"message","Email already exists"));
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
        customer.setEnabled(false); // đợi verify
        customer.setVerificationCode(UUID.randomUUID().toString());
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(AuthenticationType.DATABASE);
        customerRepository.save(customer);

        try {
            customerAuthEmailService.sendRegistrationVerification(httpRequest, customer);
            log.info("Verification Code : {}", customer.getVerificationCode());
        } catch (Exception e) {

        }
        return ResponseEntity.ok(new SimpleMessageResponse(true, "Please check your email to verify the account"));
    }

    // --- VERIFY EMAIL ---
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam("code") String code) {
        Optional<Customer> option = Optional.ofNullable(customerRepository.findByVerificationCode(code));
        if (option.isEmpty()) return ResponseEntity.badRequest().body(Map.of("code",400,"message","Invalid code"));
        Customer customer = option.get();
        customer.setEnabled(true);
        customer.setVerificationCode(null);
        customerRepository.save(customer);
        return ResponseEntity.ok(new VerifyResponse(true));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request,
                                            HttpServletRequest httpReq) {
        Customer option = customerRepository.findByEmail(request.email());
        if (option != null && option.isEnabled()) {
            String token = UUID.randomUUID().toString();
            option.setResetPasswordToken(token);
            customerRepository.save(option);

            try {
                customerAuthEmailService.sendResetPassword(httpReq, option.getEmail(), token);
                log.info("Reset Password Token : {}", token);
            } catch (Exception e) {

            }
        }
        return ResponseEntity.ok(new SimpleMessageResponse(true, "If the email exists, we have sent instructions to reset password"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        Customer option = customerRepository.findByResetPasswordToken(request.token());
        if (option == null) return ResponseEntity.badRequest().body(Map.of("code",400,"message","Invalid or expired token"));
        Customer customer = option;
        customer.setPassword(passwordEncoder.encode(request.newPassword()));
        customer.setResetPasswordToken(null);
        customerRepository.save(customer);
        return ResponseEntity.ok(new SimpleMessageResponse(true, "Password has been reset"));
    }
}

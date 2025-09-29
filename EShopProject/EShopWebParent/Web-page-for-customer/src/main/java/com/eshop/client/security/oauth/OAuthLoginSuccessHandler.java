package com.eshop.client.security.oauth;

import com.eshop.client.service.CustomerService;
import com.eshop.common.entity.AuthenticationType;
import com.eshop.common.entity.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final @Lazy CustomerService customerService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        Object oAuth2User =  authentication.getPrincipal();
        String email = null;
        String name  = null;
        if (oAuth2User instanceof OidcUser oidc) {
            // OIDC flow (có scope openid) → vào nhánh này
            email = oidc.getEmail();                  // hoặc oidc.getAttribute("email")
            name  = oidc.getName();               // hoặc oidc.getAttribute("name")
        } else if (oAuth2User instanceof OAuth2User o2) {
            // OAuth2 thường (không openid) → nhánh này
            email = o2.getAttribute("email");
            name  = o2.getAttribute("name");
        } else {
            log.warn("Unknown principal type: {}", oAuth2User.getClass());
        }

        if (email == null) {
            response.sendRedirect("/login?error"); // hoặc JSON tuỳ bạn
            return;
        }
        String countryCode = request.getLocale().getCountry();

        Customer customer = customerService.getCustomerByEmail(email);
        if (customer == null) {
            customerService.addCustomerUponOAuthLogin(name , email,countryCode);
            customer = customerService.getCustomerByEmail(email);
        }else {
            customerService.updateAuthenticationType(customer, AuthenticationType.GOOGLE);
        }

        //super.onAuthenticationSuccess(request, response, authentication);
        response.sendRedirect("");
    }
}

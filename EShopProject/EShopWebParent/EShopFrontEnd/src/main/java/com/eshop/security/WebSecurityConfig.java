package com.eshop.security;

import com.eshop.security.oauth.CustomAuthorizationRequestResolver;
import com.eshop.security.oauth.CustomerOAuth2UserService;
import com.eshop.security.oauth.DatabaseLoginSuccessHandler;
import com.eshop.security.oauth.OAuthLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomerOAuth2UserService auth2UserService;

    @Autowired private OAuthLoginSuccessHandler authLoginSuccessHandler;

    @Autowired private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        // Create the default OAuth2AuthorizationRequestResolver
        OAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        // Create the custom resolver with the prompt parameter
        OAuth2AuthorizationRequestResolver customResolver = new CustomAuthorizationRequestResolver(defaultResolver);
        httpSecurity.authorizeHttpRequests(authRequests -> authRequests
                        .requestMatchers("/account_details" , "/update_account_details",
                                "/cart" , "/address_book/**", "/checkout" , "/place_order").authenticated()
                        .anyRequest().permitAll()
                )
                .csrf(cs -> cs.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(customResolver)
                        )
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(auth2UserService) // Custom OAuth2UserService
                        ).successHandler(authLoginSuccessHandler)
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(databaseLoginSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
                        .tokenValiditySeconds(14 * 24 * 60 * 60) // 14 days
                )
                .sessionManagement(se -> se
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                );

        return httpSecurity.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailsService();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(PasswordEncoderConfig.passwordEncoder());

        return authenticationProvider;
    }



}

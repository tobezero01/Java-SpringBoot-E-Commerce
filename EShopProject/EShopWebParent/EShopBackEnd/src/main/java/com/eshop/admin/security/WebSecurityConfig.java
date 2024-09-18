package com.eshop.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/webjars/**", "/css/**", "/js/**", "/images/**");
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new EShopUserDetailsService();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(
                        configuration -> configuration
                                .requestMatchers("/users/**").hasAuthority("Admin")
                                .requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
                                .requestMatchers("/products/new" , "/products/delete/**").hasAnyAuthority("Admin", "Editor")
                                .requestMatchers("/products/edit/**", "/products/save", "/products/check_unique").hasAnyAuthority("Admin", "Editor", "Salesperson")
                                .requestMatchers("/products", "/products/" , "/products/detail/**", "/products/page/**").hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
                                .requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
                                .anyRequest().authenticated()
                )
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .usernameParameter("email")
                                .permitAll()
                )
                .logout(logout -> logout
                        .permitAll()
                )
                .rememberMe(
                        rem -> rem
                                .key("AbcDefgHijKnmlOqprs_1234567890")
                                .tokenValiditySeconds(7 * 24 * 60 * 60)
                );


        return httpSecurity.build();
    }


}

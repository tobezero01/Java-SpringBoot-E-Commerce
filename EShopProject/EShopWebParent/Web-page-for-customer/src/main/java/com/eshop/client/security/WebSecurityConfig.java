package com.eshop.client.security;

import com.eshop.client.exception.JsonAccessDeniedHandler;
import com.eshop.client.exception.JsonAuthEntryPoint;
import com.eshop.client.helper.Utility;
import com.eshop.client.security.jwt.JwtAuthenticationFilter;
import com.eshop.client.security.jwt.JwtTokenService;
import com.eshop.client.security.oauth.CustomerOAuth2User;
import com.eshop.client.security.oauth.CustomerOAuth2UserService;
import com.eshop.client.security.oauth.OAuthLoginSuccessHandler;
import com.eshop.client.service.SettingService;
import com.eshop.client.setting.EmailSettingBag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final JsonAuthEntryPoint authEntryPoint;
    private final JsonAccessDeniedHandler accessDeniedHandler;
    private final SettingService settingService;
    private final CustomerOAuth2UserService customerOAuth2UserService;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        EmailSettingBag bag = settingService.getEmailSettings();
        return Utility.prepareMailSender(bag);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:4200")); // TODO: thêm domain FE thực tế
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Content-Type","Authorization","X-Requested-With"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtTokenService jwtTokenService) throws Exception {
        httpSecurity
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/home/**", "/api/categories/**",
                                "/api/products/**").permitAll()
                        .requestMatchers("/api/auth/**", "/oauth2/**","/login/oauth2/**").permitAll()
                        .requestMatchers(
                                "/api/cart/**", "/api/checkout/**", "/api/orders/**",
                                "/api/addresses/**", "/api/reviews/**", "/api/payments/**", "/api/geo/**"
                        ).authenticated()
                        .anyRequest().authenticated()

                )
                .oauth2Login(oauth -> oauth
                                // các baseUri này là mặc định, ghi rõ cho dễ đọc
                                .authorizationEndpoint(a -> a.baseUri("/oauth2/authorization"))
                                .redirectionEndpoint(r -> r.baseUri("/login/oauth2/code/*"))
                        // nếu bạn có CustomerOAuth2UserService / successHandler:
                        .userInfoEndpoint(u -> u.userService(customerOAuth2UserService))
                        .successHandler(oAuthLoginSuccessHandler)
                        .failureUrl("/login?error")
                )
                .exceptionHandling(e -> e .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        ;

        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
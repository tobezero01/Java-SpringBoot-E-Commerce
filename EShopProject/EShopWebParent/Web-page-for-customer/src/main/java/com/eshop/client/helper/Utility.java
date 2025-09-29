package com.eshop.client.helper;


import com.eshop.client.security.oauth.CustomerOAuth2User;
import com.eshop.client.setting.CurrencySettingBag;
import com.eshop.client.setting.EmailSettingBag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

public class Utility {
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();

        return siteURL.replace(request.getServletPath(), "");
    }

    public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settingBag) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(settingBag.getHost());
        mailSender.setPort(settingBag.getPort());
        mailSender.setUsername(settingBag.getUsername());
        mailSender.setPassword(settingBag.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth" , settingBag.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable" , settingBag.getSmtpSecured());
        mailProperties.setProperty("mail.smtp.ssl.trust", "*");
        mailProperties.setProperty("mail.smtp.ssl.checkserveridentity", "false");

        mailSender.setJavaMailProperties(mailProperties);
        return mailSender;
    }


}

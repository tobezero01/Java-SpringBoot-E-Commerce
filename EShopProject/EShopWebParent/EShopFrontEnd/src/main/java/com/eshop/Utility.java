package com.eshop;

import com.eshop.setting.EmailSettingBag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

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

package com.eshop.customer;

import com.eshop.Utility;
import com.eshop.common.entity.Customer;
import com.eshop.exception.CustomerNotFoundException;
import com.eshop.setting.EmailSettingBag;
import com.eshop.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {
    @Autowired private CustomerService customerService;

    @Autowired private SettingService settingService;

    @GetMapping("/forgot_password")
    public String showRequestForm() {

        return "customers/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processRequestForm(HttpServletRequest request, Model model)  {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToTokenEmail(email);
            String link = Utility.getSiteURL(request) + "/reset_password=" + token;
            sendEmail(link, email);

            model.addAttribute("message" , "We have sent a reset password to link email" + " Please Check");
        } catch (CustomerNotFoundException exception) {
            model.addAttribute("error" , exception.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error" , "Could not send email");
        }

        return "customers/forgot_password_form";
    }

    private void sendEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = email;
        String subject = "Here's the link to reset password";
        String context = "<p>Hello, </p>" +
                "<p>Click the link below to change your password</p>" +
                "<p><a href = \"" + link + "\">Change password</a></p>" +
                "Thanks";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setFrom(emailSettings.getMailFrom(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(context, true);
        mailSender.send(mimeMessage);
    }

    @GetMapping("/reset_password")
    public String showResetForm(@Param("token") String token , Model model) {
        Customer customer = customerService.getByResetPasswordToken(token);
        if (customer != null) {
            model.addAttribute("token", token);

        } else {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", "Invalid Token");
            return "message";
        }
        return "customers/reset_password_form";

    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request , Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        try {
            customerService.updatePasswordToken(token, password);
            model.addAttribute("message", "You have successfully changed your password");
            model.addAttribute("title", "Reset successfully");
            model.addAttribute("pageTitle", "Reset Password");
            return "message";
        }  catch (CustomerNotFoundException e) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", e.getMessage());
            return "message";
        }
    }
}

package com.eshop.customer;

import com.eshop.Utility;

import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.security.CustomerUserDetails;
import com.eshop.security.oauth.CustomerOAuth2User;
import com.eshop.setting.EmailSettingBag;
import com.eshop.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

@Controller
public class CustomerController {

    @Autowired CustomerService customerService;

    @Autowired
    SettingService settingService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> listCountries = customerService.listAllCountries();
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());

        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer, Model model, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        customerService.registerCustomer(customer);
        sendVerificationEmail(request, customer);
        model.addAttribute("pageTitle", "Registration Succeeded");

        return "register/register_success";
    }

    public void sendVerificationEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress  = customer.getEmail();
        String subject = emailSettings.getCustomerVerifySubject();
        String content = emailSettings.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);


        helper.setFrom(emailSettings.getMailFrom() , emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]" , customer.getFullName());
        String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
        content = content.replace("[[URL]]" , verifyURL);

        helper.setText(content, true);
        mailSender.send(message);

        System.out.println("To address : " + toAddress);
        System.out.println("Verify URL : " + verifyURL);
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code , Model model ){
        boolean verified = customerService.verify(code);
        return "register/" + (verified ? "verify_success" : "verify_fail");
    }

    @GetMapping("/account_details")
    public String viewAccountDetails(Model model, HttpServletRequest request) {
        String email = Utility.getMailOfAuthenticatedCustomer(request);
        Customer customer = customerService.getCustomerByEmail(email);
        List<Country> listCountries = customerService.listAllCountries();
        model.addAttribute("customer", customer);
        model.addAttribute("listCountries", listCountries);

        return "customers/account_form";
    }


    @PostMapping("/update_account_details")
    public String updateAccountDetails(HttpServletRequest request,Model model, Customer customer, RedirectAttributes redirectAttributes) {
        customerService.update(customer);
        redirectAttributes.addFlashAttribute("message", "Your Account has been updated successfully!");
        updateNameForAuthenticationCustomer(customer,request);

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/account_details";

        if ("address_book".equals(redirectOption)) {
            redirectURL = "redirect:/address_book";
        } else if ("cart".equals(redirectOption)){
            redirectURL = "redirect:/cart";
        }
        return redirectURL;
    }

    private void updateNameForAuthenticationCustomer(Customer customer ,HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        String fullName = customer.getFirstName() + " " + customer.getLastName();

        if (principal instanceof UsernamePasswordAuthenticationToken
                || principal instanceof RememberMeAuthenticationToken) {
            CustomerUserDetails userDetails = getCustomerDetailsObj(principal);
            Customer authenticationCustomer = userDetails.getCustomer();
            authenticationCustomer.setFirstName(customer.getFirstName());
            authenticationCustomer.setLastName(customer.getLastName());
        } else if (principal instanceof OAuth2AuthenticationToken){
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oAuth2AuthenticationToken.getPrincipal();
            oAuth2User.setFullName(fullName);
        }
    }

    private CustomerUserDetails getCustomerDetailsObj(Object principal) {
        CustomerUserDetails userDetails = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        } else if (principal instanceof RememberMeAuthenticationToken) {
            RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        }
        return userDetails;
    }
}

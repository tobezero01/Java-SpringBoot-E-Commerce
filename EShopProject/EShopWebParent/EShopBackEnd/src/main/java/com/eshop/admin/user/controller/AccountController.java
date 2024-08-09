package com.eshop.admin.user.controller;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.security.EShopUserDetails;
import com.eshop.admin.user.UserService;
import com.eshop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal EShopUserDetails eShopUserDetails, Model model) {

        String email = eShopUserDetails.getUsername();
        User user = userService.getByEmail(email);

        model.addAttribute("user", user);

        return "users/account_form";
    }


    @PostMapping("/account/update")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User saveUser = userService.updateAccount(user);
            String uploadDir = "user-photos/" + saveUser.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            userService.updateAccount(user);
        }
        redirectAttributes.addFlashAttribute("message", "Your Account update successfully!");

        return "redirect:/account";
    }
}

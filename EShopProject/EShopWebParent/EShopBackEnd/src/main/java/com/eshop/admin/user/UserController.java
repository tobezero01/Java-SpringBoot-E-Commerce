package com.eshop.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eshop.common.entity.User;



@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public String listAll(Model model) {
		List<User> users = userService.listALl();
		model.addAttribute("listUsers",users);
		return "users";
	}
	
	
	
}

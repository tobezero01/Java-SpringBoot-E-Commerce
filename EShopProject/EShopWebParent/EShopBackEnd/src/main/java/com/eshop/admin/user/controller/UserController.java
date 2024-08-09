package com.eshop.admin.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.user.UserNotFoundException;
import com.eshop.admin.user.UserService;
import com.eshop.admin.user.export.UserCsvExporter;
import com.eshop.admin.user.export.UserExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.common.entity.Role;
import com.eshop.common.entity.User;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public String listAll(Model model) {
		return listByPage(1, model, "firstName", "asc","");
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
							 @Param("sortField" ) String sortField, @Param("sortDir") String sortDir,
							 @Param("keyWord") String keyWord
							 ) {
		Page<User> page = userService.listByPage(pageNum, sortField,sortDir, keyWord);
		List<User> listUsers = page.getContent();
		long startCount = (pageNum-1)*UserService.USER_PER_PAGE + 1;
		long endCount = startCount + UserService.USER_PER_PAGE -1;

		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("currentPage",pageNum);
		model.addAttribute("startCount",startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyWord", keyWord);
		return "users/users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRoles = userService.listRoles();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create mew user");
		return "users/user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
						   @RequestParam("image") MultipartFile multipartFile) throws IOException {

		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User saveUser = userService.save(user);
			String uploadDir = "user-photos/" + saveUser.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully!");

		String firstPathEmailSave = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyWord=" + firstPathEmailSave;
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try{
			Optional<User> user = userService.getUserById(id);
			List<Role> listRoles = userService.listRoles();
			model.addAttribute("user", user);
			model.addAttribute("listRoles", listRoles);
			model.addAttribute("pageTitle", "Update User");
			model.addAttribute("id", id);
			return "users/user_form";
		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/users";
		}
	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try{
			userService.delete(id);
			model.addAttribute("pageTitle", "Delete User");
			model.addAttribute("id", id);
			redirectAttributes.addFlashAttribute("message", "The user ID : " + id + " has been delete successfully!");
		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/users";
	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id,
										  @PathVariable("status") boolean enabled,
										  RedirectAttributes redirectAttributes) {
		userService.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "enable" : "disable";
		String message = "The user Id : " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/users";
	}
	
	
	@GetMapping("/users/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		List<User> list = userService.listALl();
		UserCsvExporter userCsvExporter = new UserCsvExporter();
		userCsvExporter.export(list,response);
	}
	
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> list = userService.listALl();

		UserExcelExporter excelExporter = new UserExcelExporter();
		excelExporter.export(list, response);
	}
}

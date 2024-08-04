package com.eshop.admin.user;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Role;
import com.eshop.common.entity.User;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<User> listALl() {
		return (List<User>) userRepository.findAll();
	}
	
	public List<Role> listRoles() {
		return (List<Role>) roleRepository.findAll();
	}
	
	public void save(User user) {
		encodePass(user);
		userRepository.save(user);
	}
	
	private void encodePass(User user) {
		String encodePass = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePass);
	}
	
	public boolean isEmailUnipue(String email) {
		User user = userRepository.getUserByEmail(email);
		return user == null;
	}
	
}

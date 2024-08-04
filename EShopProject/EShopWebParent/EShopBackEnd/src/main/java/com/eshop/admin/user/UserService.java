package com.eshop.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.User;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;
	
	public List<User> listALl() {
		return (List<User>) repository.findAll();
	}
	
}

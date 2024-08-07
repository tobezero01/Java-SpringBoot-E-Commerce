package com.eshop.admin.user;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Role;
import com.eshop.common.entity.User;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

	public static final int USER_PER_PAGE = 5;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<User> listALl() {
		return (List<User>) userRepository.findAll(Sort.by("id").ascending());
	}

	public Page<User> listByPage(int pageNum , String sortField, String sortDir,  String keyWord) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum-1, USER_PER_PAGE, sort);

		if(keyWord != null) {
			return userRepository.findAll(keyWord , pageable);
		}
		return userRepository.findAll(pageable);
	}
	
	public List<Role> listRoles() {
		return (List<Role>) roleRepository.findAll();
	}
	
	public User save(User user) {
		boolean existingUser = user.getId()!=null;

		if(existingUser) {
			Optional<User> user1 = userRepository.findById(user.getId());

			if(user.getPassword().isEmpty()) {
				user.setPassword(user1.get().getPassword());
			} else {
				encodePass(user);
			}
		} else {
			encodePass(user);
		}
		 return userRepository.save(user);

	}
	
	private void encodePass(User user) {
		String encodePass = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePass);
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		User user = userRepository.getUserByEmail(email);
		if (user == null) return true;
		boolean isCreatingNew = (id == null);
		if(isCreatingNew) {
			if(user != null) return false;
		}else {
			if(user.getId() != id) {
				return false;
			}
		}
		return true;
	}

	public Optional<User> getUserById(Integer id) throws UserNotFoundException {
		try{
			return userRepository.findById(id);
		}catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user by id = " + id);
		}
	}

	public void delete(Integer id) throws UserNotFoundException {
		Long count = userRepository.countById(id);

		if(count==0 || count == null) {
			throw new UserNotFoundException("Could not find any user by id = " + id);
		}
		userRepository.deleteById(id);
	}

	public void updateUserEnabledStatus(Integer id , boolean enabled) {
		userRepository.updateEnableStatus(id, enabled);
	}
}

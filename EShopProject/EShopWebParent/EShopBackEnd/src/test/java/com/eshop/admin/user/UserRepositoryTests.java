package com.eshop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.useRepresentation;

import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.eshop.common.entity.Role;
import com.eshop.common.entity.User;

import java.util.List;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userDuc = new User("ducnhuad@gmail.com", "ducnhu1234","Nhu", "Duc");
		userDuc.addRole(roleAdmin);
		
		User saceUser = repository.save(userDuc);
		assertThat(saceUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewUserWithTwoRole() {
		User userDuc1 = new User("ducnhuad03@gmail.com", "ducnhu1234", "Nhu", "Tri");
		
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		
		userDuc1.addRole(roleAssistant);
		userDuc1.addRole(roleEditor);
		
		User saveUser = repository.save(userDuc1);
		assertThat(saveUser.getId()).isGreaterThan(0);
	}
		
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listIterableUser = repository.findAll() ;
		
		listIterableUser.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void testUserById() {
		User user = repository.findById(1).get();
		System.out.println(user);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User user = repository.findById(1).get();
		user.setEnabled(true);
		user.setEmail("ducnhuad@gmail.com");
		repository.save(user);
		System.out.println(user);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testUpdateUserRoles() {
		User user= repository.findById(2).get(); 
		Role roleEditor = new Role(3);
		Role role = new Role(2);
		
		user.getRoles().remove(roleEditor);
		//user.addRole(role);
		repository.save(user);
	}
	
	@Test
	public void testDeleteUserbyId() {
		Integer idInteger = 2;
		
		repository.deleteById(idInteger);
	}
		
	
	@Test
	public void testGetUserByEmail() {
		String email = "ducnhuad@gmail.com";
		User user = repository.getUserByEmail(email);
		System.out.println(user);
		assertThat(user).isNotNull();
	}

	@Test
	public void testCountById() {
		Integer id =1;
		Long count = repository.countById(id);
		assertThat(count).isNotNull().isGreaterThan(0);
	}

	@Test
	public void testDisableUser() {
		Integer id = 3;
		repository.updateEnableStatus(id, false);
	}

	@Test
	public void testEnableUser() {
		Integer id = 3;
		repository.updateEnableStatus(id, true);
	}

	@Test
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;

		Pageable pageable = PageRequest.of(pageNumber,pageSize);

		Page<User> page = repository.findAll(pageable);

		List<User> userList = page.getContent();

		userList.forEach(user -> {
			System.out.println(user);
		});

		assertThat(userList.size()).isEqualTo(pageSize);
	}
}

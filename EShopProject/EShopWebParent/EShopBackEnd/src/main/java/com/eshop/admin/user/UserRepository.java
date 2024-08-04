package com.eshop.admin.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eshop.common.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{

	@Query("Select u from User u Where u.email =:email ")
	public User getUserByEmail(@Param("email") String email);
	
}

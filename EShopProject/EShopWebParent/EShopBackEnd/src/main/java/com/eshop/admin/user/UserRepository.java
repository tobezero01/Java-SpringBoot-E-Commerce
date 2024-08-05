package com.eshop.admin.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eshop.common.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{

	@Query("Select u from User u Where u.email =:email ")
	public User getUserByEmail(@Param("email") String email);

	public Long countById(Integer id);

	@Query("Update User u Set u.enabled = ?2 where u.id = ?1 ")
	@Modifying
	public void updateEnableStatus(Integer id , boolean enabled);
	
}

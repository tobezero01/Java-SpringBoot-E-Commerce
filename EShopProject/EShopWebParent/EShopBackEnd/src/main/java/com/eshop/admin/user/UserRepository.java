package com.eshop.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eshop.common.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	@Query("Select u from User u WHERE u.email =:email ")
	public User getUserByEmail(@Param("email") String email);

	public Long countById(Integer id);

	@Query("Update User u Set u.enabled = ?2 WHERE u.id = ?1 ")
	@Modifying
	public void updateEnableStatus(Integer id , boolean enabled);


	@Query("Select u from User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ' , u.lastName ) LIKE %?1% ")
	public Page<User> findAll(String keyWord, Pageable pageable);
	
}

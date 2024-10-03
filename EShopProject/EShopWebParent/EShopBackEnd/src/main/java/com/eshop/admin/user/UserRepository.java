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
	@Query("SELECT u FROM User u WHERE u.email = :email")
	User getUserByEmail(@Param("email") String email);

	Long countById(Integer id);

	@Modifying
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	void updateEnableStatus(Integer id, boolean enabled);

	@Query("SELECT u FROM User u WHERE " +
			"LOWER(CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', ?1, '%'))")
	Page<User> findAll(String keyWord, Pageable pageable);
}

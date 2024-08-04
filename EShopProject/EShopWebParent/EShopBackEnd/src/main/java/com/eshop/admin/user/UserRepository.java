package com.eshop.admin.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.eshop.common.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{

}

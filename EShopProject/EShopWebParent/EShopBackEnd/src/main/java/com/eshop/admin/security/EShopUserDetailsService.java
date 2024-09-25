package com.eshop.admin.security;

import com.eshop.admin.user.UserRepository;
import com.eshop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class EShopUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);

        if(user != null) {
            return new EShopUserDetails(user);
        }
        throw new UsernameNotFoundException("Could not find the user with email : " + email);
    }
}

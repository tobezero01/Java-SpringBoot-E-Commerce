package com.eshop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
	
	@Test
	public void testEncoderPassword() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPass = "ducnhu1234";
		String encode = passwordEncoder.encode(rawPass);
		System.out.println(encode);
		
		boolean matches = passwordEncoder.matches(rawPass, encode);
		assertThat(matches).isTrue();
	}

}

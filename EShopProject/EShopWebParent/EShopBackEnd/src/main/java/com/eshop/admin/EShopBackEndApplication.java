package com.eshop.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.eshop.common.entity" , "com.eshop.admin.user"})
public class EShopBackEndApplication {
	public static void main(String[] args) {
		SpringApplication.run(EShopBackEndApplication.class, args);
	}

}

package com.eshop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.eshop.common.entity" , "com.eshop.category","com.eshop.customer"
		,"com.eshop.setting" ,"com.eshop.product"})
public class EShopFrontEndApplication {

	public static void main(String[] args) {

		SpringApplication.run(EShopFrontEndApplication.class, args);
	}
	
}

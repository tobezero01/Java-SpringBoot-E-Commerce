package com.eshop.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.eshop.common.entity" , "com.eshop.client.category","com.eshop.client.customer"
		,"com.eshop.client.setting" ,"com.eshop.client.product"})
public class WebPageForCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebPageForCustomerApplication.class, args);
	}

}

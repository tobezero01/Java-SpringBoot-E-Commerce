package com.eshop.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EntityScan({"com.eshop.common.entity"
		, "com.eshop.client.category","com.eshop.client.customer"
		,"com.eshop.client.setting" ,"com.eshop.client.product"
})
@ConfigurationPropertiesScan
public class WebPageForCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebPageForCustomerApplication.class, args);
	}

}

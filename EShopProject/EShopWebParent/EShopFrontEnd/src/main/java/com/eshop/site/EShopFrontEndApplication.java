package com.eshop.site;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EShopFrontEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(EShopFrontEndApplication.class, args);
	}
	
	public CommandLineRunner commandLineRunner() {
		return run ->  {
			System.out.println("Process running ...");
		};
	}

	
}

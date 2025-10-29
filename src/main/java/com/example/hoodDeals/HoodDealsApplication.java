package com.example.hoodDeals;
import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HoodDealsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(HoodDealsApplication.class, args);
	}
	@Override
	public void run(String... args)  {
		System.out.println("✅ P03Backend server is running and ready!");

		
	}
}

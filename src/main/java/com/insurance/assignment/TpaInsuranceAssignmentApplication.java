package com.insurance.assignment;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class TpaInsuranceAssignmentApplication {

	public static void main(String[] args) {
		System.setProperty("spring.profiles.name", "application");
		System.setProperty("spring.config.location", "file:./conf/application.yaml");
		SpringApplication.run(TpaInsuranceAssignmentApplication.class, args);
	}

}

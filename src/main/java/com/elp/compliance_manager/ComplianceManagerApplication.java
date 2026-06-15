package com.elp.compliance_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ComplianceManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComplianceManagerApplication.class, args);
	}

}

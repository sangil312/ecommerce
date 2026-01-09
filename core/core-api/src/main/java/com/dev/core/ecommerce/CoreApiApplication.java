package com.dev.core.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = "com.dev")
public class CoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreApiApplication.class, args);
	}

}

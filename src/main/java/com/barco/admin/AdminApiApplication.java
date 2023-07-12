package com.barco.admin;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author Nabeel Ahmed
 */
@SpringBootApplication
@ComponentScan(basePackages = { "com.barco.*" })
public class AdminApiApplication {

	public Logger logger = LogManager.getLogger(AdminApiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AdminApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return (args) -> {
		};
	}

	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
	}

}
package com.barco.admin;

import com.barco.admin.controller.StorageDetailRestController;
import com.barco.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import javax.annotation.PostConstruct;
import java.util.TimeZone;



@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = { "com.barco.*" })
public class AdminApiApplication {

	@Autowired
	private StorageDetailRestController storageDetailRestController;

	public static void main(String[] args) {
		SpringApplication.run(AdminApiApplication.class, args);
	}

	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return (args) -> {
			storageDetailRestController.statusChange(1008L, 1018L, Status.Delete);
		};
	}

}

package com.barco.admin;

import com.barco.admin.service.impl.JobQueueServiceImpl;
import com.barco.model.repository.JobQueueRepository;
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
	private JobQueueServiceImpl jobQueueService;

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
			jobQueueService.addJobToQueue(1001L,1018L,null);
			jobQueueService.addJobToQueue(1002L,1018L,null);
			jobQueueService.addJobToQueue(1003L,1018L,null);
			jobQueueService.addJobToQueue(1004L,1018L,null);


		};
	}

}

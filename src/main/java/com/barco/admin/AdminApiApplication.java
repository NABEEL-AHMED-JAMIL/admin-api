package com.barco.admin;

import com.barco.admin.controller.JobRestController;
import com.barco.model.dto.JobDto;
import com.barco.model.dto.TaskDto;
import com.barco.model.enums.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import javax.annotation.PostConstruct;
import java.util.TimeZone;
import java.util.UUID;


@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = { "com.barco.*" })
public class AdminApiApplication {

	@Autowired
	private JobRestController jobRestController;

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
//			for(int i=0; i<10; i++) {
//				JobDto jobDto = new JobDto();
//				jobDto.setJobName("PK-"+UUID.randomUUID());
//				jobDto.setDescription("PK-"+UUID.randomUUID());
//				jobDto.setExecutionType(Execution.Manual);
//				jobDto.setCreatedBy(1018L);
//				// task-dto
//				TaskDto taskDto = new TaskDto();
//				taskDto.setId(1208L);
//				jobDto.setTask(taskDto);
//				// notification
//				jobDto.setNotification("nabeel.amd93@gmail.com");
//				System.out.println(jobDto);
//				this.jobRestController.createJob(jobDto);
//			}1001,1018
			System.out.println(jobRestController.getJobById(1010L, 1018L));
		};
	}

}

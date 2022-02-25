package com.barco.admin;

import com.barco.admin.service.IAccessServiceService;
import com.barco.admin.service.IAppSettingService;
import com.barco.model.dto.AccessServiceDto;
import com.barco.model.dto.AppSettingDto;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.enums.Status;
import com.barco.model.util.PagingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private IAccessServiceService accessServiceService;
	@Autowired
	private IAppSettingService appSettingService;

	public static void main(String[] args) {
		SpringApplication.run(AdminApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return (args) -> {
			//this.findAllAppSettingByAppUserIdInPagination();
		};
	}

	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
	}

	public void createAccessService() {
		AccessServiceDto accessServiceDto = new AccessServiceDto();
		accessServiceDto.setCreatedBy(0L);
		accessServiceDto.setStatus(Status.Active);
		accessServiceDto.setServiceName("Email Bulk");
		accessServiceDto.setDescription("Email Bulk use for reporting and notification.");
		this.accessServiceService.createAccessService(accessServiceDto);
	}

	public void statusChangeAccessService() {
		this.accessServiceService.statusChange(1001L, 0L, Status.Active);
	}

	public void getAllAccessService() {
		logger.info("Result " + this.accessServiceService.getAllAccessService());
	}

	public void createAppSetting() {
		AppSettingDto appSettingDto = new AppSettingDto("random", "random", "random",
				0L, Status.Active);
		this.appSettingService.createAppSetting(appSettingDto);
	}

	public void statusChangeAppSetting() {
		this.appSettingService.statusChange(1002L, 0L, Status.Inactive);
	}

	public void findAllAppSettingByAppUserIdInPagination() {
		SearchTextDto searchTextDto = new SearchTextDto();
		searchTextDto.setItemName("app_setting_id");
		searchTextDto.setItemValue("1000");
		this.appSettingService.findAllAppSettingByAppUserIdInPagination(0L, "2021-12-30", "2022-01-07",
				PagingUtil.ApplyPaging("app_setting_id", "asc", 1L, 15L), null);
	}

}
package com.barco.admin.controller;

import com.barco.admin.service.IAppSettingService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.AppSettingDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.util.PagingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/appSetting.json", produces = MediaType.APPLICATION_JSON_VALUE)
public class AppSettingRestApi {

    public Logger logger = LogManager.getLogger(AppSettingRestApi.class);

    @Autowired
    private IAppSettingService appSettingService;

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @RequestMapping(value = "/createAppSetting", method = RequestMethod.POST)
    public ResponseDTO createAppSetting(@RequestBody AppSettingDto appSettingDto) {
        ResponseDTO response = null;
        try {
            logger.info("Request for createAppSetting " + appSettingDto);
            response = this.appSettingService.createAppSetting(appSettingDto);
        } catch (Exception ex) {
            logger.info("Error during createAppSetting " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    public ResponseDTO statusChange(@RequestParam(name = "appSettingId") Long appSettingId,
        @RequestParam(name = "appUserId") Long appUserId, @RequestParam(name = "status") Status status) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for statusChange AppSetting Id %d And Status %s ", appSettingId, status));
            response = this.appSettingService.statusChange(appSettingId, appUserId, status);
        } catch (Exception ex) {
            logger.info("Error during statusChange " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    @RequestMapping(value = "/findAllAppSettingByAppUserIdInPagination", method = RequestMethod.POST)
    public ResponseDTO findAllAppSettingByAppUserIdInPagination(@RequestParam(value = "appUserId", required = false) Long appUserId,
       @RequestParam(value = "page", required = false) Long page, @RequestParam(value = "limit", required = false) Long limit,
       @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate,
       @RequestParam(value = "columnName", required = false) String columnName, @RequestParam(value = "order", required = false) String order,
       @RequestBody SearchTextDto searchTextDto) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for findAllAppSettingByAppUserIdInPagination with AppUserId %d ", appUserId));
            response = this.appSettingService.findAllAppSettingByAppUserIdInPagination(appUserId ,startDate, endDate,
                PagingUtil.ApplyPaging(columnName, order, page, limit), searchTextDto);
        } catch (Exception ex) {
            logger.info("Error during findAllAppSettingByAppUserIdInPagination " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

}
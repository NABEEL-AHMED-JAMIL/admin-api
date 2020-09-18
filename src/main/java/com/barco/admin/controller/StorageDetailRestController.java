package com.barco.admin.controller;

import com.barco.admin.service.impl.StorageDetailServiceImpl;
import com.barco.model.dto.StorageDetailDto;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.pojo.pagination.PaginationDetail;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/key.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-StorageDetail := Barco-StorageDetail EndPoint" })
public class StorageDetailRestController {

    private Logger logger = LoggerFactory.getLogger(StorageDetailRestController.class);

    @Autowired
    private StorageDetailServiceImpl keyService;

    // create task
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/createKey", method = RequestMethod.POST)
    @ApiOperation(value = "Create StorageDetail", notes = "StorageDetail is use in the task.")
    public @ResponseBody ResponseDTO createKey(@RequestBody StorageDetailDto storageDetailDto) {
        ResponseDTO response = null;
        try {
            logger.info("Request for createKey " + storageDetailDto);
            response = this.keyService.createKey(storageDetailDto);
        } catch (Exception ex) {
            logger.info("Error during createKey " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
        return response;
    }

    // get key by id
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/getKey", method = RequestMethod.GET)
    @ApiOperation(value = "Get StorageDetail", notes = "Get StorageDetail by Id.")
    public @ResponseBody ResponseDTO getKey(@RequestParam(name = "id") Long keyId, @RequestParam(name = "appUserId") Long appUserId) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for getKey StorageDetail Id %d And App User Id %d ", keyId, appUserId));
            response = this.keyService.getKey(keyId, appUserId);
        } catch (Exception ex) {
            logger.info("Error during getKey " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
        return response;
    }

    // change status task by id
    // Inactive(0), Active(1), Delete(3),
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ApiOperation(value = "Change Status", notes = "Change Status by id for StorageDetail.")
    public @ResponseBody ResponseDTO statusChange(@RequestParam(name = "id") Long keyId, @RequestParam(name = "status") Status taskStatus) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for statusChange StorageDetail Id %d And Status %s ", keyId, taskStatus));
            response = this.keyService.statusChange(keyId, taskStatus);
        } catch (Exception ex) {
            logger.info("Error during statusChange " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
        return response;
    }

    // fetch all task
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/findAllKeyByAppUserIdInPagination", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch All StorageDetail", notes = "Fetch all key with app user id in pagination.")
    public @ResponseBody ResponseDTO findAllKeyByAppUserIdInPagination(@PathVariable Long appUserId, PaginationDetail paginationDetail) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for findAllKeyByAppUserIdInPagination with AppUserId %d and Pagination Detail %s", appUserId, paginationDetail));
            response = this.keyService.findAllKeyByAppUserIdInPagination(appUserId, paginationDetail);
        } catch (Exception ex) {
            logger.info("Error during findAllKeyByAppUserIdInPagination " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
        return response;
    }

}

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
import com.barco.model.searchspec.PaginationDetail;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/storage.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-StorageDetail := Barco-StorageDetail EndPoint" })
public class StorageDetailRestController {

    private Logger logger = LoggerFactory.getLogger(StorageDetailRestController.class);

    @Autowired
    private StorageDetailServiceImpl storageDetailService;

    @ResponseStatus(HttpStatus.OK) // create storage and update its work for both
    @RequestMapping(value = "/createStorage", method = RequestMethod.POST)
    @ApiOperation(value = "Create StorageDetail", notes = "StorageDetail is use in the task.")
    public @ResponseBody ResponseDTO createStorage(@RequestBody StorageDetailDto storageDetailDto) {
        ResponseDTO response = null;
        try {
            logger.info("Request for createStorage " + storageDetailDto);
            response = this.storageDetailService.createStorage(storageDetailDto);
        } catch (Exception ex) {
            logger.info("Error during createStorage " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // get storage by id
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/getStorageById", method = RequestMethod.GET)
    @ApiOperation(value = "Get StorageDetail", notes = "Get StorageDetail by Id.")
    public @ResponseBody ResponseDTO getStorageById(@RequestParam(name = "id") Long storageId, @RequestParam(name = "appUserId") Long appUserId) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for getStorageById StorageDetail Id %d And App User Id %d ", storageId, appUserId));
            response = this.storageDetailService.getStorageById(storageId, appUserId);
        } catch (Exception ex) {
            logger.info("Error during getStorageById " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }


    // change status task by id
    // Inactive(0), Active(1), Delete(3),
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ApiOperation(value = "Change Status", notes = "Change Status by id for StorageDetail.")
    public @ResponseBody ResponseDTO statusChange(@RequestParam(name = "id") Long storageId, @RequestParam(name = "appUserId") Long appUserId,
        @RequestParam(name = "status") Status storageStatus) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for statusChange StorageDetail Id %d And Status %s ", storageId, storageStatus));
            response = this.storageDetailService.statusChange(storageId, appUserId, storageStatus);
        } catch (Exception ex) {
            logger.info("Error during statusChange " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // fetch all task
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/findAllStorageByAppUserIdInPagination", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch All StorageDetail", notes = "Fetch all key with app user id in pagination.")
    public @ResponseBody ResponseDTO findAllStorageByAppUserIdInPagination(@PathVariable Long appUserId, PaginationDetail paginationDetail) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for findAllStorageByAppUserIdInPagination with AppUserId %d and Pagination Detail %s", appUserId, paginationDetail));
            response = this.storageDetailService.findAllStorageByAppUserIdInPagination(appUserId, paginationDetail);
        } catch (Exception ex) {
            logger.info("Error during findAllStorageByAppUserIdInPagination " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/pingStorage", method = RequestMethod.POST)
    @ApiOperation(value = "Ping Storage", notes = "Ping Storage help to check the connection.")
    public @ResponseBody ResponseDTO pingStorage(@RequestBody StorageDetailDto storageDetailDto) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for pingStorage %s ", storageDetailDto));
            response = this.storageDetailService.pingStorage(storageDetailDto);
        } catch (Exception ex) {
            logger.info("Error during pingStorage " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

}

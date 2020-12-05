package com.barco.admin.controller;

import com.barco.admin.service.impl.StorageDetailServiceImpl;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.dto.StorageDetailDto;
import com.barco.model.util.PaggingUtil;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/storage.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-StorageDetail := Barco-StorageDetail EndPoint" })
public class StorageDetailRestController {

    private Logger logger = LoggerFactory.getLogger(StorageDetailRestController.class);

    @Autowired
    private StorageDetailServiceImpl storageDetailService;

    // admin and super admin can access this method
    @ResponseStatus(HttpStatus.OK) // create storage
    @RequestMapping(value = "/createStorage", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADIM') or hasAuthority('ROLE_ADMIN')")
    @ApiOperation(value = "Create StorageDetail", notes = "StorageDetail is use in the task.")
    public ResponseDTO createStorage(@RequestBody StorageDetailDto storageDetailDto) {
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

    // admin and super admin can access this method
    @ResponseStatus(HttpStatus.OK) // update storage
    @RequestMapping(value = "/updateStorage", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADIM') or hasAuthority('ROLE_ADMIN')")
    @ApiOperation(value = "Update StorageDetail", notes = "StorageDetail is use in the task.")
    public ResponseDTO updateStorage(@RequestBody StorageDetailDto storageDetailDto) {
        ResponseDTO response = null;
        try {
            logger.info("Request for updateStorage " + storageDetailDto);
            response = this.storageDetailService.createStorage(storageDetailDto);
        } catch (Exception ex) {
            logger.info("Error during updateStorage " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // get storage by storage detail by id and app user
    // access by all role
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/getStorageById", method = RequestMethod.GET)
    @ApiOperation(value = "Get StorageDetail", notes = "Get StorageDetail by Id.")
    public ResponseDTO getStorageById(@RequestParam(name = "id") Long storageId,@RequestParam(name = "appUserId") Long appUserId) {
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


    // change status task by storage id and app user id only the admin and super admin action perform
    // Inactive(0), Active(1), Delete(3),
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADIM') or hasAuthority('ROLE_ADMIN')")
    @ApiOperation(value = "Change Status", notes = "Change Status by id for StorageDetail.")
    public ResponseDTO statusChange(@RequestParam(name = "id") Long storageId, @RequestParam(name = "appUserId") Long appUserId,
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
    public ResponseDTO findAllStorageByAppUserIdInPagination(@RequestParam(value = "appUserId", required = false) Long appUserId,
         @RequestParam(value = "page", required = false) Long page, @RequestParam(value = "limit", required = false) Long limit,
         @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate,
         @RequestParam(value = "columnName", required = false) String columnName, @RequestParam(value = "order", required = false) String order,
         @RequestBody SearchTextDto searchTextDto) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for findAllStorageByAppUserIdInPagination with AppUserId %d ", appUserId));
            response = this.storageDetailService.findAllStorageByAppUserIdInPagination(PaggingUtil.ApplyPagging(page, limit, order, columnName),
                    appUserId ,searchTextDto, startDate, endDate);
        } catch (Exception ex) {
            logger.info("Error during findAllStorageByAppUserIdInPagination " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/pingStorage", method = RequestMethod.POST)
    @ApiOperation(value = "Ping Storage", notes = "Ping Storage help to check the connection.")
    public ResponseDTO pingStorage(@RequestBody StorageDetailDto storageDetailDto) {
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

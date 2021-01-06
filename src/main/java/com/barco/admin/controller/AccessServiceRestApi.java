package com.barco.admin.controller;

import com.barco.admin.service.IAccessServiceService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.AccessServiceDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/access-service.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-Access-Service := Barco-Access-Service EndPoint" })
public class AccessServiceRestApi {

    public Logger logger = LogManager.getLogger(AccessServiceRestApi.class);

    @Autowired
    private IAccessServiceService accessServiceService;

    // createAccessService q.a pass (11-21-2020)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @RequestMapping(value = "/createAccessService", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create Access Service.", notes = "Endpoint help create the access service for user.")
    public ResponseDTO createAccessService(@RequestBody AccessServiceDto accessService) {
        ResponseDTO response = null;
        try {
            logger.info("Request for createAccessService " + accessService);
            response = this.accessServiceService.createAccessService(accessService);
        } catch (Exception ex) {
            logger.info("Error during createAccessService " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // change status task by id (Inactive(0), Active(1), Delete(3))
    // statusChange service q.a pass (11-21-2020)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ApiOperation(value = "Change Status", notes = "Endpoint help to change status by id for task.")
    public @ResponseBody ResponseDTO statusChange(@RequestParam(name = "id") Long accessServiceId,
        @RequestParam(name = "appUserId") Long appUserId, @RequestParam(name = "status") Status accessServiceStatus) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for statusChange Access Service Id %d And Status %s ", accessServiceId, accessServiceStatus));
            response = this.accessServiceService.statusChange(accessServiceId, appUserId, accessServiceStatus);
        } catch (Exception ex) {
            logger.info("Error during statusChange " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // get all access service q.a pass (11-21-2020)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @RequestMapping(value = "/getAllAccessService", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get All AccessService", notes = "Endpoint help retrieve all access service")
    public ResponseDTO getAllAccessService() {
        ResponseDTO response = null;
        try {
            logger.info("Request for getAllAccessService");
            // method use to access service
            response = this.accessServiceService.getAllAccessService();
        } catch (Exception ex) {
            logger.info("Error during getAllAccessService " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }
}

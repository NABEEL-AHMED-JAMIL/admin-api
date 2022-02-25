package com.barco.admin.controller;

import com.barco.admin.service.IAccessServiceService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.AccessServiceDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
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
@RequestMapping(value = "/accessService.json", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccessServiceRestApi {

    public Logger logger = LogManager.getLogger(AccessServiceRestApi.class);

    @Autowired
    private IAccessServiceService accessServiceService;

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @RequestMapping(value = "/createAccessService", method = RequestMethod.POST)
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

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    public ResponseDTO statusChange(@RequestParam(name = "accessServiceId") Long accessServiceId,
        @RequestParam(name = "appUserId") Long appUserId, @RequestParam(name = "status") Status status) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for statusChange Access Service Id %d And Status %s ", accessServiceId, status));
            response = this.accessServiceService.statusChange(accessServiceId, appUserId, status);
        } catch (Exception ex) {
            logger.info("Error during statusChange " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @RequestMapping(value = "/getAllAccessService", method = RequestMethod.GET)
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
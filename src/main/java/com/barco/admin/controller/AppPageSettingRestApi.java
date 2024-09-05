package com.barco.admin.controller;

import com.barco.admin.service.AppPageSettingService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.AppPageSettingRequest;
import com.barco.model.dto.request.SessionUser;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.security.UserSessionDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Api use to perform crud operation
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping(value="/appPageSetting.json")
@Api(value = "AppPage Setting Rest Api",
    description = "AppPage Setting Service : Service related to the [Application Page Setting] for manage admin page access.")
public class AppPageSettingRestApi {

    private Logger logger = LoggerFactory.getLogger(AppPageSettingRestApi.class);

    @Autowired
    private AppPageSettingService appPageSettingService;

    /**
     * @apiName :- addAppPageSetting
     * @apiName :- Api use to add app page setting
     * @param payload
     * @return ResponseEntity<?>
     * */
    @ApiOperation(value = "Api use to add new app page.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/addAppPageSetting", method= RequestMethod.POST)
    public ResponseEntity<?> addAppPageSetting(@RequestBody AppPageSettingRequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getUuid(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.appPageSettingService.addAppPageSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addAppPageSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateAppPageSetting
     * @apiName :- Api use to update app page setting
     * @param payload
     * @return ResponseEntity<?>
     * */
    @ApiOperation(value = "Api use to update existing app page setting.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/updateAppPageSetting", method=RequestMethod.POST)
    public ResponseEntity<?> updateAppPageSetting(@RequestBody AppPageSettingRequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getUuid(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.appPageSettingService.updateAppPageSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateAppPageSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAppPageSettingById
     * @apiName :- Api use to find app page setting by id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @ApiOperation(value = "Api use to find existing app page setting by id.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/fetchAppPageSettingById", method=RequestMethod.POST)
    public ResponseEntity<?> fetchAppPageSettingById(@RequestBody AppPageSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.appPageSettingService.fetchAppPageSettingById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAppPageSettingById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllAppPageSetting
     * @apiName :- Api use to fetch app page settings
     * @param payload
     * @return ResponseEntity<?>
     * */
    @ApiOperation(value = "Api use to fetch existing app page settings.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/fetchAllAppPageSetting", method=RequestMethod.POST)
    public ResponseEntity<?> fetchAllAppPageSetting(@RequestBody AppPageSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.appPageSettingService.fetchAllAppPageSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllAppPageSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAppPageSetting
     * @apiName :- Api use to delete app page settings
     * @param payload
     * @return ResponseEntity<?>
     * */
    @ApiOperation(value = "Api use to delete existing app page settings by id.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/deleteAppPageSetting", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAppPageSetting(@RequestBody AppPageSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.appPageSettingService.deleteAppPageSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAppPageSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllAppPageSetting
     * @apiName :- Api use to delete app page settings
     * @param payload
     * @return ResponseEntity<?>
     * */
    @ApiOperation(value = "Api use to delete existing app page settings by ids.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/deleteAllAppPageSetting", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllAppPageSetting(@RequestBody AppPageSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.appPageSettingService.deleteAllAppPageSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllAppPageSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

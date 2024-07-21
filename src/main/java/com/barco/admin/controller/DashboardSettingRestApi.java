package com.barco.admin.controller;

import com.barco.admin.service.DashboardSettingService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.DashboardSettingRequest;
import com.barco.model.dto.response.AppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Api use to perform crud operation
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping(value="/dashboardSetting.json")
public class DashboardSettingRestApi {

    private Logger logger = LoggerFactory.getLogger(DashboardSettingRestApi.class);

    @Autowired
    private DashboardSettingService dashboardSettingService;

    /**
     * @apiName :- addDashboardSetting
     * @apiNote :- Method use add the dashboard setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/addDashboardSetting", method= RequestMethod.POST)
    public ResponseEntity<?> addDashboardSetting(@RequestBody DashboardSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.dashboardSettingService.addDashboardSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addDashboardSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateDashboardSetting
     * @apiNote :- Method use to update the dashboard setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/updateDashboardSetting", method= RequestMethod.POST)
    public ResponseEntity<?> updateDashboardSetting(@RequestBody DashboardSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.dashboardSettingService.updateDashboardSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateDashboardSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllDashboardSetting
     * @apiNote :- Method use to fetch all dashboard setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchAllDashboardSetting", method= RequestMethod.POST)
    public ResponseEntity<?> fetchAllDashboardSetting(@RequestBody DashboardSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.dashboardSettingService.fetchAllDashboardSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllDashboardSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchDashboardSettingByDashboardId
     * @apiNote :- Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchDashboardSettingById", method= RequestMethod.POST)
    public ResponseEntity<?> fetchDashboardSettingById(@RequestBody DashboardSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.dashboardSettingService.fetchDashboardSettingById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchDashboardSettingById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllDashboardSettingByGroup
     * @apiNote :- Method use to fetch all dashboard setting by group
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchAllDashboardSettingByGroup", method= RequestMethod.POST)
    public ResponseEntity<?> fetchAllDashboardSettingByGroup(@RequestBody DashboardSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.dashboardSettingService.fetchAllDashboardSettingByGroup(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllDashboardSettingByGroup ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllDashboardSettingByType
     * @apiNote :- Method use to fetch all dashboard setting by type
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchAllDashboardSettingByType", method= RequestMethod.POST)
    public ResponseEntity<?> fetchAllDashboardSettingByType(@RequestBody DashboardSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.dashboardSettingService.fetchAllDashboardSettingByType(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllDashboardSettingByType ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteDashboardSettingById
     * @apiNote :- Method use to delete the dashboard by id
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/deleteDashboardSettingById", method= RequestMethod.POST)
    public ResponseEntity<?> deleteReportSettingById(@RequestBody DashboardSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.dashboardSettingService.deleteDashboardSettingById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteDashboardSettingById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllDashboardSetting
     * @apiNote :- Api use to delete dashboard by ids
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/deleteAllDashboardSetting", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllDashboardSetting(@RequestBody DashboardSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.dashboardSettingService.deleteAllDashboardSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllDashboardSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}

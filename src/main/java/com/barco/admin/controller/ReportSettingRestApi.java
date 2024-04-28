package com.barco.admin.controller;

import com.barco.admin.service.ReportSettingService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.ReportSettingRequest;
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
@RequestMapping(value="/reportSetting.json")
public class ReportSettingRestApi {

    private Logger logger = LoggerFactory.getLogger(ReportSettingRestApi.class);

    @Autowired
    private ReportSettingService reportSettingService;

    /**
     * @apiName :- addReportSetting
     * @apiNote :- Method use add the report setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/addReportSetting", method= RequestMethod.POST)
    public ResponseEntity<?> addReportSetting(@RequestBody ReportSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.reportSettingService.addReportSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addReportSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateReportSetting
     * @apiNote :- Method use to update the report setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/updateReportSetting", method= RequestMethod.POST)
    public ResponseEntity<?> updateReportSetting(@RequestBody ReportSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.reportSettingService.updateReportSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateReportSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllReportSetting
     * @apiNote :- Method use to fetch all report setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchAllReportSetting", method= RequestMethod.POST)
    public ResponseEntity<?> fetchAllReportSetting(@RequestBody ReportSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.reportSettingService.fetchAllReportSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllReportSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchReportSettingByReportId
     * @apiNote :- Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchReportSettingByReportId", method= RequestMethod.POST)
    public ResponseEntity<?> fetchReportSettingByReportId(@RequestBody ReportSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.reportSettingService.fetchReportSettingByReportId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchReportSettingByReportId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllReportByGroup
     * @apiNote :- Method use to fetch all report setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchAllReportByGroup", method= RequestMethod.POST)
    public ResponseEntity<?> fetchAllReportByGroup(@RequestBody ReportSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.reportSettingService.fetchAllReportSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllReportByGroup ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteReportSettingById
     * @apiNote :- Method use to delete the report by id
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/deleteReportSettingById", method= RequestMethod.POST)
    public ResponseEntity<?> deleteReportSettingById(@RequestBody ReportSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.reportSettingService.deleteReportSettingById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteReportSettingById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllReportSetting
     * @apiNote :- Api use to delete reports by ids
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/deleteAllReportSetting", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllReportSetting(@RequestBody ReportSettingRequest payload) {
        try {
            return new ResponseEntity<>(this.reportSettingService.deleteAllReportSetting(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllReportSetting ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}

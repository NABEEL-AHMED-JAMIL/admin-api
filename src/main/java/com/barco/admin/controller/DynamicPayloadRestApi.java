package com.barco.admin.controller;

import com.barco.admin.service.DynamicPayloadService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.DynamicPayloadRequest;
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
@RequestMapping(value="/dynamicPayload.json")
public class DynamicPayloadRestApi {

    private Logger logger = LoggerFactory.getLogger(DynamicPayloadRestApi.class);

    @Autowired
    private DynamicPayloadService dynamicPayloadService;


    /**
     * @apiName :- addDynamicPayload
     * @apiNote :- Api use to add the credential data
     * @param payload
     * @return ResponseEntity<?> addDynamicPayload
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/addDynamicPayload", method = RequestMethod.POST)
    public ResponseEntity<?> addDynamicPayload(@RequestBody DynamicPayloadRequest payload) {
        try {
            return new ResponseEntity<>(this.dynamicPayloadService.addDynamicPayload(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addDynamicPayload ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * @apiName :- updateDynamicPayload
     * @apiNote :- Api use to add the credential data
     * @param payload
     * @return ResponseEntity<?> updateDynamicPayload
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/updateDynamicPayload", method = RequestMethod.POST)
    public ResponseEntity<?> updateDynamicPayload(@RequestBody DynamicPayloadRequest payload) {
        try {
            return new ResponseEntity<>(this.dynamicPayloadService.updateDynamicPayload(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateDynamicPayload ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * @apiName :- fetchAllDynamicPayload
     * @apiNote :- Api use to add the credential data
     * @param payload
     * @return ResponseEntity<?> fetchAllDynamicPayload
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllDynamicPayload", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllDynamicPayload(@RequestBody DynamicPayloadRequest payload) {
        try {
            return new ResponseEntity<>(this.dynamicPayloadService.fetchAllDynamicPayload(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllDynamicPayload ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * @apiName :- fetchDynamicPayloadById
     * @apiNote :- Api use to add the credential data
     * @param payload
     * @return ResponseEntity<?> fetchDynamicPayloadById
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchDynamicPayloadById", method = RequestMethod.POST)
    public ResponseEntity<?> fetchDynamicPayloadById(@RequestBody DynamicPayloadRequest payload) {
        try {
            return new ResponseEntity<>(this.dynamicPayloadService.fetchDynamicPayloadById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchDynamicPayloadById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * @apiName :- deleteDynamicPayloadById
     * @apiNote :- Api use to add the credential data
     * @param payload
     * @return ResponseEntity<?> deleteDynamicPayloadById
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteDynamicPayloadById", method = RequestMethod.POST)
    public ResponseEntity<?> deleteDynamicPayloadById(@RequestBody DynamicPayloadRequest payload) {
        try {
            return new ResponseEntity<>(this.dynamicPayloadService.deleteDynamicPayloadById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteDynamicPayloadById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * @apiName :- deleteAllDynamicPayload
     * @apiNote :- Api use to add the credential data
     * @param payload
     * @return ResponseEntity<?> deleteAllDynamicPayload
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteAllDynamicPayload", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAllDynamicPayload(@RequestBody DynamicPayloadRequest payload) {
        try {
            return new ResponseEntity<>(this.dynamicPayloadService.deleteAllDynamicPayload(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllDynamicPayload ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

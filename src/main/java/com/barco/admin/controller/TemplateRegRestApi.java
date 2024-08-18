package com.barco.admin.controller;

import com.barco.admin.service.TemplateRegService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.TemplateRegRequest;
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
@RequestMapping(value="/templateReg.json")
public class TemplateRegRestApi {

    private Logger logger = LoggerFactory.getLogger(TemplateRegRestApi.class);

    @Autowired
    private TemplateRegService templateRegService;

    /**
     * @apiName :- addTemplateReg
     * @apiName :- Api use to add templateReg
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(path="/addTemplateReg", method=RequestMethod.POST)
    public ResponseEntity<?> addTemplateReg(@RequestBody TemplateRegRequest payload) {
        try {
            return new ResponseEntity<>(this.templateRegService.addTemplateReg(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addTemplateReg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateTemplateReg
     * @apiName :- Api use to edit templateReg
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(path="/updateTemplateReg", method=RequestMethod.POST)
    public ResponseEntity<?> updateTemplateReg(@RequestBody TemplateRegRequest payload) {
        try {
            return new ResponseEntity<>(this.templateRegService.updateTemplateReg(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateTemplateReg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- findTemplateRegByTemplateId
     * @apiName :- Api use to find templateReg by templateType
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(path="/findTemplateRegByTemplateId", method=RequestMethod.POST)
    public ResponseEntity<?> findTemplateRegByTemplateId(@RequestBody TemplateRegRequest payload) {
        try {
            return new ResponseEntity<>(this.templateRegService.findTemplateRegByTemplateId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while findTemplateRegByTemplateId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchTemplateReg
     * @apiName :- Api use to fetch templateReg
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(path="/fetchTemplateReg", method=RequestMethod.POST)
    public ResponseEntity<?> fetchTemplateReg(@RequestBody TemplateRegRequest payload) {
        try {
            return new ResponseEntity<>(this.templateRegService.fetchTemplateReg(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchTemplateReg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteTemplateReg
     * @apiName :- Api use to delete templateReg
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(path="/deleteTemplateReg", method=RequestMethod.POST)
    public ResponseEntity<?> deleteTemplateReg(@RequestBody TemplateRegRequest payload) {
        try {
            return new ResponseEntity<>(this.templateRegService.deleteTemplateReg(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteTemplateReg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllCredential
     * @apiName :- Api use to delete templateReg
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(path="/deleteAllTemplateReg", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllTemplateReg(@RequestBody TemplateRegRequest payload) {
        try {
            return new ResponseEntity<>(this.templateRegService.deleteAllTemplateReg(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllTemplateReg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}

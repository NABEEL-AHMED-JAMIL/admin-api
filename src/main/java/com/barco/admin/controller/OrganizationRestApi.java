package com.barco.admin.controller;

import com.barco.admin.service.OrganizationService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.excel.ExcelUtil;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.OrganizationRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/organization.json")
public class OrganizationRestApi {

    private Logger logger = LoggerFactory.getLogger(OrganizationRestApi.class);

    @Autowired
    private OrganizationService organizationService;

    /**
     * @apiName :- addOrg
     * @apiNote :- Method use to edit org
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value="/addOrg", method= RequestMethod.POST)
    public ResponseEntity<?> addOrg(@RequestBody OrganizationRequest payload) {
        try {
            return new ResponseEntity<>(this.organizationService.addOrg(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addOrg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateOrg
     * @apiNote :- Method use to update org
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value="/updateOrg", method= RequestMethod.POST)
    public ResponseEntity<?> updateOrg(@RequestBody OrganizationRequest payload) {
        try {
            return new ResponseEntity<>(this.organizationService.updateOrg(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateOrg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchOrgById
     * @apiNote :- Method use to fetch org by id
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value="/fetchOrgById", method= RequestMethod.POST)
    public ResponseEntity<?> fetchOrgById(@RequestBody OrganizationRequest payload) {
        try {
            return new ResponseEntity<>(this.organizationService.fetchOrgById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchOrgById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllOrg
     * @apiNote :- Method use to fetch all org
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value="/fetchAllOrg", method= RequestMethod.POST)
    public ResponseEntity<?> fetchAllOrg(@RequestBody OrganizationRequest payload) {
        try {
            return new ResponseEntity<>(this.organizationService.fetchAllOrg(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllOrg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteOrgById
     * @apiNote :- Method use to delete org by id
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value="/deleteOrgById", method= RequestMethod.POST)
    public ResponseEntity<?> deleteOrgById(@RequestBody OrganizationRequest payload) {
        try {
            return new ResponseEntity<>(this.organizationService.deleteOrgById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteOrgById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllOrg
     * @apiNote :- Method use to delete all org
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value="/deleteAllOrg", method= RequestMethod.POST)
    public ResponseEntity<?> deleteAllOrg(@RequestBody OrganizationRequest payload) {
        try {
            return new ResponseEntity<>(this.organizationService.deleteAllOrg(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllOrg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadOrgTemplateFile
     * @apiNote :- Api use to download Org template
     * @return ResponseEntity<?> downloadOrgTemplateFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/downloadOrgTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadOrgTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchOrgDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.organizationService.downloadOrgTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadOrgTemplateFile xlsx file", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadOrg
     * @apiNote :- Api use to download the Org
     * @return ResponseEntity<?> downloadOrg
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/downloadOrg", method = RequestMethod.POST)
    public ResponseEntity<?> downloadOrg(@RequestBody OrganizationRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchOrgDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.organizationService.downloadOrg(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadOrg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- uploadOrg
     * @apiNote :- Api use to upload the Org
     * @return ResponseEntity<?> uploadOrg
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/uploadOrg", method = RequestMethod.POST)
    public ResponseEntity<?> uploadOrg(FileUploadRequest payload) {
        try {
            if (!BarcoUtil.isNull(payload.getFile())) {
                return new ResponseEntity<>(this.organizationService.uploadOrg(payload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadOrg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

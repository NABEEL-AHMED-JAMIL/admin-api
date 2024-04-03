package com.barco.admin.controller;

import com.barco.admin.service.MgGroupService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.excel.ExcelUtil;
import com.barco.model.dto.request.EnVariablesRequest;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.GroupRequest;
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
 * Api use to perform crud operation
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping(value="/mgGroup.json")
public class MgGroupRestApi {

    private Logger logger = LoggerFactory.getLogger(MgGroupRestApi.class);

    @Autowired
    private MgGroupService mgGroupService;

    /**
     * Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value="/addGroup", method= RequestMethod.POST)
    public ResponseEntity<?> addGroup(@RequestBody GroupRequest payload) {
        try {
            return new ResponseEntity<>(this.mgGroupService.addGroup(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addGroup ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value="/updateGroup", method=RequestMethod.POST)
    public ResponseEntity<?> updateGroup(@RequestBody GroupRequest payload) {
        try {
            return new ResponseEntity<>(this.mgGroupService.updateGroup(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateGroup ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value="/fetchAllGroup", method=RequestMethod.POST)
    public ResponseEntity<?> fetchAllGroup(@RequestBody GroupRequest payload) {
        try {
            return new ResponseEntity<>(this.mgGroupService.fetchAllGroup(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllGroup ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value="/fetchGroupById", method=RequestMethod.POST)
    public ResponseEntity<?> fetchGroupById(@RequestBody GroupRequest payload) {
        try {
            return new ResponseEntity<>(this.mgGroupService.fetchGroupById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchGroupById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value="/deleteGroupById", method=RequestMethod.POST)
    public ResponseEntity<?> deleteGroupById(@RequestBody GroupRequest payload) {
        try {
            return new ResponseEntity<>(this.mgGroupService.deleteGroupById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteGroupById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadGroupTemplateFile
     * Api use to download group template
     * @return ResponseEntity<?> downloadGroupTemplateFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/downloadGroupTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadGroupTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchGroupDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.mgGroupService.downloadGroupTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadGroupTemplateFile xlsx file", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadGroup
     * Api use to download the group
     * @return ResponseEntity<?> downloadGroup
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/downloadGroup", method = RequestMethod.POST)
    public ResponseEntity<?> downloadGroup(@RequestBody EnVariablesRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchGroupDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.mgGroupService.downloadGroup(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadGroup ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- uploadGroup
     * Api use to upload the group
     * @return ResponseEntity<?> uploadGroup
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/uploadGroup", method = RequestMethod.POST)
    public ResponseEntity<?> uploadGroup(FileUploadRequest payload) {
        try {
            if (!BarcoUtil.isNull(payload.getFile())) {
                return new ResponseEntity<>(this.mgGroupService.uploadGroup(payload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadGroup ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}

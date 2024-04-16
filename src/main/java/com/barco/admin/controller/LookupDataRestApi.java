package com.barco.admin.controller;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.excel.ExcelUtil;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Lookup data access for (create,delete,update) only to admin and super admin
 * Fetch access to all the user (admin, super_admin, user)
 * Lookup should be cache data if (admin,super_admin) perform (cud) operation cache should update as well
 * if user is super admin can view all the other admin lookup
 * if user is admin only can view only his lookup
 * @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping(value="/lookupData.json")
public class LookupDataRestApi {

    private Logger logger = LoggerFactory.getLogger(LookupDataRestApi.class);

    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    /**
     * @apiName :- addLookupData
     * @apiNote :- Api use to add the lookup data
     * @param payload
     * @return ResponseEntity<?> addLookupData
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value="/addLookupData", method=RequestMethod.POST)
    public ResponseEntity<?> addLookupData(@RequestBody LookupDataRequest payload) {
        try {
            return new ResponseEntity<>(this.lookupDataCacheService.addLookupData(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addLookupData ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateLookupData
     * @apiNote :- Api use to update the lookup data
     * @param payload
     * @return ResponseEntity<?> updateLookupData
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value="/updateLookupData", method=RequestMethod.POST)
    public ResponseEntity<?> updateLookupData(@RequestBody LookupDataRequest payload) {
        try {
            return new ResponseEntity<>(this.lookupDataCacheService.updateLookupData(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateLookupData ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- findAllParentLookupByUsername
     * @apiNote :- Api use to fetch the lookup detail
     * @return ResponseEntity<?> findAllParentLookupByUsername
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/findAllParentLookupByUsername", method = RequestMethod.POST)
    public ResponseEntity<?> findAllParentLookupByUsername(@RequestBody LookupDataRequest payload) {
        try {
            return new ResponseEntity<>(this.lookupDataCacheService.findAllParentLookupByUsername(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while findAllParentLookupByUsername ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchSubLookupDataByParentLookupDataId
     * @apiNote :- Api use to fetch the sub-Lookup by parent lookup id
     * @param payload
     * @return ResponseEntity<?> fetchSubLookupDataByParentLookupDataId
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/fetchSubLookupDataByParentLookupDataId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchSubLookupDataByParentLookupDataId(@RequestBody LookupDataRequest payload) {
        try {
            return new ResponseEntity<>(this.lookupDataCacheService.fetchSubLookupDataByParentLookupDataId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchSubLookupDataByParentLookupDataId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchLookupDataByLookupType
     * @apiNote :- Api use to fetch the sub-Lookup by parent lookup type
     * @param payload
     * @return ResponseEntity<?> fetchLookupDataByLookupType
     * */
    @RequestMapping(value = "/fetchLookupDataByLookupType", method = RequestMethod.POST)
    public ResponseEntity<?> fetchLookupDataByLookupType(@RequestBody LookupDataRequest payload) {
        try {
            return new ResponseEntity<>(this.lookupDataCacheService.fetchLookupDataByLookupType(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchLookupByLookupType ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteLookupData
     * @apiNote :- Api use to delete the lookup data
     * @param payload
     * @return ResponseEntity<?> deleteLookupData
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/deleteLookupData", method = RequestMethod.POST)
    public ResponseEntity<?> deleteLookupData(@RequestBody LookupDataRequest payload) {
        try {
            return new ResponseEntity<>(this.lookupDataCacheService.deleteLookupData(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteLookupData ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadLookupDataTemplateFile
     * @apiNote :- Api use to download lookup template the lookup data
     * @return ResponseEntity<?> downloadLookupDataTemplateFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/downloadLookupDataTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadLookupDataTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchLookupDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.lookupDataCacheService.downloadLookupDataTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadLookupDataTemplateFile xlsx file", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadLookupData
     * @apiNote :- Api use to download the lookup data
     * @return ResponseEntity<?> downloadLookupData
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/downloadLookupData", method = RequestMethod.POST)
    public ResponseEntity<?> downloadLookupData(@RequestBody LookupDataRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchLookupDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.lookupDataCacheService.downloadLookupData(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadLookupData ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- uploadLookupData
     * @apiNote :- Api use to upload the lookup data
     * @return ResponseEntity<?> uploadLookupData
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/uploadLookupData", method = RequestMethod.POST)
    public ResponseEntity<?> uploadLookupData(FileUploadRequest payload) {
        try {
            if (!BarcoUtil.isNull(payload.getFile())) {
                return new ResponseEntity<>(this.lookupDataCacheService.uploadLookupData(payload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadLookup ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}

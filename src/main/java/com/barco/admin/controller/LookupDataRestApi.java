package com.barco.admin.controller;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.service.UserDetailsImpl;
import com.barco.model.util.ProcessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Api use to perform crud operation
 * Lookup data access for (create,delete,update) only to admin and super admin
 * Fetch access to all the user (admin, super_admin, user)
 * Lookup should be cache data if (admin,super_admin) perform (cud) operation cache should update as well
 * if user is super admin can view all the other admin lookup
 * if user is admin only can view only his lookup
 * @PreAuthorize("hasRole('MASTER_ADMIN')")
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/lookup.json")
public class LookupDataRestApi {

    private Logger logger = LoggerFactory.getLogger(LookupDataRestApi.class);

    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    /**
     * api-status :- done
     * @apiName :- fetchCacheData
     * Api use to fetch cache data
     * @return ResponseEntity<?> fetchCacheData
     * */
    @RequestMapping(value = "/fetchCacheData", method = RequestMethod.GET)
    public ResponseEntity<?> fetchCacheData() {
        try {
            return new ResponseEntity<>(this.lookupDataCacheService.fetchCacheData(), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addLookupData ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                    "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- addLookupData
     * Api use to add the lookup data
     * @param requestPayload
     * @return ResponseEntity<?> addLookupData
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/addLookupData", method = RequestMethod.POST)
    public ResponseEntity<?> addLookupData(@RequestBody LookupDataRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.lookupDataCacheService.addLookupData(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addLookupData ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- updateLookupData
     * Api use to update the lookup data
     * @param requestPayload
     * @return ResponseEntity<?> updateLookupData
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/updateLookupData", method = RequestMethod.PUT)
    public ResponseEntity<?> updateLookupData(@RequestBody LookupDataRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.lookupDataCacheService.updateLookupData(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateLookupData ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- fetchAllLookup
     * Api use to fetch the lookup detail
     * @param requestPayload
     * @return ResponseEntity<?> fetchAllLookup
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/fetchAllLookup", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllLookup(@RequestBody LookupDataRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.lookupDataCacheService.fetchAllLookup(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllLookup ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- fetchSubLookupByParentId
     * Api use to fetch the sub-Lookup by parent lookup id
     * @param requestPayload
     * @return ResponseEntity<?> fetchSubLookupByParentId
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/fetchSubLookupByParentId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchSubLookupByParentId(@RequestBody LookupDataRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.lookupDataCacheService.fetchSubLookupByParentId(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchSubLookupByParentId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- fetchLookupByLookupType
     * Api use to fetch the sub-Lookup by parent lookup type
     * @param requestPayload
     * @return ResponseEntity<?> fetchLookupByLookupType
     * */
    @RequestMapping(value = "/fetchLookupByLookupType", method = RequestMethod.POST)
    public ResponseEntity<?> fetchLookupByLookupType(@RequestBody LookupDataRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.lookupDataCacheService.fetchLookupByLookupType(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchLookupByLookupType ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- deleteLookupData
     * Api use to delete the lookup data
     * @param requestPayload
     * @return ResponseEntity<?> deleteLookupData
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/deleteLookupData", method = RequestMethod.PUT)
    public ResponseEntity<?> deleteLookupData(@RequestBody LookupDataRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.lookupDataCacheService.deleteLookupData(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteLookupData ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- downloadLookupTemplateFile
     * Api use to download lookup template the lookup data
     * @return ResponseEntity<?> downloadLookupTemplateFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/downloadLookupTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadLookupTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(ProcessUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchLookupDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ".xlsx";
            headers.add(ProcessUtil.CONTENT_DISPOSITION,ProcessUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(
                this.lookupDataCacheService.downloadLookupTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadLookupTemplateFile xlsx file",
                ExceptionUtil.getRootCauseMessage(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Sorry File Not Downland, Contact With Support"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- downloadLookup
     * Api use to download the lookup data
     * @param requestPayload
     * @return ResponseEntity<?> downloadLookup
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/downloadLookup", method = RequestMethod.POST)
    public ResponseEntity<?> downloadLookup(@RequestBody LookupDataRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(ProcessUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchLookupDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ".xlsx";
            headers.add(ProcessUtil.CONTENT_DISPOSITION,ProcessUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(
                this.lookupDataCacheService.downloadLookup(requestPayload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadLookup ", ExceptionUtil.getRootCauseMessage(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- uploadLookup
     * Api use to upload the lookup data
     * @param requestPayload
     * @return ResponseEntity<?> uploadLookup
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/uploadLookup", method = RequestMethod.POST)
    public ResponseEntity<?> uploadLookup(FileUploadRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            if (!BarcoUtil.isNull(requestPayload.getFile())) {
                return new ResponseEntity<>(this.lookupDataCacheService.uploadLookup(requestPayload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR, "File not found for process."), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadLookup ", ExceptionUtil.getRootCauseMessage(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR, "Sorry File Not Upload Contact With Support"), HttpStatus.BAD_REQUEST);
        }
    }

}
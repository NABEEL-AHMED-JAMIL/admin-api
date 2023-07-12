package com.barco.admin.controller;

import com.barco.admin.service.SettingApiService;
import com.barco.common.request.ConfigurationMakerRequest;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.XmlOutTagInfoUtil;
import com.barco.model.dto.request.QueryRequest;
import com.barco.model.dto.request.TemplateRegRequest;
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
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Api use to perform crud operation on setting
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/setting.json")
public class SettingRestApi {

    private Logger logger = LoggerFactory.getLogger(SettingRestApi.class);

    @Autowired
    private SettingApiService settingApiService;
    @Autowired
    private XmlOutTagInfoUtil xmlOutTagInfoUtil;

    /**
     * Integration Status :- done
     * Api use to execute dynamicQuery for select
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/dynamicQueryResponse", method = RequestMethod.POST)
    public ResponseEntity<?> dynamicQueryResponse(@RequestBody QueryRequest requestPayload) {
        try {
            return new ResponseEntity<>(this.settingApiService.dynamicQueryResponse(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while dynamicQueryResponse ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadLookupTemplateFile
     * Api use to download dynamic query
     * @return ResponseEntity<?> downloadDynamicQueryFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(value = "/downloadDynamicQueryFile", method = RequestMethod.POST)
    public ResponseEntity<?> downloadDynamicQueryFile(@RequestBody QueryRequest requestPayload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(ProcessUtil.SIMPLE_DATE_PATTERN);
            String fileName = "DynamicQuery-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ".xlsx";
            headers.add(ProcessUtil.CONTENT_DISPOSITION, ProcessUtil.FILE_NAME_HEADER + fileName);
            ByteArrayOutputStream byteArrayOutputStream = this.settingApiService.downloadDynamicQueryFile(requestPayload);
            return ResponseEntity.ok().headers(headers).body(byteArrayOutputStream.toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadDynamicQueryFile xlsx file", ExceptionUtil.getRootCauseMessage(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Sorry File Not Downland, Contact With Support"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Integration Status :- done
     * Api use to create the xml setting for source task
     * @param requestPayload
     * @return ResponseEntity<?> xmlCreateChecker
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(path = "xmlCreateChecker",  method = RequestMethod.POST)
    public ResponseEntity<?> xmlCreateChecker(@RequestBody ConfigurationMakerRequest requestPayload) {
        try {
            if (requestPayload.getXmlTagsInfo() != null) {
                return new ResponseEntity<>(new AppResponse(ProcessUtil.SUCCESS,
                    this.xmlOutTagInfoUtil.makeXml(requestPayload)), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR, "Wrong Input"), HttpStatus.OK);
            }
        } catch (Exception ex) {
            logger.error("An error occurred while xmlCreateChecker ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Integration Status :- done
     * Api use to add templateReg
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(path = "addTemplateReg",  method = RequestMethod.POST)
    public ResponseEntity<?> addTemplateReg(@RequestBody TemplateRegRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.settingApiService.addTemplateReg(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addTemplateReg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Integration Status :- done
     * Api use to edit templateReg
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(path = "editTemplateReg",  method = RequestMethod.POST)
    public ResponseEntity<?> editTemplateReg(@RequestBody TemplateRegRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.settingApiService.editTemplateReg(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editTemplateReg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Integration Status :- done
     * Api use to find templateReg by templateType
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(path = "findTemplateRegByTemplateId",  method = RequestMethod.POST)
    public ResponseEntity<?> findTemplateRegByTemplateId(@RequestBody TemplateRegRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.settingApiService.findTemplateRegByTemplateId(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while findTemplateRegByTemplateId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Integration Status :- done
     * Api use to fetch templateReg
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(path = "fetchTemplateReg",  method = RequestMethod.POST)
    public ResponseEntity<?> fetchTemplateReg(@RequestBody TemplateRegRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.settingApiService.fetchTemplateReg(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchTemplateReg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Integration Status :- done
     * Api use to delete templateReg
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(path = "deleteTemplateReg",  method = RequestMethod.POST)
    public ResponseEntity<?> deleteTemplateReg(@RequestBody TemplateRegRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.settingApiService.deleteTemplateReg(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteTemplateReg ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

}
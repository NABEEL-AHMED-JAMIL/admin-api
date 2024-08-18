package com.barco.admin.controller;

import com.barco.admin.service.SettingService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.QueryInquiryRequest;
import com.barco.model.dto.request.QueryRequest;
import com.barco.model.dto.request.SessionUser;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.util.MessageUtil;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
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
@RequestMapping(value="/setting.json")
public class SettingRestApi {

    private Logger logger = LoggerFactory.getLogger(SettingRestApi.class);

    @Autowired
    private SettingService settingService;

    /**
     * @apiName :- fetchSettingDashboard
     * @apiName :- Api use to fetch the dashboard for admin section
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value="/fetchSettingDashboard", method=RequestMethod.POST)
    public ResponseEntity<?> fetchSettingDashboard(@RequestBody SessionUser sessionUser) {
        try {
            return new ResponseEntity<>(this.settingService.fetchSettingDashboard(sessionUser), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchSettingDashboard ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchCountryData
     * @apiName :- Api use to fetch the country for admin section
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(value="/fetchCountryData", method=RequestMethod.POST)
    public ResponseEntity<?> fetchCountryData(@RequestBody SessionUser sessionUser) {
        try {
            return new ResponseEntity<>(this.settingService.fetchCountryData(sessionUser), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchCountryData ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- dynamicQueryResponse
     * @apiName :- Api use to execute dynamicQuery for select
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(value="/dynamicQueryResponse", method=RequestMethod.POST)
    public ResponseEntity<?> dynamicQueryResponse(@RequestBody QueryRequest payload) {
        try {
            return new ResponseEntity<>(this.settingService.dynamicQueryResponse(payload), HttpStatus.OK);
        } catch (SQLGrammarException ex) {
            logger.error("An error occurred while dynamicQueryResponse", ExceptionUtil.getRootCauseMessage(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.SQL_GRAMMAR_EXCEPTION), HttpStatus.BAD_REQUEST);
        }  catch (Exception ex) {
            logger.error("An error occurred while dynamicQueryResponse ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadDynamicQueryFile
     * @apiName :- downloadLookupTemplateFile
     * Api use to download dynamic query
     * @return ResponseEntity<?> downloadDynamicQueryFile
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(value="/downloadDynamicQueryFile", method=RequestMethod.POST)
    public ResponseEntity<?> downloadDynamicQueryFile(@RequestBody QueryRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "DynamicQuery-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ".xlsx";
            headers.add(BarcoUtil.CONTENT_DISPOSITION, BarcoUtil.FILE_NAME_HEADER + fileName);
            ByteArrayOutputStream byteArrayOutputStream = this.settingService.downloadDynamicQueryFile(payload);
            return ResponseEntity.ok().headers(headers).body(byteArrayOutputStream.toByteArray());
        } catch (SQLGrammarException ex) {
            logger.error("An error occurred while downloadDynamicQueryFile xlsx file", ExceptionUtil.getRootCauseMessage(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.SQL_GRAMMAR_EXCEPTION), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while downloadDynamicQueryFile xlsx file", ExceptionUtil.getRootCauseMessage(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- addQueryInquiry
     * @apiNote :- Api use to add the query inquiry
     * @param payload
     * @return ResponseEntity<?> addQueryInquiry
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(value = "/addQueryInquiry", method = RequestMethod.POST)
    public ResponseEntity<?> addQueryInquiry(@RequestBody QueryInquiryRequest payload) {
        try {
            return new ResponseEntity<>(this.settingService.addQueryInquiry(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addQueryInquiry ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateQueryInquiry
     * @apiNote :- Api use to edit the QueryInquiry data
     * @param payload
     * @return ResponseEntity<?> updateQueryInquiry
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(value = "/updateQueryInquiry", method = RequestMethod.POST)
    public ResponseEntity<?> updateQueryInquiry(@RequestBody QueryInquiryRequest payload) {
        try {
            return new ResponseEntity<>(this.settingService.updateQueryInquiry(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateQueryInquiry ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchQueryInquiryById
     * @apiNote :- Api use to fetch the QueryInquiry by QueryInquiry id
     * @param payload
     * @return ResponseEntity<?> fetchQueryInquiryById
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(value = "/fetchQueryInquiryById", method = RequestMethod.POST)
    public ResponseEntity<?> fetchQueryInquiryById(@RequestBody QueryInquiryRequest payload) {
        try {
            return new ResponseEntity<>(this.settingService.fetchQueryInquiryById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchQueryInquiryById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllQueryInquiry
     * @apiNote :- Api use to fetch the QueryInquiry data
     * @param payload
     * @return ResponseEntity<?> fetchAllQueryInquiry
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(value = "/fetchAllQueryInquiry", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllQueryInquiry(@RequestBody QueryInquiryRequest payload) {
        try {
            return new ResponseEntity<>(this.settingService.fetchAllQueryInquiry(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllQueryInquiry ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteQueryInquiry
     * @apiNote :- Api use to delete the queryInquiry
     * @param payload
     * @return ResponseEntity<?> deleteQueryInquiry
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(value = "/deleteQueryInquiryById", method = RequestMethod.POST)
    public ResponseEntity<?> deleteQueryInquiryById(@RequestBody QueryInquiryRequest payload) {
        try {
            return new ResponseEntity<>(this.settingService.deleteQueryInquiryById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteQueryInquiryById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllQueryInquiry
     * @apiNote :- Api use to delete queryInquiry
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('DEV')")
    @RequestMapping(path="/deleteAllQueryInquiry", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllQueryInquiry(@RequestBody QueryInquiryRequest payload) {
        try {
            return new ResponseEntity<>(this.settingService.deleteAllQueryInquiry(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllQueryInquiry ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

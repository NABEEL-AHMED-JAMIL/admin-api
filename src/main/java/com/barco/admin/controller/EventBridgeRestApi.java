package com.barco.admin.controller;

import com.barco.admin.service.EventBridgeService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.excel.ExcelUtil;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.security.UserSessionDetail;
import com.barco.model.util.MessageUtil;
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
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping(value="/eventBridge.json")
public class EventBridgeRestApi {

    private Logger logger = LoggerFactory.getLogger(EventBridgeRestApi.class);

    @Autowired
    private EventBridgeService eventBridgeService;

    /**
     * @apiName :- addEventBridge
     * @apiName :- Api use to add event bridge
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/addEventBridge", method= RequestMethod.POST)
    public ResponseEntity<?> addEventBridge(@RequestBody EventBridgeRequest payload) {
        try {
            return new ResponseEntity<>(this.eventBridgeService.addEventBridge(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addEventBridge ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateEventBridge
     * @apiName :- Api use to add event bridge
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/updateEventBridge", method=RequestMethod.POST)
    public ResponseEntity<?> updateEventBridge(@RequestBody EventBridgeRequest payload) {
        try {
            return new ResponseEntity<>(this.eventBridgeService.updateEventBridge(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateEventBridge ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllEventBridge
     * @apiName :- Api use to fetch event bridge
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/fetchAllEventBridge", method=RequestMethod.POST)
    public ResponseEntity<?> fetchAllEventBridge(@RequestBody EventBridgeRequest payload) {
        try {
            return new ResponseEntity<>(this.eventBridgeService.fetchAllEventBridge(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllEventBridge ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchEventBridgeById
     * @apiName :- Api use to fetch event bridge by id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/fetchEventBridgeById", method=RequestMethod.POST)
    public ResponseEntity<?> fetchEventBridgeById(@RequestBody EventBridgeRequest payload) {
        try {
            return new ResponseEntity<>(this.eventBridgeService.fetchEventBridgeById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchEventBridgeById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchEventBridgeByBridgeType
     * @apiName :- Api use to fetch event bridge by id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/fetchEventBridgeByBridgeType", method=RequestMethod.POST)
    public ResponseEntity<?> fetchEventBridgeByBridgeType(@RequestBody EventBridgeRequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.eventBridgeService.fetchEventBridgeByBridgeType(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchEventBridgeByBridgeType ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteEventBridgeById
     * @apiName :- Api use to delete event bridge by id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/deleteEventBridgeById", method=RequestMethod.POST)
    public ResponseEntity<?> deleteEventBridgeById(@RequestBody EventBridgeRequest payload) {
        try {
            return new ResponseEntity<>(this.eventBridgeService.deleteEventBridgeById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteEventBridgeById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllEventBridge
     * @apiName :- Api use to delete all event bridge
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/deleteAllEventBridge", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllEventBridge(@RequestBody EventBridgeRequest payload) {
        try {
            return new ResponseEntity<>(this.eventBridgeService.deleteAllEventBridge(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllEventBridge ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchLinkEventBridgeWitUser
     * @apiName :- Api use to fetch link event bridge with user
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(path="/fetchLinkEventBridgeWitUser", method=RequestMethod.POST)
    public ResponseEntity<?> fetchLinkEventBridgeWitUser(@RequestBody EventBridgeRequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.eventBridgeService.fetchLinkEventBridgeWitUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchLinkEventBridgeWitUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkEventBridgeWithUser
     * @apiName :- Api use to link event bridge with user
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(path="/linkEventBridgeWithUser", method=RequestMethod.POST)
    public ResponseEntity<?> linkEventBridgeWithUser(@RequestBody LinkEBURequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.eventBridgeService.linkEventBridgeWithUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkEventBridgeWithUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- genEventBridgeToken
     * @apiName :- Api use to link event bridge he can generate the token and event the admin
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/genEventBridgeToken", method=RequestMethod.POST)
    public ResponseEntity<?> genEventBridgeToken(@RequestBody LinkEBURequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.eventBridgeService.genEventBridgeToken(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while genEventBridgeToken ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadEventBridgeTemplateFile
     * @apiNote :- Api use to download event bridge template
     * @return ResponseEntity<?> downloadEventBridgeTemplateFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/downloadEventBridgeTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadEventBridgeTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchEventBridgeDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.eventBridgeService.downloadEventBridgeTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadEventBridgeTemplateFile xlsx file", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadEventBridge
     * @apiNote :- Api use to download the event bridge
     * @return ResponseEntity<?> downloadEventBridge
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/downloadEventBridge", method = RequestMethod.POST)
    public ResponseEntity<?> downloadEventBridge(@RequestBody EventBridgeRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchEventBridgeDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.eventBridgeService.downloadEventBridge(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadEventBridge ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- uploadEventBridge
     * @apiNote :- Api use to upload the lookup data
     * @return ResponseEntity<?> uploadEventBridge
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/uploadEventBridge", method = RequestMethod.POST)
    public ResponseEntity<?> uploadEventBridge(FileUploadRequest payload) {
        try {
            if (!BarcoUtil.isNull(payload.getFile())) {
                return new ResponseEntity<>(this.eventBridgeService.uploadEventBridge(payload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadEventBridge ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}

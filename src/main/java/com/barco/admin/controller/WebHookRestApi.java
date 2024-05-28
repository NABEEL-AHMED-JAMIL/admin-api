package com.barco.admin.controller;

import com.barco.admin.service.WebHookService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.LinkWHURequest;
import com.barco.model.dto.request.SessionUser;
import com.barco.model.dto.request.WebHookRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.security.UserSessionDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Api use to perform crud operation
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping(value="/webHook.json")
public class WebHookRestApi {

    private Logger logger = LoggerFactory.getLogger(WebHookRestApi.class);

    @Autowired
    private WebHookService webHookService;

    /**
     * @apiName :- addWebHook
     * @apiName :- Api use to add webhook
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/addWebHook", method= RequestMethod.POST)
    public ResponseEntity<?> addWebHook(@RequestBody WebHookRequest payload) {
        try {
            return new ResponseEntity<>(this.webHookService.addWebHook(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addWebHook ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateWebHook
     * @apiName :- Api use to add webhook
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/updateWebHook", method=RequestMethod.POST)
    public ResponseEntity<?> updateWebHook(@RequestBody WebHookRequest payload) {
        try {
            return new ResponseEntity<>(this.webHookService.updateWebHook(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateWebHook ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllWebHook
     * @apiName :- Api use to fetch webhook
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/fetchAllWebHook", method=RequestMethod.POST)
    public ResponseEntity<?> fetchAllWebHook(@RequestBody WebHookRequest payload) {
        try {
            return new ResponseEntity<>(this.webHookService.fetchAllWebHook(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllWebHook ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchWebHookById
     * @apiName :- Api use to fetch webhook by id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/fetchWebHookById", method=RequestMethod.POST)
    public ResponseEntity<?> fetchWebHookById(@RequestBody WebHookRequest payload) {
        try {
            return new ResponseEntity<>(this.webHookService.fetchWebHookById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchWebHookById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteWebHookById
     * @apiName :- Api use to delete webhook by id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/deleteWebHookById", method=RequestMethod.POST)
    public ResponseEntity<?> deleteWebHookById(@RequestBody WebHookRequest payload) {
        try {
            return new ResponseEntity<>(this.webHookService.deleteWebHookById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteWebHookById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllWebHook
     * @apiName :- Api use to delete all web hook
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/deleteAllWebHook", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllWebHook(@RequestBody WebHookRequest payload) {
        try {
            return new ResponseEntity<>(this.webHookService.deleteAllWebHook(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllWebHook ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchLinkWebHookWitUser
     * @apiName :- Api use to fetch link webhook with user
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(path="/fetchLinkWebHookWitUser", method=RequestMethod.POST)
    public ResponseEntity<?> fetchLinkWebHookWitUser(@RequestBody WebHookRequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.webHookService.fetchLinkWebHookWitUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchLinkWebHookWitUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkWebHookWithUser
     * @apiName :- Api use to link web hok with user
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN')")
    @RequestMapping(path="/linkWebHookWithUser", method=RequestMethod.POST)
    public ResponseEntity<?> linkWebHookWithUser(@RequestBody LinkWHURequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.webHookService.linkWebHookWithUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkWebHookWithUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- genWebHookToken
     * @apiName :- Api use to link web hook he can gernate the token and event the admin
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/genWebHookToken", method=RequestMethod.POST)
    public ResponseEntity<?> genWebHookToken(@RequestBody LinkWHURequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.webHookService.genWebHookToken(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkWebHookWithUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}

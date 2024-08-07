package com.barco.admin.controller;

import com.barco.admin.service.AppUserService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.excel.ExcelUtil;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.security.UserSessionDetail;
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
@RequestMapping(value = "/appUser.json")
public class AppUserRestApi {

    private Logger logger = LoggerFactory.getLogger(AppUserRestApi.class);

    @Autowired
    private AppUserService appUserService;

    /**
     * @apiName :- fetchAppUserProfile
     * @apiNote :- Api use to fetch app user profile
     * @param username
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/fetchAppUserProfile", method = RequestMethod.GET)
    public ResponseEntity<?> fetchAppUserProfile(@RequestParam String username) {
        try {
            return new ResponseEntity<>(this.appUserService.fetchAppUserProfile(username), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAppUserProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateAppUserProfile
     * @apiNote :- Api use to update app user profile
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateAppUserProfile", method = RequestMethod.POST)
    public ResponseEntity<?> updateAppUserProfile(@RequestBody UpdateUserProfileRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.updateAppUserProfile(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateAppUserProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateAppUserEnvVariable
     * @apiNote :- Api use to update app user env variable
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateAppUserEnvVariable", method = RequestMethod.POST)
    public ResponseEntity<?> updateAppUserEnvVariable(@RequestBody EnVariablesRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.updateAppUserEnvVariable(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateAppUserEnvVariable ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateAppUserPassword
     * @apiNote :- Api use to update app user password
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateAppUserPassword", method = RequestMethod.POST)
    public ResponseEntity<?> updateAppUserPassword(@RequestBody UpdateUserProfileRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.updateAppUserPassword(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateAppUserPassword ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- closeAppUserAccount
     * @apiNote :- Api use to close app user account
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/closeAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> closeAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.closeAppUserAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while closeAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllAppUserAccount
     * @apiNote :- Api use to delete all app user account
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DEV')")
    @RequestMapping(path="/deleteAllAppUserAccount", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.deleteAllAppUserAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadAppUserAccountTemplateFile
     * @apiNote :- Api use to download app user account template file
     * @return ResponseEntity<?> downloadAppUserAccountTemplateFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DEV')")
    @RequestMapping(value = "/downloadAppUserAccountTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadAppUserAccountTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchAppUserAccountDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.appUserService.downloadAppUserAccountTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadAppUserAccountTemplateFile xlsx file", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadAppUserAccount
     * @apiNote :- Api use to download app user account
     * @return ResponseEntity<?> AppUserRequest
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DEV')")
    @RequestMapping(value = "/downloadAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> downloadAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchAppUserAccountDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.appUserService.downloadAppUserAccount(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllAppUserAccount
     * @apiNote :- Api user to fetch all app user account
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DEV')")
    @RequestMapping(value = "/fetchAllAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.fetchAllAppUserAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- addAppUserAccount
     * @apiNote :- Api use to add app user account
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DEV')")
    @RequestMapping(value = "/addAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> addAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            // user session detail
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.appUserService.addAppUserAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editAppUserAccount
     * @apiNote :- Api use to edit the app user account
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DEV')")
    @RequestMapping(value = "/editAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> editAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            // user session detail
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.appUserService.editAppUserAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editAppUserAccount
     * @apiNote :- Api use to edit the app user account
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DEV')")
    @RequestMapping(value = "/enabledDisabledAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> enabledDisabledAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            // user session detail
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.appUserService.enabledDisabledAppUserAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while enabledDisabledAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

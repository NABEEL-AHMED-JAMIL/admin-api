package com.barco.admin.controller;

import com.barco.admin.service.AppUserService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.excel.ExcelUtil;
import com.barco.model.dto.request.AppUserRequest;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.SessionUser;
import com.barco.model.dto.request.SignupRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.security.UserSessionDetail;
import com.barco.model.util.MessageUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

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
     * @apiName :- findAppUserProfile
     * @apiNote :- Api use to
     * @param username
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/fetchAppUserProfile", method = RequestMethod.GET)
    public ResponseEntity<?> fetchAppUserProfile(@RequestParam String username) {
        try {
            return new ResponseEntity<>(this.appUserService.fetchAppUserProfile(username), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAppUserProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateAppUserProfile
     * @apiNote :- Api use to
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateAppUserProfile", method = RequestMethod.POST)
    public ResponseEntity<?> updateAppUserProfile(@RequestBody AppUserRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.updateAppUserProfile(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateAppUserProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateAppUserPassword
     * @apiNote :- Api use to
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateAppUserPassword", method = RequestMethod.POST)
    public ResponseEntity<?> updateAppUserPassword(@RequestBody AppUserRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.updateAppUserPassword(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateAppUserPassword ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- findAppUserProfile
     * @apiNote :- Api use to
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateAppUserCompany", method = RequestMethod.POST)
    public ResponseEntity<?> updateAppUserCompany(@RequestBody AppUserRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.updateAppUserCompany(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateAppUserCompany ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- closeAppUserAccount
     * @apiNote :- Api use to
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/closeAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> closeAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.closeAppUserAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while closeAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- closeAppUserAccount
     * @apiNote :- Api use to
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/fetchAllAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.fetchAllAppUserAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- addAppUserAccount
     * @apiNote :- Api use to add app user account
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/addAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> addAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            // user session detail
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.appUserService.addAppUserAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editAppUserAccount
     * @apiNote :- Api use to edit the app user account
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/editAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> editAppUserAccount(@RequestBody AppUserRequest payload) {
        try {
            // user session detail
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.appUserService.editAppUserAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- viewAppUserLinkGroupAccount
     * @apiNote :- Api use to view the link group
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/viewAppUserLinkGroupAccount", method = RequestMethod.POST)
    public ResponseEntity<?> viewAppUserLinkGroupAccount(@RequestBody AppUserRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.viewAppUserLinkGroupAccount(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while viewAppUserLinkGroupAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkOrUnlinkAppUserWithGroup
     * @apiNote :- Api use to view the link group
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/linkOrUnlinkAppUserWithGroup", method = RequestMethod.POST)
    public ResponseEntity<?> linkOrUnlinkAppUserWithGroup(@RequestBody AppUserRequest payload) {
        try {
            return new ResponseEntity<>(this.appUserService.linkOrUnlinkAppUserWithGroup(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkOrUnlinkAppUserWithGroup ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadAppUserTemplateFile
     * Api use to download group template
     * @return ResponseEntity<?> downloadAppUserTemplateFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/downloadAppUserTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadAppUserTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchAppUserDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.appUserService.downloadAppUserTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadAppUserTemplateFile xlsx file", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadAppUser
     * Api use to download the app user
     * @return ResponseEntity<?> downloadGroup
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/downloadAppUsers", method = RequestMethod.POST)
    public ResponseEntity<?> downloadAppUsers(@RequestBody AppUserRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchAppUserDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.appUserService.downloadAppUsers(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadAppUsers ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- uploadAppUsers
     * Api use to upload the appUser
     * @return ResponseEntity<?> uploadAppUsers
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/uploadAppUsers", method = RequestMethod.POST)
    public ResponseEntity<?> uploadAppUsers(FileUploadRequest payload) {
        try {
            if (!BarcoUtil.isNull(payload.getFile())) {
                return new ResponseEntity<>(this.appUserService.uploadAppUsers(payload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadAppUsers ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}

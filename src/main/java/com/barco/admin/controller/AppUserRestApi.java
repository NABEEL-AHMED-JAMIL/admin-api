package com.barco.admin.controller;

import com.barco.admin.service.AppUserService;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.SignupRequest;
import com.barco.model.dto.request.UpdateUserProfileRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.service.UserDetailsImpl;
import com.barco.model.util.ProcessUtil;
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
@CrossOrigin(origins = "*")
@RequestMapping(value = "/appUser.json")
public class AppUserRestApi {

    private Logger logger = LoggerFactory.getLogger(AppUserRestApi.class);

    @Autowired
    private AppUserService appUserService;

    /**
     * api-status :- done
     * @apiName :- tokenVerify
     * @apiNote :- Api use to check token is valid or not
     * its empty call to check the token expiry
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/tokenVerify", method = RequestMethod.GET)
    public ResponseEntity<?> tokenVerify() {
        try {
            return new ResponseEntity<>(new AppResponse(ProcessUtil.SUCCESS, "Token valid."), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while tokenVerify ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Token not valid."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- signupAppUser
     * @apiNote :- Api use to create the appUser as admin access
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/signupAppUser", method = RequestMethod.POST)
    public ResponseEntity<?> signupAppUser(@RequestBody SignupRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.appUserService.signupAppUser(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while signupAppUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                    "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- getAppUserProfile
     * @apiNote :- Api use to sign In the appUser
     * @param username
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/getAppUserProfile", method = RequestMethod.GET)
    public ResponseEntity<?> getAppUserProfile(@RequestParam String username) {
        try {
            return new ResponseEntity<>(this.appUserService.getAppUserProfile(username), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while getAppUserProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- updateAppUserProfile
     * @apiNote :- Api use to update profile
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateAppUserProfile", method = RequestMethod.POST)
    public ResponseEntity<?> updateAppUserProfile(@RequestBody UpdateUserProfileRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.appUserService.updateAppUserProfile(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateAppUserProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- updateAppUserPassword
     * @apiNote :- Api use to update profile password
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateAppUserPassword", method = RequestMethod.POST)
    public ResponseEntity<?> updateAppUserPassword(@RequestBody UpdateUserProfileRequest requestPayload) {
        try {
            return new ResponseEntity<>(this.appUserService.updateAppUserPassword(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateAppUserPassword ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- closeAppUserAccount
     * @apiNote :- Api use to update profile password
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/closeAppUserAccount", method = RequestMethod.POST)
    public ResponseEntity<?> closeAppUserAccount(@RequestBody UpdateUserProfileRequest requestPayload) {
        try {
            return new ResponseEntity<>(this.appUserService.closeAppUserAccount(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while closeAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- getSubAppUserAccount
     * @apiNote :- Api use to get sub appUser account
     * @param username
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/getSubAppUserAccount", method = RequestMethod.GET)
    public ResponseEntity<?> getSubAppUserAccount(@RequestParam String username) {
        try {
            return new ResponseEntity<>(this.appUserService.getSubAppUserAccount(username), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while getSubAppUserAccount ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

}

package com.barco.admin.controller;

import com.barco.admin.service.AppUserService;
import com.barco.admin.service.NotificationService;
import com.barco.admin.service.SettingApiService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.util.ProcessUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * Api use to perform crud operation
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/auth.json")
public class AuthRestApi {

    private Logger logger = LoggerFactory.getLogger(AuthRestApi.class);

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private SettingApiService settingApiService;
    @Autowired
    private NotificationService notificationService;

    /**
     * api-status :- done
     * @apiName :- signInAppUser
     * @apiNote :- Api use to sign In the appUser
     * @param httpServletRequest
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/signInAppUser", method = RequestMethod.POST)
    public ResponseEntity<?> signInAppUser(HttpServletRequest httpServletRequest) {
        try {
            String requestData = httpServletRequest.getReader().lines().collect(Collectors.joining());
            LoginRequest requestPayload = new Gson().fromJson(requestData, LoginRequest.class);
            requestPayload.setIpAddress(BarcoUtil.getRequestIP(httpServletRequest));
            return new ResponseEntity<>(this.appUserService.signInAppUser(requestPayload), HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            logger.error("An error occurred while signInAppUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR, "BadCredentials."), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while signInAppUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- forgotPassword
     * @apiNote :- Api use support to forgot password
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest requestPayload) {
        try {
            return new ResponseEntity<>(this.appUserService.forgotPassword(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while forgotPassword ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- resetPassword
     * @apiNote :- Api use support to forgot password
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest requestPayload) {
        try {
            return new ResponseEntity<>(this.appUserService.resetPassword(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while resetPassword ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- authClamByRefreshToken
     * @apiNote :- Api use to get refreshToken for appUser
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/authClamByRefreshToken", method = RequestMethod.POST)
    public ResponseEntity<?> authClamByRefreshToken(@RequestBody TokenRefreshRequest requestPayload) {
        try {
            return new ResponseEntity<>(this.appUserService.authClamByRefreshToken(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while authClamByRefreshToken ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- logoutAppUser
     * @apiNote :- Api use to delete refreshToken for appUser
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/logoutAppUser", method = RequestMethod.POST)
    public ResponseEntity<?> logoutAppUser(@RequestBody TokenRefreshRequest requestPayload) {
        try {
            return new ResponseEntity<>(this.appUserService.logoutAppUser(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while logoutAppUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- sendNotificationToSpecificUser
     * @apiNote :- Api use send notification to specific user
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/sendNotificationToSpecificUser", method = RequestMethod.POST)
    public ResponseEntity<?> sendNotificationToSpecificUser(@RequestBody NotificationRequest requestPayload) {
        try {
            this.notificationService.sendNotificationToSpecificUser(requestPayload);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while sendNotificationToSpecificUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                    "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }
}

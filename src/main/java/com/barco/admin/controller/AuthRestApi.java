package com.barco.admin.controller;

import com.barco.admin.service.AuthService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
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
@CrossOrigin(origins="*")
@RequestMapping(value = "/auth.json")
public class AuthRestApi {

    private Logger logger = LoggerFactory.getLogger(AuthRestApi.class);

    @Autowired
    private AuthService authService;

    /**
     * @apiName :- signInAppUser
     * @apiNote :- Api use to sign In the appUser
     * @param httpServletRequest
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value="/signInAppUser", method=RequestMethod.POST)
    public ResponseEntity<?> signInAppUser(HttpServletRequest httpServletRequest) {
        try {
            String requestData = httpServletRequest.getReader().lines().collect(Collectors.joining());
            LoginRequest requestPayload = new Gson().fromJson(requestData, LoginRequest.class);
            requestPayload.setIpAddress(BarcoUtil.getRequestIP(httpServletRequest));
            return new ResponseEntity<>(this.authService.signInAppUser(requestPayload), HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            logger.error("An error occurred while signInAppUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while signInAppUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- signupAppUser
     * @apiNote :- Api use support to forgot password
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value="/signupAppUser", method=RequestMethod.POST)
    public ResponseEntity<?> signupAppUser(HttpServletRequest httpServletRequest) {
        try {
            String requestData = httpServletRequest.getReader().lines().collect(Collectors.joining());
            SignupRequest payload = new Gson().fromJson(requestData, SignupRequest.class);
            payload.setIpAddress(BarcoUtil.getRequestIP(httpServletRequest));
            return new ResponseEntity<>(this.authService.signupAppUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while signupAppUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- forgotPassword
     * @apiNote :- Api use support to forgot password
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value="/forgotPassword", method=RequestMethod.POST)
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest payload) {
        try {
            return new ResponseEntity<>(this.authService.forgotPassword(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while forgotPassword ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- resetPassword
     * @apiNote :- Api use support to forgot password
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value="/resetPassword", method=RequestMethod.POST)
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest payload) {
        try {
            return new ResponseEntity<>(this.authService.resetPassword(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while resetPassword ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- authClamByRefreshToken
     * @apiNote :- Api use to get refreshToken for appUser
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value="/authClamByRefreshToken", method=RequestMethod.POST)
    public ResponseEntity<?> authClamByRefreshToken(@RequestBody TokenRefreshRequest payload) {
        try {
            return new ResponseEntity<>(this.authService.authClamByRefreshToken(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while authClamByRefreshToken ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- logoutAppUser
     * @apiNote :- Api use to delete refreshToken for appUser
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value="/logoutAppUser", method=RequestMethod.POST)
    public ResponseEntity<?> logoutAppUser(@RequestBody TokenRefreshRequest payload) {
        try {
            return new ResponseEntity<>(this.authService.logoutAppUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while logoutAppUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}

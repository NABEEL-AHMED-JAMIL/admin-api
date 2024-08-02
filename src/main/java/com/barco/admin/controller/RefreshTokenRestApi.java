package com.barco.admin.controller;

import com.barco.admin.service.RefreshTokenService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.TokenRefreshRequest;
import com.barco.model.dto.response.AppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Api use to perform crud operation
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping(value="/refreshToken.json")
public class RefreshTokenRestApi {

    private Logger logger = LoggerFactory.getLogger(RefreshTokenRestApi.class);

    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * @apiName :- fetchByAllRefreshToken
     * @apiNote :- Method use to fetch the data
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DEV')")
    @RequestMapping(value="/fetchByAllRefreshToken", method=RequestMethod.POST)
    public ResponseEntity<?> fetchByAllRefreshToken(@RequestBody TokenRefreshRequest payload) {
        try {
            return new ResponseEntity<>(this.refreshTokenService.fetchByAllRefreshToken(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchByAllRefreshToken ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteRefreshToken
     * @apiNote :- Api use to delete refresh token
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DEV')")
    @RequestMapping(value="/deleteRefreshToken", method=RequestMethod.POST)
    public ResponseEntity<?> deleteRefreshToken(@RequestBody TokenRefreshRequest payload) {
        try {
            return new ResponseEntity<>(this.refreshTokenService.deleteRefreshToken(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteRefreshToken ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllRefreshToken
     * @apiNote :- Api use to delete all refresh token
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DEV')")
    @RequestMapping(value="/deleteAllRefreshToken", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllRefreshToken(@RequestBody TokenRefreshRequest payload) {
        try {
            return new ResponseEntity<>(this.refreshTokenService.deleteAllRefreshToken(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllRefreshToken ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}

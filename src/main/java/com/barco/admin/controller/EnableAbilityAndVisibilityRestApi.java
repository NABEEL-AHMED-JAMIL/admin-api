package com.barco.admin.controller;

import com.barco.admin.service.EnableAbilityAndVisibilityService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.EnableAndVisibilityConfigRequest;
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
@RequestMapping(value = "/enbVisibilitySetting.json")
public class EnableAbilityAndVisibilityRestApi {

    private Logger logger = LoggerFactory.getLogger(EnableAbilityAndVisibilityRestApi.class);

    @Autowired
    private EnableAbilityAndVisibilityService enableAbilityAndVisibilityService;

    /**
     * @apiName :- addEnableAbility
     * @apiNote :- Method use add the enableAbility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/addEnableAndVisibilityConfig", method= RequestMethod.POST)
    public ResponseEntity<?> addEnableAndVisibilityConfig(@RequestBody EnableAndVisibilityConfigRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.addEnableAndVisibilityConfig(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addEnableAndVisibilityConfig ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editEnableAndVisibilityConfig
     * @apiNote :- Method use edit the enableAbility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/editEnableAndVisibilityConfig", method= RequestMethod.POST)
    public ResponseEntity<?> editEnableAndVisibilityConfig(@RequestBody EnableAndVisibilityConfigRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.editEnableAndVisibilityConfig(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editEnableAndVisibilityConfig ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllEnableAndVisibilityConfig
     * @apiNote :- Method use fetch all the enableAbility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchAllEnableAndVisibilityConfig", method= RequestMethod.POST)
    public ResponseEntity<?> fetchAllEnableAndVisibilityConfig(@RequestBody EnableAndVisibilityConfigRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.fetchAllEnableAndVisibilityConfig(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllEnableAndVisibilityConfig ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchEnableAndVisibilityConfigById
     * @apiNote :- Method use fetch the enableAbility setting by id
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchEnableAndVisibilityConfigById", method= RequestMethod.POST)
    public ResponseEntity<?> fetchEnableAndVisibilityConfigById(@RequestBody EnableAndVisibilityConfigRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.fetchEnableAndVisibilityConfigById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchEnableAndVisibilityConfigById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteEnableAndVisibilityConfigById
     * @apiNote :- Method use delete the enableAbility setting by id
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/deleteEnableAndVisibilityConfigById", method= RequestMethod.POST)
    public ResponseEntity<?> deleteEnableAndVisibilityConfigById(@RequestBody EnableAndVisibilityConfigRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.deleteEnableAndVisibilityConfigById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteEnableAndVisibilityConfigById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllEnableAndVisibilityConfig
     * @apiNote :- Method use delete all the enableAbility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/deleteAllEnableAndVisibilityConfig", method= RequestMethod.POST)
    public ResponseEntity<?> deleteAllEnableAndVisibilityConfig(@RequestBody EnableAndVisibilityConfigRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.deleteAllEnableAndVisibilityConfig(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllEnableAndVisibilityConfig ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


}

package com.barco.admin.controller;

import com.barco.admin.service.EnableAbilityAndVisibilityService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.EnableAbilityRequest;
import com.barco.model.dto.request.VisibilityRequest;
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
    @RequestMapping(value="/addEnableAbility", method= RequestMethod.POST)
    public ResponseEntity<?> addEnableAbility(@RequestBody EnableAbilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.addEnableAbility(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addEnableAbility ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editEnableAbility
     * @apiNote :- Method use edit the enableAbility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/editEnableAbility", method= RequestMethod.POST)
    public ResponseEntity<?> editEnableAbility(@RequestBody EnableAbilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.editEnableAbility(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editEnableAbility ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllEnableAbility
     * @apiNote :- Method use fetch all the enableAbility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/editEnableAbility", method= RequestMethod.POST)
    public ResponseEntity<?> fetchAllEnableAbility(@RequestBody EnableAbilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.fetchAllEnableAbility(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllEnableAbility ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchEnableAbilityById
     * @apiNote :- Method use fetch the enableAbility setting by id
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchEnableAbilityById", method= RequestMethod.POST)
    public ResponseEntity<?> fetchEnableAbilityById(@RequestBody EnableAbilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.fetchEnableAbilityById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchEnableAbilityById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteEnableAbilityById
     * @apiNote :- Method use delete the enableAbility setting by id
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/deleteEnableAbilityById", method= RequestMethod.POST)
    public ResponseEntity<?> deleteEnableAbilityById(@RequestBody EnableAbilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.deleteEnableAbilityById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteEnableAbilityById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllEnableAbility
     * @apiNote :- Method use delete all the enableAbility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/deleteAllEnableAbility", method= RequestMethod.POST)
    public ResponseEntity<?> deleteAllEnableAbility(@RequestBody EnableAbilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.deleteAllEnableAbility(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllEnableAbility ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- addVisibility
     * @apiNote :- Method use add visibility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/addVisibility", method= RequestMethod.POST)
    public ResponseEntity<?> addVisibility(@RequestBody VisibilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.addVisibility(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addVisibility ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editVisibility
     * @apiNote :- Method use edit visibility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/editVisibility", method= RequestMethod.POST)
    public ResponseEntity<?> editVisibility(@RequestBody VisibilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.editVisibility(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editVisibility ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllVisibility
     * @apiNote :- Method use fetch all visibility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchAllVisibility", method= RequestMethod.POST)
    public ResponseEntity<?> fetchAllVisibility(@RequestBody VisibilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.fetchAllVisibility(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllVisibility ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchVisibilityById
     * @apiNote :- Method use fetch visibility setting by id
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/fetchVisibilityById", method= RequestMethod.POST)
    public ResponseEntity<?> fetchVisibilityById(@RequestBody VisibilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.fetchVisibilityById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchVisibilityById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteVisibilityById
     * @apiNote :- Method use delete visibility setting by id
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/deleteVisibilityById", method= RequestMethod.POST)
    public ResponseEntity<?> deleteVisibilityById(@RequestBody VisibilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.deleteVisibilityById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteVisibilityById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllVisibility
     * @apiNote :- Method use delete all visibility setting
     * @param payload
     * @return ResponseEntity
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value="/deleteAllVisibility", method= RequestMethod.POST)
    public ResponseEntity<?> deleteAllVisibility(@RequestBody VisibilityRequest payload) {
        try {
            return new ResponseEntity<>(this.enableAbilityAndVisibilityService.deleteAllVisibility(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllVisibility ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

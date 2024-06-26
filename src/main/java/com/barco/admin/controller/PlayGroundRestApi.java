package com.barco.admin.controller;

import com.barco.admin.service.PlayGroundService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.PlayGroundRequest;
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
@RequestMapping(value="/playGround.json")
public class PlayGroundRestApi {

    private Logger logger = LoggerFactory.getLogger(PlayGroundRestApi.class);

    @Autowired
    private PlayGroundService playGroundService;

    /**
     * @apiName :- fetchAllFormForPlayGround
     * @apiNote :- Api use to add form in play ground
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllFormForPlayGround", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllFormForPlayGround(@RequestBody PlayGroundRequest payload) {
        try {
            return new ResponseEntity<>(this.playGroundService.fetchAllFormForPlayGround(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllFormForPlayGround ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchFormForPlayGroundByFormId
     * @apiNote :- Api use to add form in play ground
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchFormForPlayGroundByFormId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchFormForPlayGroundByFormId(@RequestBody PlayGroundRequest payload) {
        try {
            return new ResponseEntity<>(this.playGroundService.fetchFormForPlayGroundByFormId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchFormForPlayGroundByFormId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

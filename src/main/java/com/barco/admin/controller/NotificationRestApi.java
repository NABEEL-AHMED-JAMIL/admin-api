package com.barco.admin.controller;

import com.barco.admin.service.NotificationService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.NotificationRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/notification.json")
@Api(value = "Refresh Token Rest Api",
    description = "Refresh Token Service : Service related to the [Session & Token Regenerate] for user. ")
public class NotificationRestApi {

    private Logger logger = LoggerFactory.getLogger(NotificationRestApi.class);

    @Autowired
    private NotificationService notificationService;

    /**
     * @apiName :- updateNotification
     * @apiNote :- Api use update notification for specific user
     * @param payload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateNotification", method = RequestMethod.POST)
    @ApiOperation(value = "Api use to update notification by user.", response = ResponseEntity.class)
    public ResponseEntity<?> updateNotification(@RequestBody NotificationRequest payload) {
        try {
            return new ResponseEntity<>(this.notificationService.updateNotification(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateNotification ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllNotification
     * @apiNote :- Api use to fetch all notification for specific user
     * @param username
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/fetchAllNotification", method = RequestMethod.GET)
    @ApiOperation(value = "Api use to fetch all notification by user.", response = ResponseEntity.class)
    public ResponseEntity<?> fetchAllNotification(@RequestParam String username) {
        try {
            return new ResponseEntity<>(this.notificationService.fetchAllNotification(username), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllNotification ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

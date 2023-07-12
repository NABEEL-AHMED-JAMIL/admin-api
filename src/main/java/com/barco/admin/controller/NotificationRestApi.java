package com.barco.admin.controller;

import com.barco.admin.service.NotificationService;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.NotificationRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.util.ProcessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Api use to perform crud operation
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/notification.json")
public class NotificationRestApi {

    private Logger logger = LoggerFactory.getLogger(NotificationRestApi.class);

    @Autowired
    private NotificationService notificationService;


    /**
     * api-status :- done
     * @apiName :- updateNotification
     * @apiNote :- Api use update notification for specific user
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateNotification", method = RequestMethod.POST)
    public ResponseEntity<?> updateNotification(@RequestBody NotificationRequest requestPayload) {
        try {
            return new ResponseEntity<>(this.notificationService.updateNotification(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateNotification ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- fetchAllNotification
     * @apiNote :- Api use to fetch all notification for specific user
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/fetchAllNotification", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllNotification(@RequestBody NotificationRequest requestPayload) {
        try {
            return new ResponseEntity<>(this.notificationService.fetchAllNotification(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllNotification ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }
}

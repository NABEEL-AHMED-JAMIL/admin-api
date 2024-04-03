package com.barco.admin.controller;

import com.barco.model.dto.request.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/notification.json")
public class NotificationRestApi {

    private Logger logger = LoggerFactory.getLogger(NotificationRestApi.class);

//    @Autowired
//    private GlobalProperties globalProperties;
//    @Autowired
//    private NotificationService notificationService;

    /**
     * Process message for register the session
     * @param notificationRequest
     * @param headerAccessor
     * */
    @MessageMapping("/register")
    public void register(@Payload NotificationRequest notificationRequest, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        logger.info("register" + notificationRequest);

    }

    /**
     * Process message for unregister the session
     * @param notificationRequest
     * @param headerAccessor
     * */
    @MessageMapping("/unregister")
    public void unregister(@Payload NotificationRequest notificationRequest, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        logger.info("unregister" + notificationRequest);
    }

    /**
     * api-status :- done
     * @apiName :- updateNotification
     * @apiNote :- Api use update notification for specific user
     * @param requestPayload
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/updateNotification", method = RequestMethod.POST)
    public ResponseEntity<?> updateNotification(@RequestBody NotificationRequest requestPayload) {
        return null;
    }

    /**
     * @apiName :- fetchAllNotification
     * @apiNote :- Api use to fetch all notification for specific user
     * @param username
     * @return ResponseEntity<?>
     * */
    @RequestMapping(value = "/fetchAllNotification", method = RequestMethod.GET)
    public ResponseEntity<?> fetchAllNotification(@RequestParam String username) {
        return null;
    }
}

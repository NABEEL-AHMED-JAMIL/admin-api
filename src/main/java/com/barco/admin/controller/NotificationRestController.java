package com.barco.admin.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/notification.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-Notification := Barco-Notification EndPoint" })
public class NotificationRestController {

    private Logger logger = LoggerFactory.getLogger(NotificationRestController.class);

}


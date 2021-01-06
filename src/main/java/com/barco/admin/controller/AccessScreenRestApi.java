package com.barco.admin.controller;

import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/access-screen.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-Access-Screen := Barco-Access-Screen EndPoint" })
public class AccessScreenRestApi {

    public Logger logger = LogManager.getLogger(AccessScreenRestApi.class);

    // create screen
    // change status for screen
    // update screen
    // find all screen by authority
    // crate-form for screen
    // update-form for screen
    // change status for form screen
    // find all form by screen
}

package com.barco.admin.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/key.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-Key := Barco-Key EndPoint" })
public class KeyRestController {

    private Logger logger = LoggerFactory.getLogger(KeyRestController.class);
}

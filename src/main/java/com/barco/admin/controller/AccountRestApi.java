package com.barco.admin.controller;

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
@RequestMapping(value = "/accountService.json", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountRestApi {

    public Logger logger = LogManager.getLogger(AccountRestApi.class);
}

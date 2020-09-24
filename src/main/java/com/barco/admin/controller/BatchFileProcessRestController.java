package com.barco.admin.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/batch.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Batch-File-Process := Batch File Process Rest-EndPoint" })
public class BatchFileProcessRestController {

    private Logger logger = LoggerFactory.getLogger(BatchFileProcessRestController.class);

}

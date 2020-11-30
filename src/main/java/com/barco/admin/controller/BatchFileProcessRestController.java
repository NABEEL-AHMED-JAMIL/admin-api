package com.barco.admin.controller;

import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.JobDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/batch.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Batch-File-Process := Batch File Process Rest-EndPoint" })
public class BatchFileProcessRestController {

    private Logger logger = LoggerFactory.getLogger(BatchFileProcessRestController.class);

}

package com.barco.admin.controller;

import com.barco.common.utility.ApplicationConstants;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/test.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-Test := Barco-Test EndPoint" })
public class TestController {

    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/test1", method = RequestMethod.POST)
    @ApiOperation(value = "Test 1", notes = "Test secure api.")
    public @ResponseBody ResponseDTO test1PostMethod() {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }


    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/test2", method = RequestMethod.GET)
    @ApiOperation(value = "Test 2", notes = "Test secure api.")
    public @ResponseBody ResponseDTO test2PostMethod() {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }
}

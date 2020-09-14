package com.barco.admin.controller;

import com.barco.common.utility.ApplicationConstants;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/job.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-Job := Barco-Job EndPoint" })
public class JobRestController {

    private Logger logger = LoggerFactory.getLogger(JobRestController.class);

    // create job
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/createJob", method = RequestMethod.POST)
    @ApiOperation(value = "Create Job", notes = "Create job for run.")
    public @ResponseBody ResponseDTO createJob() {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }

    // get job by id
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/getJob", method = RequestMethod.GET)
    @ApiOperation(value = "Get Job", notes = "Get job by Id.")
    public @ResponseBody ResponseDTO getJob(@RequestParam(name = "id") Long jobId) {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }

    // change status task by id
    // Inactive(0), Active(1), Delete(3)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ApiOperation(value = "Change Status", notes = "Change Status by id for job.")
    public @ResponseBody ResponseDTO statusChange(@RequestParam(name = "id") Long jobId,
        @RequestParam(name = "status") Status jobStatus) {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }

    // fetch all job
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/fetchAllJob", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch All Job", notes = "Fetch all job with pagination.")
    public @ResponseBody ResponseDTO fetchAllJob() {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }

    // run job
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/runJob", method = RequestMethod.POST)
    @ApiOperation(value = "Run Job", notes = "Run job.")
    public @ResponseBody ResponseDTO runJob() {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }

    // skip next occurrence
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/skipNextOccurrence", method = RequestMethod.POST)
    @ApiOperation(value = "Skip Next Occurrence", notes = "Skip Next Occurrence.")
    public @ResponseBody ResponseDTO skipNextOccurrence() {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }

}

package com.barco.admin.controller;

import com.barco.admin.service.impl.JobServiceImpl;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.JobDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.pojo.pagination.PaginationDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/job.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-Job := Barco-Job EndPoint" })
public class JobRestController {

    private Logger logger = LoggerFactory.getLogger(JobRestController.class);

    @Autowired
    private JobServiceImpl jobService;

    // create job
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/createJob", method = RequestMethod.POST)
    @ApiOperation(value = "Create Job", notes = "Create job for run.")
    public @ResponseBody ResponseDTO createJob(@RequestBody JobDto jobDto) {
        ResponseDTO response = null;
        try {
            logger.info("Request for createJob ");
            response = this.jobService.createJob(jobDto);
        } catch (Exception ex) {
            logger.info("Error during createTask " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // get job by id
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/getJobById", method = RequestMethod.GET)
    @ApiOperation(value = "Get Job", notes = "Get job by Id.")
    public @ResponseBody ResponseDTO getJobById(@RequestParam(name = "id") Long jobId, @RequestParam(name = "appUserId") Long appUserId) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for getJobById Job Id %d And App User Id %d ", jobId, appUserId));
            response = this.jobService.getJobById(jobId, appUserId);
        } catch (Exception ex) {
            logger.info("Error during getJobById " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // change status task by id
    // Inactive(0), Active(1), Delete(3)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ApiOperation(value = "Change Status", notes = "Change Status by id for job.")
    public @ResponseBody ResponseDTO statusChange(@RequestParam(name = "id") Long jobId,
        @RequestParam(name = "status") Status jobStatus) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for statusChange Job Id %d And Status %s ", jobId, jobStatus));
            response = this.jobService.statusChange(jobId, jobStatus);
        } catch (Exception ex) {
            logger.info("Error during statusChange " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // fetch all job
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/findAllJobByAppUserIdInPagination", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch All Job", notes = "Fetch all job with pagination.")
    public @ResponseBody ResponseDTO findAllJobByAppUserIdInPagination(@PathVariable Long appUserId, PaginationDetail paginationDetail) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for findAllJobByAppUserIdInPagination with AppUserId %d and Pagination Detail %s",
                    appUserId, paginationDetail));
            response = this.jobService.findAllJobByAppUserIdInPagination(appUserId, paginationDetail);
        } catch (Exception ex) {
            logger.info("Error during findAllJobByAppUserIdInPagination " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // run job
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/runJob", method = RequestMethod.POST)
    @ApiOperation(value = "Run Job", notes = "Run job.")
    public @ResponseBody ResponseDTO runJob(@RequestParam(name = "id") Long jobId, @RequestParam(name = "appUserId") Long appUserId) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for runJob with job Id %d And App User Id %s ", jobId, appUserId));
            response = this.jobService.runJob(jobId, appUserId);
        } catch (Exception ex) {
            logger.info("Error during runJob " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // skip next occurrence
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/skipNextOccurrence", method = RequestMethod.POST)
    @ApiOperation(value = "Skip Next Occurrence", notes = "Skip Next Occurrence.")
    public @ResponseBody ResponseDTO skipNextOccurrence(@RequestParam(name = "id") Long jobId, @RequestParam(name = "appUserId") Long appUserId) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for skipNextOccurrence with job Id %d And App User Id %s ", jobId, appUserId));
            response = this.jobService.skipNextOccurrence(jobId, appUserId);
        } catch (Exception ex) {
            logger.info("Error during skipNextOccurrence " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

}
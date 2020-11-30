package com.barco.admin.controller;

import com.barco.admin.service.impl.JobServiceImpl;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.JobDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.searchspec.PaginationDetail;
import com.barco.model.util.PaggingUtil;
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
    public @ResponseBody ResponseDTO statusChange(@RequestParam(name = "id") Long jobId, @RequestParam(name = "appUserId") Long appUserId,
        @RequestParam(name = "status") Status jobStatus) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for statusChange Job Id %d And Status %s ", jobId, jobStatus));
            response = this.jobService.statusChange(jobId, appUserId, jobStatus);
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
    public @ResponseBody ResponseDTO findAllJobByAppUserIdInPagination(@RequestParam(value = "appUserId", required = false) Long appUserId,
       @RequestParam(value = "page", required = false) Long page, @RequestParam(value = "limit", required = false) Long limit,
       @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate,
       @RequestParam(value = "columnName", required = false) String columnName, @RequestParam(value = "order", required = false) String order,
       @RequestBody SearchTextDto searchTextDto) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for findAllJobByAppUserIdInPagination with AppUserId %d ", appUserId));
            response = this.jobService.findAllJobByAppUserIdInPagination(PaggingUtil.ApplyPagging(page, limit, order, columnName),
                    appUserId ,searchTextDto, startDate, endDate);
        } catch (Exception ex) {
            logger.info("Error during findAllJobByAppUserIdInPagination " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO(ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // addJob To Queue
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/addJobToQueue", method = RequestMethod.POST)
    @ApiOperation(value = "Run Job", notes = "Run job.")
    public @ResponseBody ResponseDTO addJobToQueue(@RequestParam(name = "id") Long jobId, @RequestParam(name = "appUserId") Long appUserId) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for addJobToQueue with job Id %d And App User Id %s ", jobId, appUserId));
            response = this.jobService.addJobToQueue(jobId, appUserId);
        } catch (Exception ex) {
            logger.info("Error during addJobToQueue " + ExceptionUtil.getRootCause(ex));
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

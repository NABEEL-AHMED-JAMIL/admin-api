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
@RequestMapping(value = "/task.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-Task := Barco-Task EndPoint" })
public class TaskRestController {

    private Logger logger = LoggerFactory.getLogger(TaskRestController.class);

    // create task
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/createTask", method = RequestMethod.POST)
    @ApiOperation(value = "Create Task", notes = "Task is use in the job.")
    public @ResponseBody ResponseDTO createTask() {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }

    // get task by id
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/getTask", method = RequestMethod.POST)
    @ApiOperation(value = "Get Task", notes = "Get Task by Id.")
    public @ResponseBody ResponseDTO getTask() {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }

    // change status task by id
    // Inactive(0), Active(1), Delete(3),
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ApiOperation(value = "Change Status", notes = "Change Status by id for job.")
    public @ResponseBody ResponseDTO statusChange() {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }

    // fetch all task
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/fetchAllTask", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch All Task", notes = "Fetch all task with pagination.")
    public @ResponseBody ResponseDTO fetchAllTask() {
        try {
            return new ResponseDTO(ApiCode.SUCCESS, "Pakistan Zindabad");
        } catch (Exception ex) {
            return new ResponseDTO(ApiCode.ERROR, ApplicationConstants.INVALID_CREDENTIAL_MSG);
        }
    }

}

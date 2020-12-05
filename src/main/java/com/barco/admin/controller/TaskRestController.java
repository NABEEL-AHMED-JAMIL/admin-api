package com.barco.admin.controller;

import com.barco.admin.service.impl.TaskServiceImpl;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.dto.TaskDto;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.util.PaggingUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/task.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Barco-Task := Barco-Task EndPoint" })
public class TaskRestController {

    private Logger logger = LoggerFactory.getLogger(TaskRestController.class);

    @Autowired
    private TaskServiceImpl taskService;

    // create task
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/createTask", method = RequestMethod.POST)
    @ApiOperation(value = "Create Task", notes = "Task is use in the job.")
    public @ResponseBody ResponseDTO createTask(@RequestBody TaskDto taskDto) {
        ResponseDTO response = null;
        try {
            logger.info("Request for createTask  " + taskDto);
            response = this.taskService.createTask(taskDto);
        } catch (Exception ex) {
            logger.info("Error during createTask " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // get task by id
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/getTaskById", method = RequestMethod.GET)
    @ApiOperation(value = "Get Task", notes = "Get Task by Id.")
    public @ResponseBody ResponseDTO getTaskById(@RequestParam(name = "id") Long taskId, @RequestParam(name = "appUserId") Long appUserId) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for getTaskById Task Id %d And App User Id %d ", taskId, appUserId));
            response = this.taskService.getTaskById(taskId, appUserId);
        } catch (Exception ex) {
            logger.info("Error during getTaskById " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    // change status task by id
    // Inactive(0), Active(1), Delete(3),
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ApiOperation(value = "Change Status", notes = "Change Status by id for job.")
    public @ResponseBody ResponseDTO statusChange(@RequestParam(name = "id") Long taskId, @RequestParam(name = "appUserId") Long appUserId,
        @RequestParam(name = "status") Status taskStatus) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for statusChange Task Id %d And Status %s ", taskId, taskStatus));
            response = this.taskService.statusChange(taskId, appUserId, taskStatus);
        } catch (Exception ex) {
            logger.info("Error during statusChange " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/findAllTaskByAppUserIdInPagination", method = RequestMethod.POST)
    @ApiOperation(value = "Get Users Api", notes = "Get list of all Users Linked to current user.")
    public @ResponseBody ResponseDTO findAllTaskByAppUserIdInPagination(@RequestParam(value = "appUserId", required = false) Long appUserId,
       @RequestParam(value = "page", required = false) Long page, @RequestParam(value = "limit", required = false) Long limit,
       @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate,
       @RequestParam(value = "columnName", required = false) String columnName, @RequestParam(value = "order", required = false) String order,
       @RequestBody SearchTextDto searchTextDto) {
        ResponseDTO response = null;
        try {
            logger.info("Request for get findAllTaskByAppUserIdInPagination " + appUserId);
            response = this.taskService.findAllTaskByAppUserIdInPagination(PaggingUtil.ApplyPagging(page, limit, order, columnName),
                    appUserId ,searchTextDto, startDate, endDate);
        } catch (Exception ex) {
            logger.info("Error during findAllTaskByAppUserIdInPagination " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

}

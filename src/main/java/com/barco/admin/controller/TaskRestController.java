package com.barco.admin.controller;

import com.barco.admin.service.impl.TaskServiceImpl;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.TaskDto;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.searchspec.PaginationDetail;
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

    // fetch all task
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/findAllTaskByAppUserIdInPagination", method = RequestMethod.POST)
    @ApiOperation(value = "Fetch All Task", notes = "Fetch all task with app user id in pagination.")
    public @ResponseBody ResponseDTO findAllTaskByAppUserIdInPagination(@PathVariable Long appUserId, PaginationDetail paginationDetail) {
        ResponseDTO response = null;
        try {
            logger.info(String.format("Request for findAllTaskByAppUserIdInPagination with AppUserId %d and Pagination Detail %s", appUserId, paginationDetail));
            response = this.taskService.findAllTaskByAppUserIdInPagination(appUserId, paginationDetail);
        } catch (Exception ex) {
            logger.info("Error during findAllTaskByAppUserIdInPagination " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }
}

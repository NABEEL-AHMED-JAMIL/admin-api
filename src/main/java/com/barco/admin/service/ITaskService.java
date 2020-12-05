package com.barco.admin.service;

import com.barco.model.dto.PaggingDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.dto.TaskDto;
import com.barco.model.enums.Status;

/**
 * @author Nabeel Ahmed
 */
public interface ITaskService {

    // create task
    public ResponseDTO createTask(TaskDto taskDto) throws Exception;

    // get task by id
    public ResponseDTO getTaskById(Long taskId, Long createBy) throws Exception;

    // change status task by id
    // Inactive(0), Active(1), Delete(3),
    public ResponseDTO statusChange(Long taskId, Long appUserId, Status taskStatus) throws Exception;

    public ResponseDTO findAllTaskByAppUserIdInPagination(PaggingDto pagging, Long adminId, SearchTextDto searchTextDto,
           String startDate, String endDate) throws Exception;
}

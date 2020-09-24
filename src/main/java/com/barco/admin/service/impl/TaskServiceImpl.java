package com.barco.admin.service.impl;

import com.barco.admin.service.ITaskService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.TaskDto;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.StorageDetail;
import com.barco.model.pojo.Task;
import com.barco.model.pojo.pagination.PaginationDetail;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.StorageDetailRepository;
import com.barco.model.repository.TaskRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
@Transactional
@Scope("prototype")
public class TaskServiceImpl implements ITaskService {

    private Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private StorageDetailRepository storageDetailRepository;

    @Override
    public ResponseDTO createTask(TaskDto taskDto) throws Exception {
        if (StringUtils.isEmpty(taskDto.getTaskName())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_NAME_MISSING);
        } else if (this.taskRepository.findByTaskNameAndStatus(taskDto.getTaskName(), Status.Active).isPresent()
                && taskDto.getId() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_ALREADY_EXIST);
        } else if (StringUtils.isEmpty(taskDto.getClassName())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.CLASS_NAME_MISSING);
        } else if (taskDto.getTaskDetailJson() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_JSON_MISSING);
        }
        // app user find
        Optional<AppUser> appUser = this.appUserRepository.findByIdAndStatus(taskDto.getCreatedBy(), Status.Active);
        if (!appUser.isPresent()) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.USER_NOT_FOUND);
        }
        // task create and update
        Task task = null;
        if (taskDto.getId() != null) {
            task = this.taskRepository.findByIdAndStatus(taskDto.getId(), Status.Active);
            if (task != null) {
                task.setModifiedBy(appUser.get().getId());
            } else {
                return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_NOT_FOUND);
            }
        } else {
            task = new Task();
            task.setCreatedBy(appUser.get().getId());
            task.setStatus(Status.Active);
        }
        task.setTaskName(taskDto.getTaskName());
        task.setClassName(taskDto.getClassName());
        task.setTaskDetailJson(taskDto.getTaskDetailJson());
        if (taskDto.getStorageDetail() != null) {
            if(taskDto.getStorageDetail().getId() != null) {
                Optional<StorageDetail> storageDetail = this.storageDetailRepository.findById(taskDto.getStorageDetail().getId());
                if (storageDetail.isPresent()) {
                    task.setStorageDetail(storageDetail.get());
                } else {
                    return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_NOT_FOUND);
                }
            }
        } else {
            task.setStorageDetail(null);
        }
        // save the detail and send back the info
        task = this.taskRepository.saveAndFlush(task);
        taskDto.setId(task.getId());
        return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, taskDto);
    }

    @Override
    public ResponseDTO getTaskById(Long taskId, Long appUserId) throws Exception  {
        Optional<Task> task = this.taskRepository.findByIdAndCreatedByAndStatus(taskId, appUserId, Status.Active);
        if (task.isPresent()) {
            return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, task);
        }
        return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG);
    }

    @Override
    public ResponseDTO statusChange(Long taskId, Status taskStatus) throws Exception  {
        return null;
    }

    @Override
    public ResponseDTO findAllTaskByAppUserIdInPagination(Long appUserId, PaginationDetail paginationDetail) throws Exception  {
        return null;
    }
}

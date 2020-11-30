package com.barco.admin.service.impl;

import com.barco.admin.service.ITaskService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.model.dto.PaggingDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.dto.TaskDto;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.StorageDetail;
import com.barco.model.pojo.Task;
import com.barco.model.searchspec.PaginationDetail;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.JobRepository;
import com.barco.model.repository.StorageDetailRepository;
import com.barco.model.repository.TaskRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
@Transactional
@Scope("prototype")
public class TaskServiceImpl implements ITaskService {

    private Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private JobRepository jobRepository;
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
        } else if (this.taskRepository.findByTaskNameAndCreatedByAndStatus(taskDto.getTaskName(), taskDto.getCreatedBy(),
                Status.Active).isPresent() && taskDto.getId() == null) {
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
        task.setTaskDetailJson(taskDto.getTaskDetailJson());
        // storage detail are optional
        if (taskDto.getStorageDetail() != null) {
            if(taskDto.getStorageDetail().getId() != null) {
                StorageDetail storageDetail = this.storageDetailRepository.findByIdAndStatus(
                        taskDto.getStorageDetail().getId(), Status.Active);
                if (storageDetail != null) {
                    task.setStorageDetail(storageDetail);
                } else {
                    return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_NOT_FOUND);
                }
            }
        } else {
            // handle the update case for un-assign the storage
            task.setStorageDetail(null);
        }
        // save the detail and send back the info
        this.taskRepository.saveAndFlush(task);
        taskDto.setId(task.getId());
        return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, taskDto);
    }

    @Override
    public ResponseDTO getTaskById(Long taskId, Long appUserId) throws Exception  {
        Optional<Task> task = this.taskRepository.findByIdAndCreatedByAndStatus(taskId, appUserId, Status.Active);
        if (task.isPresent()) {
            return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, task.get());
        }
        return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG);
    }

    @Override
    public ResponseDTO statusChange(Long taskId, Long appUserId, Status taskStatus) throws Exception  {
        // get the storage attache with task
        Optional<Task> taskDetail = this.taskRepository.findByIdAndStatusNot(taskId, Status.Delete);
        if (taskDetail.isPresent() && taskStatus.equals(Status.Active)) {
            // active storage if storage disable
            taskDetail.get().setStatus(taskStatus);
            taskDetail.get().setModifiedBy(appUserId);
            return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
        } else if (taskDetail.isPresent() && (taskStatus.equals(Status.Delete) || taskStatus.equals(Status.Inactive))) {
            Long storageAttacheCount = this.jobRepository.countByTaskId(taskId);
            if (storageAttacheCount > 0) {
                return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.Task_ATTACHE_WITH_JOB);
            } else {
                taskDetail.get().setStatus(taskStatus);
                taskDetail.get().setModifiedBy(appUserId);
                return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
            }
        }
        return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG);
    }

    @Override
    public ResponseDTO findAllTaskByAppUserIdInPagination(PaggingDto pagging, Long adminId, SearchTextDto searchTextDto, String startDate, String endDate) {
        return null;
    }
}

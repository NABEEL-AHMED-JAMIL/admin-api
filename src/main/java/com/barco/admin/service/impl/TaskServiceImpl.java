package com.barco.admin.service.impl;

import com.barco.admin.repository.AccessServiceRepository;
import com.barco.admin.service.ITaskService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.*;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.StorageDetail;
import com.barco.model.pojo.Task;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.JobRepository;
import com.barco.model.repository.StorageDetailRepository;
import com.barco.model.repository.TaskRepository;
import com.barco.model.service.QueryServices;
import com.barco.model.util.PagingUtil;
import com.barco.model.util.QueryUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Nabeel Ahmed
 */
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
    @Autowired
    private AccessServiceRepository accessServiceRepository;
    @Autowired
    private QueryServices queryServices;
    @Autowired
    private QueryUtil queryUtil;

    @Override
    public ResponseDTO createTask(TaskDto taskDto) throws Exception {
        // app user find
        Optional<AppUser> appUser = this.appUserRepository.findByIdAndStatus(taskDto.getCreatedBy(), Status.Active);
        if (!appUser.isPresent()) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.USER_NOT_FOUND);
        } else if (StringUtils.isEmpty(taskDto.getTaskName())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_NAME_MISSING);
        } else if (this.taskRepository.findByTaskNameAndCreatedByAndStatus(taskDto.getTaskName(), taskDto.getCreatedBy(),
                Status.Active).isPresent() && BarcoUtil.isNull(taskDto.getId())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_ALREADY_EXIST);
        } else if (BarcoUtil.isNull(taskDto.getAccessService()) || !this.accessServiceRepository.findByIdAndUserAccess(taskDto.getId(),
                taskDto.getCreatedBy()).isPresent()) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.SERVICE_NAME_MISSING);
        } else if (BarcoUtil.isNull(taskDto.getTaskDetailJson())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_JSON_MISSING);
        }
        // task create and update
        Task task = null;
        if (!BarcoUtil.isNull(taskDto.getId())) {
            task = this.taskRepository.findByIdAndStatus(taskDto.getId(), Status.Active);
            if (!BarcoUtil.isNull(task)) {
                task.setModifiedBy(appUser.get().getId());
            } else {
                return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_NOT_FOUND);
            }
        } else {
            task = new Task();
            task.setCreatedBy(appUser.get().getId());
            task.setStatus(Status.Active);
        }
        // access service only those which have user access
        task.setAccessService(this.accessServiceRepository.findByIdAndUserAccess(taskDto.getId(), taskDto.getCreatedBy()).get());
        task.setTaskName(taskDto.getTaskName());
        task.setTaskDetailJson(taskDto.getTaskDetailJson());
        // storage detail are optional
        if (!BarcoUtil.isNull(taskDto.getStorageDetail())) {
            if(!BarcoUtil.isNull(taskDto.getStorageDetail().getId())) {
                StorageDetail storageDetail = this.storageDetailRepository.findByIdAndStatus(taskDto.getStorageDetail().getId(),
                        Status.Active);
                if (!BarcoUtil.isNull(storageDetail)) {
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
        Optional<Task> taskDetail = this.taskRepository.findByIdAndCreatedByAndStatusNot(taskId, appUserId, Status.Delete);
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
    public ResponseDTO findAllTaskByAppUserIdInPagination(Pageable paging, Long adminId, SearchTextDto searchTextDto,
      String startDate, String endDate, String order, String columnName) throws Exception {
        ResponseDTO responseDTO = null;
        Object countQueryResult = this.queryServices.executeQueryForSingleResult(
                this.queryUtil.taskList(true, adminId, startDate, endDate, searchTextDto));
        if (!BarcoUtil.isNull(countQueryResult)) {
            /* fetch Record According to Pagination*/
            List<Object[]> result = this.queryServices.executeQuery(
                    this.queryUtil.taskList(false, adminId, startDate, endDate, searchTextDto), paging);
            if (!BarcoUtil.isNull(result) && result.size() > 0) {
                List<TaskDto> taskDtos = new ArrayList<>();
                for(Object[] obj : result) {
                    TaskDto taskDto = new TaskDto();
                    if (!BarcoUtil.isNull(obj[0])) {
                        taskDto.setId(new Long(obj[0].toString()));
                    }
                    if (!BarcoUtil.isNull(obj[1])) {
                        taskDto.setCreatedAt(Timestamp.valueOf(obj[1].toString()));
                    }
                    if (!BarcoUtil.isNull(obj[2])) {
                        taskDto.setTaskName(obj[2].toString());;
                    }
                    if (!BarcoUtil.isNull(obj[3])) {
                        taskDto.setStatus(Status.getStatus(new Long(obj[3].toString())));
                    }
                    // access-service detail
                    AccessServiceDto accessServiceDto = new AccessServiceDto();
                    if (!BarcoUtil.isNull(obj[4])) {
                        accessServiceDto.setId(new Long(obj[4].toString()));
                    }
                    if (!BarcoUtil.isNull(obj[5])) {
                        accessServiceDto.setServiceName(obj[5].toString());
                    }
                    taskDto.setAccessService(accessServiceDto);
                    StorageDetailDto storageDetailDto = new StorageDetailDto();
                    if (!BarcoUtil.isNull(obj[6])) {
                        storageDetailDto.setId(new Long(obj[6].toString()));
                    }
                    if (!BarcoUtil.isNull(obj[7])) {
                        storageDetailDto.setStorageKeyName(obj[7].toString());
                    }
                    taskDto.setStorageDetail(storageDetailDto);
                    // add task-into-list
                    taskDtos.add(taskDto);
                }
                responseDTO = new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, taskDtos,
                        PagingUtil.convertEntityToPagingDTO(Long.valueOf(countQueryResult.toString()),paging));
            }
        } else {
            responseDTO = new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, new ArrayList<>());
        }
        return responseDTO;
    }
}

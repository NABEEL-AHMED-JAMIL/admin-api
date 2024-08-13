package com.barco.admin.service.impl;

import com.barco.admin.service.SourceTaskService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.STTRequest;
import com.barco.model.dto.request.SourceTaskRequest;
import com.barco.model.dto.response.*;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.SourceTask;
import com.barco.model.pojo.SourceTaskType;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.SourceTaskRepository;
import com.barco.model.repository.SourceTaskTypeRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class SourceTaskServiceImpl implements SourceTaskService {

    private Logger logger = LoggerFactory.getLogger(SourceTaskServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private SourceTaskRepository sourceTaskRepository;
    @Autowired
    private SourceTaskTypeRepository sourceTaskTypeRepository;

    /***
     * Method use to add new source task
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addSourceTask(SourceTaskRequest payload) throws Exception {
        logger.info("Request addSourceTask :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getTaskName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getSourceTaskType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getSourceTaskType().getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_ID_MISSING);
        }
        Optional<SourceTaskType> sourceTaskType = this.sourceTaskTypeRepository.findByIdAndCreatedByAndStatusNot(
            payload.getSourceTaskType().getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!sourceTaskType.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_NOT_FOUND);
        }
        SourceTask sourceTask = new SourceTask();
        sourceTask.setTaskName(payload.getTaskName());
        sourceTask.setDescription(payload.getDescription());
        sourceTask.setSourceTaskType(sourceTaskType.get());
        sourceTask.setCreatedBy(appUser.get());
        sourceTask.setUpdatedBy(appUser.get());
        sourceTask.setStatus(APPLICATION_STATUS.ACTIVE);
        this.sourceTaskRepository.save(sourceTask);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, sourceTask.getId().toString()), payload);
    }

    /***
     * Method use to edit new source task
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse editSourceTask(SourceTaskRequest payload) throws Exception {
        logger.info("Request editSourceTask :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getTaskName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_DESCRIPTION_MISSING);
        }
        Optional<SourceTask> sourceTask = this.sourceTaskRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!sourceTask.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_NOT_FOUND);
        }
        sourceTask.get().setTaskName(payload.getTaskName());
        sourceTask.get().setDescription(payload.getDescription());
        sourceTask.get().setUpdatedBy(appUser.get());
        /**
         * Once the source task link with source task type
         * it's not allow to update until if the source task type null
         * **/
        if (BarcoUtil.isNull(sourceTask.get().getSourceTaskType())) {
            if (BarcoUtil.isNull(payload.getSourceTaskType())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_MISSING);
            } else if (BarcoUtil.isNull(payload.getSourceTaskType().getId())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_ID_MISSING);
            }
            Optional<SourceTaskType> sourceTaskType = this.sourceTaskTypeRepository.findByIdAndCreatedByAndStatusNot(
                payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
            if (!sourceTaskType.isPresent()) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_NOT_FOUND);
            }
            sourceTask.get().setSourceTaskType(sourceTaskType.get());
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            sourceTask.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        this.sourceTaskRepository.save(sourceTask.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()), payload);
    }

    /***
     * Method use to delete source task
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteSourceTask(SourceTaskRequest payload) throws Exception {
        logger.info("Request deleteSourceTask :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_ID_MISSING);
        }
        Optional<SourceTask> sourceTask = this.sourceTaskRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!sourceTask.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_NOT_FOUND);
        }
        sourceTask.get().setUpdatedBy(appUser.get());
        sourceTask.get().setStatus(APPLICATION_STATUS.DELETE);
        sourceTask.get().getSourceTaskData().stream()
            .filter(sourceTaskData -> !sourceTaskData.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(sourceTaskData -> {
                sourceTaskData.setStatus(sourceTask.get().getStatus());
                sourceTaskData.setUpdatedBy(appUser.get());
                return sourceTaskData;
            }).collect(Collectors.toList());
        this.sourceTaskRepository.save(sourceTask.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()), payload);
    }

    /***
     * Method use to delete all source task
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllSourceTask(SourceTaskRequest payload) throws Exception {
        logger.info("Request deleteAllSourceTask :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getIds())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        this.sourceTaskRepository.saveAll(
            this.sourceTaskRepository.findAllByIdIn(payload.getIds()).stream()
                .map(sourceTask -> {
                    sourceTask.setStatus(APPLICATION_STATUS.DELETE);
                    sourceTask.setUpdatedBy(appUser.get());
                    sourceTask.getSourceTaskData().stream()
                        .filter(sourceTaskData -> !sourceTaskData.getStatus().equals(APPLICATION_STATUS.DELETE))
                        .map(sourceTaskData -> {
                            sourceTaskData.setStatus(sourceTask.getStatus());
                            sourceTaskData.setUpdatedBy(appUser.get());
                            return sourceTaskData;
                        }).collect(Collectors.toList());
                    return sourceTask;
                }).collect(Collectors.toList())
        );
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_DELETED_ALL, payload);
    }

    /***
     * Method use to fetch all source task
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllSourceTask(SourceTaskRequest payload) throws Exception {
        logger.info("Request fetchAllSourceTask :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate().concat(BarcoUtil.START_DATE));
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate().concat(BarcoUtil.END_DATE));
        List<SourceTask> result = this.sourceTaskRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
            startDate, endDate, appUser.get(), APPLICATION_STATUS.DELETE);
        if (result.isEmpty()) {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, new ArrayList<>());
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, result.stream()
            .map(sourceTask -> getSourceTaskResponse(sourceTask)).collect(Collectors.toList()));
    }

    /***
     * Method use to fetch source task by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchSourceTaskById(SourceTaskRequest payload) throws Exception {
        logger.info("Request fetchSourceTaskById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_ID_MISSING);
        }
        Optional<SourceTask> sourceTask = this.sourceTaskRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!sourceTask.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_NOT_FOUND);
        }
        SourceTaskResponse sourceTaskResponse = this.getSourceTaskResponse(sourceTask.get());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, sourceTaskResponse);
    }

    @Override
    public AppResponse fetchAllSTT(STTRequest payload) throws Exception {
        logger.info("Request fetchAllSTT :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        List<SourceTaskType> result = this.sourceTaskTypeRepository.findAllByCreatedByAndStatusNotOrderByDateCreatedDesc(
            appUser.get(), APPLICATION_STATUS.DELETE);
        if (result.isEmpty()) {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, new ArrayList<>());
        }
        List<STTListResponse> sttListResponses = result.stream()
            .map(sourceTaskType -> {
                STTListResponse sttResponse = new STTListResponse();
                sttResponse.setId(sourceTaskType.getId());
                sttResponse.setServiceName(sourceTaskType.getServiceName());
                sttResponse.setDescription(sourceTaskType.getDescription());
                sttResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(sourceTaskType.getStatus().getLookupType()));
                return sttResponse;
            }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, sttListResponses);
    }

}

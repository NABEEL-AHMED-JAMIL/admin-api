package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.SourceTaskTypeService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.model.dto.request.ApiTaskTypeRequest;
import com.barco.model.dto.request.KafkaTaskTypeRequest;
import com.barco.model.dto.request.STTRequest;
import com.barco.model.dto.response.*;
import com.barco.model.enums.Action;
import com.barco.model.pojo.*;
import com.barco.model.repository.*;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.FORM_TYPE;
import com.barco.model.util.lookup.GLookup;
import com.barco.model.util.lookup.TASK_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class SourceTaskTypeServiceImpl implements SourceTaskTypeService {

    private Logger logger = LoggerFactory.getLogger(SourceTaskTypeServiceImpl.class);

    @Value("${storage.efsFileDire}")
    private String tempStoreDirectory;
    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private QueryService queryService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private GenFormRepository genFormRepository;
    @Autowired
    private SourceTaskTypeRepository sourceTaskTypeRepository;
    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private KafkaTaskTypeRepository kafkaTaskTypeRepository;
    @Autowired
    private ApiTaskTypeRepository apiTaskTypeRepository;
    @Autowired
    private SourceTaskRepository sourceTaskRepository;
    @Autowired
    private AppUserLinkSourceTaskTypeRepository appUserLinkSourceTaskTypeRepository;
    @Autowired
    private GenFormLinkSourceTaskTypeRepository genFormLinkSourceTaskTypeRepository;

    public SourceTaskTypeServiceImpl() {
    }

    /**
     * Method use to add the stt
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addSTT(STTRequest payload) throws Exception {
        logger.info("Request addSTT :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getServiceName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_SERVICE_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getTaskType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_MISSING);
        } else if ((payload.getTaskType().equals(TASK_TYPE.API.getLookupCode()) ||
            payload.getTaskType().equals(TASK_TYPE.AWS_SQS.getLookupCode()) ||
            payload.getTaskType().equals(TASK_TYPE.WEB_SOCKET.getLookupCode())) &&
            BarcoUtil.isNull(payload.getApiTaskType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_WITH_API_TYPE_MISSING);
        } else if (payload.getTaskType().equals(TASK_TYPE.KAFKA.getLookupCode()) && BarcoUtil.isNull(payload.getKafkaTaskType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_WITH_KAFKA_TYPE_MISSING);
        }
        SourceTaskType sourceTaskType = new SourceTaskType();
        sourceTaskType.setServiceName(payload.getServiceName());
        sourceTaskType.setDescription(payload.getDescription());
        sourceTaskType.setTaskType(TASK_TYPE.getRequestMethodByValue(payload.getTaskType()));
        // credential
        if (!BarcoUtil.isNull(payload.getCredentialId())) {
            Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatus(
                payload.getCredentialId(), adminUser.get().getUsername(), APPLICATION_STATUS.ACTIVE);
            if (credential.isPresent()) {
                sourceTaskType.setCredential(credential.get());
            }
        }
        sourceTaskType.setCreatedBy(adminUser.get());
        sourceTaskType.setUpdatedBy(adminUser.get());
        sourceTaskType.setStatus(APPLICATION_STATUS.ACTIVE);
        if (payload.getTaskType().equals(TASK_TYPE.AWS_SQS.getLookupCode()) ||
            payload.getTaskType().equals(TASK_TYPE.API.getLookupCode()) ||
            payload.getTaskType().equals(TASK_TYPE.WEB_SOCKET.getLookupCode())) {
            ApiTaskTypeRequest apiTaskTypeRequest = payload.getApiTaskType();
            if (BarcoUtil.isNull(apiTaskTypeRequest.getApiUrl())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.API_URL_MISSING);
            } else if (BarcoUtil.isNull(apiTaskTypeRequest.getHttpMethod())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.HTTP_METHOD_MISSING);
            }
            sourceTaskType.setApiTaskType(this.apiTaskTypeRepository.save(
                this.getApiTaskType(apiTaskTypeRequest, adminUser)));
        } else if (payload.getTaskType().equals(TASK_TYPE.KAFKA.getLookupCode())) {
            KafkaTaskTypeRequest kafkaTaskTypeRequest = payload.getKafkaTaskType();
            if (BarcoUtil.isNull(kafkaTaskTypeRequest.getNumPartitions())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.KAFKA_NUM_PARTITIONS);
            } else if (BarcoUtil.isNull(kafkaTaskTypeRequest.getServiceUrl())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.KAFKA_SERVICE_URL_MISSING);
            } else if (BarcoUtil.isNull(kafkaTaskTypeRequest.getTopicName())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.KAFKA_TOPIC_NAME_MISSING);
            } else if (BarcoUtil.isNull(kafkaTaskTypeRequest.getTopicPattern())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.KAFKA_TOPIC_PATTERN_MISSING);
            }
            sourceTaskType.setKafkaTaskType(this.kafkaTaskTypeRepository.save(
                this.getKafkaTaskType(kafkaTaskTypeRequest, adminUser)));
        }
        this.sourceTaskTypeRepository.save(sourceTaskType);
        // link app user stt giving service status
        AppUserLinkSourceTaskType appUserSTT = new AppUserLinkSourceTaskType();
        appUserSTT.setSourceTaskType(sourceTaskType);
        appUserSTT.setAppUser(adminUser.get());
        appUserSTT.setStatus(sourceTaskType.getStatus());
        appUserSTT.setCreatedBy(adminUser.get());
        appUserSTT.setUpdatedBy(adminUser.get());
        this.appUserLinkSourceTaskTypeRepository.save(appUserSTT);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, sourceTaskType.getId().toString()), payload);
    }

    /**
     * Method use to update the stt
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateSTT(STTRequest payload) throws Exception {
        logger.info("Request updateSTT :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getServiceName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_SERVICE_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getTaskType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_MISSING);
        } else if ((payload.getTaskType().equals(TASK_TYPE.API.getLookupCode()) ||
            payload.getTaskType().equals(TASK_TYPE.AWS_SQS.getLookupCode()) ||
            payload.getTaskType().equals(TASK_TYPE.WEB_SOCKET.getLookupCode())) &&
            BarcoUtil.isNull(payload.getApiTaskType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_WITH_API_TYPE_MISSING);
        } else if (payload.getTaskType().equals(TASK_TYPE.KAFKA.getLookupCode()) && BarcoUtil.isNull(payload.getKafkaTaskType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_WITH_KAFKA_TYPE_MISSING);
        }
        Optional<SourceTaskType> sourceTaskType = this.sourceTaskTypeRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!sourceTaskType.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_NOT_FOUND);
        } else if (!sourceTaskType.get().getTaskType().getLookupCode().equals(payload.getTaskType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_CANNOT_CHANGE_TO_DIFFERENT_TASK_TYPE);
        }
        sourceTaskType.get().setServiceName(payload.getServiceName());
        sourceTaskType.get().setDescription(payload.getDescription());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            sourceTaskType.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        // credential
        if (!BarcoUtil.isNull(payload.getCredentialId())) {
            Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatus(
                payload.getCredentialId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
            if (credential.isPresent()) {
                sourceTaskType.get().setCredential(credential.get());
            }
        }
        if (payload.getTaskType().equals(TASK_TYPE.AWS_SQS.getLookupCode()) ||
            payload.getTaskType().equals(TASK_TYPE.API.getLookupCode()) ||
            payload.getTaskType().equals(TASK_TYPE.WEB_SOCKET.getLookupCode())) {
            ApiTaskTypeRequest apiTaskTypeRequest = payload.getApiTaskType();
            if (BarcoUtil.isNull(apiTaskTypeRequest.getApiUrl())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.API_URL_MISSING);
            } else if (BarcoUtil.isNull(apiTaskTypeRequest.getHttpMethod())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.HTTP_METHOD_MISSING);
            }
            sourceTaskType.get().getApiTaskType().setApiUrl(apiTaskTypeRequest.getApiUrl());
            sourceTaskType.get().getApiTaskType().setHttpMethod(apiTaskTypeRequest.getHttpMethod());
            // give the same status of parent type
            if (!BarcoUtil.isNull(payload.getStatus())) {
                sourceTaskType.get().getApiTaskType().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
                sourceTaskType.get().getApiTaskType().setUpdatedBy(adminUser.get());
            }
        } else if (payload.getTaskType().equals(TASK_TYPE.KAFKA.getLookupCode())) {
            KafkaTaskTypeRequest kafkaTaskTypeRequest = payload.getKafkaTaskType();
            if (BarcoUtil.isNull(kafkaTaskTypeRequest.getNumPartitions())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.KAFKA_NUM_PARTITIONS);
            } else if (BarcoUtil.isNull(kafkaTaskTypeRequest.getServiceUrl())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.KAFKA_SERVICE_URL_MISSING);
            } else if (BarcoUtil.isNull(kafkaTaskTypeRequest.getTopicName())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.KAFKA_TOPIC_NAME_MISSING);
            } else if (BarcoUtil.isNull(kafkaTaskTypeRequest.getTopicPattern())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.KAFKA_TOPIC_PATTERN_MISSING);
            }
            sourceTaskType.get().getKafkaTaskType().setServiceUrl(kafkaTaskTypeRequest.getServiceUrl());
            sourceTaskType.get().getKafkaTaskType().setNumPartitions(kafkaTaskTypeRequest.getNumPartitions());
            sourceTaskType.get().getKafkaTaskType().setTopicName(kafkaTaskTypeRequest.getTopicName());
            sourceTaskType.get().getKafkaTaskType().setTopicPattern(kafkaTaskTypeRequest.getTopicPattern());
            // give the same status of parent type
            if (!BarcoUtil.isNull(payload.getStatus())) {
                sourceTaskType.get().getKafkaTaskType().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
                sourceTaskType.get().getKafkaTaskType().setUpdatedBy(adminUser.get());
            }
        }
        // edit all source task
        if (!BarcoUtil.isNull(sourceTaskType.get().getAppUserLinkSourceTaskTypes())) {
            this.actionAppUserLinkSourceTaskTypes(sourceTaskType.get(), adminUser.get());
        }
        // edit all form
        if (!BarcoUtil.isNull(sourceTaskType.get().getGenFormLinkSourceTaskTypes())) {
            this.actionGenFormLinkSourceTaskTypes(sourceTaskType.get(), adminUser.get());
        }
        sourceTaskType.get().setUpdatedBy(adminUser.get());
        this.sourceTaskTypeRepository.save(sourceTaskType.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()), payload);
    }

    /**
     * Method use to delete the stt
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteSTT(STTRequest payload) throws Exception {
        logger.info("Request deleteSTT :- " + payload);
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
        Optional<SourceTaskType> sourceTaskType = this.sourceTaskTypeRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!sourceTaskType.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_NOT_FOUND);
        }
        sourceTaskType.get().setUpdatedBy(appUser.get());
        sourceTaskType.get().setStatus(APPLICATION_STATUS.DELETE);
        // delete api task type
        if (!BarcoUtil.isNull(sourceTaskType.get().getApiTaskType())) {
            sourceTaskType.get().getApiTaskType().setStatus(APPLICATION_STATUS.DELETE);
            sourceTaskType.get().getApiTaskType().setUpdatedBy(appUser.get());
        } else if (!BarcoUtil.isNull(sourceTaskType.get().getKafkaTaskType())) {
            sourceTaskType.get().getKafkaTaskType().setStatus(APPLICATION_STATUS.DELETE);
            sourceTaskType.get().getKafkaTaskType().setUpdatedBy(appUser.get());
        }
        // edit all source task
        if (!BarcoUtil.isNull(sourceTaskType.get().getAppUserLinkSourceTaskTypes())) {
            this.actionAppUserLinkSourceTaskTypes(sourceTaskType.get(), appUser.get());
        }
        // edit all form
        if (!BarcoUtil.isNull(sourceTaskType.get().getGenFormLinkSourceTaskTypes())) {
            this.actionGenFormLinkSourceTaskTypes(sourceTaskType.get(), appUser.get());
        }
        // **-> setting null mean we are de-linkin the source task with source task type
        if (!BarcoUtil.isNull(sourceTaskType.get().getSourceTasks())) {
            sourceTaskType.get().getSourceTasks().stream()
                .filter(sourceTask -> !sourceTask.getStatus().equals(APPLICATION_STATUS.DELETE))
                .map(sourceTask -> {
                    sourceTask.setSourceTaskType(null);
                    sourceTask.setUpdatedBy(appUser.get());
                    if (!BarcoUtil.isNull(sourceTask.getSourceTaskData())) {
                        sourceTask.getSourceTaskData().stream()
                        .filter(sourceTaskData -> !sourceTaskData.getStatus().equals(APPLICATION_STATUS.DELETE))
                        .map(sourceTaskData -> {
                            sourceTaskData.setStatus(APPLICATION_STATUS.DELETE);
                            return sourceTaskData;
                        }).collect(Collectors.toList());
                    }
                    return sourceTask;
                }).collect(Collectors.toList());
        }
        sourceTaskType.get().setUpdatedBy(appUser.get());
        this.sourceTaskTypeRepository.save(sourceTaskType.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()), payload);
    }

    /**
     * Method use to fetch stt by stt id(source task type)
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchSTTBySttId(STTRequest payload) throws Exception {
        logger.info("Request fetchSTTBySttId :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_ID_MISSING);
        }
        Optional<SourceTaskType> sourceTaskType = this.sourceTaskTypeRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!sourceTaskType.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_NOT_FOUND);
        }
        SourceTaskTypeResponse sourceTaskTypeResponse = new SourceTaskTypeResponse();
        sourceTaskTypeResponse.setId(sourceTaskType.get().getId());
        sourceTaskTypeResponse.setServiceName(sourceTaskType.get().getServiceName());
        sourceTaskTypeResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(
            sourceTaskType.get().getStatus().getLookupCode()));
        sourceTaskTypeResponse.setTaskType(GLookup.getGLookup(this.lookupDataCacheService
            .getChildLookupDataByParentLookupTypeAndChildLookupCode(TASK_TYPE.getName(),
                sourceTaskType.get().getTaskType().getLookupCode())));
        if (!BarcoUtil.isNull(sourceTaskType.get().getCredential())) {
            CredentialResponse credentialResponse = new CredentialResponse();
            credentialResponse.setId(sourceTaskType.get().getCredential().getId());
            credentialResponse.setName(sourceTaskType.get().getCredential().getName());
            sourceTaskTypeResponse.setCredential(credentialResponse);
        }
        if (sourceTaskType.get().getTaskType().getLookupCode().equals(TASK_TYPE.KAFKA.getLookupCode())) {
            sourceTaskTypeResponse.setKafkaTaskType(this.getKafkaTaskTypeResponse(sourceTaskType.get().getKafkaTaskType()));
        } else {
            sourceTaskTypeResponse.setApiTaskType(this.getApiTaskTypeResponse(sourceTaskType.get().getApiTaskType(), this.lookupDataCacheService));
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, sourceTaskTypeResponse);
    }

    /***
     * Method use to fetch stt (source task type)
     * @param payload
     * @return AppResponse
     */
    @Override
    public AppResponse fetchAllSTT(STTRequest payload) throws Exception {
        logger.info("Request fetchAllSTT :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate().concat(BarcoUtil.START_DATE));
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate().concat(BarcoUtil.END_DATE));
        List<SourceTaskType> result = this.sourceTaskTypeRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
            startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE);
        if (result.isEmpty()) {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, new ArrayList<>());
        }
        List<STTListResponse> sttListResponses = result.stream()
            .map(sourceTaskType -> {
                STTListResponse sttResponse = new STTListResponse();
                sttResponse.setId(sourceTaskType.getId());
                sttResponse.setServiceName(sourceTaskType.getServiceName());
                sttResponse.setDescription(sourceTaskType.getDescription());
                sttResponse.setTaskType(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
                    TASK_TYPE.getName(), sourceTaskType.getTaskType().getLookupCode())));
                if (!BarcoUtil.isNull(sourceTaskType.getCredential())) {
                    CredentialResponse credentialResponse = new CredentialResponse();
                    credentialResponse.setId(sourceTaskType.getCredential().getId());
                    credentialResponse.setName(sourceTaskType.getCredential().getName());
                    credentialResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(sourceTaskType.getCredential().getStatus().getLookupType()));
                    sttResponse.setCredential(credentialResponse);
                }
                if (sourceTaskType.getTaskType().getLookupCode().equals(TASK_TYPE.KAFKA.getLookupCode()) && !BarcoUtil.isNull(sourceTaskType.getKafkaTaskType())) {
                    sttResponse.setKafkaTaskType(this.getKafkaTaskTypeResponse(sourceTaskType.getKafkaTaskType()));
                } else if (!BarcoUtil.isNull(sourceTaskType.getApiTaskType())) {
                    sttResponse.setApiTaskType(this.getApiTaskTypeResponse(sourceTaskType.getApiTaskType(), this.lookupDataCacheService));
                }
                sttResponse.setTotalTask(this.sourceTaskRepository.countBySourceTaskTypeAndStatusNot(sourceTaskType, APPLICATION_STATUS.DELETE));
                sttResponse.setTotalForm(this.genFormLinkSourceTaskTypeRepository.countBySourceTaskTypeAndStatusNot(sourceTaskType, APPLICATION_STATUS.DELETE));
                sttResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(sourceTaskType.getStatus().getLookupType()));
                sttResponse.setCreatedBy(getActionUser(sourceTaskType.getCreatedBy()));
                sttResponse.setUpdatedBy(getActionUser(sourceTaskType.getUpdatedBy()));
                sttResponse.setDateUpdated(sourceTaskType.getDateUpdated());
                sttResponse.setDateCreated(sourceTaskType.getDateCreated());
                return sttResponse;
            }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, sttListResponses);
    }

    /**
     * Method use to delete all stt
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllSTT(STTRequest payload) throws Exception {
        logger.info("Request deleteAllSTT :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        else if (BarcoUtil.isNull(payload.getIds())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        this.sourceTaskTypeRepository.saveAll(
            this.sourceTaskTypeRepository.findAllByIdIn(payload.getIds()).stream()
                .map(sourceTaskType -> {
                    // de-link all source task
                    sourceTaskType.setStatus(APPLICATION_STATUS.DELETE);
                    sourceTaskType.setUpdatedBy(appUser.get());
                    if (!BarcoUtil.isNull(sourceTaskType.getAppUserLinkSourceTaskTypes())) {
                        this.actionAppUserLinkSourceTaskTypes(sourceTaskType, appUser.get());
                    }
                    if (!BarcoUtil.isNull(sourceTaskType.getGenFormLinkSourceTaskTypes())) {
                        this.actionGenFormLinkSourceTaskTypes(sourceTaskType, appUser.get());
                    }
                    // **-> setting null mean we are de-linkin the source task with source task type
                    if (!BarcoUtil.isNull(sourceTaskType.getSourceTasks())) {
                        sourceTaskType.getSourceTasks().stream()
                            .filter(sourceTask -> !sourceTask.getStatus().equals(APPLICATION_STATUS.DELETE))
                            .map(sourceTask -> {
                                sourceTask.setSourceTaskType(null);
                                sourceTask.setUpdatedBy(appUser.get());
                                if (!BarcoUtil.isNull(sourceTask.getSourceTaskData())) {
                                    sourceTask.getSourceTaskData().stream()
                                        .filter(sourceTaskData -> !sourceTaskData.getStatus().equals(APPLICATION_STATUS.DELETE))
                                        .map(sourceTaskData -> {
                                            sourceTaskData.setStatus(APPLICATION_STATUS.DELETE);
                                            return sourceTaskData;
                                        }).collect(Collectors.toList());
                                }
                                return sourceTask;
                            }).collect(Collectors.toList());
                    }
                    return sourceTaskType;
                }).collect(Collectors.toList())
        );
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, ""), payload);
    }

    /**
     * Method use to fetch the stt link form
     * @param payload
     * @return AppResponse
     * **/
    @Override
    public AppResponse fetchAllSTTLinkForm(STTRequest payload) throws Exception {
        logger.info("Request fetchAllSTTLinkForm :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
        }
        Optional<SourceTaskType> sourceTaskType = this.sourceTaskTypeRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!sourceTaskType.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_NOT_FOUND);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(String.format(QueryService.FETCH_ALL_FORM_LINK_STT,
            sourceTaskType.get().getId(), APPLICATION_STATUS.DELETE.getLookupCode(), FORM_TYPE.SERVICE_FORM.getLookupCode(), appUser.get().getId()));
        List<SourceTaskTypeLinkFormResponse> sourceTaskTypeLinkFormResponses = new ArrayList<>();
        if (!BarcoUtil.isNull(queryResponse.getData())) {
            for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
                sourceTaskTypeLinkFormResponses.add(getSourceTaskTypeLinkFormResponse(data, this.lookupDataCacheService));
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, sourceTaskTypeLinkFormResponses);
    }

    /**
     * Method use to link the stt with form
     * @param payload
     * @return AppResponse
     * **/
    @Override
    public AppResponse linkSTTForm(STTRequest payload) throws Exception {
        logger.info("Request linkSTTForm :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getAction())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ACTION_MISSING);
        }
        if (payload.getAction().equals(Action.LINK)) {
            if (BarcoUtil.isNull(payload.getId())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_ID_MISSING);
            } else if (BarcoUtil.isNull(payload.getFormId()) && payload.getFormId().size() > 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
            }
            Optional<SourceTaskType> sourceTaskType = this.sourceTaskTypeRepository.findByIdAndCreatedByAndStatusNot(
                payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
            if (!sourceTaskType.isPresent()) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_NOT_FOUND);
            }
            List<GenForm> getForms = this.genFormRepository.findAllByIdInAndStatusNot(payload.getFormId(), APPLICATION_STATUS.DELETE);
            if (getForms.size() == 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
            }
            this.genFormLinkSourceTaskTypeRepository.saveAll(getForms.stream()
                .map(getForm -> {
                    GenFormLinkSourceTaskType genFormLinkSourceTaskType = new GenFormLinkSourceTaskType();
                    genFormLinkSourceTaskType.setGenForm(getForm);
                    genFormLinkSourceTaskType.setSourceTaskType(sourceTaskType.get());
                    if (sourceTaskType.get().getStatus().equals(APPLICATION_STATUS.ACTIVE) &&
                        getForm.getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
                        genFormLinkSourceTaskType.setStatus(APPLICATION_STATUS.ACTIVE);
                    } else {
                        genFormLinkSourceTaskType.setStatus(APPLICATION_STATUS.INACTIVE);
                    }
                    genFormLinkSourceTaskType.setCreatedBy(appUser.get());
                    genFormLinkSourceTaskType.setUpdatedBy(appUser.get());
                    return genFormLinkSourceTaskType;
                }).collect(Collectors.toList()));
            return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
        }
        if (BarcoUtil.isNull(payload.getSttLinkForm())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_LINK_STT_MISSING);
        }
        this.genFormLinkSourceTaskTypeRepository.saveAll(
            this.genFormLinkSourceTaskTypeRepository.findAllByIdInAndStatusNot(payload.getSttLinkForm(), APPLICATION_STATUS.DELETE)
            .stream().map(genFormLinkSourceTaskType -> {
                genFormLinkSourceTaskType.setStatus(APPLICATION_STATUS.DELETE);
                genFormLinkSourceTaskType.setUpdatedBy(appUser.get());
                return genFormLinkSourceTaskType;
            }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

}

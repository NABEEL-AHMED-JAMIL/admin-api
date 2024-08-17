package com.barco.admin.service.impl;

import com.barco.admin.service.DynamicPayloadService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.model.dto.request.ConfigurationMakerRequest;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.TagInfoRequest;
import com.barco.model.dto.response.ConfigurationMakerResponse;
import com.barco.model.dto.response.DynamicPayloadResponse;
import com.barco.model.dto.response.TagInfoResponse;
import com.barco.model.pojo.DynamicPayload;
import com.barco.model.pojo.DynamicPayloadTag;
import com.barco.model.repository.DynamicPayloadRepository;
import com.barco.model.repository.DynamicPayloadTagRepository;
import com.barco.model.util.JsonOutTagInfoUtil;
import com.barco.model.util.XmlOutTagInfoUtil;
import com.barco.model.dto.request.DynamicPayloadRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.GLookup;
import com.barco.model.util.lookup.PAYLOAD_TYPE;
import com.barco.model.util.lookup.TASK_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class DynamicPayloadServiceImpl implements DynamicPayloadService {

    private Logger logger = LoggerFactory.getLogger(DynamicPayloadServiceImpl.class);

    @Autowired
    private XmlOutTagInfoUtil xmlOutTagInfoUtil;
    @Autowired
    private JsonOutTagInfoUtil jsonOutTagInfoUtil;
    // service
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    // repository
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private DynamicPayloadRepository dynamicPayloadRepository;
    @Autowired
    private DynamicPayloadTagRepository dynamicPayloadTagRepository;

    /**
     * Method use to add event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request addDynamicPayload :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DYNAMIC_PAYLOAD_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DYNAMIC_PAYLOAD_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getDynamicPayloadTags().getXmlTagsInfo())
            && BarcoUtil.isNull(payload.getDynamicPayloadTags().getJsonTagsInfo())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DYNAMIC_PAYLOAD_TYPE_MISSING);
        }
        DynamicPayload dynamicPayload = new DynamicPayload();
        dynamicPayload.setName(payload.getName());
        dynamicPayload.setPayload(payload.getPayload());
        dynamicPayload.setDescription(payload.getDescription());
        dynamicPayload.setDynamicPayloadTags(!BarcoUtil.isNull(payload.getDynamicPayloadTags().getXmlTagsInfo()) ?
            this.tagsInfoFromTagsInfoRequest(payload.getDynamicPayloadTags().getXmlTagsInfo(), adminUser.get(), APPLICATION_STATUS.ACTIVE) :
            this.tagsInfoFromTagsInfoRequest(payload.getDynamicPayloadTags().getJsonTagsInfo(), adminUser.get(), APPLICATION_STATUS.ACTIVE));
        dynamicPayload.setStatus(APPLICATION_STATUS.ACTIVE);
        dynamicPayload.setCreatedBy(adminUser.get());
        dynamicPayload.setUpdatedBy(adminUser.get());
        dynamicPayload = this.dynamicPayloadRepository.save(dynamicPayload);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, dynamicPayload.getId().toString()));
    }

    /**
     * Method use to update event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request updateDynamicPayload :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DYNAMIC_PAYLOAD_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DYNAMIC_PAYLOAD_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DYNAMIC_PAYLOAD_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getDynamicPayloadTags().getXmlTagsInfo())
                && BarcoUtil.isNull(payload.getDynamicPayloadTags().getJsonTagsInfo())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DYNAMIC_PAYLOAD_TYPE_MISSING);
        }
        Optional<DynamicPayload> dynamicPayload = this.dynamicPayloadRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getName(), APPLICATION_STATUS.ACTIVE);
        if (!dynamicPayload.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DYNAMIC_NOT_FOUND);
        }
        dynamicPayload.get().setId(payload.getId());
        dynamicPayload.get().setName(payload.getName());
        dynamicPayload.get().setPayload(payload.getPayload());
        dynamicPayload.get().setDescription(payload.getDescription());
        dynamicPayload.get().setUpdatedBy(adminUser.get());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            dynamicPayload.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        // update each time new record
//        dynamicPayload.get().setDynamicPayloadTags(!BarcoUtil.isNull(payload.getDynamicPayloadTags().getXmlTagsInfo()) ?
//            this.tagsInfoFromTagsInfoRequest(payload.getDynamicPayloadTags().getXmlTagsInfo(), adminUser.get()) :
//            this.tagsInfoFromTagsInfoRequest(payload.getDynamicPayloadTags().getJsonTagsInfo(), adminUser.get()));
        this.dynamicPayloadRepository.save(dynamicPayload.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()));
    }

    /**
     * Method use to fetch all dynamic payload
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request fetchAllDynamicPayload :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        List<DynamicPayload> dynamicPayloads;
        if (!BarcoUtil.isNull(payload.getStartDate()) && !BarcoUtil.isNull(payload.getEndDate())) {
            Timestamp startDate = Timestamp.valueOf(payload.getStartDate().concat(BarcoUtil.START_DATE));
            Timestamp endDate = Timestamp.valueOf(payload.getEndDate().concat(BarcoUtil.END_DATE));
            dynamicPayloads = this.dynamicPayloadRepository.findAllByDateCreatedBetweenAndUsernameAndStatusNot(
                startDate, endDate, payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        } else {
            dynamicPayloads = this.dynamicPayloadRepository.findAllByCreatedByAndStatusNot(adminUser.get(), APPLICATION_STATUS.DELETE);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, dynamicPayloads.stream()
            .map(dynamicPayload -> this.getDynamicPayloadResponse(dynamicPayload)).collect(Collectors.toList()));
    }

    /**
     * Method use to fetch dynamic payload by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchDynamicPayloadById(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request fetchDynamicPayloadById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_ID_MISSING);
        }
        Optional<DynamicPayload> dynamicPayload = this.dynamicPayloadRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!dynamicPayload.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getDynamicPayloadResponse(dynamicPayload.get()));
    }

    /**
     * Method use to delete dynamic payload by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteDynamicPayloadById(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request deleteDynamicPayloadById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DYNAMIC_PAYLOAD_ID_MISSING);
        }
        Optional<DynamicPayload> dynamicPayload = this.dynamicPayloadRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!dynamicPayload.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DYNAMIC_NOT_FOUND);
        }
        dynamicPayload.get().setStatus(APPLICATION_STATUS.DELETE);
        this.dynamicPayloadRepository.save(dynamicPayload.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()));
    }

    /**
     * Method use to delete all dynamic payload
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request deleteAllDynamicPayload :- " + payload);
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
        List<DynamicPayload> dynamicPayloads = this.dynamicPayloadRepository.findAllByIdIn(payload.getIds());
        dynamicPayloads.forEach(reportSetting -> {
            reportSetting.setStatus(APPLICATION_STATUS.DELETE);
            reportSetting.getDynamicPayloadTags().stream().map(dynamicPayloadTag -> {
                dynamicPayloadTag.setStatus(APPLICATION_STATUS.DELETE);
                return dynamicPayloadTag;
            }).collect(Collectors.toList());;
        });
        this.dynamicPayloadRepository.saveAll(dynamicPayloads);
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_DELETED_ALL, payload);
    }

    /**
     * Method use to convert the info to xlm
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse xmlCreateChecker(ConfigurationMakerRequest payload) throws Exception {
        logger.info("Request xmlCreateChecker :- " + payload);
        if (!BarcoUtil.isNull(payload.getXmlTagsInfo())) {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DYNAMIC_PAYLOAD, this.xmlOutTagInfoUtil.makeXml(payload));
        } else {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WRONG_INPUT);
        }
    }

    /**
     * Method use to convert the info to json
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse jsonCreateChecker(ConfigurationMakerRequest payload) throws Exception {
        logger.info("Request jsonCreateChecker :- " + payload);
        if (!BarcoUtil.isNull(payload.getJsonTagsInfo())) {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DYNAMIC_PAYLOAD, this.jsonOutTagInfoUtil.makeJson(payload));
        } else {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WRONG_INPUT);
        }
    }

    /**
     * Method use to convert the tag info request to tag info
     * @param tagsInfo
     * @param appUser
     * @param status
     * @return List<DynamicPayloadTag>
     * */
    private List<DynamicPayloadTag> tagsInfoFromTagsInfoRequest(
        List<TagInfoRequest> tagsInfo, AppUser appUser, APPLICATION_STATUS status) {
        return tagsInfo.stream().map(tagInfoRequest -> {
            DynamicPayloadTag dynamicPayloadTag = new DynamicPayloadTag(tagInfoRequest.getTagKey(),
                tagInfoRequest.getTagParent(), tagInfoRequest.getTagValue());
            dynamicPayloadTag.setCreatedBy(appUser);
            dynamicPayloadTag.setUpdatedBy(appUser);
            dynamicPayloadTag.setStatus(status);
            return dynamicPayloadTag;
        }).collect(Collectors.toList());
    }

    /**
     * Method use to get the dynamic payload response
     * @param dynamicPayload
     * @return DynamicPayloadResponse
     * */
    private DynamicPayloadResponse getDynamicPayloadResponse(DynamicPayload dynamicPayload) {
        DynamicPayloadResponse dynamicPayloadResponse = new DynamicPayloadResponse();
        dynamicPayloadResponse.setId(dynamicPayload.getId());
        dynamicPayloadResponse.setName(dynamicPayload.getName());
        dynamicPayloadResponse.setDescription(dynamicPayload.getDescription());
        dynamicPayloadResponse.setPayloadType(GLookup.getGLookup(
            this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
               TASK_TYPE.getName(), dynamicPayload.getPayloadType().getLookupCode())));
        dynamicPayloadResponse.setDynamicPayloadTags(this.getConfigurationMakerResponse(dynamicPayload));
        dynamicPayloadResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(dynamicPayload.getStatus().getLookupType()));
        dynamicPayloadResponse.setCreatedBy(getActionUser(dynamicPayload.getCreatedBy()));
        dynamicPayloadResponse.setUpdatedBy(getActionUser(dynamicPayload.getUpdatedBy()));
        dynamicPayloadResponse.setDateUpdated(dynamicPayload.getDateUpdated());
        dynamicPayloadResponse.setDateCreated(dynamicPayload.getDateCreated());
        return dynamicPayloadResponse;
    }

    /**
     * Method use to get the configuration maker response
     * @param dynamicPayload
     * @return ConfigurationMakerResponse
     * */
    private ConfigurationMakerResponse getConfigurationMakerResponse(DynamicPayload dynamicPayload) {
        ConfigurationMakerResponse configurationMakerResponse = new ConfigurationMakerResponse();
        if (dynamicPayload.getPayloadType().equals(PAYLOAD_TYPE.JSON)) {
            configurationMakerResponse.setJsonTagsInfo(this.getTagInfoResponses(dynamicPayload.getDynamicPayloadTags()));
        } else {
            configurationMakerResponse.setXmlTagsInfo(this.getTagInfoResponses(dynamicPayload.getDynamicPayloadTags()));
        }
        return configurationMakerResponse;
    }

    /**
     * Method use to get the configuration maker response
     * @param dynamicPayloadTags
     * @return List<TagInfoResponse>
     * */
    private List<TagInfoResponse> getTagInfoResponses(List<DynamicPayloadTag> dynamicPayloadTags) {
        return dynamicPayloadTags.stream().map(dynamicPayloadTag -> new TagInfoResponse(dynamicPayloadTag.getId(),
            dynamicPayloadTag.getTagKey(), dynamicPayloadTag.getTagParent(), dynamicPayloadTag.getTagValue()))
            .collect(Collectors.toList());
    }

}

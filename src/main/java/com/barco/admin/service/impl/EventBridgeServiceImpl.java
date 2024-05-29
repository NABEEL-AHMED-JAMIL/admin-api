package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.EventBridgeService;
import com.barco.common.security.JwtUtils;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.LinkEBURequest;
import com.barco.model.dto.request.EventBridgeRequest;
import com.barco.model.dto.response.*;
import com.barco.model.pojo.*;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.AppUserEventBridgeRepository;
import com.barco.model.repository.CredentialRepository;
import com.barco.model.repository.EventBridgeRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.GLookup;
import com.barco.model.util.lookup.EVENT_BRIDGE_TYPE;
import java.sql.Timestamp;
import java.util.Calendar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class EventBridgeServiceImpl implements EventBridgeService {

    private Logger logger = LoggerFactory.getLogger(EventBridgeServiceImpl.class);

    private final String XRHK = "XRHK-Authorization";
    private final String XSHK = "XSHK-Authorization";

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private QueryService queryService;
    @Autowired
    private EventBridgeRepository eventBridgeRepository;
    @Autowired
    private AppUserEventBridgeRepository appUserEventBridgeRepository;
    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private AppUserRepository appUserRepository;

    /**
     * Method use to add event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addEventBridge(EventBridgeRequest payload) throws Exception {
        logger.info("Request addEventBridge :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getBridgeUrl())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_URL_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getBridgeType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getCredentialId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_CREDENTIAL_MISSING);
        }
        Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatus(
            payload.getCredentialId(), adminUser.get().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!credential.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        }
        EventBridge eventBridge = new EventBridge();
        eventBridge.setName(payload.getName());
        eventBridge.setBridgeUrl(payload.getBridgeUrl());
        eventBridge.setDescription(payload.getDescription());
        eventBridge.setBridgeType(EVENT_BRIDGE_TYPE.getByLookupCode(payload.getBridgeType()));
        eventBridge.setCredential(credential.get());
        eventBridge.setCreatedBy(adminUser.get());
        eventBridge.setUpdatedBy(adminUser.get());
        eventBridge.setStatus(APPLICATION_STATUS.ACTIVE);
        this.eventBridgeRepository.save(eventBridge);
        // link event bridge to user
        AppUserEventBridge appUserEventBridge = new AppUserEventBridge();
        appUserEventBridge.setTokenId(UUID.randomUUID().toString());
        appUserEventBridge.setEventBridge(eventBridge);
        appUserEventBridge.setAppUser(adminUser.get());
        appUserEventBridge.setCreatedBy(adminUser.get());
        appUserEventBridge.setUpdatedBy(adminUser.get());
        appUserEventBridge.setStatus(APPLICATION_STATUS.ACTIVE);
        this.appUserEventBridgeRepository.save(appUserEventBridge);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, eventBridge.getId().toString()), payload);
    }

    /**
     * Method use to update event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateEventBridge(EventBridgeRequest payload) throws Exception {
        logger.info("Request updateEventBridge :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getBridgeUrl())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_URL_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getBridgeType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getCredentialId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_CREDENTIAL_MISSING);
        }
        Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatus(
            payload.getCredentialId(), adminUser.get().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!credential.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        }
        Optional<EventBridge> eventBridge = this.eventBridgeRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!eventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_NOT_FOUND);
        }
        eventBridge.get().setBridgeUrl(payload.getBridgeUrl());
        eventBridge.get().setName(payload.getName());
        eventBridge.get().setDescription(payload.getDescription());
        eventBridge.get().setBridgeType(EVENT_BRIDGE_TYPE.getByLookupCode(payload.getBridgeType()));
        eventBridge.get().setCredential(credential.get());
        eventBridge.get().setUpdatedBy(adminUser.get());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            // if status is in-active & delete then we have filter the role and show only those role in user detail
            eventBridge.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            eventBridge.get().getAppUserEventBridges().stream()
                .map(appUserEventBridge -> {
                    appUserEventBridge.setStatus(eventBridge.get().getStatus());
                    return appUserEventBridge;
                }).collect(Collectors.toList());
        }
        this.eventBridgeRepository.save(eventBridge.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()), payload);
    }

    /**
     * Method use to fetch all event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllEventBridge(EventBridgeRequest payload) throws Exception {
        logger.info("Request fetchAllEventBridge :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.eventBridgeRepository.findAllByCreatedByAndStatusNotOrderByDateCreatedDesc(adminUser.get(), APPLICATION_STATUS.DELETE)
                .stream().map(eventBridge -> getEventBridgeResponse(eventBridge)).collect(Collectors.toList()));
    }

    /**
     * Method use to fetch all event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchEventBridgeById(EventBridgeRequest payload) throws Exception {
        logger.info("Request fetchEventBridgeById :- " + payload);
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_ID_MISSING);
        }
        Optional<EventBridge> eventBridge = this.eventBridgeRepository.findByIdAndCreatedByAndStatusNot(payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!eventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.EVENT_BRIDGE_NOT_FOUND_WITH_ID, payload.getId().toString()));
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, getEventBridgeResponse(eventBridge.get()));
    }

    /**
     * Method use to fetch all event bridge by bridge type
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchEventBridgeByBridgeType(EventBridgeRequest payload) throws Exception {
        logger.info("Request fetchEventBridgeByBridgeType :- " + payload);
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
                payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (BarcoUtil.isNull(payload.getBridgeType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_TYPE_MISSING);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.eventBridgeRepository.findAllByBridgeTypeAndCreatedByAndStatusNotOrderByDateCreatedDesc(
                EVENT_BRIDGE_TYPE.getByLookupCode(payload.getBridgeType()), adminUser.get(), APPLICATION_STATUS.DELETE)
            .stream().map(eventBridge -> getEventBridgeResponse(eventBridge)).collect(Collectors.toList()));
    }

    /**
     * Method use to delete event bridge by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteEventBridgeById(EventBridgeRequest payload) throws Exception {
        logger.info("Request deleteEventBridgeById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_ID_MISSING);
        }
        Optional<EventBridge> eventBridge = this.eventBridgeRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE);
        if (!eventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.EVENT_BRIDGE_NOT_FOUND, payload.getId().toString()));
        }
        EventBridge eventBridgeEntity = eventBridge.get();
        this.nullifyReportSettingReferences(eventBridgeEntity);
        this.eventBridgeRepository.delete(eventBridgeEntity);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()), payload);
    }

    /**
     * Method use to null the report setting reference
     * @param eventBridge
     * */
    private void nullifyReportSettingReferences(EventBridge eventBridge) {
        // null all event id for pdf
        if (!BarcoUtil.isNull(eventBridge.getReportPdfBridgeSettings())
            && eventBridge.getReportPdfBridgeSettings().size() > 0) {
            eventBridge.setReportPdfBridgeSettings(null);
        }
        // null all event id for xlsx
        if (!BarcoUtil.isNull(eventBridge.getReportXlsxBridgeSettings())
            && eventBridge.getReportXlsxBridgeSettings().size() > 0) {
            eventBridge.setReportXlsxBridgeSettings(null);
        }
        // null all event id for csv
        if (!BarcoUtil.isNull(eventBridge.getReportCsvBridgeSettings())
            && eventBridge.getReportCsvBridgeSettings().size() > 0) {
            eventBridge.setReportCsvBridgeSettings(null);
        }
        // null all event id for data
        if (!BarcoUtil.isNull(eventBridge.getReportDataBridgeSettings())
            && eventBridge.getReportDataBridgeSettings().size() > 0) {
            eventBridge.setReportDataBridgeSettings(null);
        }
        // null all event id for fist dim
        if (!BarcoUtil.isNull(eventBridge.getReportFistDimBridgeSettings())
            && eventBridge.getReportFistDimBridgeSettings().size() > 0) {
            eventBridge.setReportFistDimBridgeSettings(null);
        }
        // null all event id for sec dim
        if (!BarcoUtil.isNull(eventBridge.getReportSecDimBridgeSettings())
            && eventBridge.getReportSecDimBridgeSettings().size() > 0) {
            eventBridge.setReportSecDimBridgeSettings(null);
        }
    }

    /**
     * Method use to delete all EventBridgeevent bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllEventBridge(EventBridgeRequest payload) throws Exception {
        logger.info("Request deleteAllEventBridge :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getIds())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        this.eventBridgeRepository.deleteAll(this.eventBridgeRepository.findAllByIdIn(payload.getIds()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, ""), payload);
    }

    /**
     * Method use to fetch the link event bridge with user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchLinkEventBridgeWitUser(EventBridgeRequest payload) throws Exception {
        logger.info("Request fetchLinkEventBridgeWitUser :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_ID_MISSING);
        }
        Optional<EventBridge> eventBridge = this.eventBridgeRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!eventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.EVENT_BRIDGE_NOT_FOUND_WITH_ID, payload.getId()), payload);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(String.format(QueryService.FETCH_LINK_EVENT_BRIDGE_WITH_USER,
            eventBridge.get().getId(), APPLICATION_STATUS.DELETE.getLookupCode()));
        List<LinkRPUResponse> linkRPUResponses = new ArrayList<>();
        if (!BarcoUtil.isNull(queryResponse.getData())) {
            for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
                linkRPUResponses.add(getLinkRPUResponse(data, eventBridge.get().getStatus()));
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, linkRPUResponses);
    }

    /**
     * Method to link the EventBridge with user
     * @param payload
     * @return AppResponse
     * @throws Exception
     */
    @Override
    public AppResponse linkEventBridgeWithUser(LinkEBURequest payload) throws Exception {
        logger.info("Request linkEventBridgeWithUser :- " + payload);
        Optional<AppUser> superAdmin = getAppUser(payload.getSessionUser().getUsername());
        if (!superAdmin.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        AppResponse validationResponse = validateLinkEventBridgePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        Optional<EventBridge> EventBridge = this.eventBridgeRepository.findById(payload.getId());
        if (!EventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.EVENT_BRIDGE_NOT_FOUND_WITH_ID, payload.getId()), payload);
        }
        Optional<AppUser> appUser = this.appUserRepository.findById(payload.getAppUserId());
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.APPUSER_NOT_FOUND, payload.getAppUserId()), payload);
        }
        if (payload.getLinked()) {
            return linkEventBridge(superAdmin.get(), appUser.get(), EventBridge.get(), payload);
        } else {
            this.queryService.deleteQuery(String.format(QueryService.DELETE_APP_USER_EVENT_BRIDGE_BY_EVENT_BRIDGE_ID_AND_APP_USER_ID,
                    EventBridge.get().getId(), appUser.get().getId()));
        }
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, ""), payload);
    }

    /**
     * Method to generate the event bridge token
     * @param payload
     * @return AppResponse
     * @throws Exception
     */
    @Override
    public AppResponse genEventBridgeToken(LinkEBURequest payload) throws Exception {
        logger.info("Request genEventBridgeToken :- " + payload);
        Optional<AppUser> appUser = getAppUser(payload.getSessionUser().getUsername());
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (BarcoUtil.isNull(payload.getTokenId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_GEN_TOKEN_MISSING);
        }
        Optional<AppUserEventBridge> linkEventBridge = appUserEventBridgeRepository.findByTokenId(payload.getTokenId());
        if (!linkEventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_NOT_FOUND_WITH_GEN_TOKEN);
        }
        EventBridge eventBridge = linkEventBridge.get().getEventBridge();
        return generateTokenForEventBridge(linkEventBridge.get(), eventBridge, payload);
    }

    /**
     * Method use to gernate the otken for EventBridge
     * @param linkEventBridge
     * @param eventBridge
     * @param payload
     * @return AppResponse
     * */
    private AppResponse generateTokenForEventBridge(AppUserEventBridge linkEventBridge,
        EventBridge eventBridge, LinkEBURequest payload) throws Exception {
        if (BarcoUtil.isNull(eventBridge.getCredential())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        } else if (!eventBridge.getCredential().getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_ACTIVE);
        }
        String priKey = getCredentialPrivateKey(eventBridge.getCredential());
        linkEventBridge.setAccessToken(this.jwtUtils.generateToken(priKey, linkEventBridge.getTokenId()));
        linkEventBridge.setExpireTime(getOneYearFromNow());
        this.appUserEventBridgeRepository.save(linkEventBridge);
        payload.setAccessToken(linkEventBridge.getAccessToken());
        payload.setExpireTime(linkEventBridge.getExpireTime());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, ""), payload);
    }

    /**
     * Method use to get the appuser by username
     * @param username
     * @return AppUser
     * */
    private Optional<AppUser> getAppUser(String username) {
        return appUserRepository.findByUsernameAndStatus(username, APPLICATION_STATUS.ACTIVE);
    }

    /**
     * Method use to validate the link EventBridge payload
     * @param payload
     * @return AppResponse
     * */
    private AppResponse validateLinkEventBridgePayload(LinkEBURequest payload) {
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getAppUserId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APP_USER_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getLinked())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LINKED_MISSING);
        }
        return null;
    }

    /**
     * Method use to link with event bridge
     * @param superAdmin
     * @param appUser
     * @param eventBridge
     * @param payload
     * @return AppResponse
     *
     * */
    private AppResponse linkEventBridge(AppUser superAdmin, AppUser appUser,
        EventBridge eventBridge, LinkEBURequest payload) throws Exception {
        Credential credential = eventBridge.getCredential();
        if (BarcoUtil.isNull(credential.getContent())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_CONTENT_MISSING);
        }
        String priKey = getCredentialPrivateKey(credential);
        AppUserEventBridge linkEventBridge = getAppUserEventBridge(superAdmin, appUser, eventBridge);
        linkEventBridge.setAccessToken(this.jwtUtils.generateToken(priKey, linkEventBridge.getTokenId()));
        linkEventBridge.setExpireTime(getOneYearFromNow());
        this.appUserEventBridgeRepository.save(linkEventBridge);
        payload.setAccessToken(linkEventBridge.getAccessToken());
        payload.setExpireTime(linkEventBridge.getExpireTime());
        payload.setTokenId(linkEventBridge.getTokenId());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, ""), payload);
    }

    /**
     * Method use to create the credential from private key
     * @param credential
     * @return String
     * */
    private String getCredentialPrivateKey(Credential credential) {
        String credJsonStr = new String(Base64.getDecoder().decode(credential.getContent().getBytes()));
        JsonObject jsonObject = JsonParser.parseString(credJsonStr).getAsJsonObject();
        return jsonObject.get("priKey").getAsString();
    }

    /**
     * Method give one year from today
     * @return Timestamp
     * */
    private Timestamp getOneYearFromNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /***
     * Method use to get the event bridge detail
     * @param eventBridge
     * @return EventBridgeResponse
     * */
    private EventBridgeResponse getEventBridgeResponse(EventBridge eventBridge) {
        EventBridgeResponse eventBridgeResponse = new EventBridgeResponse();
        eventBridgeResponse.setId(eventBridge.getId());
        eventBridgeResponse.setName(eventBridge.getName());
        eventBridgeResponse.setBridgeUrl(eventBridge.getBridgeUrl());
        eventBridgeResponse.setDescription(eventBridge.getDescription());
        if (!BarcoUtil.isNull(eventBridge.getBridgeType())) {
            GLookup bridgeType = GLookup.getGLookup(this.lookupDataCacheService
                .getChildLookupDataByParentLookupTypeAndChildLookupCode(EVENT_BRIDGE_TYPE.getName(),
                   eventBridge.getBridgeType().getLookupCode()));
            eventBridgeResponse.setBridgeType(bridgeType);
        }
        if (!BarcoUtil.isNull(eventBridge.getCredential())) {
            eventBridgeResponse.setCredential(getCredentialResponse(eventBridge.getCredential()));
        }
        eventBridgeResponse.setDateUpdated(eventBridge.getDateUpdated());
        eventBridgeResponse.setDateCreated(eventBridge.getDateCreated());
        eventBridgeResponse.setCreatedBy(getActionUser(eventBridge.getCreatedBy()));
        eventBridgeResponse.setUpdatedBy(getActionUser(eventBridge.getUpdatedBy()));
        eventBridgeResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(eventBridge.getStatus().getLookupType()));
        return eventBridgeResponse;
    }

    /***
     * Method use to get the credential detail
     * @param credential
     * @return CredentialResponse
     * */
    private CredentialResponse getCredentialResponse(Credential credential) {
        CredentialResponse credentialResponse = new CredentialResponse();
        credentialResponse.setId(credential.getId());
        credentialResponse.setName(credential.getName());
        credentialResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(credential.getStatus().getLookupType()));
        return credentialResponse;
    }

}

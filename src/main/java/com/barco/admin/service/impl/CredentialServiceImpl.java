package com.barco.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.barco.admin.service.CredentialService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.CredentialRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.CredentialResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.Credential;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.CredentialRepository;
import com.barco.model.repository.SourceTaskTypeRepository;
import com.barco.model.repository.EventBridgeRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.CREDENTIAL_TYPE;
import com.barco.model.util.lookup.GLookup;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class CredentialServiceImpl implements CredentialService {

    private Logger logger = LoggerFactory.getLogger(CredentialServiceImpl.class);

    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private EventBridgeRepository eventBridgeRepository;
    @Autowired
    private SourceTaskTypeRepository sourceTaskTypeRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    /**
     * Method use to add the new credential
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addCredential(CredentialRequest payload) throws Exception {
        logger.info("Request addCredential :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getContent())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_CONTENT_MISSING);
        }
        Credential credential = new Credential();
        credential.setName(payload.getName());
        credential.setType(CREDENTIAL_TYPE.getByLookupCode(payload.getType()));
        credential.setContent(Base64.getEncoder().encodeToString(new Gson().toJson(payload.getContent()).getBytes()));
        credential.setStatus(APPLICATION_STATUS.ACTIVE);
        credential.setCreatedBy(adminUser.get());
        credential.setUpdatedBy(adminUser.get());
        this.credentialRepository.save(credential);
        payload.setId(credential.getId());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId().toString()));
    }

    /**
     * Method use to add the new credential
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateCredential(CredentialRequest payload) throws Exception {
        logger.info("Request updateCredential :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getContent())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_CONTENT_MISSING);
        }
        Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!credential.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        }
        credential.get().setName(payload.getName());
        credential.get().setType(CREDENTIAL_TYPE.getByLookupCode(payload.getType()));
        credential.get().setContent(Base64.getEncoder().encodeToString(new Gson().toJson(payload.getContent()).getBytes()));
        if (!BarcoUtil.isNull(payload.getStatus())) {
            credential.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        this.credentialRepository.save(credential.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()));
    }

    /**
     * Method use to fine all credential
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllCredential(CredentialRequest payload) throws Exception {
        logger.info("Request fetchAllCredential :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        List<Credential> credentials;
        Boolean readFull;
        if (!BarcoUtil.isNull(payload.getStartDate()) && !BarcoUtil.isNull(payload.getEndDate())) {
            readFull = true;
            Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
            Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
            credentials = this.credentialRepository.findAllByDateCreatedBetweenAndUsernameAndStatusNot(
                startDate, endDate, payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        } else {
            readFull = true;
            credentials = this.credentialRepository.findAllByCreatedByAndStatusNot(adminUser.get(), APPLICATION_STATUS.DELETE);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, credentials.stream()
            .map(credential -> this.getCredentialResponse(credential, readFull)).collect(Collectors.toList()));
    }

    /**
     * Method use to fine all credential type
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllCredentialByType(CredentialRequest payload) throws Exception {
        logger.info("Request fetchAllCredential :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getTypes())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_TYPE_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Set<CREDENTIAL_TYPE> types =  payload.getTypes().stream().map(type -> CREDENTIAL_TYPE.getByLookupCode(type)).collect(Collectors.toSet());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.credentialRepository.findAllByCreatedByAndTypeInAndStatusNot(adminUser.get(), types, APPLICATION_STATUS.DELETE).stream()
                .map(credential -> this.getCredentialResponse(credential, true)).collect(Collectors.toList()));
    }

    /**
     * Method use to fine credential by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchCredentialById(CredentialRequest payload) throws Exception {
        logger.info("Request fetchCredentialById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_ID_MISSING);
        }
        Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!credential.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        }
        CredentialResponse credentialResponse = new CredentialResponse();
        credentialResponse.setId(credential.get().getId());
        credentialResponse.setName(credential.get().getName());
        credentialResponse.setType(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
            CREDENTIAL_TYPE.getName(),credential.get().getType().getLookupCode())));
        credentialResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(credential.get().getStatus().getLookupCode()));
        credentialResponse.setDateCreated(credential.get().getDateCreated());
        credentialResponse.setContent(new Gson().fromJson(new String(Base64.getDecoder().decode(credential.get().getContent().getBytes())), Object.class));
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, credentialResponse);
    }

    /**
     * Method use to delete credential by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteCredential(CredentialRequest payload) throws Exception {
        logger.info("Request deleteCredential :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_ID_MISSING);
        }
        Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatusNot(payload.getId(),
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!credential.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        }
        credential.get().setStatus(APPLICATION_STATUS.DELETE);
        // de-link the source and event bridge
        this.deleteEventBridgesCredential(credential.get());
        this.deleteSourceTaskCredential(credential.get());
        this.credentialRepository.save(credential.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()));
    }

    /**
     * Method use to delete all credential
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllCredential(CredentialRequest payload) throws Exception {
        logger.info("Request deleteAllCredential :- " + payload);
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
        List<Credential> credentials = this.credentialRepository.findAllByIdIn(payload.getIds());
        credentials.forEach(credential -> {
            credential.setStatus(APPLICATION_STATUS.DELETE);
            // de-link the source and event bridge
            this.deleteEventBridgesCredential(credential);
            this.deleteSourceTaskCredential(credential);
        });
        this.credentialRepository.saveAll(credentials);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, ""), payload);
    }

    /**
     * Method using to delete the event bridge credential
     * @param credential
     * @return void
     * */
    private void deleteEventBridgesCredential(Credential credential) {
        if (!BarcoUtil.isNull(credential.getEventBridges())) {
            credential.getEventBridges().stream()
            .filter(eventBridge -> !eventBridge.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(eventBridge -> {
                eventBridge.setCredential(null);
                // if Credential is delete from the event bridge the delete teh all app user event bridge
                if (!BarcoUtil.isNull(eventBridge.getAppUserEventBridges())) {
                    eventBridge.getAppUserEventBridges().clear();
                }
                return eventBridge;
            }).collect(Collectors.toList());
        }
    }

    /**
     * Method using to delete the source credential
     * @param credential
     * @return void
     * */
    private void deleteSourceTaskCredential(Credential credential) {
        if (!BarcoUtil.isNull(credential.getSourceTaskTypes())) {
            credential.getSourceTaskTypes().stream()
            .filter(sourceTaskTypes -> !sourceTaskTypes.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(sourceTaskTypes -> {
                sourceTaskTypes.setCredential(null);
                return sourceTaskTypes;
            }).collect(Collectors.toList());
        }
    }

    /**
     * Method use to get the credential response
     * @param credential
     * @param readFull
     * @return CredentialResponse
     * */
    private CredentialResponse getCredentialResponse(Credential credential, Boolean readFull) {
        CredentialResponse credentialResponse = new CredentialResponse();
        credentialResponse.setId(credential.getId());
        credentialResponse.setName(credential.getName());
        credentialResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(credential.getStatus().getLookupType()));
        if (readFull) {
            credentialResponse.setType(GLookup.getGLookup(
                this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
                    CREDENTIAL_TYPE.getName(),credential.getType().getLookupCode())));
            credentialResponse.setTotalCount(
                this.eventBridgeRepository.countByCredentialAndStatusNot(credential, APPLICATION_STATUS.DELETE) +
                this.sourceTaskTypeRepository.countByCredentialAndStatusNot(credential, APPLICATION_STATUS.DELETE));
            credentialResponse.setCreatedBy(getActionUser(credential.getCreatedBy()));
            credentialResponse.setUpdatedBy(getActionUser(credential.getCreatedBy()));
            credentialResponse.setDateCreated(credential.getDateCreated());
            credentialResponse.setDateUpdated(credential.getDateUpdated());
        }
        return credentialResponse;
    }

}

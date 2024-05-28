package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.WebHookService;
import com.barco.common.security.JwtUtils;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.LinkWHURequest;
import com.barco.model.dto.request.WebHookRequest;
import com.barco.model.dto.response.*;
import com.barco.model.pojo.*;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.AppUserWebHookRepository;
import com.barco.model.repository.CredentialRepository;
import com.barco.model.repository.WebHookRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.GLookup;
import com.barco.model.util.lookup.HOOK_TYPE;
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
public class WebHookServiceImpl implements WebHookService {

    private Logger logger = LoggerFactory.getLogger(WebHookServiceImpl.class);

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private QueryService queryService;
    @Autowired
    private WebHookRepository webHookRepository;
    @Autowired
    private AppUserWebHookRepository appUserWebHookRepository;
    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private AppUserRepository appUserRepository;

    /**
     * Method use to add webhook
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addWebHook(WebHookRequest payload) throws Exception {
        logger.info("Request addWebHook :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getHookUrl())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_URL_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getHookType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getCredentialId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_CREDENTIAL_MISSING);
        }
        Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatus(
            payload.getCredentialId(), adminUser.get().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!credential.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        }
        WebHook webHook = new WebHook();
        webHook.setName(payload.getName());
        webHook.setHookUrl(payload.getHookUrl());
        webHook.setDescription(payload.getDescription());
        webHook.setHookType(HOOK_TYPE.getByLookupCode(payload.getHookType()));
        webHook.setCredential(credential.get());
        webHook.setCreatedBy(adminUser.get());
        webHook.setUpdatedBy(adminUser.get());
        webHook.setStatus(APPLICATION_STATUS.ACTIVE);
        this.webHookRepository.save(webHook);
        // link web hook to user
        AppUserWebHook appUserWebHook = new AppUserWebHook();
        appUserWebHook.setTokenId(UUID.randomUUID().toString());
        appUserWebHook.setWebhook(webHook);
        appUserWebHook.setAppUser(adminUser.get());
        appUserWebHook.setCreatedBy(adminUser.get());
        appUserWebHook.setUpdatedBy(adminUser.get());
        appUserWebHook.setStatus(APPLICATION_STATUS.ACTIVE);
        this.appUserWebHookRepository.save(appUserWebHook);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, webHook.getId().toString()), payload);
    }

    /**
     * Method use to update webhook
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateWebHook(WebHookRequest payload) throws Exception {
        logger.info("Request updateWebHook :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getHookUrl())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_URL_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getHookType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getCredentialId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_CREDENTIAL_MISSING);
        }
        Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatus(
            payload.getCredentialId(), adminUser.get().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!credential.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        }
        Optional<WebHook> webHook = this.webHookRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!webHook.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_NOT_FOUND);
        }
        webHook.get().setHookUrl(payload.getHookUrl());
        webHook.get().setName(payload.getName());
        webHook.get().setDescription(payload.getDescription());
        webHook.get().setHookType(HOOK_TYPE.getByLookupCode(payload.getHookType()));
        webHook.get().setCredential(credential.get());
        webHook.get().setUpdatedBy(adminUser.get());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            // if status is in-active & delete then we have filter the role and show only those role in user detail
            webHook.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            webHook.get().getAppUserWebHooks().stream()
                .map(appUserWebHook -> {
                    appUserWebHook.setStatus(webHook.get().getStatus());
                    return appUserWebHook;
                }).collect(Collectors.toList());
        }
        this.webHookRepository.save(webHook.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()), payload);
    }

    /**
     * Method use to fetch all webhook
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllWebHook(WebHookRequest payload) throws Exception {
        logger.info("Request fetchAllWebHook :- " + payload);
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.webHookRepository.findAllByStatusNotOrderByDateCreatedDesc(APPLICATION_STATUS.DELETE)
                .stream().map(webHook -> getWebHookResponse(webHook)).collect(Collectors.toList()));
    }

    /**
     * Method use to fetch all webhook
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchWebHookById(WebHookRequest payload) throws Exception {
        logger.info("Request fetchWebHookById :- " + payload);
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_ID_MISSING);
        }
        Optional<WebHook> webHook = this.webHookRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE);
        if (!webHook.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.WEBHOOK_NOT_FOUND_WITH_ID, payload.getId().toString()));
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, getWebHookResponse(webHook.get()));
    }

    /**
     * Method use to delete webhook by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteWebHookById(WebHookRequest payload) throws Exception {
        logger.info("Request deleteWebHookById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_ID_MISSING);
        }
        Optional<WebHook> webHook = this.webHookRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE);
        if (!webHook.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.WEBHOOK_NOT_FOUND, payload.getId().toString()));
        }
        this.webHookRepository.delete(webHook.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()), payload);
    }

    /**
     * Method use to delete all webhook
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllWebHook(WebHookRequest payload) throws Exception {
        logger.info("Request deleteAllWebHook :- " + payload);
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
        this.webHookRepository.deleteAll(this.webHookRepository.findAllByIdIn(payload.getIds()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, ""), payload);
    }

    /**
     * Method use to fetch the link webhook with user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchLinkWebHookWitUser(WebHookRequest payload) throws Exception {
        logger.info("Request fetchLinkWebHookWitUser :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_ID_MISSING);
        }
        Optional<WebHook> webHook = this.webHookRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!webHook.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.WEBHOOK_NOT_FOUND_WITH_ID, payload.getId()), payload);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(String.format(QueryService.FETCH_LINK_WEB_HOOK_WITH_USER,
            webHook.get().getId(), APPLICATION_STATUS.DELETE.getLookupCode()));
        List<LinkRPUResponse> linkRPUResponses = new ArrayList<>();
        if (!BarcoUtil.isNull(queryResponse.getData())) {
            for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
                linkRPUResponses.add(getLinkRPUResponse(data, webHook.get().getStatus()));
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, linkRPUResponses);
    }

    /**
     * Method user to link the webhook with user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkWebHookWithUser(LinkWHURequest payload) throws Exception {
        logger.info("Request linkWebHookWithUser :- " + payload);
        Optional<AppUser> superAdmin = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!superAdmin.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getAppUserId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APP_USER_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getLinked())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LINKED_MISSING);
        }
        Optional<WebHook> webHook = this.webHookRepository.findById(payload.getId());
        if (!webHook.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.WEBHOOK_NOT_FOUND_WITH_ID, payload.getId()), payload);
        }
        Optional<AppUser> appUser = this.appUserRepository.findById(payload.getAppUserId());
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.APPUSER_NOT_FOUND, payload.getAppUserId()), payload);
        }
        if (payload.getLinked()) {
            Credential credential = webHook.get().getCredential();
            if (BarcoUtil.isNull(credential.getContent())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_CONTENT_MISSING);
            }
            String credJsonStr = new String(Base64.getDecoder().decode(credential.getContent().getBytes()));
            JsonObject jsonObject = JsonParser.parseString(credJsonStr).getAsJsonObject();
            String priKey = jsonObject.get("priKey").getAsString();
            AppUserWebHook linkWebHook = getAppUserWebHook(superAdmin.get(), appUser.get(), webHook.get());
            linkWebHook.setAccessToken(this.jwtUtils.generateToken(appUser.get().getUsername(), priKey, linkWebHook.getTokenId()));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            linkWebHook.setExpireTime(new Timestamp(calendar.getTimeInMillis()));
            this.appUserWebHookRepository.save(linkWebHook);
            payload.setAccessToken(linkWebHook.getAccessToken());
            payload.setExpireTime(linkWebHook.getExpireTime());
            payload.setTokenId(linkWebHook.getTokenId());
        } else {
            this.queryService.deleteQuery(String.format(QueryService.DELETE_APP_USER_WEBHOOK_BY_WEBHOOK_ID_AND_APP_USER_ID,
                webHook.get().getId(), appUser.get().getId()));
        }
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, ""), payload);
    }

    /**
     * Method use to generate the web hook
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse genWebHookToken(LinkWHURequest payload) throws Exception {
        logger.info("Request genWebHookToken :- " + payload);
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getTokenId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_GEN_TOKEN_MISSING);
        }
        Optional<AppUserWebHook> linkWebHook = this.appUserWebHookRepository.findByTokenId(payload.getTokenId());
        if (!linkWebHook.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WEBHOOK_NOT_FOUND_WITH_GEN_TOKEN);
        }
        // check the web hook link with the cred or not if link then check status for cred status
        WebHook webHook = linkWebHook.get().getWebhook();
        if (BarcoUtil.isNull(webHook.getCredential())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        } else if (!webHook.getCredential().getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_ACTIVE);
        }
        Credential credential = webHook.getCredential();
        if (BarcoUtil.isNull(credential.getContent())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_CONTENT_MISSING);
        }
        String credJsonStr = new String(Base64.getDecoder().decode(credential.getContent().getBytes()));
        JsonObject jsonObject = JsonParser.parseString(credJsonStr).getAsJsonObject();
        String priKey = jsonObject.get("priKey").getAsString();
        linkWebHook.get().setAccessToken(this.jwtUtils.generateToken(linkWebHook.get().getAppUser().getUsername(),
             priKey, linkWebHook.get().getTokenId()));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        linkWebHook.get().setExpireTime(new Timestamp(calendar.getTimeInMillis()));
        this.appUserWebHookRepository.save(linkWebHook.get());
        payload.setAccessToken(linkWebHook.get().getAccessToken());
        payload.setExpireTime(linkWebHook.get().getExpireTime());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, ""), payload);
    }

    /***
     * Method use to get the webhook detail
     * @param webHook
     * @return WebHookResponse
     * */
    private WebHookResponse getWebHookResponse(WebHook webHook) {
        WebHookResponse webHookResponse = new WebHookResponse();
        webHookResponse.setId(webHook.getId());
        webHookResponse.setName(webHook.getName());
        webHookResponse.setHookUrl(webHook.getHookUrl());
        webHookResponse.setDescription(webHook.getDescription());
        if (!BarcoUtil.isNull(webHook.getHookType())) {
            GLookup hookType = GLookup.getGLookup(this.lookupDataCacheService
                .getChildLookupDataByParentLookupTypeAndChildLookupCode(HOOK_TYPE.getName(),
                   webHook.getHookType().getLookupCode()));
            webHookResponse.setHookType(hookType);
        }
        if (!BarcoUtil.isNull(webHook.getCredential())) {
            webHookResponse.setCredential(getCredentialResponse(webHook.getCredential()));
        }
        webHookResponse.setDateUpdated(webHook.getDateUpdated());
        webHookResponse.setDateCreated(webHook.getDateCreated());
        webHookResponse.setCreatedBy(getActionUser(webHook.getCreatedBy()));
        webHookResponse.setUpdatedBy(getActionUser(webHook.getUpdatedBy()));
        webHookResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(webHook.getStatus().getLookupType()));
        return webHookResponse;
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

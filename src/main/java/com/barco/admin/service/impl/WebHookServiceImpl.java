package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.WebHookService;
import com.barco.model.dto.request.WebHookRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.CredentialRepository;
import com.barco.model.repository.WebHookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Nabeel Ahmed
 */
@Service
public class WebHookServiceImpl implements WebHookService {

    private Logger logger = LoggerFactory.getLogger(WebHookServiceImpl.class);

    @Autowired
    private WebHookRepository webHookRepository;
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
        return null;
    }

    /**
     * Method use to update webhook
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateWebHook(WebHookRequest payload) throws Exception {
        logger.info("Request updateWebHook :- " + payload);
        return null;
    }

    /**
     * Method use to fetch all webhook
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllWebHook(WebHookRequest payload) throws Exception {
        logger.info("Request fetchAllWebHook :- " + payload);
        return null;
    }

    /**
     * Method use to fetch all webhook
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchWebHookById(WebHookRequest payload) throws Exception {
        logger.info("Request fetchWebHookById :- " + payload);
        return null;
    }

    /**
     * Method use to delete webhook by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteWebHookById(WebHookRequest payload) throws Exception {
        logger.info("Request deleteWebHookById :- " + payload);
        return null;
    }

    /**
     * Method use to delete all webhook
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllWebHook(WebHookRequest payload) throws Exception {
        logger.info("Request deleteAllWebHook :- " + payload);
        return null;
    }

    /**
     * Method use to fetch the link webhook with user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchLinkWebHookWitUser(WebHookRequest payload) throws Exception {
        logger.info("Request fetchLinkWebHookWitUser :- " + payload);
        return null;
    }

    /**
     * Method user to link the webhook with user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkWebHookWithUser(WebHookRequest payload) throws Exception {
        logger.info("Request linkWebHookWithUser :- " + payload);
        return null;
    }

}

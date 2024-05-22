package com.barco.admin.service.impl;

import com.barco.admin.service.WebHookService;
import com.barco.model.dto.request.LinkEURequest;
import com.barco.model.dto.request.WebHookRequest;
import com.barco.model.dto.response.AppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Nabeel Ahmed
 */
@Service
public class WebHookServiceImpl implements WebHookService {

    private Logger logger = LoggerFactory.getLogger(WebHookServiceImpl.class);

    @Override
    public AppResponse addWebHook(WebHookRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse updateWebHook(WebHookRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse fetchAllWebHook(WebHookRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse fetchWebHookById(WebHookRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse deleteWebHookById(WebHookRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse deleteAllWebHook(WebHookRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse fetchLinkWebHookWitUser(LinkEURequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse linkWebHookWithUser(LinkEURequest payload) throws Exception {
        return null;
    }
}

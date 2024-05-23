package com.barco.admin.service;

import com.barco.model.dto.request.WebHookRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface WebHookService extends RootService {

    public AppResponse addWebHook(WebHookRequest payload) throws Exception;

    public AppResponse updateWebHook(WebHookRequest payload) throws Exception;

    public AppResponse fetchAllWebHook(WebHookRequest payload) throws Exception;

    public AppResponse fetchWebHookById(WebHookRequest payload) throws Exception;

    public AppResponse deleteWebHookById(WebHookRequest payload) throws Exception;

    public AppResponse deleteAllWebHook(WebHookRequest payload) throws Exception;

    public AppResponse fetchLinkWebHookWitUser(WebHookRequest payload) throws Exception;

    public AppResponse linkWebHookWithUser(WebHookRequest payload) throws Exception;

}

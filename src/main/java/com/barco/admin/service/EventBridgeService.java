package com.barco.admin.service;

import com.barco.model.dto.request.LinkEBURequest;
import com.barco.model.dto.request.EventBridgeRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface EventBridgeService extends RootService {

    public AppResponse addEventBridge(EventBridgeRequest payload) throws Exception;

    public AppResponse updateEventBridge(EventBridgeRequest payload) throws Exception;

    public AppResponse fetchAllEventBridge(EventBridgeRequest payload) throws Exception;

    public AppResponse fetchEventBridgeById(EventBridgeRequest payload) throws Exception;

    public AppResponse fetchEventBridgeByBridgeType(EventBridgeRequest payload) throws Exception;

    public AppResponse deleteEventBridgeById(EventBridgeRequest payload) throws Exception;

    public AppResponse deleteAllEventBridge(EventBridgeRequest payload) throws Exception;

    public AppResponse fetchLinkEventBridgeWitUser(EventBridgeRequest payload) throws Exception;

    public AppResponse linkEventBridgeWithUser(LinkEBURequest payload) throws Exception;

    public AppResponse genEventBridgeToken(LinkEBURequest payload) throws Exception;

}

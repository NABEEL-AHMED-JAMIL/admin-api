package com.barco.admin.service;

import com.barco.common.request.ConfigurationMakerRequest;
import com.barco.model.dto.request.DynamicPayloadRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface DynamicPayloadService extends RootService {

    public AppResponse addDynamicPayload(DynamicPayloadRequest payload) throws Exception;

    public AppResponse updateDynamicPayload(DynamicPayloadRequest payload) throws Exception;

    public AppResponse fetchAllDynamicPayload(DynamicPayloadRequest payload) throws Exception;

    public AppResponse fetchDynamicPayloadById(DynamicPayloadRequest payload) throws Exception;

    public AppResponse deleteDynamicPayloadById(DynamicPayloadRequest payload) throws Exception;

    public AppResponse deleteAllDynamicPayload(DynamicPayloadRequest payload) throws Exception;

    public AppResponse xmlCreateChecker(ConfigurationMakerRequest payload) throws Exception;

    public AppResponse jsonCreateChecker(ConfigurationMakerRequest payload) throws Exception;

}

package com.barco.admin.service;

import com.barco.model.dto.request.STTRequest;
import com.barco.model.dto.request.SourceTaskRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface SourceTaskService extends RootService {

    public AppResponse addSourceTask(SourceTaskRequest payload) throws Exception;

    public AppResponse editSourceTask(SourceTaskRequest payload) throws Exception;

    public AppResponse deleteSourceTask(SourceTaskRequest payload) throws Exception;

    public AppResponse deleteAllSourceTask(SourceTaskRequest payload) throws Exception;

    public AppResponse fetchAllSourceTask(SourceTaskRequest payload) throws Exception;

    public AppResponse fetchSourceTaskById(SourceTaskRequest payload) throws Exception;

    public AppResponse fetchAllSTT(STTRequest payload) throws Exception;

}

package com.barco.admin.service;

import com.barco.model.dto.request.STTRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface SourceTaskTypeService extends RootService {

    public AppResponse addSTT(STTRequest payload) throws Exception;

    public AppResponse editSTT(STTRequest payload) throws Exception;

    public AppResponse deleteSTT(STTRequest payload) throws Exception;

    public AppResponse fetchSTTBySttId(STTRequest payload) throws Exception;

    public AppResponse fetchAllSTT(STTRequest payload) throws Exception;

    public AppResponse deleteAllSTT(STTRequest payload) throws Exception;

    public AppResponse fetchAllSTTLinkForm(STTRequest payload) throws Exception;

    public AppResponse linkSTTForm(STTRequest payload) throws Exception;

}

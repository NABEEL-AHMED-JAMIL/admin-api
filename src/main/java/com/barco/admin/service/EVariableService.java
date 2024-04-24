package com.barco.admin.service;

import com.barco.model.dto.request.EnVariablesRequest;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.LinkEURequest;
import com.barco.model.dto.response.AppResponse;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface EVariableService extends RootService {

    public AppResponse addEnVariable(EnVariablesRequest payload) throws Exception;

    public AppResponse updateEnVariable(EnVariablesRequest payload) throws Exception;

    public AppResponse fetchAllEnVariable(EnVariablesRequest payload) throws Exception;

    public AppResponse fetchEnVariableById(EnVariablesRequest payload) throws Exception;

    public AppResponse fetchUserEnvByEnvKey(EnVariablesRequest payload) throws Exception;

    public AppResponse deleteEnVariableById(EnVariablesRequest payload) throws Exception;

    public AppResponse deleteAllEnVariable(EnVariablesRequest payload) throws Exception;

    public ByteArrayOutputStream downloadEnVariableTemplateFile() throws Exception;

    public ByteArrayOutputStream downloadEnVariable(EnVariablesRequest payload) throws Exception;

    public AppResponse uploadEnVariable(FileUploadRequest payload) throws Exception;

    public AppResponse fetchLinkEVariableWitUser(LinkEURequest payload) throws Exception;

    public AppResponse linkEVariableWithUser(LinkEURequest payload) throws Exception;

}

package com.barco.admin.service;

import com.barco.model.dto.request.NotificationRequest;
import com.barco.model.dto.request.QueryRequest;
import com.barco.model.dto.request.TemplateRegRequest;
import com.barco.model.dto.response.AppResponse;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface SettingApiService {

    public AppResponse dynamicQueryResponse(QueryRequest queryRequest);

    public ByteArrayOutputStream downloadDynamicQueryFile(QueryRequest queryRequest) throws Exception;

    public AppResponse addTemplateReg(TemplateRegRequest requestPayload) throws Exception;

    public AppResponse editTemplateReg(TemplateRegRequest requestPayload) throws Exception;

    public AppResponse findTemplateRegByTemplateId(TemplateRegRequest requestPayload) throws Exception;

    public AppResponse fetchTemplateReg(TemplateRegRequest requestPayload) throws Exception;

    public AppResponse deleteTemplateReg(TemplateRegRequest requestPayload) throws Exception;

}
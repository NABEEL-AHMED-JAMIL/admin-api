package com.barco.admin.service;

import com.barco.model.dto.request.QueryRequest;
import com.barco.model.dto.request.SessionUser;
import com.barco.model.dto.response.AppResponse;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface SettingService extends RootService {

    public AppResponse fetchSettingDashboard(SessionUser principal);

    public AppResponse dynamicQueryResponse(QueryRequest payload);

    public ByteArrayOutputStream downloadDynamicQueryFile(QueryRequest payload) throws Exception;

}

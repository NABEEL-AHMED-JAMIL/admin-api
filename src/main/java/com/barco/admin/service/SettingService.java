package com.barco.admin.service;

import com.barco.model.dto.request.QueryRequest;
import com.barco.model.dto.request.SessionUser;
import com.barco.model.dto.response.AppResponse;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface SettingService extends RootService {

    public AppResponse fetchSettingDashboard(SessionUser sessionUser) throws Exception;

    public AppResponse fetchCountryData(SessionUser sessionUser) throws Exception;

    public AppResponse dynamicQueryResponse(QueryRequest payload) throws Exception;

    public ByteArrayOutputStream downloadDynamicQueryFile(QueryRequest payload) throws Exception;

}

package com.barco.admin.service;

import com.barco.model.dto.request.QueryInquiryRequest;
import com.barco.model.dto.request.QueryRequest;
import com.barco.model.dto.request.SessionUser;
import com.barco.model.dto.response.AppResponse;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface SettingService extends RootService {

    public AppResponse fetchStatisticsDashboard(SessionUser payload) throws Exception;

    public AppResponse fetchCountryData(SessionUser payload) throws Exception;

    public AppResponse dynamicQueryResponse(QueryRequest payload) throws Exception;

    public ByteArrayOutputStream downloadDynamicQueryFile(QueryRequest payload) throws Exception;

    public AppResponse addQueryInquiry(QueryInquiryRequest payload) throws Exception;

    public AppResponse updateQueryInquiry(QueryInquiryRequest payload) throws Exception;

    public AppResponse fetchQueryInquiryById(QueryInquiryRequest payload) throws Exception;

    public AppResponse fetchAllQueryInquiry(QueryInquiryRequest payload) throws Exception;

    public AppResponse deleteQueryInquiryById(QueryInquiryRequest payload) throws Exception;

    public AppResponse deleteAllQueryInquiry(QueryInquiryRequest payload) throws Exception;

}

package com.barco.admin.service;

import com.barco.model.dto.report.ReportRequest;
import com.barco.model.dto.request.ReportSettingRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface ReportSettingService extends RootService {
    
    public AppResponse addReportSetting(ReportSettingRequest payload) throws Exception;

    public AppResponse updateReportSetting(ReportSettingRequest payload) throws Exception;

    public AppResponse fetchAllReportSetting(ReportSettingRequest payload) throws Exception;

    public AppResponse fetchReportSettingByReportId(ReportSettingRequest payload) throws Exception;

    public AppResponse fetchAllReportByGroup(ReportSettingRequest payload) throws Exception;

    public AppResponse deleteReportSettingById(ReportSettingRequest payload) throws Exception;

    public AppResponse deleteAllReportSetting(ReportSettingRequest payload) throws Exception;

    public AppResponse fetchReportResult(ReportRequest payload) throws Exception;

}

package com.barco.admin.service;

import com.barco.model.dto.request.DashboardSettingRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface DashboardSettingService extends RootService {

    public AppResponse addDashboardSetting(DashboardSettingRequest payload) throws Exception;

    public AppResponse updateDashboardSetting(DashboardSettingRequest payload) throws Exception;

    public AppResponse fetchAllDashboardSetting(DashboardSettingRequest payload) throws Exception;

    public AppResponse fetchDashboardSettingById(DashboardSettingRequest payload) throws Exception;

    public AppResponse fetchAllDashboardSettingByGroup(DashboardSettingRequest payload) throws Exception;

    public AppResponse deleteDashboardSettingById(DashboardSettingRequest payload) throws Exception;

    public AppResponse deleteAllDashboardSetting(DashboardSettingRequest payload) throws Exception;

}

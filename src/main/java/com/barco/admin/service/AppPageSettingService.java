package com.barco.admin.service;

import com.barco.model.dto.request.AppPageSettingRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * Api use to perform crud operation
 * @author Nabeel Ahmed
 */
public interface AppPageSettingService {

    public AppResponse addAppPageSetting(AppPageSettingRequest payload) throws Exception;

    public AppResponse updateAppPageSetting(AppPageSettingRequest payload) throws Exception;

    public AppResponse fetchAppPageSettingById(AppPageSettingRequest payload) throws Exception;

    public AppResponse fetchAllAppPageSetting(AppPageSettingRequest payload) throws Exception;

    public AppResponse deleteAppPageSetting(AppPageSettingRequest payload) throws Exception;

    public AppResponse deleteAllAppPageSetting(AppPageSettingRequest payload) throws Exception;
}

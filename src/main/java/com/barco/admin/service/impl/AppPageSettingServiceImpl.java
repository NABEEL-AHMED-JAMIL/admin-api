package com.barco.admin.service.impl;

import com.barco.admin.service.AppPageSettingService;
import com.barco.model.dto.request.AppPageSettingRequest;
import com.barco.model.dto.response.AppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Nabeel Ahmed
 */
@Service
public class AppPageSettingServiceImpl implements AppPageSettingService {

    private Logger logger = LoggerFactory.getLogger(AppPageSettingServiceImpl.class);

    public AppPageSettingServiceImpl() {}

    /**
     * Method use for add app page setting
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse addAppPageSetting(AppPageSettingRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use for update app page setting
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse updateAppPageSetting(AppPageSettingRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use for fetch all app page setting
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse fetchAppPageSettingById(AppPageSettingRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use for fetch all app page setting
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse fetchAllAppPageSetting(AppPageSettingRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use to delete app page setting
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse deleteAppPageSetting(AppPageSettingRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use to delete all app page setting
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse deleteAllAppPageSetting(AppPageSettingRequest payload) throws Exception {
        return null;
    }
}

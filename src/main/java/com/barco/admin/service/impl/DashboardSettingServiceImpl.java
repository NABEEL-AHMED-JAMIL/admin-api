package com.barco.admin.service.impl;

import com.barco.admin.service.DashboardSettingService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.model.dto.request.DashboardSettingRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.DashboardSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Nabeel Ahmed
 */
@Service
public class DashboardSettingServiceImpl implements DashboardSettingService {

    private Logger logger = LoggerFactory.getLogger(DashboardSettingServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private DashboardSettingRepository dahDashboardSettingRepository;

    /**
     * Method use to add dashboard setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addDashboardSetting(DashboardSettingRequest payload) throws Exception {
        logger.info("Request addDashboardSetting :- " + payload);
        return null;
    }

    /**
     * Method use to update the dashboard setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateDashboardSetting(DashboardSettingRequest payload) throws Exception {
        logger.info("Request updateDashboardSetting :- " + payload);
        return null;
    }

    /**
     * Method use to fetch all dashboard setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllDashboardSetting(DashboardSettingRequest payload) throws Exception {
        logger.info("Request fetchAllDashboardSetting :- " + payload);
        return null;
    }

    /**
     * Method use to fetch dashboard by dashboard id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchDashboardSettingByDashboardId(DashboardSettingRequest payload) throws Exception {
        logger.info("Request fetchDashboardSettingByDashboardId :- " + payload);
        return null;
    }

    /**
     * Method use to fetch all dashboard by group
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllDashboardSettingByGroup(DashboardSettingRequest payload) throws Exception {
        logger.info("Request fetchAllDashboardSettingByGroup :- " + payload);
        return null;
    }

    /**
     * Method use to delete the dashboard by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteDashboardSettingById(DashboardSettingRequest payload) throws Exception {
        logger.info("Request deleteDashboardSettingById :- " + payload);
        return null;
    }

    /**
     * Method use to delete all dashboard
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllDashboardSetting(DashboardSettingRequest payload) throws Exception {
        logger.info("Request deleteAllDashboardSetting :- " + payload);
        return null;
    }
}

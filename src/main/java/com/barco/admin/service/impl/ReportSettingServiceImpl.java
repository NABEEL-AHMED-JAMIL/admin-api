package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.ReportSettingService;
import com.barco.model.dto.request.ReportSettingRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.ReportSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Nabeel Ahmed
 */
@Service
public class ReportSettingServiceImpl implements ReportSettingService {

    private Logger logger = LoggerFactory.getLogger(ReportSettingServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private ReportSettingRepository reportSettingRepository;


    /**
     * Method use to add report setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addReportSetting(ReportSettingRequest payload) throws Exception {
        logger.info("Request addReportSetting :- " + payload);
        return null;
    }

    /**
     * Method use to update report setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateReportSetting(ReportSettingRequest payload) throws Exception {
        logger.info("Request updateReportSetting :- " + payload);
        return null;
    }

    /**
     * Method use to fetch all report
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllReportSetting(ReportSettingRequest payload) throws Exception {
        logger.info("Request fetchAllReportSetting :- " + payload);
        return null;
    }

    /**
     * Method use to fetch all report by group
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllReportByGroup(ReportSettingRequest payload) throws Exception {
        logger.info("Request fetchAllReportByGroup :- " + payload);
        return null;
    }

    /**
     * Method use to fetch the report setting by report id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchReportSettingByReportId(ReportSettingRequest payload) throws Exception {
        logger.info("Request fetchReportSettingByReportId :- " + payload);
        return null;
    }

    /**
     * Method use to delete the report setting by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteReportSettingById(ReportSettingRequest payload) throws Exception {
        logger.info("Request deleteReportSettingById :- " + payload);
        return null;
    }

    /**
     * Method use to delete all report setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllReportSetting(ReportSettingRequest payload) throws Exception {
        logger.info("Request deleteAllReportSetting :- " + payload);
        return null;
    }
}

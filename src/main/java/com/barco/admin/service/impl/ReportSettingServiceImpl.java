package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.ReportSettingService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.ReportSettingRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.ReportSettingResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.ReportSetting;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.LookupDataRepository;
import com.barco.model.repository.ReportSettingRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class ReportSettingServiceImpl implements ReportSettingService {

    private Logger logger = LoggerFactory.getLogger(ReportSettingServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ReportSettingRepository reportSettingRepository;
    @Autowired
    private LookupDataRepository lookupDataRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    /**
     * Method use to add report setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addReportSetting(ReportSettingRequest payload) throws Exception {
        logger.info("Request addReportSetting :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId().toString()));
    }

    /**
     * Method use to update report setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateReportSetting(ReportSettingRequest payload) throws Exception {
        logger.info("Request updateReportSetting :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_ID_MISSING);
        }
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()));
    }

    /**
     * Method use to fetch all report
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllReportSetting(ReportSettingRequest payload) throws Exception {
        logger.info("Request fetchAllReportSetting :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        List<ReportSetting> reportSettings;
        if (!BarcoUtil.isNull(payload.getStartDate()) && !BarcoUtil.isNull(payload.getEndDate())) {
            Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
            Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
            reportSettings = this.reportSettingRepository.findAllByDateCreatedBetweenAndUsernameAndStatusNot(
                startDate, endDate, payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        } else {
            reportSettings = this.reportSettingRepository.findAllByCreatedByAndStatusNot(adminUser.get(), APPLICATION_STATUS.DELETE);
        }
        List<ReportSettingResponse> reportSettingResponses = reportSettings
            .stream().map(reportSetting -> {
                ReportSettingResponse reportSettingResponse = getReportSettingResponse(reportSetting);
                return reportSettingResponse;
            }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, reportSettingResponses);
    }

    /**
     * Method use to fetch the report setting by report id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchReportSettingByReportId(ReportSettingRequest payload) throws Exception {
        logger.info("Request fetchReportSettingByReportId :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_ID_MISSING);
        }
        Optional<ReportSetting> reportSetting = this.reportSettingRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!reportSetting.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_NOT_FOUND);
        }
        ReportSettingResponse reportSettingResponse = getReportSettingResponse(reportSetting.get());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, reportSettingResponse);
    }

    /**
     * Method use to fetch all report by group
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllReportByGroup(ReportSettingRequest payload) throws Exception {
        logger.info("Request fetchAllReportByGroup :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
                payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        List<ReportSetting> reportSettings;
        if (!BarcoUtil.isNull(payload.getStartDate()) && !BarcoUtil.isNull(payload.getEndDate())) {
            Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
            Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
            reportSettings = this.reportSettingRepository.findAllByDateCreatedBetweenAndUsernameAndStatusNot(
                    startDate, endDate, payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        } else {
            reportSettings = this.reportSettingRepository.findAllByCreatedByAndStatusNot(adminUser.get(), APPLICATION_STATUS.DELETE);
        }
        Map<String, List<ReportSettingResponse>> reportSettingHashtable = reportSettings
            .stream().map(reportSetting -> {
                ReportSettingResponse reportSettingResponse = getReportSettingResponse(reportSetting);
                return reportSettingResponse;
            }).collect(Collectors.groupingBy(reportSetting -> reportSetting.getGroupType().getLookupType(), Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, reportSettingHashtable);
    }

    /**
     * Method use to delete the report setting by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteReportSettingById(ReportSettingRequest payload) throws Exception {
        logger.info("Request deleteReportSettingById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_ID_MISSING);
        }
        Optional<ReportSetting> reportSetting = this.reportSettingRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!reportSetting.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_NOT_FOUND);
        }
        reportSetting.get().setStatus(APPLICATION_STATUS.DELETE);
        this.reportSettingRepository.save(reportSetting.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()));
    }

    /**
     * Method use to delete all report setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllReportSetting(ReportSettingRequest payload) throws Exception {
        logger.info("Request deleteAllReportSetting :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getIds())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        List<ReportSetting> reportSettings = this.reportSettingRepository.findAllByIdIn(payload.getIds());
        reportSettings.forEach(reportSetting -> reportSetting.setStatus(APPLICATION_STATUS.DELETE));
        this.reportSettingRepository.saveAll(reportSettings);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, ""), payload);
    }
}

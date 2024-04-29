package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.ReportSettingService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.ReportSettingRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.ReportSettingResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.GenForm;
import com.barco.model.pojo.ReportSetting;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.GenFormRepository;
import com.barco.model.repository.LookupDataRepository;
import com.barco.model.repository.ReportSettingRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.*;
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
    private GenFormRepository genFormRepository;
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
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getGroupType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_GROUP_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getPayloadRef())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_PAYLOAD_REF_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsPdf())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_PDF_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsCsv())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_CSV_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsXlsx())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_XLSX_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsData())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_DATA_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsFirstDimension())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_FIRST_DIMENSION_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsSecondDimension())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_SECOND_DIMENSION_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsThirdDimension())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_THIRD_DIMENSION_MISSING);
        }
        Optional<GenForm> genForm = Optional.empty();
        if (!BarcoUtil.isNull(payload.getFormRequestId())) {
            genForm = this.genFormRepository.findByIdAndCreatedByAndStatusNot(
                payload.getFormRequestId(), adminUser.get(), APPLICATION_STATUS.DELETE);
            if (!genForm.isPresent()) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
            }
        }
        ReportSetting reportSetting = new ReportSetting();
        reportSetting = getReportSetting(payload, reportSetting);
        if (genForm.isPresent()) {
            reportSetting.setGenForm(genForm.get());
        }
        reportSetting.setStatus(APPLICATION_STATUS.ACTIVE);
        reportSetting.setCreatedBy(adminUser.get());
        reportSetting.setUpdatedBy(adminUser.get());
        this.reportSettingRepository.save(reportSetting);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, reportSetting.getId().toString()));
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
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_ID_MISSING);
        } else if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getGroupType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_GROUP_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getPayloadRef())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_PAYLOAD_REF_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsPdf())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_PDF_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsCsv())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_CSV_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsXlsx())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_XLSX_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsData())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_DATA_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsFirstDimension())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_FIRST_DIMENSION_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsSecondDimension())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_SECOND_DIMENSION_MISSING);
        } else if (BarcoUtil.isNull(payload.getIsThirdDimension())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_THIRD_DIMENSION_MISSING);
        }
        Optional<ReportSetting> reportSetting = this.reportSettingRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!reportSetting.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_NOT_FOUND);
        }
        Optional<GenForm> genForm = Optional.empty();
        if (!BarcoUtil.isNull(payload.getFormRequestId())) {
            genForm = this.genFormRepository.findByIdAndCreatedByAndStatusNot(
                payload.getFormRequestId(), adminUser.get(), APPLICATION_STATUS.DELETE);
            if (!genForm.isPresent()) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
            }
        }
        reportSetting.get().setGenForm(genForm.orElse(null));
        reportSetting.get().setUpdatedBy(adminUser.get());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            reportSetting.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        this.reportSettingRepository.save(getReportSetting(payload, reportSetting.get()));
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
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_ID_MISSING);
        }
        Optional<ReportSetting> reportSetting = this.reportSettingRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!reportSetting.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, getReportSettingResponse(reportSetting.get()));
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
        Map<String, List<ReportSettingResponse>> reportSettingHashtable = reportSettings.stream()
            .map(reportSetting -> getReportSettingResponse(reportSetting))
            .collect(Collectors.groupingBy(reportSetting -> reportSetting.getGroupType().getLookupType(), Collectors.toList()));
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
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_ID_MISSING);
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

    /**
     * Method use to get dashboard setting
     * @param reportSetting
     * @return ReportSettingResponse
     * */
    private ReportSettingResponse getReportSettingResponse(ReportSetting reportSetting) {
        ReportSettingResponse reportSettingResponse = new ReportSettingResponse();
        reportSettingResponse.setId(reportSetting.getId());
        reportSettingResponse.setName(reportSetting.getName());
        reportSettingResponse.setGroupType(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(reportSetting.getGroupType())));
        reportSettingResponse.setDescription(reportSetting.getDescription());
        reportSettingResponse.setPayloadRef(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
            PAYLOAD_REF.getName(), Long.valueOf(reportSetting.getPayloadRef().getLookupCode()))));
        reportSettingResponse.setIsPdf(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsPdf().getLookupType()));
        reportSettingResponse.setPdfUrl(reportSetting.getPdfUrl());
        reportSettingResponse.setPdfApiToken(reportSetting.getPdfApiToken());
        reportSettingResponse.setIsXlsx(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsXlsx().getLookupType()));
        reportSettingResponse.setXlsxUrl(reportSetting.getXlsxUrl());
        reportSettingResponse.setXlsxApiToken(reportSetting.getXlsxApiToken());
        reportSettingResponse.setIsCsv(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsCsv().getLookupType()));
        reportSettingResponse.setCsvUrl(reportSetting.getCsvUrl());
        reportSettingResponse.setCsvApiToken(reportSetting.getCsvApiToken());
        reportSettingResponse.setIsData(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsData().getLookupType()));
        reportSettingResponse.setDataUrl(reportSetting.getDataUrl());
        reportSettingResponse.setDataApiToken(reportSetting.getDataApiToken());
        reportSettingResponse.setIsFirstDimension(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsFirstDimension().getLookupType()));
        reportSettingResponse.setFirstDimensionUrl(reportSetting.getFirstDimensionUrl());
        reportSettingResponse.setFirstDimensionLKValue(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(reportSetting.getFirstDimensionLKValue())));
        reportSettingResponse.setFirstDimensionApiToken(reportSetting.getFirstDimensionApiToken());
        reportSettingResponse.setIsSecondDimension(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsSecondDimension().getLookupType()));
        reportSettingResponse.setSecondDimensionUrl(reportSetting.getSecondDimensionUrl());
        reportSettingResponse.setSecondDimensionLKValue(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(reportSetting.getSecondDimensionLKValue())));
        reportSettingResponse.setSecondDimensionApiToken(reportSetting.getSecondDimensionApiToken());
        reportSettingResponse.setIsThirdDimension(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsThirdDimension().getLookupType()));
        reportSettingResponse.setThirdDimensionUrl(reportSetting.getThirdDimensionUrl());
        reportSettingResponse.setThirdDimensionLKValue(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(reportSetting.getThirdDimensionLKValue())));
        reportSettingResponse.setThirdDimensionApiToken(reportSetting.getThirdDimensionApiToken());
        reportSettingResponse.setDistinctLKValue(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(reportSetting.getDistinctLKValue())));
        reportSettingResponse.setAggLKValue(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(reportSetting.getAggLKValue())));
        reportSettingResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(reportSetting.getStatus().getLookupType()));
        if (!BarcoUtil.isNull(reportSetting.getGenForm())) {
            reportSettingResponse.setFormRequestId(reportSetting.getGenForm().getId());
        }
        reportSettingResponse.setCreatedBy(getActionUser(reportSetting.getCreatedBy()));
        reportSettingResponse.setUpdatedBy(getActionUser(reportSetting.getUpdatedBy()));
        reportSettingResponse.setDateUpdated(reportSetting.getDateUpdated());
        reportSettingResponse.setDateCreated(reportSetting.getDateCreated());
        return reportSettingResponse;
    }
}

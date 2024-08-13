package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.ReportSettingService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.report.ReportRequest;
import com.barco.model.dto.request.ReportSettingRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.EventBridgeResponse;
import com.barco.model.dto.response.FormResponse;
import com.barco.model.dto.response.ReportSettingResponse;
import com.barco.model.pojo.*;
import com.barco.model.repository.*;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
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
    @Autowired
    private EventBridgeRepository eventBridgeRepository;

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
        } else if (BarcoUtil.isNull(payload.getDateFilter())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_DATA_FILTER_MISSING);
        } else if (BarcoUtil.isNull(payload.getRecordReport())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_RECORD_REPORT_MISSING);
        } else if (BarcoUtil.isNull(payload.getFetchRate())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_FETCH_RATE_MISSING);
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
        reportSetting = this.getReportSetting(payload, reportSetting);
        Optional<LookupData> groupType = this.lookupDataRepository.findByLookupType(payload.getGroupType());
        if (groupType.isPresent()) {
            reportSetting.setGroupType(groupType.get());
        }
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
        } else if (BarcoUtil.isNull(payload.getDateFilter())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_DATA_FILTER_MISSING);
        } else if (BarcoUtil.isNull(payload.getRecordReport())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_RECORD_REPORT_MISSING);
        } else if (BarcoUtil.isNull(payload.getFetchRate())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_FETCH_RATE_MISSING);
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
        Optional<LookupData> groupType = this.lookupDataRepository.findByLookupType(payload.getGroupType());
        if (groupType.isPresent()) {
            reportSetting.get().setGroupType(groupType.get());
        }
        this.reportSettingRepository.save(this.getReportSetting(payload, reportSetting.get()));
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
            Timestamp startDate = Timestamp.valueOf(payload.getStartDate().concat(BarcoUtil.START_DATE));
            Timestamp endDate = Timestamp.valueOf(payload.getEndDate().concat(BarcoUtil.END_DATE));
            reportSettings = this.reportSettingRepository.findAllByDateCreatedBetweenAndUsernameAndStatusNot(
                startDate, endDate, payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        } else {
            reportSettings = this.reportSettingRepository.findAllByCreatedByAndStatusNot(adminUser.get(), APPLICATION_STATUS.DELETE);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, reportSettings.stream()
            .map(reportSetting -> this.getReportSettingResponse(reportSetting)).collect(Collectors.toList()));
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
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getReportSettingResponse(reportSetting.get()));
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
            Timestamp startDate = Timestamp.valueOf(payload.getStartDate().concat(BarcoUtil.START_DATE));
            Timestamp endDate = Timestamp.valueOf(payload.getEndDate().concat(BarcoUtil.END_DATE));
            reportSettings = this.reportSettingRepository.findAllByDateCreatedBetweenAndUsernameAndStatusNot(
                startDate, endDate, payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        } else {
            reportSettings = this.reportSettingRepository.findAllByCreatedByAndStatusNot(adminUser.get(), APPLICATION_STATUS.DELETE);
        }
        Map<String, List<ReportSettingResponse>> reportSettingHashtable = reportSettings.stream()
            .filter(reportSetting -> !BarcoUtil.isNull(reportSetting.getGroupType()) && reportSetting.getStatus().equals(APPLICATION_STATUS.ACTIVE))
            .map(reportSetting -> getReportSettingResponse(reportSetting))
            .collect(Collectors.groupingBy(reportSetting -> (String) reportSetting.getGroupType().getLookupValue(), Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, reportSettingHashtable);
    }

    /**
     * Method use to delete the report setting by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    @Transactional
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
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_DELETED_ALL, payload);
    }

    /**
     * Method use to fetch the report response and write with the data.
     * @param payload
     * */
    @Override
    public AppResponse fetchReportResult(ReportRequest payload) throws Exception {
        logger.info("Request fetchReportResult :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getReportId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_SETTING_ID_MISSING);
        }
        Optional<ReportSetting> reportSetting = this.reportSettingRepository.findByIdAndUsernameAndStatusNot(
            payload.getReportId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!reportSetting.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_FETCH_SUCCESSFULLY, ""),
            this.fetchReportData(payload, reportSetting.get()));
    }

    /**
     * Method use to handle the report
     * */
    private Object fetchReportData(ReportRequest payload, ReportSetting reportSetting) {
        return null;
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
        reportSettingResponse.setDateFilter(UI_LOOKUP.getStatusByLookupType(reportSetting.getDateFilter().getLookupType()));
        reportSettingResponse.setRecordReport(UI_LOOKUP.getStatusByLookupType(reportSetting.getRecordReport().getLookupType()));
        reportSettingResponse.setFetchRate(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
            FETCH_LIMIT.getName(), Long.valueOf(reportSetting.getFetchRate().getLookupCode()))));
        if (!BarcoUtil.isNull(reportSetting.getGroupType())) {
            LookupData lookupData = reportSetting.getGroupType();
            reportSettingResponse.setGroupType(new GLookup(lookupData.getLookupType(),
                lookupData.getLookupCode().toString(), lookupData.getLookupValue()));
        }
        reportSettingResponse.setDescription(reportSetting.getDescription());
        reportSettingResponse.setPayloadRef(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
            PAYLOAD_REF.getName(), Long.valueOf(reportSetting.getPayloadRef().getLookupCode()))));
        // pdf
        reportSettingResponse.setIsPdf(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsPdf().getLookupType()));
        if (!BarcoUtil.isNull(reportSetting.getPdfBridge())) {
            reportSettingResponse.setPdfBridge(this.getEventBridgeResponse(reportSetting.getPdfBridge()));
        }
        // xlsx
        reportSettingResponse.setIsXlsx(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsXlsx().getLookupType()));
        if (!BarcoUtil.isNull(reportSetting.getXlsxBridge())) {
            reportSettingResponse.setXlsxBridge(this.getEventBridgeResponse(reportSetting.getXlsxBridge()));
        }
        // csv
        reportSettingResponse.setIsCsv(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsCsv().getLookupType()));
        if (!BarcoUtil.isNull(reportSetting.getCsvBridge())) {
            reportSettingResponse.setCsvBridge(this.getEventBridgeResponse(reportSetting.getCsvBridge()));
        }
        // data
        reportSettingResponse.setIsData(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsData().getLookupType()));
        if (!BarcoUtil.isNull(reportSetting.getDataBridge())) {
            reportSettingResponse.setDataBridge(this.getEventBridgeResponse(reportSetting.getDataBridge()));
        }
        // first dimension
        reportSettingResponse.setIsFirstDimension(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsFirstDimension().getLookupType()));
        reportSettingResponse.setFirstDimensionLKValue(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(reportSetting.getFirstDimensionLKValue())));
        if (!BarcoUtil.isNull(reportSetting.getFirstDimensionBridge())) {
            reportSettingResponse.setFirstDimensionBridge(this.getEventBridgeResponse(reportSetting.getFirstDimensionBridge()));
        }
        // second dimension
        reportSettingResponse.setIsSecondDimension(UI_LOOKUP.getStatusByLookupType(reportSetting.getIsSecondDimension().getLookupType()));
        reportSettingResponse.setSecondDimensionLKValue(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(reportSetting.getSecondDimensionLKValue())));
        if (!BarcoUtil.isNull(reportSetting.getSecondDimensionBridge())) {
            reportSettingResponse.setSecondDimensionBridge(this.getEventBridgeResponse(reportSetting.getSecondDimensionBridge()));
        }
        // lk value
        reportSettingResponse.setDistinctLKValue(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(reportSetting.getDistinctLKValue())));
        reportSettingResponse.setAggLKValue(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(reportSetting.getAggLKValue())));
        reportSettingResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(reportSetting.getStatus().getLookupType()));
        if (!BarcoUtil.isNull(reportSetting.getGenForm())) {
            reportSettingResponse.setFormResponse(getFormResponse(reportSetting.getGenForm()));
        }
        reportSettingResponse.setCreatedBy(getActionUser(reportSetting.getCreatedBy()));
        reportSettingResponse.setUpdatedBy(getActionUser(reportSetting.getUpdatedBy()));
        reportSettingResponse.setDateUpdated(reportSetting.getDateUpdated());
        reportSettingResponse.setDateCreated(reportSetting.getDateCreated());
        return reportSettingResponse;
    }

    /**
     * Method use to get dashboard setting
     * @param payload
     * @param reportSetting
     * @return ReportSetting
     * */
    private ReportSetting getReportSetting(ReportSettingRequest payload, ReportSetting reportSetting) {
        reportSetting.setName(payload.getName());
        reportSetting.setDateFilter(UI_LOOKUP.getByLookupCode(payload.getDateFilter()));
        reportSetting.setRecordReport(UI_LOOKUP.getByLookupCode(payload.getRecordReport()));
        reportSetting.setFetchRate(FETCH_LIMIT.getByLookupCode(payload.getFetchRate()));
        reportSetting.setDescription(payload.getDescription());
        reportSetting.setPayloadRef(PAYLOAD_REF.getByLookupCode(payload.getPayloadRef()));
        reportSetting.setIsPdf(UI_LOOKUP.getByLookupCode(payload.getIsPdf()));
        // pdf bridge
        setEventBridge(reportSetting::setPdfBridge, payload.getPdfBridgeId());
        // xlsx bridge
        reportSetting.setIsXlsx(UI_LOOKUP.getByLookupCode(payload.getIsXlsx()));
        setEventBridge(reportSetting::setXlsxBridge, payload.getXlsxBridgeId());
        // csv
        reportSetting.setIsCsv(UI_LOOKUP.getByLookupCode(payload.getIsCsv()));
        setEventBridge(reportSetting::setCsvBridge, payload.getCsvBridgeId());
        // data
        reportSetting.setIsData(UI_LOOKUP.getByLookupCode(payload.getIsData()));
        setEventBridge(reportSetting::setDataBridge, payload.getDataBridgeId());
        // first dimension
        reportSetting.setIsFirstDimension(UI_LOOKUP.getByLookupCode(payload.getIsFirstDimension()));
        reportSetting.setFirstDimensionLKValue(payload.getFirstDimensionLKValue());
        setEventBridge(reportSetting::setFirstDimensionBridge, payload.getFirstDimensionBridgeId());
        // second dimension
        reportSetting.setIsSecondDimension(UI_LOOKUP.getByLookupCode(payload.getIsSecondDimension()));
        reportSetting.setSecondDimensionLKValue(payload.getSecondDimensionLKValue());
        setEventBridge(reportSetting::setSecondDimensionBridge, payload.getSecondDimensionBridgeId());
        // other detail
        reportSetting.setDistinctLKValue(payload.getDistinctLKValue());
        reportSetting.setAggLKValue(payload.getAggLKValue());
        return reportSetting;
    }

    /**
     * Method use to set the value to the target setter
     * @param setter
     * @param bridgeId
     * */
    private void setEventBridge(Consumer<EventBridge> setter, Long bridgeId) {
        if (!BarcoUtil.isNull(bridgeId)) {
            Optional<EventBridge> eventBridge = this.eventBridgeRepository.findByIdAndStatusNot(
                bridgeId, APPLICATION_STATUS.DELETE);
            eventBridge.ifPresent(setter);
        } else {
            setter.accept(null);
        }
    }

    /**
     * Method use to convert the event bridge to reponse
     * @param eventBridge
     * @return EventBridgeResponse
     * */
    private EventBridgeResponse getEventBridgeResponse(EventBridge eventBridge) {
        EventBridgeResponse eventBridgeResponse = new EventBridgeResponse();
        eventBridgeResponse.setId(eventBridge.getId());
        eventBridgeResponse.setName(eventBridge.getName());
        return eventBridgeResponse;
    }

    /**
     * Method use to convert the gen form to form response
     * @param genForm
     * @return FormResponse
     * */
    private FormResponse getFormResponse(GenForm genForm) {
        FormResponse formResponse = new FormResponse();
        formResponse.setId(genForm.getId());
        formResponse.setFormName(genForm.getFormName());
        return formResponse;
    }

}

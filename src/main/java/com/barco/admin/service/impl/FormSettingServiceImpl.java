package com.barco.admin.service.impl;

import com.barco.admin.service.FormSettingService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.common.utility.excel.SheetFiled;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.*;
import com.barco.model.enums.Action;
import com.barco.model.pojo.*;
import com.barco.model.repository.*;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.*;
import com.barco.model.util.validation.STTCValidation;
import com.barco.model.util.validation.STTFValidation;
import com.barco.model.util.validation.STTSValidation;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 * Service use to mg the form setting
 */
@Service
public class FormSettingServiceImpl implements FormSettingService {

    private Logger logger = LoggerFactory.getLogger(FormSettingServiceImpl.class);

    @Value("${storage.efsFileDire}")
    private String tempStoreDirectory;
    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private QueryService queryService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private GenFormRepository genFormRepository;
    @Autowired
    private GenSectionRepository genSectionRepository;
    @Autowired
    private GenControlRepository genControlRepository;
    @Autowired
    private SourceTaskTypeRepository sourceTaskTypeRepository;
    @Autowired
    private ReportSettingRepository reportSettingRepository;
    @Autowired
    private DashboardSettingRepository dashboardSettingRepository;
    @Autowired
    private LookupDataRepository lookupDataRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private GenFormLinkSourceTaskTypeRepository genFormLinkSourceTaskTypeRepository;
    @Autowired
    private GenControlLinkGenSectionRepository genControlLinkGenSectionRepository;
    @Autowired
    private GenSectionLinkGenFormRepository genSectionLinkGenFormRepository;

    public FormSettingServiceImpl() {
    }

    /**
     * Method use to add the form
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addForm(FormRequest payload) throws Exception {
        logger.info("Request addForm :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getFormName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getFormType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_TYPE_MISSING);
        }
        GenForm genForm = new GenForm();
        genForm.setFormName(payload.getFormName());
        // home page
        if (!BarcoUtil.isNull(payload.getHomePage())) {
            Optional<LookupData> homePage = this.lookupDataRepository.findByLookupType(payload.getHomePage());
            if (homePage.isPresent()) {
                genForm.setHomePage(homePage.get());
            } else {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.HOME_PAGE_NOT_FOUND);
            }
        }
        // report id
        if (!BarcoUtil.isNull(payload.getReportId())) {
            Optional<ReportSetting> reportSetting = this.reportSettingRepository.findByIdAndUsernameAndStatusNot(
                payload.getReportId(), adminUser.get().getUsername(), APPLICATION_STATUS.DELETE);
            if (reportSetting.isPresent()) {
                genForm.setReport(reportSetting.get());
            } else {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_NOT_FOUND);
            }
        }
        // dashboard
        if (!BarcoUtil.isNull(payload.getDashboardId())) {
            Optional<DashboardSetting> dashboardSetting = this.dashboardSettingRepository.findByIdAndUsernameAndStatusNot(
                payload.getDashboardId(), adminUser.get().getUsername(), APPLICATION_STATUS.DELETE);
            if (dashboardSetting.isPresent()) {
                genForm.setDashboard(dashboardSetting.get());
            } else {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_NOT_FOUND);
            }
        }
        genForm.setDescription(payload.getDescription());
        genForm.setFormType(FORM_TYPE.getByLookupCode(payload.getFormType()));
        if (!BarcoUtil.isNull(payload.getServiceId())) {
            genForm.setServiceId(payload.getServiceId());
        }
        genForm.setCreatedBy(adminUser.get());
        genForm.setUpdatedBy(adminUser.get());
        genForm.setStatus(APPLICATION_STATUS.ACTIVE);
        genForm = this.genFormRepository.save(genForm);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, genForm.getId().toString()), payload);
    }

    /**
     * Method use to update the form
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateForm(FormRequest payload) throws Exception {
        logger.info("Request updateForm :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getFormName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getFormType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_TYPE_MISSING);
        }
        Optional<GenForm> genForm = this.genFormRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!genForm.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
        }
        genForm.get().setFormName(payload.getFormName());
        genForm.get().setDescription(payload.getDescription());
        genForm.get().setFormType(FORM_TYPE.getByLookupCode(payload.getFormType()));
        if (!BarcoUtil.isNull(payload.getServiceId())) {
            genForm.get().setServiceId(payload.getServiceId());
        }
        // home page
        if (!BarcoUtil.isNull(payload.getHomePage())) {
            Optional<LookupData> homePage = this.lookupDataRepository.findByLookupType(payload.getHomePage());
            if (homePage.isPresent()) {
                genForm.get().setHomePage(homePage.get());
            } else {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.HOME_PAGE_NOT_FOUND);
            }
        } else {
            genForm.get().setHomePage((LookupData) BarcoUtil.NULL);
        }
        // report id
        if (!BarcoUtil.isNull(payload.getReportId())) {
            Optional<ReportSetting> reportSetting = this.reportSettingRepository.findByIdAndUsernameAndStatusNot(
                payload.getReportId(), adminUser.get().getUsername(), APPLICATION_STATUS.DELETE);
            if (reportSetting.isPresent()) {
                genForm.get().setReport(reportSetting.get());
            } else {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.REPORT_NOT_FOUND);
            }
        } else {
            genForm.get().setReport((ReportSetting) BarcoUtil.NULL);
        }
        // dashboard
        if (!BarcoUtil.isNull(payload.getDashboardId())) {
            Optional<DashboardSetting> dashboardSetting = this.dashboardSettingRepository.findByIdAndUsernameAndStatusNot(
                payload.getDashboardId(), adminUser.get().getUsername(), APPLICATION_STATUS.DELETE);
            if (dashboardSetting.isPresent()) {
                genForm.get().setDashboard(dashboardSetting.get());
            } else {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_NOT_FOUND);
            }
        } else {
            genForm.get().setDashboard((DashboardSetting) BarcoUtil.NULL);
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            genForm.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            if (!BarcoUtil.isNull(genForm.get().getGenFormLinkSourceTaskTypes())) {
                this.actionOnGenFormLinkSourceTaskTypes(genForm.get(), adminUser.get());
            }
            if (!BarcoUtil.isNull(genForm.get().getGenSectionLinkGenForms())) {
                this.actionOnGenSectionLinkGenForms(genForm.get(), adminUser.get());
            }
        }
        genForm.get().setUpdatedBy(adminUser.get());
        this.genFormRepository.save(genForm.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, genForm.get().getId().toString()), payload);
    }

    /**
     * Method use to delete the form by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteFormById(FormRequest payload) throws Exception {
        logger.info("Request deleteFormById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
        }
        Optional<GenForm> genForm = this.genFormRepository.findByIdAndCreatedByAndStatusNot(payload.getId(),
            adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!genForm.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
        }
        genForm.get().setStatus(APPLICATION_STATUS.DELETE);
        genForm.get().setUpdatedBy(adminUser.get());
        if (!BarcoUtil.isNull(genForm.get().getGenFormLinkSourceTaskTypes())) {
            this.actionOnGenFormLinkSourceTaskTypes(genForm.get(), adminUser.get());
        }
        if (!BarcoUtil.isNull(genForm.get().getGenSectionLinkGenForms())) {
            this.actionOnGenSectionLinkGenForms(genForm.get(), adminUser.get());
        }
        if (!BarcoUtil.isNull(genForm.get().getReportSettings())) {
            this.actionOnReportSettingLinkGenForms(genForm.get(), adminUser.get());
        }
        this.genFormRepository.save(genForm.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()), payload);
    }

    /**
     * Method use to fetch the form by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchFormByFormId(FormRequest payload) throws Exception {
        logger.info("Request fetchFormByFormId :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Optional<GenForm> genForm = this.genFormRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!genForm.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getFormResponse(genForm.get()));
    }

    /**
     * Method use to fetch the form
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchForms(FormRequest payload) throws Exception {
        logger.info("Request fetchForms :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate().concat(BarcoUtil.START_DATE));
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate().concat(BarcoUtil.END_DATE));
        List<GenForm> result = this.genFormRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
            startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE);
        if (result.isEmpty()) {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, new ArrayList<>());
        }
        List<FormResponse> formResponses = result.stream()
            .map(genForm -> {
                FormResponse formResponse = this.getFormResponse(genForm);
                formResponse.setTotalStt(this.genFormLinkSourceTaskTypeRepository.countByGenFormAndStatusNot(genForm, APPLICATION_STATUS.DELETE));
                formResponse.setTotalSection(this.genSectionLinkGenFormRepository.countByGenFormAndStatusNot(genForm, APPLICATION_STATUS.DELETE));
                return formResponse;
            }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, formResponses);
    }

    /**
     * Method use to fetch the form by form type
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchFormsByFormType(FormRequest payload) throws Exception {
        logger.info("Request fetchForms :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getFormType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_TYPE_MISSING);
        }
        List<GenForm> result = this.genFormRepository.findAllByFormTypeAndStatusNot(
            FORM_TYPE.getByLookupCode(payload.getFormType()), APPLICATION_STATUS.DELETE);
        if (result.isEmpty()) {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, new ArrayList<>());
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, result.stream()
            .map(genForm -> this.getFormResponse(genForm)).collect(Collectors.toList()));
    }

    /**
     * Method use to delete all forms
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllForms(FormRequest payload) throws Exception {
        logger.info("Request deleteAllForms :- " + payload);
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
        this.genFormRepository.saveAll(
            this.genFormRepository.findAllByIdIn(payload.getIds()).stream()
                .map(genForm -> {
                    genForm.setUpdatedBy(appUser.get());
                    genForm.setStatus(APPLICATION_STATUS.DELETE);
                    if (!BarcoUtil.isNull(genForm.getGenFormLinkSourceTaskTypes())) {
                        this.actionOnGenFormLinkSourceTaskTypes(genForm, appUser.get());
                    }
                    if (!BarcoUtil.isNull(genForm.getGenSectionLinkGenForms())) {
                        this.actionOnGenSectionLinkGenForms(genForm, appUser.get());
                    }
                    if (!BarcoUtil.isNull(genForm.getReportSettings())) {
                        this.actionOnReportSettingLinkGenForms(genForm, appUser.get());
                    }
                    return genForm;
                }).collect(Collectors.toList())
        );
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, ""), payload);
    }

    /**
     * Method use to delete all forms
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllFormLinkSTT(FormRequest payload) throws Exception {
        logger.info("Request fetchAllFormLinkSTT :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
        }
        Optional<GenForm> getForm = this.genFormRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!getForm.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(String.format(QueryService.FETCH_ALL_STT_LINK_FORM,
            getForm.get().getId(), APPLICATION_STATUS.DELETE.getLookupCode(), appUser.get().getId()));
        List<FormLinkSourceTaskTypeResponse> formLinkSourceTaskTypeResponses = new ArrayList<>();
        if (!BarcoUtil.isNull(queryResponse.getData())) {
            for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
                formLinkSourceTaskTypeResponses.add(this.getFormLinkSourceTaskTypeResponse(data, this.lookupDataCacheService));
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, formLinkSourceTaskTypeResponses);
    }

    /**
     * Method use to delete all forms
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkFormSTT(FormRequest payload) throws Exception {
        logger.info("Request linkFormSTT :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getAction())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ACTION_MISSING);
        }
        if (payload.getAction().equals(Action.LINK)) {
            if (BarcoUtil.isNull(payload.getId())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
            } else if (BarcoUtil.isNull(payload.getSttId()) && payload.getSttId().size() > 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_ID_MISSING);
            }
            Optional<GenForm> getForm = this.genFormRepository.findByIdAndCreatedByAndStatusNot(
                payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
            if (!getForm.isPresent()) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
            }
            List<SourceTaskType> sourceTaskTypes = this.sourceTaskTypeRepository.findAllByIdInAndStatusNot(
                payload.getSttId(), APPLICATION_STATUS.DELETE);
            if (sourceTaskTypes.size() == 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SOURCE_TASK_TYPE_NOT_FOUND);
            }
            this.genFormLinkSourceTaskTypeRepository.saveAll(sourceTaskTypes.stream()
                .map(geSourceTaskType -> {
                    GenFormLinkSourceTaskType genFormLinkSourceTaskType = new GenFormLinkSourceTaskType();
                    genFormLinkSourceTaskType.setGenForm(getForm.get());
                    genFormLinkSourceTaskType.setSourceTaskType(geSourceTaskType);
                    if (geSourceTaskType.getStatus().equals(APPLICATION_STATUS.ACTIVE) &&
                        getForm.get().getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
                        genFormLinkSourceTaskType.setStatus(APPLICATION_STATUS.ACTIVE);
                    } else {
                        genFormLinkSourceTaskType.setStatus(APPLICATION_STATUS.INACTIVE);
                    }
                    genFormLinkSourceTaskType.setCreatedBy(appUser.get());
                    genFormLinkSourceTaskType.setUpdatedBy(appUser.get());
                    return genFormLinkSourceTaskType;
                }).collect(Collectors.toList()));
            return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
        }
        if (BarcoUtil.isNull(payload.getFormLinkStt())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_LINK_STT_MISSING);
        }
        this.genFormLinkSourceTaskTypeRepository.saveAll(
            this.genFormLinkSourceTaskTypeRepository.findAllByIdInAndStatusNot(payload.getFormLinkStt(), APPLICATION_STATUS.DELETE).stream()
                .map(genFormLinkSourceTaskType -> {
                    genFormLinkSourceTaskType.setStatus(APPLICATION_STATUS.DELETE);
                    genFormLinkSourceTaskType.setUpdatedBy(appUser.get());
                    return genFormLinkSourceTaskType;
                }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

    /**
     * Method use to fetch all form link section
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllFormLinkSection(FormRequest payload) throws Exception {
        logger.info("Request fetchAllFormLinkSection :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
        }
        Optional<GenForm> getForm = this.genFormRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!getForm.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(String.format(QueryService.FETCH_ALL_SECTION_LINK_FORM,
            getForm.get().getId(), APPLICATION_STATUS.DELETE.getLookupCode(), appUser.get().getId()));
        List<FormLinkSectionResponse> formLinkSectionResponses = new ArrayList<>();
        if (!BarcoUtil.isNull(queryResponse.getData())) {
            for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
                formLinkSectionResponses.add(getFormLinkSectionResponse(data));
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, formLinkSectionResponses);
    }

    /***
     * Method use to link form section
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkFormSection(FormRequest payload) throws Exception {
        logger.info("Request linkFormSection :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getAction())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ACTION_MISSING);
        }
        if (payload.getAction().equals(Action.LINK)) {
            if (BarcoUtil.isNull(payload.getId())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
            } else if (BarcoUtil.isNull(payload.getSectionId()) && payload.getSectionId().size() > 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_ID_MISSING);
            }
            Optional<GenForm> getForm = this.genFormRepository.findByIdAndCreatedByAndStatusNot(
                payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
            if (!getForm.isPresent()) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
            }
            List<GenSection> getSections = this.genSectionRepository.findAllByIdInAndStatusNot(
                payload.getSectionId(), APPLICATION_STATUS.DELETE);
            if (getSections.size() == 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NOT_FOUND);
            }
            this.genSectionLinkGenFormRepository.saveAll(getSections.stream()
                .map(genSection -> {
                    GenSectionLinkGenForm genSectionLinkGenForm = new GenSectionLinkGenForm();
                    genSectionLinkGenForm.setSectionOrder(0l); // default order set
                    genSectionLinkGenForm.setGenForm(getForm.get());
                    genSectionLinkGenForm.setGenSection(genSection);
                    if (genSection.getStatus().equals(APPLICATION_STATUS.ACTIVE) &&
                        getForm.get().getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
                        genSectionLinkGenForm.setStatus(APPLICATION_STATUS.ACTIVE);
                    } else {
                        genSectionLinkGenForm.setStatus(APPLICATION_STATUS.INACTIVE);
                    }
                    genSectionLinkGenForm.setCreatedBy(appUser.get());
                    genSectionLinkGenForm.setUpdatedBy(appUser.get());
                    return genSectionLinkGenForm;
                }).collect(Collectors.toList()));
            return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
        }
        if (BarcoUtil.isNull(payload.getFormLinkSection())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_LINK_SECTION_MISSING);
        }
        this.genSectionLinkGenFormRepository.saveAll(this.genSectionLinkGenFormRepository.findAllByIdInAndStatusNot(
            payload.getFormLinkSection(), APPLICATION_STATUS.DELETE).stream()
            .map(genSectionLinkGenForm -> {
                genSectionLinkGenForm.setStatus(APPLICATION_STATUS.DELETE);
                genSectionLinkGenForm.setUpdatedBy(appUser.get());
                return genSectionLinkGenForm;
            }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

    /**
     * Method use to set form section order
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkFormSectionOrder(FormRequest payload) throws Exception {
        logger.info("Request linkFormSectionOrder :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (BarcoUtil.isNull(payload.getFormLinkSection())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_LINK_SECTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getSectionOrder())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_LINK_FORM_ORDER_MISSING);
        }
        this.genSectionLinkGenFormRepository.saveAll(this.genSectionLinkGenFormRepository.findAllByIdInAndStatusNot(
            payload.getFormLinkSection(), APPLICATION_STATUS.DELETE).stream()
            .map(getSectionLinkGenForm -> {
                getSectionLinkGenForm.setSectionOrder(payload.getSectionOrder());
                getSectionLinkGenForm.setUpdatedBy(appUser.get());
                return getSectionLinkGenForm;
            }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getFormLinkSection()), payload);
    }

    /**
     * Method use to add section
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addSection(SectionRequest payload) throws Exception {
        logger.info("Request addSection :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getSectionName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_DESCRIPTION_MISSING);
        }
        GenSection genSection = new GenSection();
        genSection.setSectionName(payload.getSectionName());
        genSection.setDescription(payload.getDescription());
        genSection.setStatus(APPLICATION_STATUS.ACTIVE);
        genSection.setCreatedBy(adminUser.get());
        genSection.setUpdatedBy(adminUser.get());
        genSection = this.genSectionRepository.save(genSection);
        payload.setId(genSection.getId());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
    }

    /**
     * Method use to update section
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateSection(SectionRequest payload) throws Exception {
        logger.info("Request updateSection :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getSectionName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_DESCRIPTION_MISSING);
        }
        Optional<GenSection> genSection = this.genSectionRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!genSection.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NOT_FOUND);
        }
        genSection.get().setSectionName(payload.getSectionName());
        genSection.get().setDescription(payload.getDescription());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            genSection.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            if (!BarcoUtil.isNull(genSection.get().getGenSectionLinkGenForms())) {
                this.actionOnGenSectionLinkGenForms(genSection.get(), adminUser.get());
            }
            if (!BarcoUtil.isNull(genSection.get().getGenControlLinkGenSections())) {
                this.actionOnGenSectionsLinkGenControl(genSection.get(), adminUser.get());
            }
        }
        genSection.get().setUpdatedBy(adminUser.get());
        this.genSectionRepository.save(genSection.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

    /**
     * Method use to delete section by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteSectionById(SectionRequest payload) throws Exception {
        logger.info("Request deleteSectionById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_ID_MISSING);
        }
        Optional<GenSection> genSection = this.genSectionRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!genSection.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NOT_FOUND);
        }
        genSection.get().setStatus(APPLICATION_STATUS.DELETE);
        if (!BarcoUtil.isNull(genSection.get().getGenSectionLinkGenForms())) {
            this.actionOnGenSectionLinkGenForms(genSection.get(), adminUser.get());
        }
        if (!BarcoUtil.isNull(genSection.get().getGenControlLinkGenSections())) {
            this.actionOnGenSectionsLinkGenControl(genSection.get(), adminUser.get());
        }
        this.genSectionRepository.save(genSection.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()), payload);
    }

    /**
     * Method use to fetch section by section id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchSectionBySectionId(SectionRequest payload) throws Exception {
        logger.info("Request fetchSectionBySectionId :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_ID_MISSING);
        }
        Optional<GenSection> genSection = this.genSectionRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!genSection.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getSectionResponse(genSection.get()));
    }

    /**
     * Method use to fetch section
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchSections(SectionRequest payload) throws Exception {
        logger.info("Request fetchSections :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        List<GenSection> result;
        if (BarcoUtil.isNull(payload.getStartDate()) && BarcoUtil.isNull(payload.getEndDate())) {
            Timestamp startDate = Timestamp.valueOf(payload.getStartDate().concat(BarcoUtil.START_DATE));
            Timestamp endDate = Timestamp.valueOf(payload.getEndDate().concat(BarcoUtil.END_DATE));
            result = this.genSectionRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
                startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE);
        } else {
            result = this.genSectionRepository.findAllByCreatedByAndStatusNotOrderByDateCreatedDesc(
                adminUser.get(), APPLICATION_STATUS.DELETE);
        }
        if (result.isEmpty()) {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, new ArrayList<>());
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            result.stream().map(genSection -> {
                SectionResponse sectionResponse = this.getSectionResponse(genSection);
                // no need to give the count if start date and end date not there
                if (!BarcoUtil.isNull(payload.getStartDate()) && !BarcoUtil.isNull(payload.getEndDate())) {
                    sectionResponse.setTotalForm(this.genSectionLinkGenFormRepository
                        .countByGenSectionAndStatusNot(genSection, APPLICATION_STATUS.DELETE));
                    sectionResponse.setTotalControl(this.genControlLinkGenSectionRepository
                        .countByGenSectionAndStatusNot(genSection, APPLICATION_STATUS.DELETE));
                }
                return sectionResponse;
            }).collect(Collectors.toList()));
    }

    /**
     * Method use to delete all section
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllSections(SectionRequest payload) throws Exception {
        logger.info("Request deleteAllSections :- " + payload);
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
        this.genSectionRepository.saveAll(this.genSectionRepository.findAllByIdIn(payload.getIds()).stream()
            .map((genSection -> {
                genSection.setStatus(APPLICATION_STATUS.DELETE);
                if (!BarcoUtil.isNull(genSection.getGenSectionLinkGenForms())) {
                    this.actionOnGenSectionLinkGenForms(genSection, appUser.get());
                }
                if (!BarcoUtil.isNull(genSection.getGenControlLinkGenSections())) {
                    this.actionOnGenSectionsLinkGenControl(genSection, appUser.get());
                }
                return genSection;
            })).collect(Collectors.toList())
        );
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, ""), payload);
    }

    /**
     * Method use to fetch all selection link control
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllSectionLinkControl(SectionRequest payload) throws Exception {
        logger.info("Request fetchAllSectionLinkControl :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_ID_MISSING);
        }
        Optional<GenSection> genSection = this.genSectionRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!genSection.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NOT_FOUND);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(String.format(QueryService.FETCH_ALL_SECTION_LINK_CONTROLS,
            genSection.get().getId(), APPLICATION_STATUS.DELETE.getLookupCode(), appUser.get().getId()));
        List<SectionLinkControlResponse> sectionLinkControlResponses = new ArrayList<>();
        if (!BarcoUtil.isNull(queryResponse.getData())) {
            for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
                sectionLinkControlResponses.add(getSectionLinkControlResponse(data, this.lookupDataCacheService));
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, sectionLinkControlResponses);
    }

    /**
     * Method ue to link section with control
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkSectionControl(SectionRequest payload) throws Exception {
        logger.info("Request linkSectionControl :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getAction())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ACTION_MISSING);
        }
        if (payload.getAction().equals(Action.LINK)) {
            if (BarcoUtil.isNull(payload.getId())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_ID_MISSING);
            } else if (BarcoUtil.isNull(payload.getControlId()) && payload.getControlId().size() > 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_ID_MISSING);
            }
            Optional<GenSection> getSection = this.genSectionRepository.findByIdAndCreatedByAndStatusNot(
                payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
            if (!getSection.isPresent()) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NOT_FOUND);
            }
            List<GenControl> getGenControls = this.genControlRepository.findAllByIdInAndStatusNot(
                payload.getControlId(), APPLICATION_STATUS.DELETE);
            if (getGenControls.size() == 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_NOT_FOUND);
            }
            this.genControlLinkGenSectionRepository.saveAll(getGenControls.stream()
                .map(getControl -> {
                    GenControlLinkGenSection genControlLinkGenSection = new GenControlLinkGenSection();
                    genControlLinkGenSection.setGenControl(getControl);
                    genControlLinkGenSection.setGenSection(getSection.get());
                    genControlLinkGenSection.setControlOrder(0l); // default order set
                    genControlLinkGenSection.setFieldWidth(0l); // default order set
                    if (getSection.get().getStatus().equals(APPLICATION_STATUS.ACTIVE) &&
                        getControl.getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
                        genControlLinkGenSection.setStatus(APPLICATION_STATUS.ACTIVE);
                    } else {
                        genControlLinkGenSection.setStatus(APPLICATION_STATUS.INACTIVE);
                    }
                    genControlLinkGenSection.setCreatedBy(appUser.get());
                    genControlLinkGenSection.setUpdatedBy(appUser.get());
                    return genControlLinkGenSection;
                }).collect(Collectors.toList()));
            return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
        }
        if (BarcoUtil.isNull(payload.getSectionLinkControl())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_LINK_CONTROL_MISSING);
        }
        this.genControlLinkGenSectionRepository.saveAll(this.genControlLinkGenSectionRepository.findAllByIdInAndStatusNot(
            payload.getSectionLinkControl(), APPLICATION_STATUS.DELETE).stream()
            .map(genControlLinkGenSection -> {
                genControlLinkGenSection.setStatus(APPLICATION_STATUS.DELETE);
                genControlLinkGenSection.setUpdatedBy(appUser.get());
                return genControlLinkGenSection;
            }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

    /**
     * Method use to link section with control order
     * @param payload
     * @return AppResponse
     * */
    @Override
    @Transactional
    public AppResponse linkSectionControlOrder(SectionRequest payload) throws Exception {
        logger.info("Request linkSectionControlOrder :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (BarcoUtil.isNull(payload.getSectionLinkControl())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_LINK_CONTROL_MISSING);
        } else if (BarcoUtil.isNull(payload.getControlOrder())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_LINK_SECTION_ORDER_MISSING);
        }
        this.genControlLinkGenSectionRepository.saveAll(
            this.genControlLinkGenSectionRepository.findAllByIdInAndStatusNot(payload.getSectionLinkControl(), APPLICATION_STATUS.DELETE)
                 .stream().map(genControlLinkGenSection -> {
                    genControlLinkGenSection.setControlOrder(payload.getControlOrder());
                    genControlLinkGenSection.setFieldWidth(payload.getFieldWidth());
                    genControlLinkGenSection.setUpdatedBy(appUser.get());
                    return genControlLinkGenSection;
                }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getSectionLinkControl()), payload);
    }

    /**
     * Method use to fetch all section link form
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllSectionLinkForm(SectionRequest payload) throws Exception {
        logger.info("Request fetchAllSectionLinkForm :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_ID_MISSING);
        }
        Optional<GenSection> genSection = this.genSectionRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!genSection.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NOT_FOUND);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(String.format(QueryService.FETCH_ALL_FORM_LINK_SECTION,
            genSection.get().getId(), APPLICATION_STATUS.DELETE.getLookupCode(), appUser.get().getId()));
        List<SectionLinkFormResponse> sectionLinkFormResponse = new ArrayList<>();
        if (!BarcoUtil.isNull(queryResponse.getData())) {
            for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
                sectionLinkFormResponse.add(this.getSectionLinkFromResponse(data, this.lookupDataCacheService));
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, sectionLinkFormResponse);
    }

    /**
     * Method use to link section with form
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkSectionForm(SectionRequest payload) throws Exception {
        logger.info("Request linkSectionForm :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getAction())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ACTION_MISSING);
        }
        if (payload.getAction().equals(Action.LINK)) {
            if (BarcoUtil.isNull(payload.getId())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_ID_MISSING);
            } else if (BarcoUtil.isNull(payload.getFormId()) && payload.getFormId().size() > 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_ID_MISSING);
            }
            Optional<GenSection> genSection = this.genSectionRepository.findByIdAndCreatedByAndStatusNot(
                payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
            if (!genSection.isPresent()) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NOT_FOUND);
            }
            List<GenForm> getForms = this.genFormRepository.findAllByIdInAndStatusNot(payload.getFormId(), APPLICATION_STATUS.DELETE);
            if (getForms.size() == 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
            }
            this.genSectionLinkGenFormRepository.saveAll(getForms.stream()
                .map(getForm -> {
                    GenSectionLinkGenForm genSectionLinkGenForm = new GenSectionLinkGenForm();
                    genSectionLinkGenForm.setSectionOrder(0l); // default order set
                    genSectionLinkGenForm.setGenForm(getForm);
                    genSectionLinkGenForm.setGenSection(genSection.get());
                    if (genSection.get().getStatus().equals(APPLICATION_STATUS.ACTIVE) &&
                        getForm.getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
                        genSectionLinkGenForm.setStatus(APPLICATION_STATUS.ACTIVE);
                    } else {
                        genSectionLinkGenForm.setStatus(APPLICATION_STATUS.INACTIVE);
                    }
                    genSectionLinkGenForm.setCreatedBy(appUser.get());
                    genSectionLinkGenForm.setUpdatedBy(appUser.get());
                    return genSectionLinkGenForm;
                }).collect(Collectors.toList()));
            return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
        }
        if (BarcoUtil.isNull(payload.getSectionLinkForm())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_LINK_FORM_MISSING);
        }
        this.genSectionLinkGenFormRepository.saveAll(
            this.genSectionLinkGenFormRepository.findAllByIdInAndStatusNot(
                payload.getSectionLinkForm(), APPLICATION_STATUS.DELETE).stream()
                .map(genSectionLinkGenForm -> {
                    genSectionLinkGenForm.setStatus(APPLICATION_STATUS.DELETE);
                    genSectionLinkGenForm.setUpdatedBy(appUser.get());
                    return genSectionLinkGenForm;
                }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

    /**
     * Method use to link section with form order
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkSectionFormOrder(SectionRequest payload) throws Exception {
        logger.info("Request linkSectionFormOrder :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (BarcoUtil.isNull(payload.getSectionLinkForm())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_LINK_FORM_MISSING);
        } else if (BarcoUtil.isNull(payload.getSectionOrder())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_LINK_FORM_ORDER_MISSING);
        }
        this.genSectionLinkGenFormRepository.saveAll(
            this.genSectionLinkGenFormRepository.findAllByIdInAndStatusNot(
                payload.getSectionLinkForm(), APPLICATION_STATUS.DELETE).stream()
                .map(sectionLinkGenForm -> {
                    sectionLinkGenForm.setSectionOrder(payload.getSectionOrder());
                    sectionLinkGenForm.setUpdatedBy(appUser.get());
                    return sectionLinkGenForm;
                }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getSectionLinkForm()), payload);
    }

    /**
     * Method use to add control (form control)
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addControl(ControlRequest payload) throws Exception {
        logger.info("Request addControl :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getFieldType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_FIELD_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getControlName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getFieldName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_FIELD_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getFieldTitle())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_FIELD_TITLE_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        GenControl genControl = new GenControl();
        genControl.setControlName(payload.getControlName());
        genControl.setFieldType(FIELD_TYPE.getByLookupCode(payload.getFieldType()));
        genControl.setFieldTitle(payload.getFieldTitle());
        genControl.setFieldName(payload.getFieldName());
        genControl.setDescription(payload.getDescription());
        genControl.setPlaceHolder(payload.getPlaceHolder());
        genControl.setMinLength(payload.getMinLength());
        genControl.setMaxLength(payload.getMaxLength());
        if (FIELD_TYPE.RADIO.getLookupCode().equals(payload.getFieldType()) ||
            FIELD_TYPE.CHECKBOX.getLookupCode().equals(payload.getFieldType()) ||
            FIELD_TYPE.SELECT.getLookupCode().equals(payload.getFieldType()) ||
            FIELD_TYPE.MULTI_SELECT.getLookupCode().equals(payload.getFieldType())) {
            if (!BarcoUtil.isNull(payload.getFieldLkValue())) {
                Optional<LookupData> fieldLkValue = this.lookupDataRepository.findByLookupType(payload.getFieldLkValue());
                if (fieldLkValue.isPresent()) {
                    genControl.setFieldLkValue(fieldLkValue.get());
                }
            }
        }
        genControl.setMandatory(IS_DEFAULT.getByLookupCode(payload.getMandatory()));
        genControl.setIsDefault(IS_DEFAULT.getByLookupCode(payload.getIsDefault()));
        genControl.setDefaultValue(payload.getDefaultValue());
        genControl.setApiLkValue(payload.getApiLkValue());
        genControl.setPattern(payload.getPattern());
        genControl.setStatus(APPLICATION_STATUS.ACTIVE);
        genControl.setCreatedBy(adminUser.get());
        genControl.setUpdatedBy(adminUser.get());
        this.genControlRepository.save(genControl);
        payload.setId(genControl.getId());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()));
    }

    /**
     * Method use to edit control (form control)
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateControl(ControlRequest payload) throws Exception {
        logger.info("Request updateControl :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getFieldType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_FIELD_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getControlName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getFieldName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_FIELD_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getFieldTitle())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_FIELD_TITLE_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Optional<GenControl> genControl = this.genControlRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!genControl.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_NOT_FOUND);
        }
        genControl.get().setFieldType(FIELD_TYPE.getByLookupCode(payload.getFieldType()));
        if (FIELD_TYPE.RADIO.getLookupCode().equals(payload.getFieldType()) ||
            FIELD_TYPE.CHECKBOX.getLookupCode().equals(payload.getFieldType()) ||
            FIELD_TYPE.SELECT.getLookupCode().equals(payload.getFieldType()) ||
            FIELD_TYPE.MULTI_SELECT.getLookupCode().equals(payload.getFieldType())) {
            genControl.get().setFieldType(FIELD_TYPE.getByLookupCode(payload.getFieldType()));
        }
        genControl.get().setControlName(payload.getControlName());
        genControl.get().setFieldTitle(payload.getFieldTitle());
        genControl.get().setFieldName(payload.getFieldName());
        genControl.get().setDescription(payload.getDescription());
        genControl.get().setPlaceHolder(payload.getPlaceHolder());
        genControl.get().setMinLength(payload.getMinLength());
        genControl.get().setMaxLength(payload.getMaxLength());
        genControl.get().setMandatory(IS_DEFAULT.getByLookupCode(payload.getMandatory()));
        genControl.get().setIsDefault(IS_DEFAULT.getByLookupCode(payload.getIsDefault()));
        genControl.get().setDefaultValue(payload.getDefaultValue());
        genControl.get().setApiLkValue(payload.getApiLkValue());
        genControl.get().setPattern(payload.getPattern());
        if (FIELD_TYPE.RADIO.getLookupCode().equals(payload.getFieldType()) ||
            FIELD_TYPE.CHECKBOX.getLookupCode().equals(payload.getFieldType()) ||
            FIELD_TYPE.SELECT.getLookupCode().equals(payload.getFieldType()) ||
            FIELD_TYPE.MULTI_SELECT.getLookupCode().equals(payload.getFieldType())) {
            Optional<LookupData> fieldLkValue = this.lookupDataRepository.findByLookupType(payload.getFieldLkValue());
            if (fieldLkValue.isPresent()) {
                genControl.get().setFieldLkValue(fieldLkValue.get());
            }
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            genControl.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            if (!BarcoUtil.isNull(genControl.get().getGenControlLinkGenSections())) {
                this.actionOnGenControlLinkGenSections(genControl.get(), adminUser.get());
            }
        }
        this.genControlRepository.save(genControl.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

    /**
     * Method use to delete control by id (form control)
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteControlById(ControlRequest payload) throws Exception {
        logger.info("Request deleteControlById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_ID_MISSING);
        }
        Optional<GenControl> genControl = this.genControlRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!genControl.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_NOT_FOUND);
        }
        genControl.get().setStatus(APPLICATION_STATUS.DELETE);
        if (!BarcoUtil.isNull(genControl.get().getGenControlLinkGenSections())) {
            this.actionOnGenControlLinkGenSections(genControl.get(), adminUser.get());
        }
        this.genControlRepository.save(genControl.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()), payload);
    }

    /**
     * Method use to fetch control by control id (form control)
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchControlByControlId(ControlRequest payload) throws Exception {
        logger.info("Request fetchControlByControlId :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_ID_MISSING);
        }
        Optional<GenControl> genControl = this.genControlRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!genControl.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getControlResponse(genControl.get()));
    }

    /**
     * Method use to fetch control
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchControls(ControlRequest payload) throws Exception {
        logger.info("Request fetchControls :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate().concat(BarcoUtil.START_DATE));
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate().concat(BarcoUtil.END_DATE));
        List<ControlResponse> controlResponses = this.genControlRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
            startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE).stream()
            .map(genControl -> {
                ControlResponse controlResponse = this.getControlResponse(genControl);
                controlResponse.setTotalSection(this.genControlLinkGenSectionRepository.countByGenControlAndStatusNot(genControl, APPLICATION_STATUS.DELETE));
                return controlResponse;
            }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, controlResponses);
    }

    /**
     * Method use to delete all controls (form control)
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllControls(ControlRequest payload) throws Exception {
        logger.info("Request deleteAllControls :- " + payload);
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
        this.genControlRepository.saveAll(
            this.genControlRepository.findAllByIdIn(payload.getIds()).stream()
                .map(genControl -> {
                    genControl.setStatus(APPLICATION_STATUS.DELETE);
                    if (!BarcoUtil.isNull(genControl.getGenControlLinkGenSections())) {
                        this.actionOnGenControlLinkGenSections(genControl, appUser.get());
                    }
                    return genControl;
                }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_DELETED_ALL, payload);
    }

    /**
     * Method use to fetch all controls link section
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllControlLinkSection(ControlRequest payload) throws Exception {
        logger.info("Request fetchAllControlLinkSection :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_ID_MISSING);
        }
        Optional<GenControl> genControl = this.genControlRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
        if (!genControl.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_NOT_FOUND);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(String.format(QueryService.FETCH_ALL_CONTROLS_LINK_SECTION,
            genControl.get().getId(), APPLICATION_STATUS.DELETE.getLookupCode(), appUser.get().getId()));
        List<ControlLinkSectionResponse> controlLinkSectionResponses = new ArrayList<>();
        if (!BarcoUtil.isNull(queryResponse.getData())) {
            for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
                controlLinkSectionResponses.add(getControlLinkSectionResponse(data));
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, controlLinkSectionResponses);
    }

    /**
     * Method use to link section
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkControlSection(ControlRequest payload) throws Exception {
        logger.info("Request linkControlSection :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getAction())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ACTION_MISSING);
        }
        if (payload.getAction().equals(Action.LINK)) {
             if (BarcoUtil.isNull(payload.getId())) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_ID_MISSING);
            } else if (BarcoUtil.isNull(payload.getSectionId()) && payload.getSectionId().size() > 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_ID_MISSING);
            }
            Optional<GenControl> genControl = this.genControlRepository.findByIdAndCreatedByAndStatusNot(
                payload.getId(), appUser.get(), APPLICATION_STATUS.DELETE);
            if (!genControl.isPresent()) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_NOT_FOUND);
            }
            List<GenSection> genSections = this.genSectionRepository.findAllByIdInAndStatusNot(
                payload.getSectionId(), APPLICATION_STATUS.DELETE);
            if (genSections.size() == 0) {
                return new AppResponse(BarcoUtil.ERROR, MessageUtil.SECTION_NOT_FOUND);
            }
            this.genControlLinkGenSectionRepository.saveAll(genSections.stream()
                .map(genSection -> {
                    GenControlLinkGenSection genControlLinkGenSection = new GenControlLinkGenSection();
                    genControlLinkGenSection.setGenControl(genControl.get());
                    genControlLinkGenSection.setGenSection(genSection);
                    genControlLinkGenSection.setControlOrder(0l);  // default order set
                    genControlLinkGenSection.setFieldWidth(0l);
                    if (genSection.getStatus().equals(APPLICATION_STATUS.ACTIVE) &&
                        genControl.get().getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
                        genControlLinkGenSection.setStatus(APPLICATION_STATUS.ACTIVE);
                    } else {
                        genControlLinkGenSection.setStatus(APPLICATION_STATUS.INACTIVE);
                    }
                    genControlLinkGenSection.setCreatedBy(appUser.get());
                    genControlLinkGenSection.setUpdatedBy(appUser.get());
                    return genControlLinkGenSection;
                }).collect(Collectors.toList()));
            return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
        }
        if (BarcoUtil.isNull(payload.getControlLinkSection())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_LINK_SECTION_MISSING);
        }
        this.genControlLinkGenSectionRepository.saveAll(this.genControlLinkGenSectionRepository.findAllByIdInAndStatusNot(
            payload.getControlLinkSection(), APPLICATION_STATUS.DELETE).stream()
            .map(genControlLinkGenSection -> {
                genControlLinkGenSection.setStatus(APPLICATION_STATUS.DELETE);
                genControlLinkGenSection.setUpdatedBy(appUser.get());
                return genControlLinkGenSection;
            }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

    /**
     * Method use to link section order
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkControlSectionOrder(ControlRequest payload) throws Exception {
        logger.info("Request linkControlSectionOrder :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (BarcoUtil.isNull(payload.getControlLinkSection())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_LINK_SECTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getControlOrder())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_LINK_SECTION_ORDER_MISSING);
        }
        this.genControlLinkGenSectionRepository.saveAll(
            this.genControlLinkGenSectionRepository.findAllByIdInAndStatusNot(
                payload.getControlLinkSection(), APPLICATION_STATUS.DELETE).stream()
                .map(genControlLinkGenSection -> {
                    genControlLinkGenSection.setControlOrder(payload.getControlOrder());
                    genControlLinkGenSection.setFieldWidth(payload.getFieldWidth());
                    genControlLinkGenSection.setUpdatedBy(appUser.get());
                    return genControlLinkGenSection;
                }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getControlLinkSection()), payload);
    }

    /**
     * Method use to download stt* all file template
     * @param payload
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadSTTCommonTemplateFile(STTFileUploadRequest payload) throws Exception {
        logger.info("Request downloadSTTCommonTemplateFile :- " + payload);
        return downloadTemplateFile(this.tempStoreDirectory, this.bulkExcel,
            this.lookupDataCacheService.getSheetFiledMap().get(payload.getDownloadType()));
    }

    /**
     * Method use to download stt* all file
     * @param payload
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadSTTCommon(STTFileUploadRequest payload) throws Exception {
        logger.info("Request downloadSTTCommon :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            throw new Exception(MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDownloadType())) {
            throw new Exception(MessageUtil.DOWNLOAD_TYPE_MISSING);
        }
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(payload.getDownloadType());
        List<String> header = sheetFiled.getColTitle();
        XSSFWorkbook workbook = new XSSFWorkbook();
        this.bulkExcel.setWb(workbook);
        XSSFSheet xssfSheet = workbook.createSheet(payload.getDownloadType());
        this.bulkExcel.setSheet(xssfSheet);
        AtomicInteger rowCount = new AtomicInteger();
        this.bulkExcel.fillBulkHeader(rowCount.get(), header);
        // fill data
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            throw new Exception(MessageUtil.APPUSER_NOT_FOUND);
        }
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate().concat(BarcoUtil.START_DATE));
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate().concat(BarcoUtil.END_DATE));
        if (payload.getDownloadType().equals(this.bulkExcel.STT_FORM)) {
            List<GenForm> getForms;
            if (!BarcoUtil.isNull(payload.getIds()) && payload.getIds().size() > 0) {
                getForms = this.genFormRepository.findAllByDateCreatedBetweenAndCreatedByAndIdInAndStatusNotOrderByDateCreatedDesc(
                    startDate, endDate, adminUser.get(), payload.getIds(), APPLICATION_STATUS.DELETE);
            } else {
                getForms = this.genFormRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
                    startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE);
            }
            getForms.forEach(genForm -> {
                rowCount.getAndIncrement();
                List<String> dataCellValue = new ArrayList<>();
                dataCellValue.add(genForm.getFormName());
                dataCellValue.add(genForm.getFormType().name());
                if (!BarcoUtil.isNull(genForm.getHomePage())) {
                    dataCellValue.add(genForm.getHomePage().getLookupType());
                }
                dataCellValue.add(genForm.getServiceId());
                dataCellValue.add(genForm.getDescription());
                this.bulkExcel.fillBulkBody(dataCellValue, rowCount.get());
            });
        } else if (payload.getDownloadType().equals(this.bulkExcel.STT_SECTION)) {
            List<GenSection> genSections;
            if (!BarcoUtil.isNull(payload.getIds()) && payload.getIds().size() > 0) {
                genSections = this.genSectionRepository.findAllByDateCreatedBetweenAndCreatedByAndIdInAndStatusNotOrderByDateCreatedDesc(
                    startDate, endDate, adminUser.get(), payload.getIds(), APPLICATION_STATUS.DELETE);
            } else {
                genSections = this.genSectionRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
                    startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE);
            }
            genSections.forEach(genSection -> {
                rowCount.getAndIncrement();
                List<String> dataCellValue = new ArrayList<>();
                dataCellValue.add(genSection.getSectionName());
                dataCellValue.add(genSection.getDescription());
                this.bulkExcel.fillBulkBody(dataCellValue, rowCount.get());
            });
        } else if (payload.getDownloadType().equals(this.bulkExcel.STT_CONTROL)) {
            List<GenControl> genControls;
            if (!BarcoUtil.isNull(payload.getIds()) && payload.getIds().size() > 0) {
                genControls = this.genControlRepository.findAllByDateCreatedBetweenAndCreatedByAndIdInAndStatusNotOrderByDateCreatedDesc(
                    startDate, endDate, adminUser.get(), payload.getIds(), APPLICATION_STATUS.DELETE);
            } else {
                genControls = this.genControlRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
                    startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE);
            }
            genControls.forEach(genControl -> {
                rowCount.getAndIncrement();
                List<String> dataCellValue = new ArrayList<>();
                dataCellValue.add(genControl.getControlName());
                dataCellValue.add(genControl.getDescription());
                dataCellValue.add(genControl.getFieldName());
                dataCellValue.add(genControl.getFieldTitle());
                dataCellValue.add(genControl.getPlaceHolder());
                dataCellValue.add(genControl.getPattern());
                dataCellValue.add(genControl.getFieldType().name());
                dataCellValue.add(!BarcoUtil.isNull(genControl.getMinLength()) ?
                    genControl.getMinLength().toString(): this.bulkExcel.BLANK_VAL);
                dataCellValue.add(!BarcoUtil.isNull(genControl.getMaxLength()) ?
                    genControl.getMaxLength().toString(): this.bulkExcel.BLANK_VAL);
                dataCellValue.add(genControl.getMandatory().name());
                this.bulkExcel.fillBulkBody(dataCellValue, rowCount.get());
            });
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        return outStream;
    }

    /**
     * Method use to upload stt* file with data
     * @param fileObject
     * @return AppResponse
     * */
    @Override
    public AppResponse uploadSTTCommon(FileUploadRequest fileObject) throws Exception {
        logger.info("Request uploadSTTCommon :- " + fileObject);
        if (!fileObject.getFile().getContentType().equalsIgnoreCase(this.bulkExcel.SHEET_TYPE)) {
            logger.info("File Type " + fileObject.getFile().getContentType());
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.XLSX_FILE_ONLY);
        } else if (BarcoUtil.isNull(fileObject.getData())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND_XLSX);
        }
        Gson gson = new Gson();
        STTFileUploadRequest sttFileUReq = gson.fromJson((String) fileObject.getData(), STTFileUploadRequest.class);
        if (BarcoUtil.isNull(sttFileUReq.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(sttFileUReq.getUploadType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.UPLOAD_TYPE_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            sttFileUReq.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Optional<LookupData> uploadLimit = this.lookupDataRepository.findByLookupType(LookupUtil.UPLOAD_LIMIT);
        XSSFWorkbook workbook = new XSSFWorkbook(fileObject.getFile().getInputStream());
        if (BarcoUtil.isNull(workbook) || workbook.getNumberOfSheets() == 0) {
            return new AppResponse(BarcoUtil.ERROR,  MessageUtil.YOU_UPLOAD_EMPTY_FILE);
        }
        XSSFSheet sheet = null;
        if (sttFileUReq.getUploadType().equals(this.bulkExcel.STT) ||
            sttFileUReq.getUploadType().equals(this.bulkExcel.STT_FORM) ||
            sttFileUReq.getUploadType().equals(this.bulkExcel.STT_SECTION) ||
            sttFileUReq.getUploadType().equals(this.bulkExcel.STT_CONTROL)) {
            sheet = workbook.getSheet(sttFileUReq.getUploadType());
        }
        // target sheet upload limit validation
        if (BarcoUtil.isNull(sheet)) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.SHEET_NOT_FOUND, sttFileUReq.getUploadType()));
        } else if (sheet.getLastRowNum() < 1) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.YOU_CANT_UPLOAD_EMPTY_FILE);
        } else if (sheet.getLastRowNum() > Long.valueOf(uploadLimit.get().getLookupValue())) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.FILE_SUPPORT_ROW_AT_TIME, uploadLimit.get().getLookupValue()));
        }
        logger.info(String.format(MessageUtil.UPLOAD_FILE_TYPE, sttFileUReq.getUploadType()));
        if (sttFileUReq.getUploadType().equals(this.bulkExcel.STT_FORM)) {
            return this.uploadSTTForm(sheet, adminUser.get());
        } else if (sttFileUReq.getUploadType().equals(this.bulkExcel.STT_SECTION)) {
            return this.uploadSTTSection(sheet, adminUser.get());
        } else if (sttFileUReq.getUploadType().equals(this.bulkExcel.STT_CONTROL)) {
            return this.uploadSTTControl(sheet, adminUser.get());
        }
        return new AppResponse(BarcoUtil.ERROR, MessageUtil.WRONG_UPLOAD_TYPE_DEFINE);
    }

    /**
     * Method use to upload the stt form
     * @pa ram sheet
     * @param appUser
     * @return AppResponse
     * */
    private AppResponse uploadSTTForm(XSSFSheet sheet, AppUser appUser) throws Exception {
        List<STTFValidation> sttfValidations = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Iterator<Row> rows = sheet.iterator();
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.STT_FORM);
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (currentRow.getRowNum() == 0) {
                for (int i=0; i<sheetFiled.getColTitle().size(); i++) {
                    if (!currentRow.getCell(i).getStringCellValue().equals(sheetFiled.getColTitle().get(i))) {
                        return new AppResponse(BarcoUtil.ERROR, "File at row " + (currentRow.getRowNum() + 1) +
                            " " + sheetFiled.getColTitle().get(i) + " heading missing.");
                    }
                }
            } else if (currentRow.getRowNum() > 0) {
                STTFValidation sttfValidation = new STTFValidation();
                sttfValidation.setRowCounter(currentRow.getRowNum()+1);
                for (int i=0; i<sheetFiled.getColTitle().size(); i++) {
                    int index = 0;
                    if (i == index) {
                        sttfValidation.setFormName(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttfValidation.setFormType(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttfValidation.setHomePage(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttfValidation.setServiceId(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttfValidation.setDescription(this.bulkExcel.getCellDetail(currentRow, i));
                    }
                }
                sttfValidation.isValidSTTF();
                if (!BarcoUtil.isNull(sttfValidation.getHomePage()) &&
                    !this.lookupDataRepository.findByLookupType(sttfValidation.getHomePage()).isPresent()) {
                    // have to check home page contain in the db or not
                    sttfValidation.setErrorMsg(String.format("Homepage not found at row %s.<br>", sttfValidation.getRowCounter()));
                }
                if (!BarcoUtil.isNull(sttfValidation.getErrorMsg())) {
                    errors.add(sttfValidation.getErrorMsg());
                    continue;
                }
                sttfValidations.add(sttfValidation);
            }
        }
        if (errors.size() > 0) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.TOTAL_STTF_INVALID, errors.size()), errors);
        }
        sttfValidations.forEach(sttfValidation -> {
            GenForm genForm = new GenForm();
            genForm.setFormName(sttfValidation.getFormName());
            genForm.setDescription(sttfValidation.getDescription());
            genForm.setFormType(FORM_TYPE.findEnumByName(sttfValidation.getFormType()));
            genForm.setServiceId(sttfValidation.getServiceId());
            if (!BarcoUtil.isNull(sttfValidation.getHomePage())) {
                genForm.setHomePage(this.lookupDataRepository.findByLookupType(sttfValidation.getHomePage()).get());
            }
            genForm.setStatus(APPLICATION_STATUS.ACTIVE);
            genForm.setCreatedBy(appUser);
            genForm.setUpdatedBy(appUser);
            this.genFormRepository.save(genForm);
        });
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, ""));
    }

    /**
     * Method use to upload the stt section
     * @param sheet
     * @param appUser
     * @return AppResponse
     * */
    private AppResponse uploadSTTSection(XSSFSheet sheet, AppUser appUser) throws Exception {
        List<STTSValidation> sttsValidations = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Iterator<Row> rows = sheet.iterator();
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.STT_SECTION);
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (currentRow.getRowNum() == 0) {
                for (int i=0; i<sheetFiled.getColTitle().size(); i++) {
                    if (!currentRow.getCell(i).getStringCellValue().equals(sheetFiled.getColTitle().get(i))) {
                        return new AppResponse(BarcoUtil.ERROR, "File at row " + (currentRow.getRowNum() + 1)
                            + " " + sheetFiled.getColTitle().get(i) + " heading missing.");
                    }
                }
            } else if (currentRow.getRowNum() > 0) {
                STTSValidation sttsValidation = new STTSValidation();
                sttsValidation.setRowCounter(currentRow.getRowNum()+1);
                for (int i=0; i<sheetFiled.getColTitle().size(); i++) {
                    int index = 0;
                    if (i == index) {
                        sttsValidation.setSectionName(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttsValidation.setDescription(this.bulkExcel.getCellDetail(currentRow, i));
                    }
                }
                sttsValidation.isValidSTTS();
                if (!BarcoUtil.isNull(sttsValidation.getErrorMsg())) {
                    errors.add(sttsValidation.getErrorMsg());
                    continue;
                }
                sttsValidations.add(sttsValidation);
            }
        }
        if (errors.size() > 0) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.TOTAL_STTS_INVALID, errors.size()), errors);
        }
        sttsValidations.forEach(sttsValidation -> {
            GenSection genSection = new GenSection();
            genSection.setSectionName(sttsValidation.getSectionName());
            genSection.setDescription(sttsValidation.getDescription());
            genSection.setStatus(APPLICATION_STATUS.ACTIVE);
            genSection.setCreatedBy(appUser);
            genSection.setUpdatedBy(appUser);
            this.genSectionRepository.save(genSection);
        });
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, ""));
    }

    /**
     * Method use to upload the stt control
     * @param sheet
     * @param appUser
     * @return AppResponse
     * */
    private AppResponse uploadSTTControl(XSSFSheet sheet, AppUser appUser) throws Exception {
        List<STTCValidation> sttcValidations = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Iterator<Row> rows = sheet.iterator();
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.STT_CONTROL);
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (currentRow.getRowNum() == 0) {
                for (int i=0; i<sheetFiled.getColTitle().size(); i++) {
                    if (!currentRow.getCell(i).getStringCellValue().equals(sheetFiled.getColTitle().get(i))) {
                        return new AppResponse(BarcoUtil.ERROR, "File at row " + (currentRow.getRowNum() + 1)
                            + " " + sheetFiled.getColTitle().get(i) + " heading missing.");
                    }
                }
            } else if (currentRow.getRowNum() > 0) {
                STTCValidation sttcValidation = new STTCValidation();
                sttcValidation.setRowCounter(currentRow.getRowNum()+1);
                for (int i=0; i<sheetFiled.getColTitle().size(); i++) {
                    int index = 0;
                    if (i == index) {
                        sttcValidation.setControlName(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttcValidation.setDescription(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttcValidation.setFieldName(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttcValidation.setFieldTitle(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttcValidation.setPlaceHolder(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttcValidation.setPattern(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttcValidation.setFieldType(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttcValidation.setMinLength(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttcValidation.setMaxLength(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        sttcValidation.setRequired(this.bulkExcel.getCellDetail(currentRow, i));
                    }
                }
                sttcValidation.isValidSTTC();
                if (!BarcoUtil.isNull(sttcValidation.getFieldLkValue()) &&
                        !this.lookupDataRepository.findByLookupType(sttcValidation.getFieldLkValue()).isPresent()) {
                    // have to check home page contain in the db or not
                    sttcValidation.setErrorMsg(String.format("Filed Lookup not found at row %s.<br>", sttcValidation.getRowCounter()));
                }
                if (!BarcoUtil.isNull(sttcValidation.getErrorMsg())) {
                    errors.add(sttcValidation.getErrorMsg());
                    continue;
                }
                sttcValidations.add(sttcValidation);
            }
        }
        if (errors.size() > 0) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.TOTAL_STTC_INVALID, errors.size()), errors);
        }
        sttcValidations.forEach(sttcValidation -> {
            GenControl genControl = new GenControl();
            genControl.setControlName(sttcValidation.getControlName());
            genControl.setDescription(sttcValidation.getDescription());
            genControl.setFieldType(FIELD_TYPE.findEnumByName(sttcValidation.getFieldType()));
            genControl.setFieldTitle(sttcValidation.getFieldTitle());
            genControl.setFieldName(sttcValidation.getFieldName());
            genControl.setPlaceHolder(sttcValidation.getPlaceHolder());
            if (!BarcoUtil.isNull(sttcValidation.getMinLength())) {
                genControl.setMinLength(Long.valueOf(sttcValidation.getMinLength()));
            }
            if (!BarcoUtil.isNull(sttcValidation.getMaxLength())) {
                genControl.setMaxLength(Long.valueOf(sttcValidation.getMaxLength()));
            }
            if (FIELD_TYPE.RADIO.name().equals(sttcValidation.getFieldType()) ||
                FIELD_TYPE.CHECKBOX.name().equals(sttcValidation.getFieldType()) ||
                FIELD_TYPE.SELECT.name().equals(sttcValidation.getFieldType()) ||
                FIELD_TYPE.MULTI_SELECT.name().equals(sttcValidation.getFieldType())) {
                if (!BarcoUtil.isNull(sttcValidation.getFieldLkValue())) {
                    genControl.setFieldLkValue(this.lookupDataRepository.findByLookupType(sttcValidation.getFieldLkValue()).get());
                }
            }
            genControl.setMandatory(IS_DEFAULT.findEnumByName(sttcValidation.getRequired()));
            genControl.setIsDefault(IS_DEFAULT.NO_DEFAULT);
            genControl.setPattern(sttcValidation.getPattern());
            genControl.setStatus(APPLICATION_STATUS.ACTIVE);
            genControl.setCreatedBy(appUser);
            genControl.setUpdatedBy(appUser);
            this.genControlRepository.save(genControl);
        });
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, ""));
    }

    /**
     * Method use to get form response
     * @param genForm
     * @return FormResponse
     * */
    private FormResponse getFormResponse(GenForm genForm) {
        FormResponse formResponse = new FormResponse();
        formResponse.setId(genForm.getId());
        formResponse.setFormName(genForm.getFormName());
        formResponse.setDescription(genForm.getDescription());
        formResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(genForm.getStatus().getLookupCode()));
        formResponse.setFormType(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
            FORM_TYPE.getName(), genForm.getFormType().getLookupCode())));
        if (!BarcoUtil.isNull(genForm.getHomePage())) {
            LookupData lookupData = genForm.getHomePage();
            formResponse.setHomePage(new GLookup(lookupData.getLookupType(), lookupData.getLookupCode(), lookupData.getLookupValue()));
        }
        // report
        if (!BarcoUtil.isNull(genForm.getReport())) {
            ReportSetting reportSetting = genForm.getReport();
            formResponse.setReport(new ReportSettingResponse(reportSetting.getId(), reportSetting.getName()));
        }
        // dashboard
        if (!BarcoUtil.isNull(genForm.getDashboard())) {
            DashboardSetting dashboardSetting = genForm.getDashboard();
            formResponse.setDashboard(new DashboardSettingResponse(dashboardSetting.getId(), dashboardSetting.getName()));
        }
        formResponse.setServiceId(genForm.getServiceId());
        formResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(genForm.getStatus().getLookupType()));
        formResponse.setCreatedBy(getActionUser(genForm.getCreatedBy()));
        formResponse.setUpdatedBy(getActionUser(genForm.getUpdatedBy()));
        formResponse.setDateUpdated(genForm.getDateUpdated());
        formResponse.setDateCreated(genForm.getDateCreated());
        return formResponse;
    }

    /**
     * Method use to get section response
     * @param genSection
     * @return SectionResponse
     * **/
    private SectionResponse getSectionResponse(GenSection genSection) {
        SectionResponse sectionResponse = new SectionResponse();
        sectionResponse.setId(genSection.getId());
        sectionResponse.setSectionName(genSection.getSectionName());
        sectionResponse.setDescription(genSection.getDescription());
        sectionResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(genSection.getStatus().getLookupCode()));
        sectionResponse.setCreatedBy(getActionUser(genSection.getCreatedBy()));
        sectionResponse.setUpdatedBy(getActionUser(genSection.getUpdatedBy()));
        sectionResponse.setDateUpdated(genSection.getDateUpdated());
        sectionResponse.setDateCreated(genSection.getDateCreated());
        return sectionResponse;
    }

    /**
     * Method use to get control response
     * @param genControl
     * @return ControlResponse
     * **/
    private ControlResponse getControlResponse(GenControl genControl) {
        ControlResponse controlResponse = new ControlResponse();
        controlResponse.setId(genControl.getId());
        controlResponse.setControlName(genControl.getControlName());
        controlResponse.setDescription(genControl.getDescription());
        controlResponse.setFieldType(GLookup.getGLookup(this.lookupDataCacheService
            .getChildLookupDataByParentLookupTypeAndChildLookupCode(FIELD_TYPE.getName(),
                genControl.getFieldType().getLookupCode())));
        controlResponse.setFieldTitle(genControl.getFieldTitle());
        controlResponse.setFieldName(genControl.getFieldName());
        controlResponse.setPlaceHolder(genControl.getPlaceHolder());
        controlResponse.setMinLength(genControl.getMinLength());
        controlResponse.setMaxLength(genControl.getMaxLength());
        if (!BarcoUtil.isNull(genControl.getFieldLkValue())) {
            controlResponse.setFieldLkValue(genControl.getFieldLkValue().getLookupType());
        }
        controlResponse.setMandatory(GLookup.getGLookup(this.lookupDataCacheService
            .getChildLookupDataByParentLookupTypeAndChildLookupCode(IS_DEFAULT.getName(),
                genControl.getMandatory().getLookupCode())));
        controlResponse.setIsDefault(GLookup.getGLookup(this.lookupDataCacheService
            .getChildLookupDataByParentLookupTypeAndChildLookupCode(IS_DEFAULT.getName(),
                genControl.getIsDefault().getLookupCode())));
        controlResponse.setDefaultValue(genControl.getDefaultValue());
        controlResponse.setApiLkValue(genControl.getApiLkValue());
        controlResponse.setPattern(genControl.getPattern());
        controlResponse.setCreatedBy(getActionUser(genControl.getCreatedBy()));
        controlResponse.setUpdatedBy(getActionUser(genControl.getUpdatedBy()));
        controlResponse.setDateUpdated(genControl.getDateUpdated());
        controlResponse.setDateCreated(genControl.getDateCreated());
        controlResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(genControl.getStatus().getLookupCode()));
        return controlResponse;
    }

}

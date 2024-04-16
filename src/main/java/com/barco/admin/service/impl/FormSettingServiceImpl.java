package com.barco.admin.service.impl;

import com.barco.admin.service.FormSettingService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.ControlRequest;
import com.barco.model.dto.request.FormRequest;
import com.barco.model.dto.request.SectionRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.ControlResponse;
import com.barco.model.dto.response.FormResponse;
import com.barco.model.dto.response.SectionResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.GenControl;
import com.barco.model.pojo.GenForm;
import com.barco.model.pojo.GenSection;
import com.barco.model.repository.*;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 * Service use to mg the form setting
 */
@Service
public class FormSettingServiceImpl implements FormSettingService {

    private Logger logger = LoggerFactory.getLogger(FormSettingServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private GenFormRepository genFormRepository;
    @Autowired
    private GenSectionRepository genSectionRepository;
    @Autowired
    private GenControlRepository genControlRepository;
    @Autowired
    private LookupDataRepository lookupDataRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private GenFormLinkSourceTaskTypeRepository genFormLinkSourceTaskTypeRepository;
    @Autowired
    private GenControlInteractionsRepository genControlInteractionsRepository;
    @Autowired
    private GenControlLinkGenSectionRepository genControlLinkGenSectionRepository;
    @Autowired
    private GenSectionLinkGenFormRepository genSectionLinkGenFormRepository;


    /**
     * Method use to add the form
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addForm(FormRequest payload) {
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
        genForm.setDescription(payload.getDescription());
        genForm.setFormType(FORM_TYPE.getByLookupCode(payload.getFormType()));
        genForm.setHomePage(payload.getHomePage());
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
     * Method use to edit the form
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse editForm(FormRequest payload) {
        logger.info("Request editForm :- " + payload);
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
        genForm.get().setHomePage(payload.getHomePage());
        if (!BarcoUtil.isNull(payload.getServiceId())) {
            genForm.get().setServiceId(payload.getServiceId());
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            genForm.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            if (!BarcoUtil.isNull(genForm.get().getGenFormLinkSourceTaskTypes())) {
                genForm.get().getGenFormLinkSourceTaskTypes().stream()
                    .filter(genFormLinkStt -> !genFormLinkStt.getStatus().equals(APPLICATION_STATUS.DELETE.getLookupValue()))
                    .map(genFormLinkStt -> {
                        genFormLinkStt.setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
                        return genFormLinkStt;
                    }).collect(Collectors.toList());
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
    public AppResponse deleteFormById(FormRequest payload) {
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
        if (!BarcoUtil.isNull(payload.getStatus())) {
            genForm.get().getGenFormLinkSourceTaskTypes().stream()
                .filter(genFormLinkStt -> !genFormLinkStt.getStatus().equals(APPLICATION_STATUS.DELETE.getLookupValue()))
                .map(genFormLinkStt -> {
                    genFormLinkStt.setStatus(APPLICATION_STATUS.DELETE);
                    genFormLinkStt.setUpdatedBy(adminUser.get());
                    return genFormLinkStt;
                }).collect(Collectors.toList());
        }
        this.genSectionLinkGenFormRepository.deleteAllByStatusAndFormIdAndAppUserId(
            APPLICATION_STATUS.DELETE, genForm.get().getId(), adminUser.get().getId());
        this.genFormRepository.save(genForm.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()), payload);
    }

    /**
     * Method use to fetch the form by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchFormByFormId(FormRequest payload) {
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
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, getFormResponse(genForm.get()));
    }

    /**
     * Method use to fetch the form
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchForms(FormRequest payload) {
        logger.info("Request fetchForms :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
        List<GenForm> result = this.genFormRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNot(
            startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE);
        if (result.isEmpty()) {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, new ArrayList<>());
        }
        List<FormResponse> formResponses = result.stream().map(genForm -> {
            FormResponse formResponse = getFormResponse(genForm);
            formResponse.setTotalStt(this.genFormLinkSourceTaskTypeRepository.countByGenFormAndStatusNot(genForm, APPLICATION_STATUS.DELETE));
            formResponse.setTotalSection(this.genSectionLinkGenFormRepository.countByGenFormAndStatusNot(genForm, APPLICATION_STATUS.DELETE));
            return formResponse;
        }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, formResponses);
    }

    /**
     * Method use to delete all forms
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllForms(FormRequest payload) throws Exception {
        return null;
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
     * Method use to edit section
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse editSection(SectionRequest payload) throws Exception {
        logger.info("Request editSection :- " + payload);
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
        genSection.get().setId(payload.getId());
        genSection.get().setSectionName(payload.getSectionName());
        genSection.get().setDescription(payload.getDescription());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            genSection.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            if (!BarcoUtil.isNull(genSection.get().getGenSectionLinkGenForms())) {
                genSection.get().getGenSectionLinkGenForms().stream()
                    .filter(genSectionLinkGenForm -> !genSectionLinkGenForm.getStatus().equals(APPLICATION_STATUS.DELETE.getLookupValue()))
                    .map(genSectionLinkGenForm -> {
                        genSectionLinkGenForm.setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
                        return genSectionLinkGenForm;
                    }).collect(Collectors.toList());
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
            genSection.get().getGenSectionLinkGenForms().stream()
                .filter(genSectionLinkGenForm -> !genSectionLinkGenForm.getStatus().equals(APPLICATION_STATUS.DELETE.getLookupValue()))
                .map(genSectionLinkGenForm -> {
                    genSectionLinkGenForm.setStatus(APPLICATION_STATUS.DELETE);
                    return genSectionLinkGenForm;
                }).collect(Collectors.toList());
        }
        this.genControlLinkGenSectionRepository.deleteAllByStatusAndSectionIdAndAppUserId(
            APPLICATION_STATUS.ACTIVE, genSection.get().getId(), adminUser.get().getId());
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
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, getSectionResponse(genSection.get()));
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
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
        List<GenSection> result = this.genSectionRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNot(
            startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE);
        if (result.isEmpty()) {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, new ArrayList<>());
        }
        List<SectionResponse> sectionResponses = result.stream().map(genSection -> {
            SectionResponse sectionResponse = getSectionResponse(genSection);
            sectionResponse.setTotalForm(this.genSectionLinkGenFormRepository.countByGenSectionAndStatusNot(
                genSection, APPLICATION_STATUS.DELETE));
            sectionResponse.setTotalControl(this.genControlLinkGenSectionRepository.countByGenSectionAndStatusNot(
                genSection, APPLICATION_STATUS.DELETE));
            return sectionResponse;
        }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, sectionResponses);
    }

    /**
     * Method use to delete all section
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllSections(SectionRequest payload) throws Exception {
        logger.info("Request deleteAllSections :- " + payload);
        return null;
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
        } else if (BarcoUtil.isNull(payload.getFieldWidth())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_FIELD_WIDTH_MISSING);
        } else if (BarcoUtil.isNull(payload.getMandatory())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_MANDATORY_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        GenControl genControl = new GenControl();
        genControl.setControlName(payload.getControlName());
        genControl.setFieldType(FILED_TYPE.getByLookupCode(payload.getFieldType()));
        genControl.setFieldTitle(payload.getFieldTitle());
        genControl.setFieldName(payload.getFieldName());
        genControl.setDescription(payload.getDescription());
        genControl.setPlaceHolder(payload.getPlaceHolder());
        genControl.setFieldWidth(payload.getFieldWidth());
        genControl.setMinLength(payload.getMinLength());
        genControl.setMaxLength(payload.getMaxLength());
        if (FILED_TYPE.RADIO.getLookupCode().equals(payload.getFieldType()) ||
            FILED_TYPE.CHECKBOX.getLookupCode().equals(payload.getFieldType()) ||
            FILED_TYPE.SELECT.getLookupCode().equals(payload.getFieldType()) ||
            FILED_TYPE.MULTI_SELECT.getLookupCode().equals(payload.getFieldType())) {
            genControl.setFieldLkValue(payload.getFieldLkValue());
        }
        genControl.setDisabled(IS_DEFAULT.getByLookupCode(payload.getDisabled()));
        genControl.setMandatory(IS_DEFAULT.getByLookupCode(payload.getMandatory()));
        genControl.setIsDefault(IS_DEFAULT.getByLookupCode(payload.getIsDefault()));
        genControl.setDefaultValue(payload.getDefaultValue());
        genControl.setPattern(payload.getPattern());
        genControl.setStatus(APPLICATION_STATUS.ACTIVE);
        genControl.setCreatedBy(adminUser.get());
        genControl.setUpdatedBy(adminUser.get());
        this.genControlRepository.save(genControl);
        payload.setId(genControl.getId());
        return new AppResponse(BarcoUtil.SUCCESS, String.format("STTControl added with %d.", payload.getId()));
    }

    /**
     * Method use to edit control (form control)
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse editControl(ControlRequest payload) throws Exception {
        logger.info("Request editControl :- " + payload);
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
        } else if (BarcoUtil.isNull(payload.getFieldWidth())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_FIELD_WIDTH_MISSING);
        } else if (BarcoUtil.isNull(payload.getMandatory())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CONTROL_MANDATORY_MISSING);
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
        if (FILED_TYPE.RADIO.getLookupCode().equals(payload.getFieldType()) ||
            FILED_TYPE.CHECKBOX.getLookupCode().equals(payload.getFieldType()) ||
            FILED_TYPE.SELECT.getLookupCode().equals(payload.getFieldType()) ||
            FILED_TYPE.MULTI_SELECT.getLookupCode().equals(payload.getFieldType())) {
            genControl.get().setFieldType(FILED_TYPE.getByLookupCode(payload.getFieldType()));
        }
        genControl.get().setControlName(payload.getControlName());
        genControl.get().setFieldTitle(payload.getFieldTitle());
        genControl.get().setFieldName(payload.getFieldName());
        genControl.get().setDescription(payload.getDescription());
        genControl.get().setPlaceHolder(payload.getPlaceHolder());
        genControl.get().setFieldWidth(payload.getFieldWidth());
        genControl.get().setMinLength(payload.getMinLength());
        genControl.get().setMaxLength(payload.getMaxLength());
        genControl.get().setDisabled(IS_DEFAULT.getByLookupCode(payload.getDisabled()));
        genControl.get().setMandatory(IS_DEFAULT.getByLookupCode(payload.getMandatory()));
        genControl.get().setIsDefault(IS_DEFAULT.getByLookupCode(payload.getIsDefault()));
        genControl.get().setDefaultValue(payload.getDefaultValue());
        genControl.get().setPattern(payload.getPattern());
        if (FILED_TYPE.RADIO.getLookupCode().equals(payload.getFieldType()) ||
            FILED_TYPE.CHECKBOX.getLookupCode().equals(payload.getFieldType()) ||
            FILED_TYPE.SELECT.getLookupCode().equals(payload.getFieldType()) ||
            FILED_TYPE.MULTI_SELECT.getLookupCode().equals(payload.getFieldType())) {
            genControl.get().setFieldLkValue(payload.getFieldLkValue());
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            genControl.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            if (!BarcoUtil.isNull(genControl.get().getGenControlLinkGenSections())) {
                genControl.get().getGenControlLinkGenSections().stream()
                    .filter(genControlLinkGenSection -> !genControlLinkGenSection.getStatus().equals(APPLICATION_STATUS.DELETE.getLookupValue()))
                    .map(genControlLinkGenSection -> {
                        genControlLinkGenSection.setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
                        return genControlLinkGenSection;
                    }).collect(Collectors.toList());
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
        if (!BarcoUtil.isNull(payload.getStatus())) {
            genControl.get().setStatus(APPLICATION_STATUS.DELETE);
            if (!BarcoUtil.isNull(genControl.get().getGenControlLinkGenSections())) {
                genControl.get().getGenControlLinkGenSections().stream()
                    .filter(genControlLinkGenSection -> !genControlLinkGenSection.getStatus().equals(APPLICATION_STATUS.DELETE.getLookupValue()))
                    .map(genControlLinkGenSection -> {
                        genControlLinkGenSection.setStatus(APPLICATION_STATUS.DELETE);
                        return genControlLinkGenSection;
                    }).collect(Collectors.toList());
            }
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
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, getControlResponse(genControl.get()));
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
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
        List<ControlResponse> controlResponses = this.genControlRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNot(
            startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE)
            .stream()
            .map(genControl -> {
                ControlResponse controlResponse = getControlResponse(genControl);
                controlResponse.setTotalSection(this.genControlLinkGenSectionRepository.countByGenControlAndStatusNot(genControl, APPLICATION_STATUS.DELETE));
                return controlResponse;
            }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, controlResponses);
    }

    @Override
    public AppResponse deleteAllControls(ControlRequest payload) throws Exception {
        logger.info("Request deleteAllControls :- " + payload);
        return null;
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
            formResponse.setHomePage(this.getDBLoopUp(this.lookupDataRepository.findByLookupType(genForm.getHomePage())));
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
            .getChildLookupDataByParentLookupTypeAndChildLookupCode(FILED_TYPE.getName(),
                genControl.getFieldType().getLookupCode())));
        controlResponse.setFieldTitle(genControl.getFieldTitle());
        controlResponse.setFieldName(genControl.getFieldName());
        controlResponse.setPlaceHolder(genControl.getPlaceHolder());
        controlResponse.setFieldWidth(genControl.getFieldWidth());
        controlResponse.setMinLength(genControl.getMinLength());
        controlResponse.setMaxLength(genControl.getMaxLength());
        controlResponse.setFieldLkValue(genControl.getFieldLkValue());
        controlResponse.setMandatory(GLookup.getGLookup(this.lookupDataCacheService
            .getChildLookupDataByParentLookupTypeAndChildLookupCode(IS_DEFAULT.getName(),
                genControl.getMandatory().getLookupCode())));
        controlResponse.setIsDefault(GLookup.getGLookup(this.lookupDataCacheService
            .getChildLookupDataByParentLookupTypeAndChildLookupCode(IS_DEFAULT.getName(),
                genControl.getIsDefault().getLookupCode())));
        controlResponse.setDisabled(GLookup.getGLookup(this.lookupDataCacheService
            .getChildLookupDataByParentLookupTypeAndChildLookupCode(IS_DEFAULT.getName(),
                genControl.getDisabled().getLookupCode())));
        controlResponse.setDefaultValue(genControl.getDefaultValue());
        controlResponse.setPattern(genControl.getPattern());
        controlResponse.setCreatedBy(getActionUser(genControl.getCreatedBy()));
        controlResponse.setUpdatedBy(getActionUser(genControl.getUpdatedBy()));
        controlResponse.setDateUpdated(genControl.getDateUpdated());
        controlResponse.setDateCreated(genControl.getDateCreated());
        controlResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(genControl.getStatus().getLookupCode()));
        return controlResponse;
    }

}

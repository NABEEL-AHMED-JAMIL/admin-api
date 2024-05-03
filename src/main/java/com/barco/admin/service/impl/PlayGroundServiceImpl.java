package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.PlayGroundService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.dform.IDynamicValidation;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.request.PlayGroundRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.dform.IDynamicControl;
import com.barco.model.dto.dform.IDynamicForm;
import com.barco.model.dto.dform.IDynamicSection;
import com.barco.model.enums.ErrorAssosiation;
import com.barco.model.pojo.GenControl;
import com.barco.model.pojo.GenControlLinkGenSection;
import com.barco.model.pojo.GenForm;
import com.barco.model.pojo.GenSectionLinkGenForm;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.GenControlLinkGenSectionRepository;
import com.barco.model.repository.GenFormRepository;
import com.barco.model.repository.GenSectionLinkGenFormRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.FIELD_TYPE;
import com.barco.model.util.lookup.GLookup;
import com.barco.model.util.lookup.IS_DEFAULT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 * TemplateReg can be email and etc
 */
@Service
public class PlayGroundServiceImpl implements PlayGroundService {

    private Logger logger = LoggerFactory.getLogger(PlayGroundServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private GenFormRepository genFormRepository;
    @Autowired
    private GenControlLinkGenSectionRepository genControlLinkGenSectionRepository;
    @Autowired
    private GenSectionLinkGenFormRepository genSectionLinkGenFormRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    /**
     * Method use to fetch all form for test
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllFormForPlayGround(PlayGroundRequest payload) throws Exception {
        logger.info("Request fetchAllFormForPlayGround :- " + payload);
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.genFormRepository.findAllByDateCreatedBetweenAndStatusNotOrderByDateCreatedDesc(startDate, endDate, APPLICATION_STATUS.DELETE)
            .stream().map(genForm -> {
                IDynamicForm dynamicForm = new IDynamicForm();
                dynamicForm.setId(genForm.getId());
                dynamicForm.setName(genForm.getFormName());
                return dynamicForm;
            }).collect(Collectors.toList()));
    }

    /**
     * Method use to fetch the form by form id for test
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchFormForPlayGroundByFormId(PlayGroundRequest payload) throws Exception {
        logger.info("Request fetchFormForPlayGroundByFormId :- " + payload);
        Optional<GenForm> genForm = this.genFormRepository.findByIdAndStatus(payload.getId(), APPLICATION_STATUS.ACTIVE);
        if (!genForm.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, getDynamicForm(genForm.get()));
    }

    /**
     * Method use to get the dynamic form
     * @param genForm
     * @return IDynamicForm
     * */
    public IDynamicForm getDynamicForm(GenForm genForm) throws Exception {
        IDynamicForm dynamicForm = new IDynamicForm();
        dynamicForm.setId(genForm.getId());
        dynamicForm.setName(genForm.getFormName());
        dynamicForm.setDescription(genForm.getDescription());
        if (!BarcoUtil.isNull(genForm.getGenSectionLinkGenForms())) {
            dynamicForm.setSections(this.genSectionLinkGenFormRepository
                .findAllByGenFormAndStatus(genForm, APPLICATION_STATUS.ACTIVE)
                .stream().map(sectionLinkForm -> {
                    try {
                        return getDynamicSection(sectionLinkForm);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).sorted(Comparator.comparing(IDynamicSection::getOrder)).collect(Collectors.toList()));
        }
        return dynamicForm;
    }

    /**
     * Method use to get the dynamic section
     * @param sectionLinkForm
     * @return IDynamicSection
     * */
    public IDynamicSection getDynamicSection(GenSectionLinkGenForm sectionLinkForm) throws Exception {
        IDynamicSection dynamicSection = new IDynamicSection();
        dynamicSection.setOrder(sectionLinkForm.getSectionOrder());
        dynamicSection.setId(sectionLinkForm.getGenSection().getId());
        dynamicSection.setName(sectionLinkForm.getGenSection().getSectionName());
        dynamicSection.setControls(this.genControlLinkGenSectionRepository.findAllByGenSectionAndStatus(
            sectionLinkForm.getGenSection(), APPLICATION_STATUS.ACTIVE)
            .stream().map(genControlLinkSection -> {
                try {
                    return getIDynamicControl(genControlLinkSection);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).sorted(Comparator.comparing(IDynamicControl::getOrder)).collect(Collectors.toList()));
        return dynamicSection;
    }

    /**
     * Method use to get the dynamic control
     * @param genCntLinkGenSct
     * @return IDynamicControl
     * */
    public IDynamicControl getIDynamicControl(GenControlLinkGenSection genCntLinkGenSct) throws Exception {
        IDynamicControl dynamicControl = new IDynamicControl();
        dynamicControl.setOrder(genCntLinkGenSct.getControlOrder());
        dynamicControl.setDisabledPattern(genCntLinkGenSct.getDisabledPattern());
        dynamicControl.setVisiblePattern(genCntLinkGenSct.getVisiblePattern());
        dynamicControl.setId(genCntLinkGenSct.getGenControl().getId());
        dynamicControl.setType(GLookup.getGLookup(this.lookupDataCacheService
            .getChildLookupDataByParentLookupTypeAndChildLookupCode(FIELD_TYPE.getName(),
                genCntLinkGenSct.getGenControl().getFieldType().getLookupCode())));
        dynamicControl.setLabel(genCntLinkGenSct.getGenControl().getFieldTitle());
        dynamicControl.setName(genCntLinkGenSct.getGenControl().getControlName());
        dynamicControl.setValue(genCntLinkGenSct.getGenControl().getDefaultValue());
        dynamicControl.setPlaceHolder(genCntLinkGenSct.getGenControl().getPlaceHolder());
        dynamicControl.setWidth(genCntLinkGenSct.getGenControl().getFieldWidth());
        if (!BarcoUtil.isNull(genCntLinkGenSct.getGenControl().getFieldLkValue())) {
            LookupDataRequest lookupDataRequest = new LookupDataRequest();
            lookupDataRequest.setLookupType(genCntLinkGenSct.getGenControl().getFieldLkValue());
            dynamicControl.setSelectMenuOptions((Map<String, Object>) this.lookupDataCacheService
                .fetchLookupDataByLookupType(lookupDataRequest).getData());
        }
        this.addValidation(dynamicControl, genCntLinkGenSct.getGenControl());
        return dynamicControl;
    }

    /**
     * Method use to add validation
     * @param dynamicControl
     * @param genControl
     * */
    private void addValidation(IDynamicControl dynamicControl, GenControl genControl) {
        List<IDynamicValidation> dynamicValidations = new ArrayList<>();
        if (!BarcoUtil.isNull(genControl.getMandatory())
            && genControl.getMandatory().equals(IS_DEFAULT.YES_DEFAULT)) {
            dynamicValidations.add(new IDynamicValidation(ErrorAssosiation.REQUIRED,
                String.format("%s is required.", dynamicControl.getName().toUpperCase())));
        }
        if (!BarcoUtil.isNull(genControl.getMinLength())) {
            dynamicValidations.add(new IDynamicValidation(ErrorAssosiation.MIN_LENGTH,
                String.format("%s min length.", dynamicControl.getName().toUpperCase())));
        }
        if (!BarcoUtil.isNull(genControl.getMaxLength())) {
            dynamicValidations.add(new IDynamicValidation(ErrorAssosiation.MAX_LENGTH,
                String.format("%s max length.", dynamicControl.getName().toUpperCase())));
        }
        if (!BarcoUtil.isNull(genControl.getPattern())) {
            dynamicValidations.add(new IDynamicValidation(ErrorAssosiation.PATTERN,
                String.format("%s not match with pattern.", dynamicControl.getName().toUpperCase()),
                genControl.getPattern()));
        }
        if (dynamicValidations.size() > 0) {
            dynamicControl.setValidators(dynamicValidations);
        }
    }

}

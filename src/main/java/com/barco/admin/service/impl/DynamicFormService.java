package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.RootService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.dform.request.IDynamicControl;
import com.barco.model.dto.dform.request.IDynamicForm;
import com.barco.model.dto.dform.request.IDynamicSection;
import com.barco.model.dto.dform.request.IDynamicValidation;
import com.barco.model.dto.request.LoginRequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.LookupDataResponse;
import com.barco.model.enums.ErrorAssociation;
import com.barco.model.pojo.GenControl;
import com.barco.model.pojo.GenControlLinkGenSection;
import com.barco.model.pojo.GenForm;
import com.barco.model.pojo.GenSectionLinkGenForm;
import com.barco.model.repository.GenControlLinkGenSectionRepository;
import com.barco.model.repository.GenSectionLinkGenFormRepository;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.FIELD_TYPE;
import com.barco.model.util.lookup.GLookup;
import com.barco.model.util.lookup.IS_DEFAULT;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 * dynamic form
 */
public class DynamicFormService {

    private Logger logger = LoggerFactory.getLogger(DynamicFormService.class);

    @Autowired
    private GenSectionLinkGenFormRepository genSectionLinkGenFormRepository;
    @Autowired
    private GenControlLinkGenSectionRepository genControlLinkGenSectionRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

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
        dynamicControl.setId(genCntLinkGenSct.getGenControl().getId());
        dynamicControl.setName(genCntLinkGenSct.getGenControl().getFieldName());
        dynamicControl.setOrder(genCntLinkGenSct.getControlOrder());
        dynamicControl.setWidth(genCntLinkGenSct.getFieldWidth());
        dynamicControl.setType(GLookup.getGLookup(this.lookupDataCacheService
            .getChildLookupDataByParentLookupTypeAndChildLookupCode(FIELD_TYPE.getName(),
                genCntLinkGenSct.getGenControl().getFieldType().getLookupCode())));
        dynamicControl.setLabel(genCntLinkGenSct.getGenControl().getFieldTitle());
        dynamicControl.setPattern(genCntLinkGenSct.getGenControl().getPattern());
        if (dynamicControl.getType().getLookupCode().equals(FIELD_TYPE.MULTI_SELECT.getLookupCode())) {
            dynamicControl.setValue(!BarcoUtil.isBlank(genCntLinkGenSct.getGenControl().getDefaultValue())
                ? genCntLinkGenSct.getGenControl().getDefaultValue().split(","): new Object[]{});
        } else {
            dynamicControl.setValue(genCntLinkGenSct.getGenControl().getDefaultValue());
        }
        dynamicControl.setPlaceHolder(genCntLinkGenSct.getGenControl().getPlaceHolder());
        if (!BarcoUtil.isNull(genCntLinkGenSct.getGenControl().getFieldLkValue())) {
            dynamicControl.setSelectMenuOptions(this.getGLookup((Map<String, Object>) this.lookupDataCacheService
                .fetchLookupDataByLookupType(new LookupDataRequest(genCntLinkGenSct.getGenControl().getFieldLkValue().getLookupType())).getData()));
        }
        dynamicControl.setApiLkValue(genCntLinkGenSct.getGenControl().getApiLkValue());
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
        if (!BarcoUtil.isNull(genControl.getMandatory()) && genControl.getMandatory().equals(IS_DEFAULT.YES_DEFAULT)) {
            dynamicValidations.add(new IDynamicValidation(ErrorAssociation.REQUIRED.getAssosiation(),
                String.format("%s is required.", dynamicControl.getLabel())));
        }
        if (!BarcoUtil.isNull(genControl.getMinLength())) {
            dynamicValidations.add(new IDynamicValidation(ErrorAssociation.MIN_LENGTH.getAssosiation(),
                String.format("%s min %s length.", dynamicControl.getLabel(), genControl.getMinLength()),
                    String.valueOf(genControl.getMinLength())));
        }
        if (!BarcoUtil.isNull(genControl.getMaxLength())) {
            dynamicValidations.add(new IDynamicValidation(ErrorAssociation.MAX_LENGTH.getAssosiation(),
                String.format("%s max %s length.", dynamicControl.getLabel(), genControl.getMaxLength()),
                    String.valueOf(genControl.getMaxLength())));
        }
        if (patternNotRequiredFiled(genControl) && !BarcoUtil.isNull(genControl.getPattern())) {
            dynamicValidations.add(new IDynamicValidation(ErrorAssociation.PATTERN.getAssosiation(),
                String.format("%s not match with pattern.", dynamicControl.getLabel()), genControl.getPattern()));
        }
        dynamicControl.setValidators(dynamicValidations);
    }

    /**
     * Method use to check the pattern not required field
     * @param genControl
     * @return boolean
     * */
    private boolean patternNotRequiredFiled(GenControl genControl) {
        if (genControl.getFieldType().equals(FIELD_TYPE.DATE) ||
            genControl.getFieldType().equals(FIELD_TYPE.MONTH) ||
            genControl.getFieldType().equals(FIELD_TYPE.YEAR) ||
            genControl.getFieldType().equals(FIELD_TYPE.WEEK)) {
            return false;
        }
        return true;
    }

    /**
     * Method use to convert lookup date to g-lookup
     * @param selectMenuOptions
     * @return List<GLookup>
     * */
    private List<GLookup> getGLookup(Map<String, Object> selectMenuOptions) {
        List<LookupDataResponse> lookupDataResponses = (List<LookupDataResponse>) selectMenuOptions.get(RootService.SUB_LOOKUP_DATA);
        return lookupDataResponses.stream().map(lookupDataResponse -> GLookup.getGLookupV2(lookupDataResponse)).collect(Collectors.toList());
    }
}

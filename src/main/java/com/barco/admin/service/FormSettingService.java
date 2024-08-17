package com.barco.admin.service;

import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface FormSettingService extends RootService {

    public AppResponse addForm(FormRequest payload) throws Exception;

    public AppResponse updateForm(FormRequest payload) throws Exception;

    public AppResponse deleteFormById(FormRequest payload) throws Exception;

    public AppResponse fetchFormByFormId(FormRequest payload) throws Exception;

    public AppResponse fetchForms(FormRequest payload) throws Exception;

    public AppResponse fetchFormsByFormType(FormRequest payload) throws Exception;

    public AppResponse deleteAllForms(FormRequest payload) throws Exception;

    public AppResponse fetchAllFormLinkSTT(FormRequest payload) throws Exception;

    public AppResponse linkFormSTT(FormRequest payload) throws Exception;

    public AppResponse fetchAllFormLinkSection(FormRequest payload) throws Exception;

    public AppResponse linkFormSection(FormRequest payload) throws Exception;

    public AppResponse linkFormSectionOrder(FormRequest payload) throws Exception;

    public AppResponse addSection(SectionRequest payload) throws Exception;

    public AppResponse updateSection(SectionRequest payload) throws Exception;

    public AppResponse deleteSectionById(SectionRequest payload) throws Exception;

    public AppResponse fetchSectionBySectionId(SectionRequest payload) throws Exception;

    public AppResponse fetchSections(SectionRequest payload) throws Exception;

    public AppResponse deleteAllSections(SectionRequest payload) throws Exception;

    public AppResponse fetchAllSectionLinkControl(SectionRequest payload) throws Exception;

    public AppResponse linkSectionControl(SectionRequest payload) throws Exception;

    public AppResponse linkSectionControlOrder(SectionRequest payload) throws Exception;

    public AppResponse fetchAllSectionLinkForm(SectionRequest payload) throws Exception;

    public AppResponse linkSectionForm(SectionRequest payload) throws Exception;

    public AppResponse linkSectionFormOrder(SectionRequest payload) throws Exception;

    public AppResponse addControl(ControlRequest payload) throws Exception;

    public AppResponse updateControl(ControlRequest payload) throws Exception;

    public AppResponse deleteControlById(ControlRequest payload) throws Exception;

    public AppResponse fetchControlByControlId(ControlRequest payload) throws Exception;

    public AppResponse fetchControls(ControlRequest payload) throws Exception;

    public AppResponse deleteAllControls(ControlRequest payload) throws Exception;

    public AppResponse fetchAllControlLinkSection(ControlRequest payload) throws Exception;

    public AppResponse linkControlSection(ControlRequest payload) throws Exception;

    public AppResponse linkControlSectionOrder(ControlRequest payload) throws Exception;

    public ByteArrayOutputStream downloadSTTCommonTemplateFile(STTFileUploadRequest payload) throws Exception;

    public ByteArrayOutputStream downloadSTTCommon(STTFileUploadRequest payload) throws Exception;

    public AppResponse uploadSTTCommon(FileUploadRequest payload) throws Exception;

}

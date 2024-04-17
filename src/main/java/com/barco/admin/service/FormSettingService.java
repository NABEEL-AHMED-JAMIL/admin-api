package com.barco.admin.service;

import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface FormSettingService extends RootService {

    public AppResponse addSTT(STTRequest payload) throws Exception;

    public AppResponse editSTT(STTRequest payload) throws Exception;

    public AppResponse deleteSTT(STTRequest payload) throws Exception;

    public AppResponse fetchSTTBySttId(STTRequest payload) throws Exception;

    public AppResponse fetchSTT(STTRequest payload) throws Exception;

    public AppResponse deleteAllSTT(FormRequest payload) throws Exception;

    public AppResponse addForm(FormRequest payload) throws Exception;

    public AppResponse editForm(FormRequest payload) throws Exception;

    public AppResponse deleteFormById(FormRequest payload) throws Exception;

    public AppResponse fetchFormByFormId(FormRequest payload) throws Exception;

    public AppResponse fetchForms(FormRequest payload) throws Exception;

    public AppResponse deleteAllForms(FormRequest payload) throws Exception;

    public AppResponse addSection(SectionRequest payload) throws Exception;

    public AppResponse editSection(SectionRequest payload) throws Exception;

    public AppResponse deleteSectionById(SectionRequest payload) throws Exception;

    public AppResponse fetchSectionBySectionId(SectionRequest payload) throws Exception;

    public AppResponse fetchSections(SectionRequest payload) throws Exception;

    public AppResponse deleteAllSections(SectionRequest payload) throws Exception;

    public AppResponse addControl(ControlRequest payload) throws Exception;

    public AppResponse editControl(ControlRequest payload) throws Exception;

    public AppResponse deleteControlById(ControlRequest payload) throws Exception;

    public AppResponse fetchControlByControlId(ControlRequest payload) throws Exception;

    public AppResponse fetchControls(ControlRequest payload) throws Exception;

    public AppResponse deleteAllControls(ControlRequest payload) throws Exception;

    public ByteArrayOutputStream downloadSTTCommonTemplateFile(STTFileUploadRequest payload) throws Exception;

    public ByteArrayOutputStream downloadSTTCommon(STTFileUploadRequest payload) throws Exception;

    public AppResponse uploadSTTCommon(FileUploadRequest payload) throws Exception;

}

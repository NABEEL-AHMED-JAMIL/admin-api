package com.barco.admin.service;

import com.barco.model.dto.request.ControlRequest;
import com.barco.model.dto.request.FormRequest;
import com.barco.model.dto.request.SectionRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface FormSettingService extends RootService {

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

}

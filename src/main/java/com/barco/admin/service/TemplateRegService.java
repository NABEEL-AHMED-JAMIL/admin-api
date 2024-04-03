package com.barco.admin.service;

import com.barco.model.dto.request.TemplateRegRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface TemplateRegService extends RootService {

	public AppResponse addTemplateReg(TemplateRegRequest payload) throws Exception;

	public AppResponse editTemplateReg(TemplateRegRequest payload) throws Exception;

	public AppResponse findTemplateRegByTemplateId(TemplateRegRequest payload) throws Exception;

	public AppResponse fetchTemplateReg(TemplateRegRequest payload) throws Exception;

	public AppResponse deleteTemplateReg(TemplateRegRequest payload) throws Exception;
	
}

package com.barco.admin.service;

import com.barco.model.dto.request.OrganizationRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface OrganizationService extends RootService {

    public AppResponse addOrg(OrganizationRequest payload) throws Exception;

    public AppResponse updateOrg(OrganizationRequest payload) throws Exception;

    public AppResponse fetchOrgById(OrganizationRequest payload) throws Exception;

    public AppResponse fetchAllOrg(OrganizationRequest payload) throws Exception;

    public AppResponse deleteOrgById(OrganizationRequest payload) throws Exception;

    public AppResponse deleteAllOrg(OrganizationRequest payload) throws Exception;

}

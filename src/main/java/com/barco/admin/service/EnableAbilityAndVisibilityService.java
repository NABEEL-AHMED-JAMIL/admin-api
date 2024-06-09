package com.barco.admin.service;

import com.barco.model.dto.request.EnableAbilityRequest;
import com.barco.model.dto.request.VisibilityRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface EnableAbilityAndVisibilityService extends RootService {


    public AppResponse addEnableAbility(EnableAbilityRequest payload) throws Exception;

    public AppResponse editEnableAbility(EnableAbilityRequest payload) throws Exception;

    public AppResponse fetchAllEnableAbility(EnableAbilityRequest payload) throws Exception;

    public AppResponse fetchEnableAbilityById(EnableAbilityRequest payload) throws Exception;

    public AppResponse deleteEnableAbilityById(EnableAbilityRequest payload) throws Exception;

    public AppResponse deleteAllEnableAbility(EnableAbilityRequest payload) throws Exception;

    public AppResponse addVisibility(VisibilityRequest payload) throws Exception;

    public AppResponse editVisibility(VisibilityRequest payload) throws Exception;

    public AppResponse fetchAllVisibility(VisibilityRequest payload) throws Exception;

    public AppResponse fetchVisibilityById(VisibilityRequest payload) throws Exception;

    public AppResponse deleteVisibilityById(VisibilityRequest payload) throws Exception;

    public AppResponse deleteAllVisibility(VisibilityRequest payload) throws Exception;

}

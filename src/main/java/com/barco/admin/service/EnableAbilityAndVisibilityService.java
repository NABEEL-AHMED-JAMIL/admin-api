package com.barco.admin.service;

import com.barco.model.dto.request.EnableAndVisibilityConfigRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface EnableAbilityAndVisibilityService extends RootService {


    public AppResponse addEnableAndVisibilityConfig(EnableAndVisibilityConfigRequest payload) throws Exception;

    public AppResponse editEnableAndVisibilityConfig(EnableAndVisibilityConfigRequest payload) throws Exception;

    public AppResponse fetchAllEnableAndVisibilityConfig(EnableAndVisibilityConfigRequest payload) throws Exception;

    public AppResponse fetchEnableAndVisibilityConfigById(EnableAndVisibilityConfigRequest payload) throws Exception;

    public AppResponse deleteEnableAndVisibilityConfigById(EnableAndVisibilityConfigRequest payload) throws Exception;

    public AppResponse deleteAllEnableAndVisibilityConfig(EnableAndVisibilityConfigRequest payload) throws Exception;

}

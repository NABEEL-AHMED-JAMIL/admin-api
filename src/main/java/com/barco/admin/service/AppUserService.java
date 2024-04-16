package com.barco.admin.service;

import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface AppUserService extends RootService {

    public AppResponse fetchAppUserProfile(String username) throws Exception;

    public AppResponse updateAppUserProfile(UpdateUserProfileRequest payload) throws Exception;

    public AppResponse updateAppUserCompany(CompanyRequest payload) throws Exception;

    public AppResponse updateAppUserEnvVariable(EnVariablesRequest payload) throws Exception;

    public AppResponse updateAppUserPassword(UpdateUserProfileRequest payload) throws Exception;

    public AppResponse closeAppUserAccount(AppUserRequest payload) throws Exception;

    public AppResponse deleteAllAppUserAccount(AppUserRequest payload) throws Exception;

    public AppResponse fetchAllAppUserAccount(AppUserRequest payload) throws Exception;

    public AppResponse addAppUserAccount(AppUserRequest payload) throws Exception;

    public AppResponse editAppUserAccount(AppUserRequest payload) throws Exception;

    public AppResponse enabledDisabledAppUserAccount(AppUserRequest payload) throws Exception;

}

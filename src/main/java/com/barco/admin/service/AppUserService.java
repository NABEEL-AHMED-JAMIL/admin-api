package com.barco.admin.service;

import com.barco.model.dto.request.AppUserRequest;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.response.AppResponse;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface AppUserService extends RootService {

    public AppResponse fetchAppUserProfile(String username) throws Exception;

    public AppResponse updateAppUserProfile(AppUserRequest payload) throws Exception;

    public AppResponse updateAppUserPassword(AppUserRequest payload) throws Exception;

    public AppResponse updateAppUserCompany(AppUserRequest payload) throws Exception;

    public AppResponse closeAppUserAccount(AppUserRequest payload) throws Exception;

    public AppResponse fetchAllAppUserAccount(AppUserRequest payload) throws Exception;

    public AppResponse addAppUserAccount(AppUserRequest payload) throws Exception;

    public AppResponse editAppUserAccount(AppUserRequest payload) throws Exception;

}

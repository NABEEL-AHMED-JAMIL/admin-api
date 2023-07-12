package com.barco.admin.service;

import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface AppUserService {

    public AppResponse getAppUserProfile(String username) throws Exception;

    public AppResponse updateAppUserProfile(UpdateUserProfileRequest requestPayload) throws Exception;

    public AppResponse updateAppUserPassword(UpdateUserProfileRequest requestPayload) throws Exception;

    public AppResponse closeAppUserAccount(UpdateUserProfileRequest requestPayload) throws Exception;

    public AppResponse getSubAppUserAccount(String username) throws Exception;

    public AppResponse signInAppUser(LoginRequest requestPayload) throws Exception;

    public AppResponse signupAppUser(SignupRequest requestPayload) throws Exception;

    public AppResponse forgotPassword(ForgotPasswordRequest requestPayload) throws Exception;

    public AppResponse resetPassword(PasswordResetRequest requestPayload) throws Exception;

    public AppResponse authClamByRefreshToken(TokenRefreshRequest requestPayload)  throws Exception;

    public AppResponse logoutAppUser(TokenRefreshRequest requestPayload)  throws Exception;

}

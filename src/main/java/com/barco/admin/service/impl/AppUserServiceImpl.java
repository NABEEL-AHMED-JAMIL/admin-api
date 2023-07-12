package com.barco.admin.service.impl;

import com.barco.admin.service.AppUserService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.emailer.EmailMessageRequest;
import com.barco.common.emailer.EmailMessagesFactory;
import com.barco.common.security.JwtUtils;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.*;
import com.barco.model.util.lookuputil.APPLICATION_STATUS;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.RefreshToken;
import com.barco.model.pojo.Role;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.RoleRepository;
import com.barco.model.repository.TemplateRegRepository;
import com.barco.model.service.RefreshTokenService;
import com.barco.model.service.UserDetailsImpl;
import com.barco.model.util.ProcessUtil;
import com.barco.model.util.lookuputil.EMAIL_TEMPLATE;
import com.barco.model.util.lookuputil.GLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private Logger logger = LoggerFactory.getLogger(AppUserServiceImpl.class);

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private TemplateRegRepository templateRegRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailMessagesFactory emailMessagesFactory;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    /**
     * Method use to get appUser detail
     * @param username
     * @return AppResponse
     * */
    @Override
    public AppResponse getAppUserProfile(String username) throws Exception {
        logger.info("Request getAppUserProfile :- " + username);
        if (BarcoUtil.isNull(username)) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            username, APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        AppUser appUserDetail = appUser.get();
        AppUserResponse appUserResponse = this.getAppUserDetail(appUserDetail);
        if (!appUserDetail.getAppUserRoles().isEmpty()) {
            appUserResponse.setRoleResponse(appUserDetail.getAppUserRoles()
                .stream().map(role -> {
                    RoleResponse roleResponse = new RoleResponse();
                    roleResponse.setRoleId(role.getRoleId());
                    roleResponse.setRoleName(role.getRoleName());
                    roleResponse.setStatus(GLookup.getGLookup(
                        this.lookupDataCacheService.getChildLookupById(
                        APPLICATION_STATUS.getName(), role.getStatus())));
                    roleResponse.setDateCreated(role.getDateCreated());
                    return roleResponse;
            }).collect(Collectors.toSet()));
        }
        if (!BarcoUtil.isNull(appUserDetail.getParentAppUser())) {
            appUserResponse.setParentAppUser(this.getAppUserDetail(appUserDetail.getParentAppUser()));
        }
        return new AppResponse(ProcessUtil.SUCCESS, "User detail.", appUserResponse);
    }


    /**
     * Method use to update the user detail
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateAppUserProfile(UpdateUserProfileRequest requestPayload) throws Exception {
        logger.info("Request updateAppUserProfile :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        } else if (BarcoUtil.isNull(requestPayload.getFirstName())) {
            return new AppResponse(ProcessUtil.ERROR, "FirstName missing.");
        } else if (BarcoUtil.isNull(requestPayload.getLastName())) {
            return new AppResponse(ProcessUtil.ERROR, "LastName missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findFirstByUsernameAndStatusNot(
            requestPayload.getUsername(), APPLICATION_STATUS.DELETE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        appUser.get().setFirstName(requestPayload.getFirstName());
        appUser.get().setLastName(requestPayload.getLastName());
        appUser.get().setStatus(requestPayload.getStatus());
        appUser.get().getAppUserChildren()
            .stream().filter(appUser1 -> {
                return appUser1.getStatus() != APPLICATION_STATUS.DELETE.getLookupCode();
            }).map(appUser1 -> {
                appUser1.setStatus(requestPayload.getStatus());
                return appUser1;
            }).collect(Collectors.toSet());
        this.appUserRepository.save(appUser.get());
        if (this.sendUpdateAppUserProfile(requestPayload)) {
            return new AppResponse(ProcessUtil.SUCCESS, "AppUser Profile Update.", requestPayload);
        }
        return new AppResponse(ProcessUtil.ERROR, "Account updated, Email not send contact with support.", requestPayload);
    }

    /**
     * Method use to update the app user password
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateAppUserPassword(UpdateUserProfileRequest requestPayload) throws Exception {
        logger.info("Request updateAppUserPassword :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        } else if (BarcoUtil.isNull(requestPayload.getOldPassword())) {
            return new AppResponse(ProcessUtil.ERROR, "OldPassword missing.");
        } else if (BarcoUtil.isNull(requestPayload.getNewPassword())) {
            return new AppResponse(ProcessUtil.ERROR, "NewPassword missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            requestPayload.getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        if (!this.passwordEncoder.matches(requestPayload.getOldPassword(), appUser.get().getPassword())) {
            return new AppResponse(ProcessUtil.ERROR, "Old password not match.");
        }
        appUser.get().setPassword(this.passwordEncoder.encode(requestPayload.getNewPassword()));
        this.appUserRepository.save(appUser.get());
        if (this.sendUpdateAppUserPassword(requestPayload)) {
            return new AppResponse(ProcessUtil.SUCCESS, "AppUser Profile Update.", requestPayload);
        }
        return new AppResponse(ProcessUtil.ERROR, "Account updated, Email not send contact with support.", requestPayload);
    }

    /**
     * Method use to close account
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse closeAppUserAccount(UpdateUserProfileRequest requestPayload) throws Exception {
        logger.info("Request closeAppUserAccount :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            requestPayload.getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        appUser.get().setStatus(APPLICATION_STATUS.DELETE.getLookupCode());
        // if its have any sub-user then delete as well
        appUser.get().getAppUserChildren()
        .stream().filter(appUser1 -> appUser1.getStatus() != APPLICATION_STATUS.DELETE.getLookupCode() )
        .map(appUser1 -> {
            appUser1.setStatus(APPLICATION_STATUS.DELETE.getLookupCode());
            return appUser1;
        }).collect(Collectors.toSet());
        /**
         * Will update rest of the code -> important
         * stop all running job and close all source task
         * */
        this.appUserRepository.save(appUser.get());
        requestPayload.setEmail(appUser.get().getEmail());
        if (this.sendCloseAppUserAccount(requestPayload)) {
            return new AppResponse(ProcessUtil.SUCCESS, "AppUser Close.", requestPayload);
        }
        return new AppResponse(ProcessUtil.ERROR, "Account Close.", requestPayload);
    }

    /**
     * Method use to get sub appUser account
     * @param username
     * @return AppResponse
     * */
    @Override
    public AppResponse getSubAppUserAccount(String username) throws Exception {
        logger.info("Request getSubAppUserAccount :- " + username);
        if (BarcoUtil.isNull(username)) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            username, APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        AppUserResponse appUserResponse = this.getAppUserDetail(appUser.get());
        appUserResponse.setSubAppUser(appUser.get().getAppUserChildren().stream()
            .filter(appUser1 -> appUser1.getStatus() != APPLICATION_STATUS.DELETE.getLookupCode() )
            .map(appUser1 -> this.getAppUserDetail(appUser1)).collect(Collectors.toList()));
        return new AppResponse(ProcessUtil.SUCCESS, "AppUser Sub Account.", appUserResponse);
    }

    /**
     * Method use for signIn appUser
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse signInAppUser(LoginRequest requestPayload) throws Exception {
        // spring auth manager will call user detail service
        Authentication authentication = this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(requestPayload.getUsername(), requestPayload.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // get the user detail from authentication
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // token generate
        String jwtToken = this.jwtUtils.generateTokenFromUsername(userDetails.getUsername());
        // refresh token generate
        RefreshToken refreshToken = this.refreshTokenService.createRefreshToken(
            userDetails.getAppUserId(), requestPayload.getIpAddress());
        return new AppResponse(ProcessUtil.SUCCESS, "User successfully authenticate.",
            new AuthResponse(userDetails.getAppUserId(), jwtToken, refreshToken.getToken(),
                refreshToken.getIpAddress(), userDetails.getUsername(), userDetails.getEmail(),
                userDetails.getProfileImage(), userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList())));
    }

    /**
     * Method use for signUp appUser as admin
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse signupAppUser(SignupRequest requestPayload) throws Exception {
        logger.info("Request signupAppUser :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        } else if (BarcoUtil.isNull(requestPayload.getEmail())) {
            return new AppResponse(ProcessUtil.ERROR, "Email missing.");
        } else if (BarcoUtil.isNull(requestPayload.getPassword())) {
            return new AppResponse(ProcessUtil.ERROR, "Password missing.");
        } else if (this.appUserRepository.existsByUsername(requestPayload.getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username is already taken.");
        } else if (this.appUserRepository.existsByEmail(requestPayload.getEmail())) {
            return new AppResponse(ProcessUtil.ERROR, "Email is already in exist.");
        } else if (BarcoUtil.isNull(requestPayload.getRoles())) {
            return new AppResponse(ProcessUtil.ERROR, "Roles missing.");
        }
        // check if the username and email exist or not
        AppUser appUser = new AppUser();
        appUser.setFirstName(requestPayload.getFirstName());
        appUser.setLastName(requestPayload.getLastName());
        appUser.setEmail(requestPayload.getEmail());
        appUser.setUsername(requestPayload.getUsername());
        appUser.setPassword(this.passwordEncoder.encode(requestPayload.getPassword()));
        appUser.setProfileImg(requestPayload.getProfileImg());
        // by default active user no need extra action
        appUser.setStatus(APPLICATION_STATUS.ACTIVE.getLookupCode());
        // set the parent user which is master admin
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            requestPayload.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (adminUser.isPresent()) {
            appUser.setParentAppUser(adminUser.get());
        } else {
            return new AppResponse(ProcessUtil.ERROR, String.format("Admin user not found.",
                requestPayload.getAccessUserDetail().getUsername()));
        }
        // register user role default as admin role
        Set<Role> userRole = this.roleRepository.findAllRoleByRoleIdInAndStatus(
            requestPayload.getRoles(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (userRole.size() > 0) {
            appUser.setAppUserRoles(userRole);
        }
        // saving process
        this.appUserRepository.save(appUser);
        requestPayload.setRole(userRole.stream().map(
            Role::getRoleName).collect(Collectors.joining(",")));
        if (!this.sendRegisterUser(requestPayload)) {
            return new AppResponse(ProcessUtil.ERROR, String.format(
                "User successfully register email send failed contact with support %s.", appUser.getUsername()));
        }
        return new AppResponse(ProcessUtil.SUCCESS, String.format(
            "User successfully register %s.", appUser.getUsername()));
    }

    /**
     * Method use support to forgot password
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse forgotPassword(ForgotPasswordRequest requestPayload) throws Exception {
        logger.info("Request forgotPassword :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getEmail())) {
            return new AppResponse(ProcessUtil.ERROR, "Email missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByEmailAndStatus(
            requestPayload.getEmail(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (appUser.isPresent()) {
            requestPayload.setUsername(appUser.get().getUsername());
            requestPayload.setAppUserId(appUser.get().getAppUserId());
            if (this.sendForgotPassword(requestPayload)) {
                return new AppResponse(ProcessUtil.SUCCESS, "Email send successfully");
            }
            return new AppResponse(ProcessUtil.ERROR,"Email not send contact with support.");
        }
        return new AppResponse(ProcessUtil.ERROR, "Account not exist.");
    }

    /**
     * Method use to reset app user password
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse resetPassword(PasswordResetRequest requestPayload) throws Exception {
        logger.info("Request resetPassword :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getEmail())) {
            return new AppResponse(ProcessUtil.ERROR, "Email missing.");
        } else if (BarcoUtil.isNull(requestPayload.getNewPassword())) {
            return new AppResponse(ProcessUtil.ERROR, "New password missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByEmailAndStatus(
            requestPayload.getEmail(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (appUser.isPresent()) {
            appUser.get().setPassword(this.passwordEncoder.encode(requestPayload.getNewPassword()));
            this.appUserRepository.save(appUser.get());
            if (this.sendResetPassword(requestPayload)) {
                return new AppResponse(ProcessUtil.SUCCESS, "Email send successfully.");
            }
            return new AppResponse(ProcessUtil.ERROR,"Email not send contact with support.");
        }
        return new AppResponse(ProcessUtil.ERROR, "Account not exist.");
    }

    /**
     * Method generate new token base on refresh token
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse authClamByRefreshToken(TokenRefreshRequest requestPayload) throws Exception {
        AtomicReference<String> requestRefreshToken = new AtomicReference<>(requestPayload.getRefreshToken());
        return this.refreshTokenService.findByToken(requestRefreshToken.get())
            .map(this.refreshTokenService::verifyExpiration)
            .map(appResponse -> {
                if (appResponse.getStatus().equals(ProcessUtil.SUCCESS)) {
                    RefreshToken refreshToken = (RefreshToken) appResponse.getData();
                    requestRefreshToken.set(this.jwtUtils.generateTokenFromUsername(refreshToken.getAppUser().getUsername()));
                }
                return new AppResponse(appResponse.getStatus(), appResponse.getMessage(), requestRefreshToken);
            }).orElse(new AppResponse(ProcessUtil.ERROR, "Token not found", requestRefreshToken));
    }

    /**
     * Method use to delete the token to log Out the session
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse logoutAppUser(TokenRefreshRequest requestPayload) throws Exception {
        return this.refreshTokenService.deleteRefreshToken(requestPayload);
    }

    private AppUserResponse getAppUserDetail(AppUser appUser) {
        AppUserResponse appUserResponse = new AppUserResponse();
        appUserResponse.setAppUserId(appUser.getAppUserId());
        appUserResponse.setFirstName(appUser.getFirstName());
        appUserResponse.setLastName(appUser.getLastName());
        appUserResponse.setUsername(appUser.getUsername());
        appUserResponse.setProfile(appUser.getProfileImg());
        appUserResponse.setEmail(appUser.getEmail());
        appUserResponse.setStatus(GLookup.getGLookup(
            this.lookupDataCacheService.getChildLookupById(
                APPLICATION_STATUS.getName(), appUser.getStatus())));
        appUserResponse.setDateCreated(appUser.getDateCreated());
        appUserResponse.setRoleResponse(appUser.getAppUserRoles().stream()
            .map(role -> getRoleResponse(role)).collect(Collectors.toSet()));
        return appUserResponse;
    }

    private RoleResponse getRoleResponse(Role role) {
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setRoleId(role.getRoleId());
        roleResponse.setRoleName(role.getRoleName());
        roleResponse.setStatus(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupById(
            APPLICATION_STATUS.getName(), role.getStatus())));
        return roleResponse;
    }

    /**
     * sendRegisterUser method use on user register.
     * @param requestPayload
     * */
    private boolean sendRegisterUser(SignupRequest requestPayload) {
        try {
            EmailMessageRequest emailMessageRequest = this.getEmailMessageRequest(
                requestPayload.getEmail(), "User Registered", EMAIL_TEMPLATE.REGISTER_USER.getLookupCode());
            emailMessageRequest.getBodyMap().put("request.username", requestPayload.getUsername());
            emailMessageRequest.getBodyMap().put("request.password", requestPayload.getPassword());
            emailMessageRequest.getBodyMap().put("request.role", requestPayload.getRole());
            logger.info("Email Send Status :- " +
                this.emailMessagesFactory.sendSimpleMail(emailMessageRequest));
            return true;
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
            return false;
        }
    }

    /**
     * sendForgotPassword method use to send forgot password email
     * @param requestPayload
     * */
    private boolean sendForgotPassword(ForgotPasswordRequest requestPayload) {
        try {
            EmailMessageRequest emailMessageRequest = this.getEmailMessageRequest(
                requestPayload.getEmail(), "Forgot Password", EMAIL_TEMPLATE.FORGOT_PASS.getLookupCode());
            LookupDataResponse resetPasswordLink = this.lookupDataCacheService
                .getParentLookupById(ProcessUtil.RESET_PASSWORD_LINK);
            emailMessageRequest.getBodyMap().put("request.username", requestPayload.getUsername());
            emailMessageRequest.getBodyMap().put("request.forgotPasswordPageUrl", resetPasswordLink.getLookupValue()
                +"?token="+ this.jwtUtils.generateTokenFromUsername(requestPayload.toString()));
            logger.info("Email Send Status :- " + this.emailMessagesFactory.sendSimpleMail(emailMessageRequest));
            return true;
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
            return false;
        }
    }

    /**
     * sendResetPassword method use to send reset confirm email
     * @param requestPayload
     * */
    private boolean sendResetPassword(PasswordResetRequest requestPayload) {
        try {
            EmailMessageRequest emailMessageRequest = this.getEmailMessageRequest(
                requestPayload.getEmail(), "Password Updated", EMAIL_TEMPLATE.RESET_PASS.getLookupCode());
            emailMessageRequest.getBodyMap().put("request.username", requestPayload.getUsername());
            logger.info("Email Send Status :- " +
                this.emailMessagesFactory.sendSimpleMail(emailMessageRequest));
            return true;
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
            return false;
        }
    }

    /**
     * sendUpdateAppUserProfile method use to send update profile email
     * @param requestPayload
     * */
    private boolean sendUpdateAppUserProfile(UpdateUserProfileRequest requestPayload) {
        try {
            EmailMessageRequest emailMessageRequest = this.getEmailMessageRequest(
                requestPayload.getEmail(), "Profile Updated", EMAIL_TEMPLATE.UPDATE_ACCOUNT_PROFILE.getLookupCode());
            emailMessageRequest.getBodyMap().put("request.username", requestPayload.getUsername());
            emailMessageRequest.getBodyMap().put("request.firstName", requestPayload.getFirstName());
            emailMessageRequest.getBodyMap().put("request.lastName", requestPayload.getLastName());
            logger.info("Email Send Status :- " + this.emailMessagesFactory.sendSimpleMail(emailMessageRequest));
            return true;
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
            return false;
        }
    }

    /**
     * sendUpdateAppUserPassword method use to send reset password confirm email
     * @param requestPayload
     * */
    private boolean sendUpdateAppUserPassword(UpdateUserProfileRequest requestPayload) {
        try {
            EmailMessageRequest emailMessageRequest = this.getEmailMessageRequest(requestPayload.getEmail(),
                "Password Updated", EMAIL_TEMPLATE.RESET_PASS.getLookupCode());
            emailMessageRequest.getBodyMap().put("request.username", requestPayload.getUsername());
            logger.info("Email Send Status :- " +
                this.emailMessagesFactory.sendSimpleMail(emailMessageRequest));
            return true;
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
            return false;
        }
    }

    /**
     * sendCloseAppUserAccount method use to send close account email
     * @param requestPayload
     * */
    private boolean sendCloseAppUserAccount(UpdateUserProfileRequest requestPayload) {
        try {
            EmailMessageRequest emailMessageRequest = this.getEmailMessageRequest(
                requestPayload.getEmail(), "Account Close", EMAIL_TEMPLATE.CLOSE_ACCOUNT.getLookupCode());
            logger.info("Email Send Status :- " + this.emailMessagesFactory.sendSimpleMail(emailMessageRequest));
            return true;
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
            return false;
        }
    }

    private EmailMessageRequest getEmailMessageRequest(String recipient, String subject, Long templateType) {
        Map<String, Object> metaData = new HashMap<>();
        EmailMessageRequest emailMessageRequest = new EmailMessageRequest();
        emailMessageRequest.setFromEmail(this.lookupDataCacheService.getParentLookupById(ProcessUtil.EMAIL_SENDER).getLookupValue());
        emailMessageRequest.setRecipients(recipient);
        emailMessageRequest.setSubject(subject);
        emailMessageRequest.setBodyPayload(this.templateRegRepository.findByTemplateTypeAndStatus(
            templateType, APPLICATION_STATUS.ACTIVE.getLookupCode())
        .orElseThrow(() -> new RuntimeException("Not Found Template")).getTemplateContent()); // will modify
        emailMessageRequest.setBodyMap(metaData);
        return emailMessageRequest;
    }

}

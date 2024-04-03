package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.RefreshTokenService;
import com.barco.common.emailer.EmailMessagesFactory;
import com.barco.common.security.JwtUtils;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AuthResponse;
import com.barco.model.pojo.*;
import com.barco.model.repository.*;
import com.barco.model.security.UserSessionDetail;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.LookupUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.barco.admin.service.AuthService;
import com.barco.model.dto.response.AppResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author Nabeel Ahmed
 */
@Service
public class AuthServiceImpl implements AuthService {

    private Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private TemplateRegRepository templateRegRepository;
    @Autowired
    private SubAppUserRepository subAppUserRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailMessagesFactory emailMessagesFactory;


    public AuthServiceImpl() {}

    /**
     * Method use for signIn appUser
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse signInAppUser(LoginRequest payload) throws Exception {
        logger.info("Request signInAppUser :- " + payload);
        // spring auth manager will call user detail service
        Authentication authentication = this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(payload.getUsername(), payload.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // get the user detail from authentication
        UserSessionDetail userDetails = (UserSessionDetail) authentication.getPrincipal();
        String jwtToken = this.jwtUtils.generateTokenFromUsername(userDetails.getUsername());
        RefreshToken refreshToken = this.refreshTokenService.createRefreshToken(userDetails.getId(), payload.getIpAddress());
        AuthResponse authResponse = new AuthResponse(jwtToken, refreshToken.getToken());
        authResponse.setIpAddress(payload.getIpAddress());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.USER_SUCCESSFULLY_AUTHENTICATE, getAuthResponseDetail(authResponse, userDetails));
    }

    /**
     * Method use for signUp appUser as admin
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse signupAppUser(SignupRequest payload) throws Exception {
        logger.info("Request signupAppUser :- " + payload);
        if (BarcoUtil.isNull(payload.getFirstName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FIRST_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getLastName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LAST_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EMAIL_MISSING);
        } else if (BarcoUtil.isNull(payload.getPassword())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PASSWORD_MISSING);
        } else if (this.appUserRepository.existsByUsername(payload.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_ALREADY_TAKEN);
        } else if (this.appUserRepository.existsByEmail(payload.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EMAIL_ALREADY_IN_USE);
        }
        // check if the username and email exist or not
        AppUser appUser = new AppUser();
        appUser.setFirstName(payload.getFirstName());
        appUser.setLastName(payload.getLastName());
        appUser.setEmail(payload.getEmail());
        appUser.setUsername(payload.getUsername());
        appUser.setImg(payload.getProfileImg());
        appUser.setIpAddress(payload.getIpAddress());
        appUser.setStatus(APPLICATION_STATUS.ACTIVE);
        appUser.setPassword(this.passwordEncoder.encode(payload.getPassword()));
        // set the parent user which is master admin
        Optional<AppUser> superAdmin = this.appUserRepository.findByUsernameAndStatus(
            this.lookupDataCacheService.getParentLookupDataByParentLookupType(
                LookupUtil.ROOT_USER).getLookupValue(), APPLICATION_STATUS.ACTIVE);
        if (superAdmin.isPresent()) {
            appUser.setCreatedBy(superAdmin.get());
            appUser.setUpdatedBy(superAdmin.get());
        }
        // register user role default as admin role
        Optional<Role> adminRole = this.roleRepository.findByNameAndStatus(
            this.lookupDataCacheService.getParentLookupDataByParentLookupType(
                LookupUtil.DEFAULT_ROLE).getLookupValue(), APPLICATION_STATUS.ACTIVE);
        if (adminRole.isPresent()) {
            appUser.setAppUserRoles(Set.of(adminRole.get()));
        }
        Optional<Profile> adminProfile = this.profileRepository.findProfileByProfileName(
            this.lookupDataCacheService.getParentLookupDataByParentLookupType(
                LookupUtil.DEFAULT_PROFILE).getLookupValue());
        if (adminProfile.isPresent()) {
            appUser.setProfile(adminProfile.get());
        }
        this.appUserRepository.save(appUser);
        this.sendRegisterUser(appUser, this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
        SubAppUser subAppUser = new SubAppUser();
        subAppUser.setAppUserParent(superAdmin.get());
        subAppUser.setAppUserChild(appUser);
        subAppUser.setCreatedBy(superAdmin.get());
        subAppUser.setUpdatedBy(superAdmin.get());
        subAppUser.setStatus(APPLICATION_STATUS.ACTIVE);
        this.subAppUserRepository.save(subAppUser);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(
            MessageUtil.USER_SUCCESSFULLY_REGISTER, appUser.getUsername()), payload);
    }

    /**
     * Method use to send email the forgot password
     * @param payload
     * @return AuthResponse
     * */
    @Override
    public AppResponse forgotPassword(ForgotPasswordRequest payload) throws Exception {
        logger.info("Request forgotPassword :- " + payload);
        if (BarcoUtil.isNull(payload.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EMAIL_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByEmailAndStatus(
            payload.getEmail(), APPLICATION_STATUS.ACTIVE);
        if (appUser.isPresent()) {
            this.sendForgotPasswordEmail(appUser.get(), this.lookupDataCacheService,
                this.templateRegRepository, this.emailMessagesFactory, this.jwtUtils);
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.EMAIL_SEND_SUCCESSFULLY);
        }
        return new AppResponse(BarcoUtil.ERROR, MessageUtil.ACCOUNT_NOT_EXIST);
    }


    /**
     * Method use to reset app user password
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse resetPassword(PasswordResetRequest payload) throws Exception {
        logger.info("Request resetPassword :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EMAIL_MISSING);
        } else if (BarcoUtil.isNull(payload.getNewPassword())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PASSWORD_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByEmailAndStatus(
            payload.getSessionUser().getEmail(), APPLICATION_STATUS.ACTIVE);
        if (appUser.isPresent()) {
            appUser.get().setPassword(this.passwordEncoder.encode(payload.getNewPassword()));
            this.appUserRepository.save(appUser.get());
            this.sendResetPasswordEmail(appUser.get(), this.lookupDataCacheService,
                this.templateRegRepository, this.emailMessagesFactory);
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.EMAIL_SEND_SUCCESSFULLY);
        }
        return new AppResponse(BarcoUtil.ERROR, MessageUtil.ACCOUNT_NOT_EXIST);
    }

    /**
     * Method generate new token base on refresh token
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse authClamByRefreshToken(TokenRefreshRequest payload) throws Exception {
        AtomicReference<String> requestRefreshToken = new AtomicReference<>(payload.getRefreshToken());
        return this.refreshTokenService.findByToken(requestRefreshToken.get())
            .map(this.refreshTokenService::verifyExpiration)
            .map(appResponse -> {
                if (appResponse.getStatus().equals(BarcoUtil.SUCCESS)) {
                    RefreshToken refreshToken = (RefreshToken) appResponse.getData();
                    requestRefreshToken.set(this.jwtUtils.generateTokenFromUsername(refreshToken.getCreatedBy().getUsername()));
                }
                return new AppResponse(appResponse.getStatus(), appResponse.getMessage(), requestRefreshToken);
            }).orElse(new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.DATA_NOT_FOUND, MessageUtil.REFRESH_TOKEN), requestRefreshToken));
    }

    /**
     * Method use to delete the token to log Out the session
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse logoutAppUser(TokenRefreshRequest payload) throws Exception {
        return this.refreshTokenService.deleteRefreshToken(payload);
    }

}

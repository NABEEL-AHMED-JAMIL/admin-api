package com.barco.admin.service.impl;

import com.barco.admin.service.*;
import com.barco.common.emailer.EmailMessagesFactory;
import com.barco.common.security.JwtUtils;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AuthResponse;
import com.barco.model.pojo.*;
import com.barco.model.repository.*;
import com.barco.model.security.UserSessionDetail;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.ACCOUNT_TYPE;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.GLookup;
import com.barco.model.util.lookup.LookupUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private EnvVariablesRepository envVariablesRepository;
    @Autowired
    private EventBridgeRepository eventBridgeRepository;
    @Autowired
    private AppUserEnvRepository appUserEnvRepository;
    @Autowired
    private SubAppUserRepository subAppUserRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailMessagesFactory emailMessagesFactory;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EventBridgeService eventBridgeService;


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
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.USER_SUCCESSFULLY_AUTHENTICATE,
            this.getAuthResponseDetail(authResponse, userDetails));
    }

    /**
     * Method use for signUp appUser as user-customer
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
        /**
         * ALL USER REGISTER FROM THE MAIN REGISTER PAGE THEY ARE NORMAL USER
         * AND THEY WILL GET THE NORMAL ACCOUNT TYPE
         * AND THEY WILL GET THE USER DEFAULT PROFILE
         * AND THEY WILL GET THE USER DEFAULT USER ROLE
         * **/
        // register user will get the default role USER
        Optional<Role> userRole = this.roleRepository.findByNameAndStatus(
            this.lookupDataCacheService.getParentLookupDataByParentLookupType(
                LookupUtil.DEFAULT_ROLE).getLookupValue(), APPLICATION_STATUS.ACTIVE);
        if (userRole.isPresent()) {
            appUser.setAppUserRoles(Set.of(userRole.get()));
        }
        // register user will get the default profile USER
        Optional<Profile> userProfile = this.profileRepository.findProfileByProfileName(
            this.lookupDataCacheService.getParentLookupDataByParentLookupType(
               LookupUtil.DEFAULT_PROFILE).getLookupValue());
        if (userProfile.isPresent()) {
            appUser.setProfile(userProfile.get());
        }
        // register user account type as 'Customer'
        appUser.setAccountType(ACCOUNT_TYPE.NORMAL);
        this.appUserRepository.save(appUser);
        // notification & register email
        Optional<AppUser> superAdmin = this.appUserRepository.findByUsernameAndStatus(this.lookupDataCacheService
            .getParentLookupDataByParentLookupType(LookupUtil.ROOT_USER).getLookupValue(), APPLICATION_STATUS.ACTIVE);
        if (superAdmin.isPresent()) {
            appUser.setCreatedBy(superAdmin.get());
            appUser.setUpdatedBy(superAdmin.get());
        }
        this.sendRegisterUserEmail(appUser, this.lookupDataCacheService,
             this.templateRegRepository, this.emailMessagesFactory);
        this.sendNotification(superAdmin.get().getUsername(), MessageUtil.REQUESTED_FOR_NEW_ACCOUNT,
            String.format(MessageUtil.NEW_USER_REGISTER_WITH_ID, appUser.getId()),
            superAdmin.get(), this.lookupDataCacheService, this.notificationService);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.USER_SUCCESSFULLY_REGISTER,
             appUser.getUsername()), payload);
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
        Optional<AppUser> appUser = this.appUserRepository.findByEmailAndStatus(payload.getEmail(), APPLICATION_STATUS.ACTIVE);
        if (appUser.isPresent()) {
            // email and notification
            this.sendForgotPasswordEmail(appUser.get(), this.lookupDataCacheService,
                this.templateRegRepository, this.emailMessagesFactory, this.jwtUtils);
            this.sendNotification(appUser.get().getUsername(), MessageUtil.FORGOT_PASSWORD,
                MessageUtil.FORGOT_EMAIL_SEND_TO_YOUR_EMAIL, appUser.get(),
                this.lookupDataCacheService, this.notificationService);
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
            this.sendNotification(appUser.get().getUsername(), MessageUtil.RESET_PASSWORD,
                MessageUtil.RESET_EMAIL_SEND_TO_YOUR_EMAIL, appUser.get(),
                this.lookupDataCacheService, this.notificationService);
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
        logger.info("Request authClamByRefreshToken :- " + payload);
        Optional<RefreshToken> refreshToken = this.refreshTokenService.findByToken(payload.getRefreshToken());
        if (!refreshToken.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.DATA_NOT_FOUND, MessageUtil.REFRESH_TOKEN), payload);
        }
        AppResponse appResponse = this.refreshTokenService.verifyExpiration(refreshToken.get());
        if (appResponse.getStatus().equals(BarcoUtil.SUCCESS)) {
            payload.setRefreshToken(this.jwtUtils.generateTokenFromUsername(refreshToken.get().getCreatedBy().getUsername()));
        }
        return new AppResponse(appResponse.getStatus(), appResponse.getMessage(), payload);
    }

    /**
     * Method use to delete the token to log Out the session
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse logoutAppUser(TokenRefreshRequest payload) throws Exception {
        logger.info("Request logoutAppUser :- " + payload);
        return this.refreshTokenService.deleteRefreshToken(payload);
    }

}

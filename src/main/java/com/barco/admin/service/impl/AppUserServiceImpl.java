package com.barco.admin.service.impl;

import com.barco.admin.service.AppUserService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.NotificationService;
import com.barco.common.emailer.EmailMessagesFactory;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.AppUserRequest;
import com.barco.model.dto.request.CompanyRequest;
import com.barco.model.dto.request.EnVariablesRequest;
import com.barco.model.dto.request.UpdateUserProfileRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.AppUserResponse;
import com.barco.model.dto.response.CompanyResponse;
import com.barco.model.dto.response.EnVariablesResponse;
import com.barco.model.pojo.*;
import com.barco.model.repository.*;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class AppUserServiceImpl implements AppUserService {

    private Logger logger = LoggerFactory.getLogger(AppUserServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private AppUserEnvRepository appUserEnvRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TemplateRegRepository templateRegRepository;
    @Autowired
    private SubAppUserRepository subAppUserRepository;
    @Autowired
    private EmailMessagesFactory emailMessagesFactory;

    /**
     * Method use to get appUser detail
     * @param username
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAppUserProfile(String username) throws Exception {
        logger.info("Request fetchAppUserProfile :- " + username);
        if (BarcoUtil.isNull(username)) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(username, APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        AppUserResponse appUserResponse = this.getAppUserDetail(appUser.get());
        if (!BarcoUtil.isNull(appUser.get().getCompany())) {
            Company company = appUser.get().getCompany();
            CompanyResponse companyResponse = new CompanyResponse(company.getId(), company.getName(),
                company.getAddress(), company.getEmail(), company.getPhone());
            appUserResponse.setCompany(companyResponse);
        }
        if (!BarcoUtil.isNull(appUser.get().getGroupUsers())) {
            appUserResponse.setGroups(appUser.get().getGroupUsers().stream()
                .filter(groupUser -> groupUser.getStatus().equals(APPLICATION_STATUS.ACTIVE))
                .map(GroupUser::getGroups).map(groups -> getGroupResponse(groups))
                .collect(Collectors.toList()));
        }
        if (!BarcoUtil.isNull(appUser.get().getAppUserEnvs())) {
            appUserResponse.setEnVariables(appUser.get().getAppUserEnvs().stream()
                .filter(appUserEnv -> appUserEnv.getStatus().equals(APPLICATION_STATUS.ACTIVE))
                .map(appUserEnv -> {
                    EnVariablesResponse enVariables = new EnVariablesResponse();
                    enVariables.setId(appUserEnv.getId());
                    enVariables.setEnvKey(appUserEnv.getEnvVariables().getEnvKey());
                    enVariables.setEnvValue(appUserEnv.getEnvValue());
                    enVariables.setDescription(appUserEnv.getEnvVariables().getDescription());
                    return enVariables;
            }).collect(Collectors.toList()));
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, appUserResponse);
    }

    /**
     * Method use to update app user profile
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateAppUserProfile(UpdateUserProfileRequest payload) throws Exception {
        logger.info("Request updateAppUserProfile :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        appUser.get().setFirstName(payload.getFirstName());
        appUser.get().setLastName(payload.getLastName());
        appUser.get().setIpAddress(payload.getIpAddress());
        this.appUserRepository.save(appUser.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, appUser.get().getUsername()), payload);
    }

    /**
     * Method use to update app user company
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateAppUserCompany(CompanyRequest payload) throws Exception {
        logger.info("Request updateAppUserCompany :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.COMPANY_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.COMPANY_EMAIL_MISSING);
        }
        Company company = null;
        if (!BarcoUtil.isNull(appUser.get().getCompany())) {
            appUser.get().getCompany().setName(payload.getName());
            appUser.get().getCompany().setEmail(payload.getEmail());
            appUser.get().getCompany().setPhone(payload.getPhone());
            appUser.get().getCompany().setAddress(payload.getAddress());
            appUser.get().getCompany().setUpdatedBy(appUser.get());
            appUser.get().getCompany().setStatus(APPLICATION_STATUS.ACTIVE);
        } else {
            company = new Company();
            company.setName(payload.getName());
            company.setEmail(payload.getEmail());
            company.setPhone(payload.getPhone());
            company.setAddress(payload.getAddress());
            company.setCreatedBy(appUser.get());
            company.setUpdatedBy(appUser.get());
            company.setStatus(APPLICATION_STATUS.ACTIVE);
            appUser.get().setCompany(company);
        }
        this.appUserRepository.save(appUser.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, appUser.get().getUsername()), payload);
    }

    /**
     * Method use to update app user env variable
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateAppUserEnvVariable(EnVariablesRequest payload) throws Exception {
        logger.info("Request updateAppUserEnvVariable :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        Optional<AppUserEnv> appUserEnv = this.appUserEnvRepository.findById(payload.getId());
        if (!appUserEnv.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.ENV_NOT_FOUND_WITH_ID, payload.getId().toString()));
        }
        appUserEnv.get().setEnvValue(payload.getEnvValue());
        appUserEnv.get().setUpdatedBy(appUser.get());
        appUserEnv.get().setStatus(APPLICATION_STATUS.ACTIVE);
        this.appUserEnvRepository.save(appUserEnv.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()));
    }

    /**
     * Method use to update app user password
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateAppUserPassword(UpdateUserProfileRequest payload) throws Exception {
        logger.info("Request updateAppUserPassword :- " + payload);
        if (BarcoUtil.isNull(payload.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getOldPassword())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.OLD_PASSWORD_MISSING);
        } else if (BarcoUtil.isNull(payload.getNewPassword())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.NEW_PASSWORD_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (!this.passwordEncoder.matches(payload.getOldPassword(), appUser.get().getPassword())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.OLD_PASSWORD_NOT_MATCH);
        }
        appUser.get().setPassword(this.passwordEncoder.encode(payload.getNewPassword()));
        this.appUserRepository.save(appUser.get());
        this.sendResetPasswordEmail(appUser.get(), this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, appUser.get().getUsername()), payload);
    }

    /**
     * Method use to close app user account
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse closeAppUserAccount(AppUserRequest payload) throws Exception {
        logger.info("Request closeAppUserAccount :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return null;
    }

    /**
     * Method use to delete app user account
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllAppUserAccount(AppUserRequest payload) throws Exception {
        logger.info("Request deleteAllAppUserAccount :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return null;
    }

    /**
     * Method use to fetch all app user account
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllAppUserAccount(AppUserRequest payload) throws Exception {
        logger.info("Request fetchAllAppUserAccount :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        List<AppUserResponse> subAppUserResponses = appUser.get().getSubAppUsers().stream()
            .filter(subAppUser -> !subAppUser.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(subAppUser -> {
                AppUser appUserChild = subAppUser.getAppUserChild();
                AppUserResponse appUserResponse = getAppUserDetail(appUserChild);
                appUserResponse.setTotalSubUser(appUserChild.getSubAppUsers().size());
                appUserResponse.setCreatedBy(getActionUser(appUserChild.getCreatedBy()));
                appUserResponse.setUpdatedBy(getActionUser(appUserChild.getCreatedBy()));
                appUserResponse.setDateCreated(appUserChild.getDateCreated());
                appUserResponse.setDateUpdated(appUserChild.getDateUpdated());
                appUserResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(appUserChild.getStatus().getLookupType()));
                if (!BarcoUtil.isNull(appUserChild.getCompany())) {
                    Company company = appUserChild.getCompany();
                    CompanyResponse companyResponse = new CompanyResponse(company.getId(), company.getName(),
                        company.getAddress(), company.getEmail(), company.getPhone());
                    appUserResponse.setCompany(companyResponse);
                }
                return appUserResponse;
            }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, subAppUserResponses);
    }

    /**
     * Method use to add app user account
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addAppUserAccount(AppUserRequest payload) throws Exception {
        logger.info("Request addAppUserAccount :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
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
        } else if (BarcoUtil.isNull(payload.getIpAddress())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IP_ADDRESS_MISSING);
        } else if (BarcoUtil.isNull(payload.getAssignRole())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ROLE_MISSING);
        } else if (BarcoUtil.isNull(payload.getProfile())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PROFILE_MISSING);
        }
        // check the access for role and profile for user creating
        AppUser appUser = new AppUser();
        appUser.setFirstName(payload.getFirstName());
        appUser.setLastName(payload.getLastName());
        appUser.setEmail(payload.getEmail());
        appUser.setUsername(payload.getUsername());
        appUser.setImg(payload.getProfileImg());
        appUser.setIpAddress(payload.getIpAddress());
        appUser.setStatus(APPLICATION_STATUS.ACTIVE);
        appUser.setPassword(this.passwordEncoder.encode(payload.getPassword()));
        if (adminUser.isPresent()) {
            appUser.setCreatedBy(adminUser.get());
            appUser.setUpdatedBy(adminUser.get());
        }
        // register user role default as admin role
        Set<Role> roleList = this.roleRepository.findAllByNameInAndStatus(payload.getAssignRole(), APPLICATION_STATUS.ACTIVE);
        if (roleList.size() > 0) {
            appUser.setAppUserRoles(roleList);
        }
        Optional<Profile> profile = this.profileRepository.findProfileByIdAndStatus(payload.getProfile(), APPLICATION_STATUS.ACTIVE);
        if (profile.isPresent()) {
            appUser.setProfile(profile.get());
        }
        this.appUserRepository.save(appUser);
        SubAppUser subAppUser = new SubAppUser();
        subAppUser.setAppUserParent(adminUser.get());
        subAppUser.setAppUserChild(appUser);
        subAppUser.setCreatedBy(adminUser.get());
        subAppUser.setUpdatedBy(adminUser.get());
        subAppUser.setStatus(APPLICATION_STATUS.ACTIVE);
        this.subAppUserRepository.save(subAppUser);
        this.sendRegisterUser(appUser, this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
        this.sendNotification(adminUser.get().getUsername(), MessageUtil.NEW_ACCOUNT_ADDED, String.format(MessageUtil.NEW_USER_REGISTER_WITH_ID,
            appUser.getId()), adminUser.get(), this.lookupDataCacheService, this.notificationService);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.USER_SUCCESSFULLY_REGISTER, appUser.getUsername()), payload);
    }

    /**
     * Method use to edit app user account
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse editAppUserAccount(AppUserRequest payload) throws Exception {
        logger.info("Request editAppUserAccount :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getFirstName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FIRST_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getLastName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LAST_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EMAIL_MISSING);
        } else if (BarcoUtil.isNull(payload.getIpAddress())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IP_ADDRESS_MISSING);
        } else if (BarcoUtil.isNull(payload.getAssignRole())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ROLE_MISSING);
        } else if (BarcoUtil.isNull(payload.getProfile())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PROFILE_MISSING);
        }
        // check the access for role and profile for user creating
        Optional<AppUser> appUser = this.appUserRepository.findById(payload.getId());
        if (!BarcoUtil.isNull(payload.getFirstName())) {
            appUser.get().setFirstName(payload.getFirstName());
        }
        if (!BarcoUtil.isNull(payload.getLastName())) {
            appUser.get().setLastName(payload.getLastName());
        }
        if (!BarcoUtil.isNull(payload.getEmail())) {
            appUser.get().setEmail(payload.getEmail());
        }
        if (!payload.getUsername().equals(appUser.get().getUsername()) &&
            this.appUserRepository.existsByUsername(payload.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_ALREADY_TAKEN);
        } else if (!payload.getEmail().equals(appUser.get().getEmail()) &&
            this.appUserRepository.existsByEmail(payload.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EMAIL_ALREADY_IN_USE);
        }
        if (!BarcoUtil.isNull(payload.getUsername())) {
            appUser.get().setUsername(payload.getUsername());
        }
        if (!BarcoUtil.isNull(payload.getIpAddress())) {
            appUser.get().setIpAddress(payload.getIpAddress());
        }
        if (adminUser.isPresent()) {
            appUser.get().setUpdatedBy(adminUser.get());
        }
        // register user role default as admin role
        Set<Role> roleList = this.roleRepository.findAllByNameInAndStatus(payload.getAssignRole(), APPLICATION_STATUS.ACTIVE);
        if (roleList.size() > 0) {
            appUser.get().setAppUserRoles(roleList);
        }
        Optional<Profile> profile = this.profileRepository.findProfileByIdAndStatus(payload.getProfile(), APPLICATION_STATUS.ACTIVE);
        if (profile.isPresent()) {
            appUser.get().setProfile(profile.get());
        }
        this.appUserRepository.save(appUser.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, appUser.get().getUsername()), payload);
    }

    /**
     * Method use to enabled and disabled app user account
     * @param payload
     * @return AppResponse
     * @throws Exception
     * @throws Exception
     * */
    @Override
    public AppResponse enabledDisabledAppUserAccount(AppUserRequest payload) throws Exception {
        logger.info("Request enabledDisabledAppUserAccount :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getStatus())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPLICATION_STATUS_NOT_FOUND);
        }
        // check the access for role and profile for user creating
        Optional<AppUser> appUser = this.appUserRepository.findById(payload.getId());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            // if status is in-active & delete then we have filter the role and show only those role in user detail
            appUser.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            this.enabledDisabledProfilePermissionsAccesses(appUser.get(), adminUser.get());
            this.enabledDisabledAppUserRoleAccesses(appUser.get(), adminUser.get());
            this.enabledDisabledAppUserEnvs(appUser.get(), adminUser.get());
            this.enabledDisabledGroupUsers(appUser.get(), adminUser.get());
            // disabled all other detail same like
            if (!BarcoUtil.isNull(appUser.get().getSubAppUsers()) && appUser.get().getSubAppUsers().size() > 0) {
                appUser.get().getSubAppUsers().stream()
                    .map(subAppUser -> {
                        try {
                            subAppUser.setStatus(appUser.get().getStatus());
                            subAppUser.setUpdatedBy(adminUser.get());
                            subAppUser.getAppUserChild().setStatus(appUser.get().getStatus());
                            subAppUser.getAppUserChild().setUpdatedBy(adminUser.get());
                            this.enabledDisabledProfilePermissionsAccesses(subAppUser.getAppUserChild(), adminUser.get());
                            this.enabledDisabledAppUserRoleAccesses(subAppUser.getAppUserChild(), adminUser.get());
                            this.enabledDisabledAppUserEnvs(subAppUser.getAppUserChild(), adminUser.get());
                            this.enabledDisabledGroupUsers(subAppUser.getAppUserChild(), adminUser.get());
                            this.sendEnabledDisabledRegisterUser(subAppUser.getAppUserChild(), this.lookupDataCacheService,
                                this.templateRegRepository, this.emailMessagesFactory);
                            this.sendNotification(appUser.get().getUsername(), MessageUtil.ACCOUNT_STATUS, (appUser.get().getStatus().equals(APPLICATION_STATUS.ACTIVE) ?
                                MessageUtil.ACCOUNT_ENABLED : MessageUtil.ACCOUNT_DISABLED), appUser.get(), this.lookupDataCacheService, this.notificationService);
                        } catch (Exception ex) {
                            logger.error("Error while send notification to user :- " + ExceptionUtil.getRootCauseMessage(ex));
                        }
                        return subAppUser;
                    }).collect(Collectors.toList());
            }
        }
        this.appUserRepository.save(appUser.get());
        this.sendEnabledDisabledRegisterUser(appUser.get(), this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
        this.sendNotification(appUser.get().getUsername(), MessageUtil.ACCOUNT_STATUS, (appUser.get().getStatus().equals(APPLICATION_STATUS.ACTIVE) ?
            MessageUtil.ACCOUNT_ENABLED : MessageUtil.ACCOUNT_DISABLED), appUser.get(), this.lookupDataCacheService, this.notificationService);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, appUser.get().getUsername()), payload);
    }

}

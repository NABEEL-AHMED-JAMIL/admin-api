package com.barco.admin.service.impl;

import com.barco.admin.service.AppUserService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.emailer.EmailMessagesFactory;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.AppUserRequest;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.AppUserResponse;
import com.barco.model.dto.response.CompanyResponse;
import com.barco.model.pojo.*;
import com.barco.model.repository.*;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.LookupUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
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
    private RoleRepository roleRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
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
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, appUserResponse);
    }

    /**
     * Method use to update app user profile
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateAppUserProfile(AppUserRequest payload) throws Exception {
        logger.info("Request updateAppUserProfile :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return null;
    }

    /**
     * Method use to update app user password
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateAppUserPassword(AppUserRequest payload) throws Exception {
        logger.info("Request updateAppUserPassword :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
                payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return null;
    }

    /**
     * Method use to update app user company
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateAppUserCompany(AppUserRequest payload) throws Exception {
        logger.info("Request updateAppUserCompany :- " + payload);
        return null;
    }

    /**
     * Method use to close app user account
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse closeAppUserAccount(AppUserRequest payload) throws Exception {
        logger.info("Request closeAppUserAccount :- " + payload);
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
                appUserResponse.setCreatedBy(getActionUser(appUserChild.getCreatedBy()));
                appUserResponse.setUpdatedBy(getActionUser(appUserChild.getCreatedBy()));
                appUserResponse.setDateCreated(appUserChild.getDateCreated());
                appUserResponse.setDateUpdated(appUserChild.getDateUpdated());
                appUserResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(appUserChild.getStatus().getLookupType()));
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
        this.sendRegisterUser(appUser, this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
        SubAppUser subAppUser = new SubAppUser();
        subAppUser.setAppUserParent(adminUser.get());
        subAppUser.setAppUserChild(appUser);
        subAppUser.setCreatedBy(adminUser.get());
        subAppUser.setUpdatedBy(adminUser.get());
        subAppUser.setStatus(APPLICATION_STATUS.ACTIVE);
        this.subAppUserRepository.save(subAppUser);
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
        return null;
    }

}

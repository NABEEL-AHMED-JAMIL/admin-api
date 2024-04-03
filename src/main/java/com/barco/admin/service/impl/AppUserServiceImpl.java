package com.barco.admin.service.impl;

import com.barco.admin.service.AppUserService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.AppUserRequest;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.AppUserResponse;
import com.barco.model.dto.response.CompanyResponse;
import com.barco.model.pojo.*;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.RoleRepository;
import com.barco.model.repository.SubAppUserRepository;
import com.barco.model.repository.TemplateRegRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.LookupUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.Set;

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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private TemplateRegRepository templateRegRepository;
    @Autowired
    private SubAppUserRepository subAppUserRepository;

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
        return null;
    }

    /**
     * Method use to add app user account
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addAppUserAccount(AppUserRequest payload) throws Exception {
        logger.info("Request addAppUserAccount :- " + payload);
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
        return null;
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

    /**
     * Method use to view appUser link group account
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse viewAppUserLinkGroupAccount(AppUserRequest payload) throws Exception {
        logger.info("Request viewAppUserLinkGroupAccount :- " + payload);
        return null;
    }

    /**
     * Method use to link and unlink appUser with group
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkOrUnlinkAppUserWithGroup(AppUserRequest payload) throws Exception {
        logger.info("Request linkOrUnlinkAppUserWithGroup :- " + payload);
        return null;
    }

    /**
     * Method use to download app user template
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadAppUserTemplateFile() throws Exception {
        logger.info("Request downloadAppUserTemplateFile ");
        return null;
    }

    /**
     * Method use to download app users
     * @param payload
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadAppUsers(AppUserRequest payload) throws Exception {
        logger.info("Request downloadAppUsers :- " + payload);
        return null;
    }

    /**
     * Method use to upload app users
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse uploadAppUsers(FileUploadRequest payload) throws Exception {
        logger.info("Request uploadAppUsers :- " + payload);
        return null;
    }
}

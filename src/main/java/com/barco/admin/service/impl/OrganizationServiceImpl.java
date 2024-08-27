package com.barco.admin.service.impl;

import com.barco.admin.service.EventBridgeService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.NotificationService;
import com.barco.admin.service.OrganizationService;
import com.barco.common.emailer.EmailMessagesFactory;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.AppUserRequest;
import com.barco.model.dto.request.LinkEBURequest;
import com.barco.model.dto.request.OrganizationRequest;
import com.barco.model.dto.request.SessionUser;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.pojo.*;
import com.barco.model.repository.*;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.ACCOUNT_TYPE;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.EVENT_BRIDGE_TYPE;
import com.barco.model.util.lookup.LookupUtil;
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
public class OrganizationServiceImpl implements OrganizationService {

    private Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private ETLCountryRepository etlCountryRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private AppUserEnvRepository appUserEnvRepository;
    @Autowired
    private EnvVariablesRepository envVariablesRepository;
    @Autowired
    private EventBridgeRepository eventBridgeRepository;
    @Autowired
    private TemplateRegRepository templateRegRepository;
    @Autowired
    private EventBridgeService eventBridgeService;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EmailMessagesFactory emailMessagesFactory;

    public OrganizationServiceImpl() {}

    /**
     * Method use to create the org account
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse addOrgAccount(OrganizationRequest payload) throws Exception {
        logger.info("Request addOrgAccount :- {}.", payload);
        AppResponse validationResponse = this.validateAddOrgAccountPayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        Optional<ETLCountry> etlCountry = this.etlCountryRepository.findByCountryCode(payload.getCountryCode());
        if (etlCountry.isEmpty()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_COUNTRY_CODE_NOT_FOUND);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        Organization organization = this.createOrganization(payload);
        this.organizationRepository.save(organization);
        // check the access for role and profile for user creating
        AppUserRequest orgAppUser = payload.getUser();
        AppUser appUser = new AppUser();
        appUser.setFirstName(orgAppUser.getFirstName());
        appUser.setLastName(orgAppUser.getLastName());
        appUser.setEmail(orgAppUser.getEmail());
        appUser.setUsername(orgAppUser.getUsername());
        appUser.setImg(orgAppUser.getProfileImg());
        appUser.setIpAddress(orgAppUser.getIpAddress());
        appUser.setPassword(this.passwordEncoder.encode(orgAppUser.getPassword()));
        appUser.setStatus(APPLICATION_STATUS.ACTIVE);
        appUser.setOrganization(organization);
        // account type
        if (!BarcoUtil.isNull(orgAppUser.getAccountType())) {
            appUser.setAccountType(ACCOUNT_TYPE.getByLookupCode(orgAppUser.getAccountType()));
        }
        // register user role default as admin role
        Set<Role> roleList = this.roleRepository.findAllByNameInAndStatus(orgAppUser.getAssignRole(), APPLICATION_STATUS.ACTIVE);
        if (!roleList.isEmpty()) {
            appUser.setAppUserRoles(roleList);
        }
        // profile
        Optional<Profile> profile = this.profileRepository.findProfileByIdAndStatus(orgAppUser.getProfile(), APPLICATION_STATUS.ACTIVE);
        profile.ifPresent(appUser::setProfile);
        appUser.setCreatedBy(adminUser.get());
        appUser.setUpdatedBy(adminUser.get());
        this.appUserRepository.save(appUser);
        // notification & register email
        Optional<AppUser> superAdmin = this.appUserRepository.findByUsernameAndStatus(
            this.lookupDataCacheService.getParentLookupDataByParentLookupType(LookupUtil.ROOT_USER).getLookupValue(), APPLICATION_STATUS.ACTIVE);
        if (superAdmin.isPresent()) {
            // e-variable for account
            this.envVariablesRepository.findAllByCreatedByAndStatusNotOrderByDateCreatedDesc(superAdmin.get(), APPLICATION_STATUS.DELETE)
            .forEach(envVariables -> {
                this.appUserEnvRepository.save(this.getAppUserEnv(adminUser.get(), appUser, envVariables));
            });
            // event bridge only receiver event bridge if exist and create by the main user
            this.eventBridgeRepository.findAllByBridgeTypeInAndCreatedByAndStatusNotOrderByDateCreatedDesc(List.of(EVENT_BRIDGE_TYPE.WEB_HOOK_RECEIVE),
                superAdmin.get(), APPLICATION_STATUS.DELETE)
                .forEach(eventBridge -> {
                    try {
                        LinkEBURequest linkEBURequest = new LinkEBURequest();
                        linkEBURequest.setId(eventBridge.getId());
                        linkEBURequest.setAppUserId(appUser.getId());
                        linkEBURequest.setLinked(Boolean.TRUE);
                        linkEBURequest.setSessionUser(new SessionUser(superAdmin.get().getUsername()));
                        this.eventBridgeService.linkEventBridgeWithUser(linkEBURequest);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
        }
        // email send to the admin
        this.sendNotification(MessageUtil.NEW_ORG_ACCOUNT_ADDED, String.format(MessageUtil.NEW_ORG_USER_REGISTER_WITH_ID,
            appUser.getId()), adminUser.get(), this.lookupDataCacheService, this.notificationService);
        // email send to the user
        this.sendRegisterUserEmail(appUser, this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, organization.getId()), payload);
    }

    /**
     * Method use to update the org account
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse updateOrgAccount(OrganizationRequest payload) throws Exception {
        logger.info("Request updateOrgAccount :- {}.", payload);
        AppResponse validationResponse = this.validateUpdateOrgAccountPayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (!BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        Optional<Organization> organizationOpt = this.organizationRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE);
        if (organizationOpt.isEmpty()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_NOT_FOUND);
        }
        this.organizationRepository.save(this.updateOrganizationFromPayload(organizationOpt.get(), payload));
        // check the access for role and profile for user creating
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        AppUserRequest orgAppUser = payload.getUser();
        Optional<AppUser> appUser = this.appUserRepository.findById(payload.getId());
        if (!BarcoUtil.isNull(orgAppUser.getFirstName())) {
            appUser.get().setFirstName(orgAppUser.getFirstName());
        }
        if (!BarcoUtil.isNull(orgAppUser.getLastName())) {
            appUser.get().setLastName(orgAppUser.getLastName());
        }
        if (!orgAppUser.getUsername().equals(appUser.get().getUsername()) && this.appUserRepository.existsByUsername(orgAppUser.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_ALREADY_TAKEN);
        } else if (!orgAppUser.getEmail().equals(appUser.get().getEmail()) && this.appUserRepository.existsByEmail(orgAppUser.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EMAIL_ALREADY_IN_USE);
        }
        if (!BarcoUtil.isNull(orgAppUser.getEmail())) {
            appUser.get().setEmail(orgAppUser.getEmail());
        }
        if (!BarcoUtil.isNull(orgAppUser.getUsername())) {
            appUser.get().setUsername(orgAppUser.getUsername());
        }
        if (!BarcoUtil.isNull(orgAppUser.getIpAddress())) {
            appUser.get().setIpAddress(orgAppUser.getIpAddress());
        }
        // account type
        if (!BarcoUtil.isNull(orgAppUser.getAccountType())) {
            appUser.get().setAccountType(ACCOUNT_TYPE.getByLookupCode(orgAppUser.getAccountType()));
        }
        // register user role default as admin role
        Set<Role> roleList = this.roleRepository.findAllByNameInAndStatus(orgAppUser.getAssignRole(), APPLICATION_STATUS.ACTIVE);
        if (!roleList.isEmpty()) {
            appUser.get().setAppUserRoles(roleList);
        }
        // profile
        Optional<Profile> profile = this.profileRepository.findProfileByIdAndStatus(orgAppUser.getProfile(), APPLICATION_STATUS.ACTIVE);
        profile.ifPresent(value -> appUser.get().setProfile(value));
        adminUser.ifPresent(user -> appUser.get().setUpdatedBy(user));
        this.appUserRepository.save(appUser.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
    }

    /**
     * Method use to fetch the org by id
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse fetchOrgAccountById(OrganizationRequest payload) throws Exception {
        logger.info("Request fetchOrgAccountById :- {}.", payload);
        AppResponse usernameExist = this.isUsernameAndIdExist(payload);
        if (!BarcoUtil.isNull(usernameExist)) {
            return usernameExist;
        }
        return this.organizationRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE)
            .map(organization -> new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getOrganizationResponse(organization)))
            .orElseGet(() -> new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND));
    }

    /**
     * Method use to fetch the all org
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse fetchAllOrgAccount(OrganizationRequest payload) throws Exception {
        logger.info("Request fetchAllOrgAccount :- {}.", payload);
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.organizationRepository.findAllByStatusNotOrderByDateCreatedDesc(APPLICATION_STATUS.DELETE).stream()
                .map(this::getOrganizationResponse).collect(Collectors.toList()));
    }

    /**
     * Method use to delete the org by id
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse deleteOrgAccountById(OrganizationRequest payload) throws Exception {
        logger.info("Request deleteOrgAccountById :- {}.", payload);
        AppResponse usernameExist = this.isUsernameAndIdExist(payload);
        if (!BarcoUtil.isNull(usernameExist)) {
            return usernameExist;
        }
        // need to call the sp
        Optional<Organization> organization = this.organizationRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE);
        if (organization.isEmpty()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_NOT_FOUND);
        }
        // delete the org and delete the all user and delete the all setting and resource as well
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()), payload);
    }

    /**
     * Method use to delete all org
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse deleteAllOrgAccount(OrganizationRequest payload) throws Exception {
        logger.info("Request deleteAllOrgAccount :- {}.", payload);
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getIds())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        // need to call the sp
        // delete the org and delete the all user and delete the all setting and resource as well
        List<Organization> organizations = this.organizationRepository.findAllByIdIn(payload.getIds());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_DELETED_ALL, payload);
    }

    /**
     * Method use to validate the payload
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    private AppResponse validateAddOrgAccountPayload(OrganizationRequest payload) throws Exception {
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getPhone())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_PHONE_MISSING);
        } else if (BarcoUtil.isNull(payload.getCountryCode())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_COUNTRY_CODE_MISSING);
        } else if (BarcoUtil.isNull(payload.getAddress())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_ADDRESS_MISSING);
        } else if (BarcoUtil.isNull(payload.getUser())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USER_MISSING);
        }
        AppUserRequest user = payload.getUser();
        if (BarcoUtil.isNull(user.getFirstName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FIRST_NAME_MISSING);
        } else if (BarcoUtil.isNull(user.getLastName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LAST_NAME_MISSING);
        } else if (BarcoUtil.isNull(user.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(user.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EMAIL_MISSING);
        } else if (BarcoUtil.isNull(user.getPassword())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PASSWORD_MISSING);
        } else if (this.appUserRepository.existsByUsername(user.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_ALREADY_TAKEN);
        } else if (this.appUserRepository.existsByEmail(user.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EMAIL_ALREADY_IN_USE);
        } else if (BarcoUtil.isNull(user.getIpAddress())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IP_ADDRESS_MISSING);
        } else if (BarcoUtil.isNull(user.getAssignRole())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ROLE_MISSING);
        } else if (BarcoUtil.isNull(user.getProfile())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PROFILE_MISSING);
        } else if (BarcoUtil.isNull(user.getAccountType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PROFILE_ACCOUNT_TYPE_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    /**
     * Method use to validate the payload
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    private AppResponse validateUpdateOrgAccountPayload(OrganizationRequest payload) throws Exception {
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getPhone())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_PHONE_MISSING);
        } else if (BarcoUtil.isNull(payload.getCountryCode())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_COUNTRY_CODE_MISSING);
        } else if (BarcoUtil.isNull(payload.getAddress())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_ADDRESS_MISSING);
        }
        AppUserRequest user = payload.getUser();
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        } else if (BarcoUtil.isNull(user.getFirstName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FIRST_NAME_MISSING);
        } else if (BarcoUtil.isNull(user.getLastName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LAST_NAME_MISSING);
        } else if (BarcoUtil.isNull(user.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(user.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EMAIL_MISSING);
        } else if (BarcoUtil.isNull(user.getIpAddress())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IP_ADDRESS_MISSING);
        } else if (BarcoUtil.isNull(user.getAssignRole())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ROLE_MISSING);
        } else if (BarcoUtil.isNull(user.getProfile())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PROFILE_MISSING);
        } else if (BarcoUtil.isNull(user.getAccountType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PROFILE_ACCOUNT_TYPE_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    /**
     * Method use to validate the username
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    private AppResponse validateUsername(OrganizationRequest payload) throws Exception {
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (this.appUserRepository.findByUsernameAndStatus(payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE).isEmpty()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    /**
     * Method use to get the valid username
     * check user exist or not
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    private AppResponse isUsernameAndIdExist(OrganizationRequest payload) throws Exception {
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    /**
     * Method use to create the organization reg
     * @param payload
     * @return Organization
     * @throws Exception
     * */
    private Organization createOrganization(OrganizationRequest payload) throws Exception {
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        Organization organization = new Organization();
        organization.setName(payload.getName());
        organization.setPhone(payload.getPhone());
        organization.setAddress(payload.getAddress());
        organization.setCountry(this.etlCountryRepository.findByCountryCode(payload.getCountryCode()).get());
        organization.setCreatedBy(appUserOpt.get());
        organization.setUpdatedBy(appUserOpt.get());
        organization.setStatus(APPLICATION_STATUS.ACTIVE);
        return organization;
    }

    /**
     * Method use to update the org
     * @param organization
     * @param payload
     * @return Organization
     * @throws Exception
     * */
    private Organization updateOrganizationFromPayload(Organization organization, OrganizationRequest payload) throws Exception {
        if (!BarcoUtil.isNull(payload.getName())) {
            organization.setName(payload.getName());
        }
        if (!BarcoUtil.isNull(payload.getPhone())) {
            organization.setPhone(payload.getPhone());
        }
        if (!BarcoUtil.isNull(payload.getAddress())) {
            organization.setAddress(payload.getAddress());
        }
        if (!BarcoUtil.isNull(payload.getCountryCode())) {
            organization.setCountry(this.etlCountryRepository.findByCountryCode(payload.getCountryCode()).get());
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            organization.setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        appUserOpt.ifPresent(organization::setUpdatedBy);
        return organization;
    }

}

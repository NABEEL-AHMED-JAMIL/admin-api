package com.barco.admin.service.impl;

import com.barco.admin.service.AppUserService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.NotificationService;
import com.barco.common.emailer.EmailMessagesFactory;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.common.utility.excel.SheetFiled;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.AppUserResponse;
import com.barco.model.dto.response.EventBridgeResponse;
import com.barco.model.pojo.*;
import com.barco.model.repository.*;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.GLookup;
import com.barco.model.util.lookup.EVENT_BRIDGE_TYPE;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class AppUserServiceImpl implements AppUserService {

    private Logger logger = LoggerFactory.getLogger(AppUserServiceImpl.class);

    @Value("${storage.efsFileDire}")
    private String tempStoreDirectory;
    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private AppUserEnvRepository appUserEnvRepository;
    @Autowired
    private EnvVariablesRepository envVariablesRepository;
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
        if (!BarcoUtil.isNull(appUser.get().getAppUserEnvs())) {
            appUserResponse.setEnVariables(appUser.get().getAppUserEnvs()
                .stream()
                .filter(appUserEnv -> appUserEnv.getStatus().equals(APPLICATION_STATUS.ACTIVE))
                .map(appUserEnv -> getEnVariablesResponse(appUserEnv))
                .collect(Collectors.toList()));
        }
        if (!BarcoUtil.isNull(appUser.get().getAppUserEventBridges())) {
            appUserResponse.setEventBridge(appUser.get().getAppUserEventBridges()
                .stream()
                .filter(appUserEventBridge -> appUserEventBridge.getStatus().equals(APPLICATION_STATUS.ACTIVE))
                .map(appUserEventBridge -> {
                    EventBridgeResponse eventBridgeResponse = getEventBridgeResponse(appUserEventBridge);
                    if (!BarcoUtil.isNull(appUserEventBridge.getEventBridge().getBridgeType())) {
                        GLookup bridgeType = GLookup.getGLookup(this.lookupDataCacheService
                            .getChildLookupDataByParentLookupTypeAndChildLookupCode(EVENT_BRIDGE_TYPE.getName(),
                                appUserEventBridge.getEventBridge().getBridgeType().getLookupCode()));
                        eventBridgeResponse.setBridgeType(bridgeType);
                    }
                    return eventBridgeResponse;
                })
                .collect(Collectors.toList()));
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
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(payload.getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (!this.passwordEncoder.matches(payload.getOldPassword(), appUser.get().getPassword())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.OLD_PASSWORD_NOT_MATCH);
        }
        appUser.get().setPassword(this.passwordEncoder.encode(payload.getNewPassword()));
        this.appUserRepository.save(appUser.get());
        // send to the same user email and notification
        this.sendResetPasswordEmail(appUser.get(), this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
        // notification update password
        this.sendNotification(appUser.get().getUsername(), MessageUtil.PASSWORD_UPDATED, MessageUtil.PASSWORD_UPDATE_MESSAGE,
            appUser.get(), this.lookupDataCacheService, this.notificationService);
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
        } else if (BarcoUtil.isNull(payload.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        // if status is in-active & delete then we have filter the role and show only those role in user detail
        appUser.get().setStatus(APPLICATION_STATUS.DELETE);
        appUser.get().setUpdatedBy(adminUser.get());
        this.enabledDisabledProfilePermissionsAccesses(appUser.get(), adminUser.get());
        this.enabledDisabledAppUserRoleAccesses(appUser.get(), adminUser.get());
        this.enabledDisabledAppUserEnvs(appUser.get(), adminUser.get());
        this.appUserRepository.save(appUser.get());
        // email to user
        this.sendCloseUserAccountEmail(appUser.get(), this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
        // notification to admin
        this.sendNotification(adminUser.get().getUsername(), MessageUtil.ACCOUNT_STATUS, String.format(MessageUtil.ACCOUNT_CLOSE_DETAIL,
            appUser.get().getUsername()), adminUser.get(), this.lookupDataCacheService, this.notificationService);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, appUser.get().getUsername()), payload);
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
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getIds())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        Iterator<AppUser> appUsers= this.appUserRepository.findAllById(payload.getIds()).iterator();
        while (appUsers.hasNext()) {
            AppUser appUser = appUsers.next();
            appUser.setStatus(APPLICATION_STATUS.DELETE);
            appUser.setUpdatedBy(adminUser.get());
            this.enabledDisabledProfilePermissionsAccesses(appUser, adminUser.get());
            this.enabledDisabledAppUserRoleAccesses(appUser, adminUser.get());
            this.enabledDisabledAppUserEnvs(appUser, adminUser.get());
            this.appUserRepository.save(appUser);
            // email to user
            this.sendCloseUserAccountEmail(appUser, this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
            // notification to admin
            this.sendNotification(adminUser.get().getUsername(), MessageUtil.ACCOUNT_STATUS, String.format(MessageUtil.ACCOUNT_CLOSE_DETAIL,
                appUser.getUsername()), adminUser.get(), this.lookupDataCacheService, this.notificationService);
        }
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, ""), payload);
    }

    /**
     * Method use to download app user account template
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadAppUserAccountTemplateFile() throws Exception {
        logger.info("Request downloadAppUserAccountTemplateFile ");
        return downloadTemplateFile(this.tempStoreDirectory, this.bulkExcel,
            this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.APP_USER));
    }

    /**
     * Method use to download app user account
     * @param payload
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadAppUserAccount(AppUserRequest payload) throws Exception {
        logger.info("Request downloadAppUserAccount :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            throw new Exception(MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            throw new Exception(MessageUtil.APPUSER_NOT_FOUND);
        }
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.APP_USER);
        XSSFWorkbook workbook = new XSSFWorkbook();
        this.bulkExcel.setWb(workbook);
        XSSFSheet xssfSheet = workbook.createSheet(sheetFiled.getSheetName());
        this.bulkExcel.setSheet(xssfSheet);
        AtomicInteger rowCount = new AtomicInteger();
        this.bulkExcel.fillBulkHeader(rowCount.get(), sheetFiled.getColTitle());
        // change to start date and end date filter and if ids include then only download that ids
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
        Iterator<AppUser> appUserIterator;
        if (!BarcoUtil.isNull(payload.getIds()) && payload.getIds().size() > 0) {
            appUserIterator = this.appUserRepository.findAllByDateCreatedBetweenAndAppUserParentAndAppUserIdInAndStatusNotOrderByDateCreatedDesc(
                startDate, endDate, appUser.get(), payload.getIds(), APPLICATION_STATUS.DELETE).iterator();
        } else {
            appUserIterator = this.appUserRepository.findAllByDateCreatedBetweenAndAppUserParentAndStatusNotOrderByDateCreatedDesc(
                startDate, endDate, appUser.get(), APPLICATION_STATUS.DELETE).iterator();
        }
        while (appUserIterator.hasNext()) {
            AppUser appUserIter = appUserIterator.next();
            rowCount.getAndIncrement();
            List<String> dataCellValue = new ArrayList<>();
            dataCellValue.add(appUserIter.getFirstName());
            dataCellValue.add(appUserIter.getLastName());
            dataCellValue.add(appUserIter.getEmail());
            dataCellValue.add(appUserIter.getUsername());
            dataCellValue.add(appUserIter.getIpAddress());
            dataCellValue.add(appUserIter.getProfile().getProfileName());
            dataCellValue.add(appUserIter.getAppUserRoles().stream()
                .map(appUserRole -> appUserRole.getName()).collect(Collectors.joining(",")));
            this.bulkExcel.fillBulkBody(dataCellValue, rowCount.get());
        }
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        workbook.write(outSteam);
        return outSteam;
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
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
        List<AppUserResponse> subAppUserResponses = this.appUserRepository.findAllByDateCreatedBetweenAndAppUserParentAndStatusNotOrderByDateCreatedDesc(
            startDate, endDate, appUser.get(), APPLICATION_STATUS.DELETE).stream()
            .map(subAppUser -> {
                AppUserResponse appUserResponse = getAppUserDetail(subAppUser);
                appUserResponse.setTotalSubUser(subAppUser.getSubAppUsers().size());
                appUserResponse.setCreatedBy(getActionUser(subAppUser.getCreatedBy()));
                appUserResponse.setUpdatedBy(getActionUser(subAppUser.getUpdatedBy()));
                appUserResponse.setDateCreated(subAppUser.getDateCreated());
                appUserResponse.setDateUpdated(subAppUser.getDateUpdated());
                appUserResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(subAppUser.getStatus().getLookupType()));
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
        List<EnvVariables> envVariablesList = this.envVariablesRepository.findAllByStatusNotOrderByDateCreatedDesc(APPLICATION_STATUS.DELETE);
        for (EnvVariables envVariables : envVariablesList) {
            this.appUserEnvRepository.save(getAppUserEnv(adminUser.get(), appUser, envVariables));
        }
        // email send to the user
        this.sendRegisterUser(appUser, this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
        // email send to the admin
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
            appUser.get().setUpdatedBy(adminUser.get());
            this.enabledDisabledProfilePermissionsAccesses(appUser.get(), adminUser.get());
            this.enabledDisabledAppUserRoleAccesses(appUser.get(), adminUser.get());
            this.enabledDisabledAppUserEnvs(appUser.get(), adminUser.get());
        }
        this.appUserRepository.save(appUser.get());
        // email to the user
        this.sendEnabledDisabledRegisterUser(appUser.get(), this.lookupDataCacheService, this.templateRegRepository, this.emailMessagesFactory);
        // notification to admin
        this.sendNotification(adminUser.get().getUsername(), MessageUtil.ACCOUNT_STATUS, (appUser.get().getStatus().equals(APPLICATION_STATUS.ACTIVE) ?
            String.format(MessageUtil.ACCOUNT_ENABLED, appUser.get().getUsername()) : String.format(MessageUtil.ACCOUNT_DISABLED, appUser.get().getUsername())),
            adminUser.get(), this.lookupDataCacheService, this.notificationService);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, appUser.get().getUsername()), payload);
    }

}

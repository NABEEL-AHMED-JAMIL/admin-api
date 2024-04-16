package com.barco.admin.service;

import com.barco.admin.service.impl.QueryService;
import com.barco.common.emailer.EmailMessageRequest;
import com.barco.common.emailer.EmailMessagesFactory;
import com.barco.common.emailer.EmailUtil;
import com.barco.common.security.JwtUtils;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.common.utility.excel.SheetFiled;
import com.barco.model.dto.request.ForgotPasswordRequest;
import com.barco.model.dto.request.MessageRequest;
import com.barco.model.dto.request.NotificationRequest;
import com.barco.model.dto.response.*;
import com.barco.model.pojo.*;
import com.barco.model.repository.TemplateRegRepository;
import com.barco.model.security.UserSessionDetail;
import com.barco.model.util.ModelUtil;
import com.barco.model.util.lookup.*;
import com.google.gson.Gson;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.barco.model.util.lookup.EMAIL_TEMPLATE.*;
import static com.barco.model.util.lookup.EMAIL_TEMPLATE.REGISTER_USER;

/**
 * Api use to perform crud operation on root user
 * @author Nabeel Ahmed
 */
public interface RootService {

    Logger logger = LoggerFactory.getLogger(RootService.class);

    String PARENT_LOOKUP_DATA = "PARENT_LOOKUP_DATA";
    String SUB_LOOKUP_DATA = "SUB_LOOKUP_DATA";

    /**
     * Method use to fetch the rup-response
     * @param data
     * @return LinkRPUResponse
     * */
    public default LinkRPUResponse getLinkRPUResponse(HashMap<String, Object> data, APPLICATION_STATUS status) {
        LinkRPUResponse linkRPUResponse = new LinkRPUResponse();
        if (data.containsKey(QueryService.ID)) {
            linkRPUResponse.setId(Long.valueOf(data.get(QueryService.ID).toString()));
        }
        if (data.containsKey(QueryService.EMAIL) && !BarcoUtil.isNull(data.get(QueryService.EMAIL))) {
            linkRPUResponse.setEmail(data.get(QueryService.EMAIL).toString());
        }
        if (data.containsKey(QueryService.USERNAME) && !BarcoUtil.isNull(data.get(QueryService.USERNAME))) {
            linkRPUResponse.setUsername(data.get(QueryService.USERNAME).toString());
        }
        if (data.containsKey(QueryService.FULL_NAME) && !BarcoUtil.isNull(data.get(QueryService.FULL_NAME))) {
            linkRPUResponse.setFullName(data.get(QueryService.FULL_NAME).toString());
        }
        if (data.containsKey(QueryService.PROFILE_IMG) && !BarcoUtil.isNull(data.get(QueryService.PROFILE_IMG))) {
            linkRPUResponse.setProfileImg(data.get(QueryService.PROFILE_IMG).toString());
        }
        if (data.containsKey(QueryService.LINK_DATA) && !BarcoUtil.isNull(data.get(QueryService.LINK_DATA))) {
            linkRPUResponse.setLinkData(data.get(QueryService.LINK_DATA).toString());
        }
        if (data.containsKey(QueryService.LINK_STATUS) && !BarcoUtil.isNull(data.get(QueryService.LINK_STATUS))) {
            linkRPUResponse.setLinkStatus(APPLICATION_STATUS.getStatusByLookupCode(Long.valueOf(data.get(QueryService.LINK_STATUS).toString())));
        } else {
            linkRPUResponse.setLinkStatus(APPLICATION_STATUS.getStatusByLookupCode(status.getLookupCode()));
        }
        if (data.containsKey(QueryService.LINKED) && !BarcoUtil.isNull(data.get(QueryService.LINKED))) {
            linkRPUResponse.setLinked(Boolean.valueOf(data.get(QueryService.LINKED).toString()));
        }
        if (data.containsKey(QueryService.ENV_VALUE) && !BarcoUtil.isNull(data.get(QueryService.ENV_VALUE))) {
            linkRPUResponse.setEnvValue(data.get(QueryService.ENV_VALUE).toString());
        }
        linkRPUResponse.setProfile(new ProfileResponse(Long.valueOf(data.get(QueryService.PROFILE_ID).toString()),
                data.get(QueryService.PROFILE_NAME).toString()));
        return linkRPUResponse;
    }

    /**
     * Method use to wrap the auth response
     * @param authResponse
     * @param userDetails
     * @return AuthResponse
     * */
    public default AuthResponse getAuthResponseDetail(AuthResponse authResponse, UserSessionDetail userDetails) {
        authResponse.setId(userDetails.getId());
        authResponse.setUsername(userDetails.getUsername());
        authResponse.setFirstName(userDetails.getFirstName());
        authResponse.setLastName(userDetails.getLastName());
        authResponse.setUsername(userDetails.getUsername());
        authResponse.setEmail(userDetails.getEmail());
        authResponse.setProfileImage(userDetails.getProfileImage());
        authResponse.setIpAddress(userDetails.getIpAddress());
        authResponse.setRoles(userDetails.getAuthorities().stream()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .collect(Collectors.toList()));
        if (!BarcoUtil.isNull(userDetails.getProfile())) {
            authResponse.setProfile(this.getProfilePermissionResponse(userDetails.getProfile()));
        }
        return authResponse;
    }

    /**
     * getAppUserDetail method use to convert entity to dto
     * @param appUser
     * */
    public default AppUserResponse getAppUserDetail(AppUser appUser) {
        AppUserResponse appUserResponse = new AppUserResponse();
        appUserResponse.setId(appUser.getId());
        appUserResponse.setFirstName(appUser.getFirstName());
        appUserResponse.setLastName(appUser.getLastName());
        appUserResponse.setEmail(appUser.getEmail());
        appUserResponse.setUsername(appUser.getUsername());
        appUserResponse.setProfileImg(appUser.getImg());
        appUserResponse.setIpAddress(appUser.getIpAddress());
        appUserResponse.setRoles(appUser.getAppUserRoles().stream()
            .map(role -> role.getName()).collect(Collectors.toList()));
        if (!BarcoUtil.isNull(appUser.getProfile())) {
            appUserResponse.setProfile(this.getProfilePermissionResponse(appUser.getProfile()));
        }
        return appUserResponse;
    }

    /**
     * getRoleResponse method use to convert entity to dto
     * @param profile
     * */
    public default ProfileResponse getProfilePermissionResponse(Profile profile) {
        ProfileResponse profilePermission = new ProfileResponse();
        profilePermission.setId(profile.getId());
        profilePermission.setProfileName(profile.getProfileName());
        profilePermission.setPermission(profile.getProfilePermissions().stream()
            .filter(permissionOption -> permissionOption.getStatus().equals(APPLICATION_STATUS.ACTIVE))
            .map(permission -> permission.getPermission().getPermissionName())
            .collect(Collectors.toList()));
        return profilePermission;
    }

    /**
     * Method use to get the action user as response
     * @param appUser
     * */
    public default ActionByUser getActionUser(AppUser appUser) {
        ActionByUser actionByUser = new ActionByUser();
        actionByUser.setId(appUser.getId());
        actionByUser.setEmail(appUser.getEmail());
        actionByUser.setUsername(appUser.getUsername());
        return actionByUser;
    }

    /**
     * Method use to download template file
     * @param tempStoreDirectory
     * @param bulkExcel
     * @param sheetFiled
     * */
    public default ByteArrayOutputStream downloadTemplateFile(
        String tempStoreDirectory, BulkExcel bulkExcel, SheetFiled sheetFiled) throws Exception {
        String basePath = tempStoreDirectory + File.separator;
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream inputStream = cl.getResourceAsStream(bulkExcel.BATCH);
        String fileUploadPath = basePath + System.currentTimeMillis()+bulkExcel.XLSX_EXTENSION;
        FileOutputStream fileOut = new FileOutputStream(fileUploadPath);
        IOUtils.copy(inputStream, fileOut);
        // after copy the stream into file close
        if (inputStream != null) {
            inputStream.close();
        }
        // 2nd insert data to newly copied file. So that template couldn't be changed.
        XSSFWorkbook workbook = new XSSFWorkbook(new File(fileUploadPath));
        bulkExcel.setWb(workbook);
        XSSFSheet sheet = workbook.createSheet(sheetFiled.getSheetName());
        bulkExcel.setSheet(sheet);
        bulkExcel.fillBulkHeader(0, sheetFiled.getColTitle());
        // Priority
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        File file = new File(fileUploadPath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(Files.readAllBytes(file.toPath()));
        file.delete();
        return byteArrayOutputStream;
    }

    /**
     * sendRegisterUser method use on user register.
     * @param appUser
     * @param lookupDataCacheService
     * @param templateRegRepository
     * @param emailMessagesFactory
     * */
    public default boolean sendRegisterUser(AppUser appUser, LookupDataCacheService lookupDataCacheService,
        TemplateRegRepository templateRegRepository, EmailMessagesFactory emailMessagesFactory) {
        try {
            LookupDataResponse senderEmail = lookupDataCacheService.getParentLookupDataByParentLookupType(
                LookupUtil.NON_REPLY_EMAIL_SENDER);
            Optional<TemplateReg> templateReg = templateRegRepository.findFirstByTemplateNameAndStatusNot(
                REGISTER_USER.name(), APPLICATION_STATUS.INACTIVE);
            if (!templateReg.isPresent()) {
                logger.info("No Template Found With %s", REGISTER_USER.name());
                return false;
            }
            Map<String, Object> metaData = new HashMap<>();
            metaData.put(EmailUtil.USERNAME, appUser.getUsername());
            metaData.put(EmailUtil.FULL_NAME, appUser.getFirstName().concat(" ").concat(appUser.getLastName()));
            metaData.put(EmailUtil.ROLE, appUser.getAppUserRoles().stream().map(role -> role.getName()).collect(Collectors.joining(",")));
            metaData.put(EmailUtil.PROFILE, appUser.getProfile().getProfileName());
            // email send request
            EmailMessageRequest emailMessageRequest = new EmailMessageRequest();
            emailMessageRequest.setFromEmail(senderEmail.getLookupValue());
            emailMessageRequest.setRecipients(appUser.getEmail());
            emailMessageRequest.setSubject(EmailUtil.USER_REGISTERED);
            emailMessageRequest.setBodyMap(metaData);
            emailMessageRequest.setBodyPayload(templateReg.get().getTemplateContent());
            logger.info("Email Send Status :- " + emailMessagesFactory.sendSimpleMail(emailMessageRequest));
            return true;
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
            return false;
        }
    }

    /**
     * sendRegisterUser method use on user register.
     * @param appUser
     * @param lookupDataCacheService
     * @param templateRegRepository
     * @param emailMessagesFactory
     * */
    public default boolean sendEnabledDisabledRegisterUser(AppUser appUser, LookupDataCacheService lookupDataCacheService,
        TemplateRegRepository templateRegRepository, EmailMessagesFactory emailMessagesFactory) {
        try {
            LookupDataResponse senderEmail = lookupDataCacheService.getParentLookupDataByParentLookupType(LookupUtil.NON_REPLY_EMAIL_SENDER);
            Optional<TemplateReg> templateReg;
            if (appUser.getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
                templateReg = templateRegRepository.findFirstByTemplateNameAndStatusNot(ACTIVE_USER_ACCOUNT.name(), APPLICATION_STATUS.INACTIVE);
            } else {
                templateReg = templateRegRepository.findFirstByTemplateNameAndStatusNot(BLOCK_USER_ACCOUNT.name(), APPLICATION_STATUS.INACTIVE);
            }
            if (!templateReg.isPresent()) {
                logger.info("No Template Found With %s", (appUser.getStatus().equals(APPLICATION_STATUS.ACTIVE) ?
                    ACTIVE_USER_ACCOUNT.name() : BLOCK_USER_ACCOUNT.name()));
                return false;
            }
            Map<String, Object> metaData = new HashMap<>();
            metaData.put(EmailUtil.USERNAME, appUser.getUsername());
            metaData.put(EmailUtil.FULL_NAME, appUser.getFirstName().concat(" ").concat(appUser.getLastName()));
            metaData.put(EmailUtil.ROLE, appUser.getAppUserRoles().stream().map(role -> role.getName()).collect(Collectors.joining(",")));
            metaData.put(EmailUtil.PROFILE, appUser.getProfile().getProfileName());
            // email send request
            EmailMessageRequest emailMessageRequest = new EmailMessageRequest();
            emailMessageRequest.setFromEmail(senderEmail.getLookupValue());
            emailMessageRequest.setRecipients(appUser.getEmail());
            emailMessageRequest.setSubject(appUser.getStatus().equals(APPLICATION_STATUS.ACTIVE) ?
                EmailUtil.YOUR_ACCOUNT_IS_NOW_ACTIVE : EmailUtil.YOUR_ACCOUNT_HAS_BEEN_BLOCKED);
            emailMessageRequest.setBodyMap(metaData);
            emailMessageRequest.setBodyPayload(templateReg.get().getTemplateContent());
            logger.info("Email Send Status :- " + emailMessagesFactory.sendSimpleMail(emailMessageRequest));
            return true;
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
            return false;
        }
    }

    /**
     * sendRegisterUser method use on user register.
     * @param appUser
     * @param lookupDataCacheService
     * @param templateRegRepository
     * @param emailMessagesFactory
     * */
    public default boolean sendForgotPasswordEmail(AppUser appUser, LookupDataCacheService lookupDataCacheService,
        TemplateRegRepository templateRegRepository, EmailMessagesFactory emailMessagesFactory, JwtUtils jwtUtils) {
        try {
            LookupDataResponse senderEmail = lookupDataCacheService.getParentLookupDataByParentLookupType(LookupUtil.NON_REPLY_EMAIL_SENDER);
            LookupDataResponse forgotPasswordUrl = lookupDataCacheService.getParentLookupDataByParentLookupType(LookupUtil.RESET_PASSWORD_LINK);
            Optional<TemplateReg> templateReg = templateRegRepository.findFirstByTemplateNameAndStatusNot(FORGOT_USER_PASSWORD.name(), APPLICATION_STATUS.INACTIVE);
            if (!templateReg.isPresent()) {
                logger.info("No Template Found With %s", FORGOT_USER_PASSWORD.name());
                return false;
            }
            // forgot password
            ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
            forgotPasswordRequest.setId(appUser.getId());
            forgotPasswordRequest.setEmail(appUser.getEmail());
            forgotPasswordRequest.setUsername(appUser.getUsername());
            // meta data
            Map<String, Object> metaData = new HashMap<>();
            metaData.put(EmailUtil.USERNAME, appUser.getUsername());
            metaData.put(EmailUtil.FULL_NAME, appUser.getFirstName().concat(" ").concat(appUser.getLastName()));
            metaData.put(EmailUtil.FORGOT_PASSWORD_URL, forgotPasswordUrl.getLookupValue()
                +"?token="+ jwtUtils.generateTokenFromUsernameResetPassword(forgotPasswordRequest.toString()));
            // email send request
            EmailMessageRequest emailMessageRequest = new EmailMessageRequest();
            emailMessageRequest.setFromEmail(senderEmail.getLookupValue());
            emailMessageRequest.setRecipients(appUser.getEmail());
            emailMessageRequest.setSubject(EmailUtil.FORGOT_PASSWORD);
            emailMessageRequest.setBodyMap(metaData);
            emailMessageRequest.setBodyPayload(templateReg.get().getTemplateContent());
            logger.info("Email Send Status :- " + emailMessagesFactory.sendSimpleMail(emailMessageRequest));
            return true;
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
            return false;
        }
    }

    /**
     * sendResetPassword method use to send reset confirm email
     * @param appUser
     * @param lookupDataCacheService
     * @param templateRegRepository
     * @param emailMessagesFactory
     * */
    public default boolean sendResetPasswordEmail(AppUser appUser, LookupDataCacheService lookupDataCacheService,
        TemplateRegRepository templateRegRepository, EmailMessagesFactory emailMessagesFactory) {
        try {
            LookupDataResponse senderEmail = lookupDataCacheService.getParentLookupDataByParentLookupType(
                LookupUtil.NON_REPLY_EMAIL_SENDER);
            Optional<TemplateReg> templateReg = templateRegRepository.findFirstByTemplateNameAndStatusNot(
                RESET_USER_PASSWORD.name(), APPLICATION_STATUS.INACTIVE);
            if (!templateReg.isPresent()) {
                logger.info("No Template Found With %s", RESET_USER_PASSWORD.name());
                return false;
            }
            // meta data
            Map<String, Object> metaData = new HashMap<>();
            metaData.put(EmailUtil.USERNAME, appUser.getUsername());
            metaData.put(EmailUtil.FULL_NAME, appUser.getFirstName().concat(" ").concat(appUser.getLastName()));
            // email send request
            EmailMessageRequest emailMessageRequest = new EmailMessageRequest();
            emailMessageRequest.setFromEmail(senderEmail.getLookupValue());
            emailMessageRequest.setRecipients(appUser.getEmail());
            emailMessageRequest.setSubject(EmailUtil.PASSWORD_UPDATED);
            emailMessageRequest.setBodyMap(metaData);
            emailMessageRequest.setBodyPayload(templateReg.get().getTemplateContent());
            logger.info("Email Send Status :- " + emailMessagesFactory.sendSimpleMail(emailMessageRequest));
            return true;
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
            return false;
        }
    }

    /**
     * Method use to create the group response
     * @param groups
     * @return GroupUser
     * */
    public default GroupResponse getGroupResponse(Groups groups) {
        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setId(groups.getId());
        groupResponse.setName(groups.getName());
        groupResponse.setAvatar(groupResponse.getName().charAt(0)+"");
        groupResponse.setDescription(groups.getDescription());
        groupResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(groups.getStatus().getLookupType()));
        groupResponse.setCreatedBy(getActionUser(groups.getCreatedBy()));
        groupResponse.setUpdatedBy(getActionUser(groups.getUpdatedBy()));
        groupResponse.setDateUpdated(groups.getDateUpdated());
        groupResponse.setDateCreated(groups.getDateCreated());
        return groupResponse;
    }

    /**
     * Method use to create the group user
     * @param groups
     * @param appUser
     * @param adminUser
     * @param userType
     * @return GroupUser
     * */
    public default GroupUser getGroupUser(Groups groups, AppUser adminUser,
        AppUser appUser, GROUP_USER_TYPE userType) {
        GroupUser groupUser = new GroupUser();
        groupUser.setGroups(groups);
        groupUser.setAppUser(appUser);
        groupUser.setCreatedBy(adminUser);
        groupUser.setUpdatedBy(adminUser);
        groupUser.setStatus(APPLICATION_STATUS.ACTIVE);
        groupUser.setUserType(userType);
        return groupUser;
    }

    /***
     * Method use to get the env variable
     * @param envVariables
     * @return EnVariablesResponse
     * */
    public default EnVariablesResponse getEnVariablesResponse(EnvVariables envVariables) {
        EnVariablesResponse enVariablesResponse = new EnVariablesResponse();
        enVariablesResponse.setId(envVariables.getId());
        enVariablesResponse.setEnvKey(envVariables.getEnvKey());
        enVariablesResponse.setDescription(envVariables.getDescription());
        enVariablesResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(envVariables.getStatus().getLookupType()));
        enVariablesResponse.setCreatedBy(getActionUser(envVariables.getCreatedBy()));
        enVariablesResponse.setUpdatedBy(getActionUser(envVariables.getUpdatedBy()));
        enVariablesResponse.setDateUpdated(envVariables.getDateUpdated());
        enVariablesResponse.setDateCreated(envVariables.getDateCreated());
        return enVariablesResponse;
    }

    /**
     * Method use to send notification
     * @param sendTo
     * @param title
     * @param message
     * @param appUser
     * @throws Exception
     * @throws Exception
     * */
    public default void sendNotification(String sendTo, String title, String message, AppUser appUser,
        LookupDataCacheService lookupDataCacheService, NotificationService notificationService) throws Exception {
        LookupDataResponse notificationTime = lookupDataCacheService.getParentLookupDataByParentLookupType(LookupUtil.NOTIFICATION_DISAPPEAR_TIME);
        notificationService.addNotification(new NotificationRequest(sendTo, new MessageRequest(title, message),
            NOTIFICATION_TYPE.USER_NOTIFICATION.getLookupCode(), ModelUtil.addDays(new Timestamp(System.currentTimeMillis()),
            Long.valueOf(notificationTime.getLookupValue())), NOTIFICATION_STATUS.UNREAD.getLookupCode()), appUser);
    }

    /**
     * Method use to get the db lookup
     * @param lookupData
     * @return GLookup
     * */
    public default GLookup getDBLoopUp(Optional<LookupData> lookupData) {
        if (lookupData.isPresent()) {
            return new GLookup(lookupData.get().getLookupType(), lookupData.get().getLookupCode(), lookupData.get().getLookupValue());
        }
        return null;
    }

    /**
     * Method use to get the templateReg response
     * @param templateReg
     * @return TemplateRegResponse
     * */
    public default TemplateRegResponse getTemplateRegResponse(TemplateReg templateReg) {
        TemplateRegResponse templateRegResponse = new TemplateRegResponse();
        templateRegResponse.setId(templateReg.getId());
        templateRegResponse.setTemplateName(templateReg.getTemplateName());
        templateRegResponse.setTemplateContent(templateReg.getTemplateContent());
        templateRegResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(templateReg.getStatus().getLookupType()));
        templateRegResponse.setCreatedBy(getActionUser(templateReg.getCreatedBy()));
        templateRegResponse.setUpdatedBy(getActionUser(templateReg.getUpdatedBy()));
        templateRegResponse.setDateUpdated(templateReg.getDateUpdated());
        templateRegResponse.setDateCreated(templateReg.getDateCreated());
        return templateRegResponse;
    }

    /**
     * Method use to add the link detail
     * @param superAdmin
     * @param role
     * @param appUser
     * @return AppUserRoleAccess
     * */
    public default AppUserRoleAccess getAppUserRoleAccess(AppUser superAdmin, Role role, AppUser appUser) {
        AppUserRoleAccess appUserRoleAccess = new AppUserRoleAccess();
        appUserRoleAccess.setCreatedBy(superAdmin);
        appUserRoleAccess.setUpdatedBy(superAdmin);
        appUserRoleAccess.setRole(role);
        appUserRoleAccess.setAppUser(appUser);
        appUserRoleAccess.setStatus(APPLICATION_STATUS.ACTIVE);
        if (role.getStatus().getLookupType().equals(APPLICATION_STATUS.INACTIVE.getLookupType()) ||
                appUser.getStatus().getLookupType().equals(APPLICATION_STATUS.INACTIVE.getLookupType())) {
            appUserRoleAccess.setStatus(APPLICATION_STATUS.INACTIVE);
        }
        return appUserRoleAccess;
    }

    /**
     * Method use to add the link detail
     * @param superAdmin
     * @param profile
     * @param appUser
     * @return AppUserRoleAccess
     * */
    public default AppUserProfileAccess getAppUserProfileAccess(AppUser superAdmin, Profile profile, AppUser appUser) {
        AppUserProfileAccess appUserRoleAccess = new AppUserProfileAccess();
        appUserRoleAccess.setCreatedBy(superAdmin);
        appUserRoleAccess.setUpdatedBy(superAdmin);
        appUserRoleAccess.setProfile(profile);
        appUserRoleAccess.setAppUser(appUser);
        appUserRoleAccess.setStatus(APPLICATION_STATUS.ACTIVE);
        if (profile.getStatus().getLookupType().equals(APPLICATION_STATUS.INACTIVE.getLookupType()) ||
                appUser.getStatus().getLookupType().equals(APPLICATION_STATUS.INACTIVE.getLookupType())) {
            appUserRoleAccess.setStatus(APPLICATION_STATUS.INACTIVE);
        }
        return appUserRoleAccess;
    }

    /**
     * Method use to convert the role to role response
     * @param role
     * @return RoleResponse
     * */
    public default RoleResponse gateRoleResponse(Role role) {
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setId(role.getId());
        roleResponse.setName(role.getName());
        roleResponse.setDescription(role.getDescription());
        roleResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(role.getStatus().getLookupType()));
        roleResponse.setCreatedBy(getActionUser(role.getCreatedBy()));
        roleResponse.setUpdatedBy(getActionUser(role.getUpdatedBy()));
        roleResponse.setDateUpdated(role.getDateUpdated());
        roleResponse.setDateCreated(role.getDateCreated());
        return roleResponse;
    }

    /**
     * Method use to convert pojo to dto as response
     * @param profile
     * @return ProfileResponse
     * */
    public default ProfileResponse gateProfileResponse(Profile profile) {
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setId(profile.getId());
        profileResponse.setProfileName(profile.getProfileName());
        profileResponse.setDescription(profile.getDescription());
        profileResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(profile.getStatus().getLookupType()));
        profileResponse.setCreatedBy(getActionUser(profile.getCreatedBy()));
        profileResponse.setUpdatedBy(getActionUser(profile.getUpdatedBy()));
        profileResponse.setDateUpdated(profile.getDateUpdated());
        profileResponse.setDateCreated(profile.getDateCreated());
        return profileResponse;
    }

    /**
     * Method use to convert pojo to deto as response
     * @param permission
     * @return ProfileResponse
     * */
    public default PermissionResponse gatePermissionResponse(Permission permission) {
        PermissionResponse permissionResponse = new PermissionResponse();
        permissionResponse.setId(permission.getId());
        permissionResponse.setPermissionName(permission.getPermissionName());
        permissionResponse.setDescription(permission.getDescription());
        permissionResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(permission.getStatus().getLookupType()));
        permissionResponse.setCreatedBy(getActionUser(permission.getCreatedBy()));
        permissionResponse.setUpdatedBy(getActionUser(permission.getUpdatedBy()));
        permissionResponse.setDateUpdated(permission.getDateUpdated());
        permissionResponse.setDateCreated(permission.getDateCreated());
        return permissionResponse;
    }

    /**
     * Method us to get the lookupData
     * @param lookupData
     * */
    public default LookupDataResponse getLookupDataDetail(LookupData lookupData) {
        LookupDataResponse parentLookupData = new LookupDataResponse();
        parentLookupData = this.fillLookupDataResponse(lookupData, parentLookupData, false);
        if (!BarcoUtil.isNull(lookupData.getLookupChildren()) && lookupData.getLookupChildren().size() > 0) {
            parentLookupData.setLookupChildren(lookupData.getLookupChildren().stream()
                .map(childLookup -> this.fillLookupDataResponse(childLookup, new LookupDataResponse(), false))
                .collect(Collectors.toSet()));
        }
        return parentLookupData;
    }

    /**
     * Method use to fill the lookup data
     * @param lookupData
     * @param lookupDataResponse
     * */
    public default LookupDataResponse fillLookupDataResponse(
        LookupData lookupData, LookupDataResponse lookupDataResponse, Boolean isFull) {
        lookupDataResponse.setId(lookupData.getId());
        lookupDataResponse.setLookupCode(lookupData.getLookupCode());
        lookupDataResponse.setLookupValue(lookupData.getLookupValue());
        lookupDataResponse.setLookupType(lookupData.getLookupType());
        if (isFull) {
            lookupDataResponse.setUiLookup(UI_LOOKUP.getStatusByLookupType(lookupData.getUiLookup().getLookupType()));
            lookupDataResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(lookupData.getStatus().getLookupType()));
            lookupDataResponse.setDescription(lookupData.getDescription());
            lookupDataResponse.setCreatedBy(getActionUser(lookupData.getCreatedBy()));
            lookupDataResponse.setUpdatedBy(getActionUser(lookupData.getUpdatedBy()));
            lookupDataResponse.setDateUpdated(lookupData.getDateUpdated());
            lookupDataResponse.setDateCreated(lookupData.getDateCreated());
        }
        return lookupDataResponse;
    }

    /**
     * Method use to enabled or disabled the profile permissions accesses
     * @param appUser
     * @param adminUser
     * */
    public default void enabledDisabledProfilePermissionsAccesses(AppUser appUser, AppUser adminUser) {
        if (!BarcoUtil.isNull(appUser.getProfilePermissionsAccesses()) && appUser.getProfilePermissionsAccesses().size() > 0) {
            appUser.getProfilePermissionsAccesses().stream()
                .map(profileAccess -> {
                    profileAccess.setStatus(appUser.getStatus());
                    profileAccess.setUpdatedBy(adminUser);
                    return profileAccess;
                }).collect(Collectors.toList());
        }
    }

    /**
     * Method use to enabled or disabled the app user role accesses
     * @param appUser
     * @param adminUser
     * */
    public default void enabledDisabledAppUserRoleAccesses(AppUser appUser, AppUser adminUser) {
        if (!BarcoUtil.isNull(appUser.getAppUserRoleAccesses()) && appUser.getAppUserRoleAccesses().size() > 0) {
            appUser.getAppUserRoleAccesses().stream()
                .map(appUserRoleAccess -> {
                    appUserRoleAccess.setStatus(appUser.getStatus());
                    appUserRoleAccess.setUpdatedBy(adminUser);
                    return appUserRoleAccess;
                }).collect(Collectors.toList());
        }
    }

    /**
     * Method use to enabled and disabled the app user envs
     * @param appUser
     * @param adminUser
     * */
    public default void enabledDisabledAppUserEnvs(AppUser appUser, AppUser adminUser) {
        if (!BarcoUtil.isNull(appUser.getAppUserEnvs()) && appUser.getAppUserEnvs().size() > 0) {
            appUser.getAppUserEnvs().stream()
                .map(appUserEnv -> {
                    appUserEnv.setStatus(appUser.getStatus());
                    appUserEnv.setUpdatedBy(adminUser);
                    return appUserEnv;
                }).collect(Collectors.toList());
        }
    }

    /**
     * Method use to enabled and disabled  the group users
     * @param appUser
     * @param adminUser
     * **/
    public default void enabledDisabledGroupUsers(AppUser appUser, AppUser adminUser) {
        if (!BarcoUtil.isNull(appUser.getGroupUsers()) && appUser.getGroupUsers().size() > 0) {
            appUser.getGroupUsers().stream()
                .map(groupUser -> {
                    groupUser.setStatus(appUser.getStatus());
                    groupUser.setUpdatedBy(adminUser);
                    return groupUser;
                }).collect(Collectors.toList());
        }
    }


}
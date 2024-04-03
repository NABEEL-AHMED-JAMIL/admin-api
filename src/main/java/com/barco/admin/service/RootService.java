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
import com.barco.model.dto.response.*;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.Profile;
import com.barco.model.pojo.TemplateReg;
import com.barco.model.repository.TemplateRegRepository;
import com.barco.model.security.UserSessionDetail;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.LookupUtil;
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
            linkRPUResponse.setLinkStatus(APPLICATION_STATUS.getStatusByLookupCode(
                    Long.valueOf(data.get(QueryService.LINK_STATUS).toString())));
        } else {
            linkRPUResponse.setLinkStatus(APPLICATION_STATUS.getStatusByLookupCode(status.getLookupCode()));
        }
        if (data.containsKey(QueryService.LINKED) && !BarcoUtil.isNull(data.get(QueryService.LINKED))) {
            linkRPUResponse.setLinked(Boolean.valueOf(data.get(QueryService.LINKED).toString()));
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
    public default boolean sendForgotPasswordEmail(AppUser appUser, LookupDataCacheService lookupDataCacheService,
        TemplateRegRepository templateRegRepository, EmailMessagesFactory emailMessagesFactory, JwtUtils jwtUtils) {
        try {
            LookupDataResponse senderEmail = lookupDataCacheService.getParentLookupDataByParentLookupType(LookupUtil.NON_REPLY_EMAIL_SENDER);
            LookupDataResponse forgotPasswordUrl = lookupDataCacheService.getParentLookupDataByParentLookupType(LookupUtil.RESET_PASSWORD_LINK);
            Optional<TemplateReg> templateReg = templateRegRepository.findFirstByTemplateNameAndStatusNot(FORGOT_USER_PASSWORD.name(), APPLICATION_STATUS.INACTIVE);
            if (!templateReg.isPresent()) {
                logger.info("No Template Found With %s", REGISTER_USER.name());
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
                logger.info("No Template Found With %s", REGISTER_USER.name());
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
}

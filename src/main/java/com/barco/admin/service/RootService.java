package com.barco.admin.service;

import com.barco.admin.service.impl.QueryService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.common.utility.excel.SheetFiled;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.*;
import com.barco.model.pojo.*;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.ModelUtil;
import com.barco.model.util.lookup.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.*;
import java.util.stream.Collectors;

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
        if (data.containsKey(QueryService.LINK_ID) && !BarcoUtil.isNull(data.get(QueryService.LINK_ID))) {
            linkRPUResponse.setLinkId(Long.valueOf(data.get(QueryService.LINK_ID).toString()));
        }
        if (data.containsKey(QueryService.LINK_DATA) && !BarcoUtil.isNull(data.get(QueryService.LINK_DATA))) {
            linkRPUResponse.setLinkData(data.get(QueryService.LINK_DATA).toString());
        }
        if (data.containsKey(QueryService.LINKED) && !BarcoUtil.isNull(data.get(QueryService.LINKED))) {
            linkRPUResponse.setLinked(Boolean.valueOf(data.get(QueryService.LINKED).toString()));
        }
        if (data.containsKey(QueryService.ENV_VALUE) && !BarcoUtil.isNull(data.get(QueryService.ENV_VALUE))) {
            linkRPUResponse.setEnvValue(data.get(QueryService.ENV_VALUE).toString());
        }
        if (data.containsKey(QueryService.TOKEN_ID) && !BarcoUtil.isNull(data.get(QueryService.TOKEN_ID))) {
            linkRPUResponse.setTokenId(data.get(QueryService.TOKEN_ID).toString());
        }
        if (data.containsKey(QueryService.ACCESS_TOKEN) && !BarcoUtil.isNull(data.get(QueryService.ACCESS_TOKEN))) {
            linkRPUResponse.setAccessToken(data.get(QueryService.ACCESS_TOKEN).toString());
        }
        if (data.containsKey(QueryService.EXPIRE_TIME) && !BarcoUtil.isNull(data.get(QueryService.EXPIRE_TIME))) {
            linkRPUResponse.setExpireTime(data.get(QueryService.EXPIRE_TIME).toString());
        }
        if (data.containsKey(QueryService.LINK_STATUS) && !BarcoUtil.isNull(data.get(QueryService.LINK_STATUS))) {
            linkRPUResponse.setLinkStatus(APPLICATION_STATUS.getStatusByLookupCode(Long.valueOf(data.get(QueryService.LINK_STATUS).toString())));
        } else {
            linkRPUResponse.setLinkStatus(APPLICATION_STATUS.getStatusByLookupCode(status.getLookupCode()));
        }
        linkRPUResponse.setProfile(new ProfileResponse(Long.valueOf(data.get(QueryService.PROFILE_ID).toString()),
            data.get(QueryService.PROFILE_NAME).toString(), data.get(QueryService.DESCRIPTION).toString()));
        return linkRPUResponse;
    }

    /**
     * Method use to control link section
     * @param data
     * @return ControlLinkSectionResponse
     * */
    public default ControlLinkSectionResponse getControlLinkSectionResponse(HashMap<String, Object> data) {
        ControlLinkSectionResponse controlLinkSectionResponse = new ControlLinkSectionResponse();
        if (data.containsKey(QueryService.ID)) {
            controlLinkSectionResponse.setId(Long.valueOf(data.get(QueryService.ID).toString()));
        }
        if (data.containsKey(QueryService.SECTION_NAME) && !BarcoUtil.isNull(data.get(QueryService.SECTION_NAME))) {
            controlLinkSectionResponse.setSectionName(data.get(QueryService.SECTION_NAME).toString());
        }
        if (data.containsKey(QueryService.DESCRIPTION) && !BarcoUtil.isNull(data.get(QueryService.DESCRIPTION))) {
            controlLinkSectionResponse.setDescription(data.get(QueryService.DESCRIPTION).toString());
        }
        if (data.containsKey(QueryService.STATUS) && !BarcoUtil.isNull(data.get(QueryService.STATUS))) {
            controlLinkSectionResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(Long.valueOf(data.get(QueryService.STATUS).toString())));
        }
        if (data.containsKey(QueryService.LINK_STATUS) && !BarcoUtil.isNull(data.get(QueryService.LINK_STATUS))) {
            controlLinkSectionResponse.setLinkedControl(Boolean.valueOf(data.get(QueryService.LINK_STATUS).toString()));
        }
        if (data.containsKey(QueryService.LINK_SECTION_ID) && !BarcoUtil.isNull(data.get(QueryService.LINK_SECTION_ID))) {
            controlLinkSectionResponse.setLinkSectionId(Long.valueOf(data.get(QueryService.LINK_SECTION_ID).toString()));
        }
        if (data.containsKey(QueryService.CONTROL_ORDER) && !BarcoUtil.isNull(data.get(QueryService.CONTROL_ORDER))) {
            controlLinkSectionResponse.setControlOrder(Long.valueOf(data.get(QueryService.CONTROL_ORDER).toString()));
        }
        if (data.containsKey(QueryService.FILED_WIDTH) && !BarcoUtil.isNull(data.get(QueryService.FILED_WIDTH))) {
            controlLinkSectionResponse.setFieldWidth(Long.valueOf(data.get(QueryService.FILED_WIDTH).toString()));
        }
        return controlLinkSectionResponse;
    }

    /**
     * Method use to get section link control
     * @param
     * @return SectionLinkControlResponse
     * */
    public default SectionLinkControlResponse getSectionLinkControlResponse(HashMap<String, Object> data, LookupDataCacheService lookupDataCacheService) {
        SectionLinkControlResponse sectionLinkControlResponse = new SectionLinkControlResponse();
        if (data.containsKey(QueryService.ID)) {
            sectionLinkControlResponse.setId(Long.valueOf(data.get(QueryService.ID).toString()));
        }
        if (data.containsKey(QueryService.CONTROL_NAME) && !BarcoUtil.isNull(data.get(QueryService.CONTROL_NAME))) {
            sectionLinkControlResponse.setControlName(data.get(QueryService.CONTROL_NAME).toString());
        }
        if (data.containsKey(QueryService.FIELD_TYPE) && !BarcoUtil.isNull(data.get(QueryService.FIELD_TYPE))) {
            GLookup fieldType = GLookup.getGLookup(lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
                FIELD_TYPE.getName(), Long.valueOf(data.get(QueryService.FIELD_TYPE).toString())));
            sectionLinkControlResponse.setFieldType(fieldType);
        }
        if (data.containsKey(QueryService.STATUS) && !BarcoUtil.isNull(data.get(QueryService.STATUS))) {
            sectionLinkControlResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(Long.valueOf(data.get(QueryService.STATUS).toString())));
        }
        if (data.containsKey(QueryService.LINK_STATUS) && !BarcoUtil.isNull(data.get(QueryService.LINK_STATUS))) {
            sectionLinkControlResponse.setLinkedSection(Boolean.valueOf(data.get(QueryService.LINK_STATUS).toString()));
        }
        if (data.containsKey(QueryService.LINK_CONTROL_ID) && !BarcoUtil.isNull(data.get(QueryService.LINK_CONTROL_ID))) {
            sectionLinkControlResponse.setLinkControlId(Long.valueOf(data.get(QueryService.LINK_CONTROL_ID).toString()));
        }
        if (data.containsKey(QueryService.CONTROL_ORDER) && !BarcoUtil.isNull(data.get(QueryService.CONTROL_ORDER))) {
            sectionLinkControlResponse.setControlOrder(Long.valueOf(data.get(QueryService.CONTROL_ORDER).toString()));
        }
        if (data.containsKey(QueryService.FILED_WIDTH) && !BarcoUtil.isNull(data.get(QueryService.FILED_WIDTH))) {
            sectionLinkControlResponse.setFieldWidth(Long.valueOf(data.get(QueryService.FILED_WIDTH).toString()));
        }
        return sectionLinkControlResponse;
    }

    /**
     * Method use to link section with form
     * @param data
     * @param lookupDataCacheService
     * @return SectionLinkFormResponse
     * */
    public default SectionLinkFormResponse getSectionLinkFromResponse(HashMap<String, Object> data, LookupDataCacheService lookupDataCacheService) {
        SectionLinkFormResponse sectionLinkFormResponse = new SectionLinkFormResponse();
        if (data.containsKey(QueryService.ID)) {
            sectionLinkFormResponse.setId(Long.valueOf(data.get(QueryService.ID).toString()));
        }
        if (data.containsKey(QueryService.FORM_NAME) && !BarcoUtil.isNull(data.get(QueryService.FORM_NAME))) {
            sectionLinkFormResponse.setFormName(data.get(QueryService.FORM_NAME).toString());
        }
        if (data.containsKey(QueryService.FORM_TYPE) && !BarcoUtil.isNull(data.get(QueryService.FORM_TYPE))) {
            GLookup formType = GLookup.getGLookup(lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
                FORM_TYPE.getName(), Long.valueOf(data.get(QueryService.FORM_TYPE).toString())));
            sectionLinkFormResponse.setFormType(formType);
        }
        if (data.containsKey(QueryService.STATUS) && !BarcoUtil.isNull(data.get(QueryService.STATUS))) {
            sectionLinkFormResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(Long.valueOf(data.get(QueryService.STATUS).toString())));
        }
        if (data.containsKey(QueryService.LINK_STATUS) && !BarcoUtil.isNull(data.get(QueryService.LINK_STATUS))) {
            sectionLinkFormResponse.setLinkStatus(Boolean.valueOf(data.get(QueryService.LINK_STATUS).toString()));
        }
        if (data.containsKey(QueryService.LINK_FORM_ID) && !BarcoUtil.isNull(data.get(QueryService.LINK_FORM_ID))) {
            sectionLinkFormResponse.setSectionLinkForm(Long.valueOf(data.get(QueryService.LINK_FORM_ID).toString()));
        }
        if (data.containsKey(QueryService.SECTION_ORDER) && !BarcoUtil.isNull(data.get(QueryService.SECTION_ORDER))) {
            sectionLinkFormResponse.setSectionOrder(Long.valueOf(data.get(QueryService.SECTION_ORDER).toString()));
        }
        return sectionLinkFormResponse;
    }

    /**
     * Method use to link section with form
     * @param data
     * @return SectionLinkFormResponse
     * */
    public default FormLinkSectionResponse getFormLinkSectionResponse(HashMap<String, Object> data) {
        FormLinkSectionResponse formLinkSectionResponse = new FormLinkSectionResponse();
        if (data.containsKey(QueryService.ID)) {
            formLinkSectionResponse.setId(Long.valueOf(data.get(QueryService.ID).toString()));
        }
        if (data.containsKey(QueryService.SECTION_NAME) && !BarcoUtil.isNull(data.get(QueryService.SECTION_NAME))) {
            formLinkSectionResponse.setSectionName(data.get(QueryService.SECTION_NAME).toString());
        }
        if (data.containsKey(QueryService.DESCRIPTION) && !BarcoUtil.isNull(data.get(QueryService.DESCRIPTION))) {
            formLinkSectionResponse.setDescription(data.get(QueryService.DESCRIPTION).toString());
        }
        if (data.containsKey(QueryService.STATUS) && !BarcoUtil.isNull(data.get(QueryService.STATUS))) {
            formLinkSectionResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(Long.valueOf(data.get(QueryService.STATUS).toString())));
        }
        if (data.containsKey(QueryService.LINK_STATUS) && !BarcoUtil.isNull(data.get(QueryService.LINK_STATUS))) {
            formLinkSectionResponse.setLinkStatus(Boolean.valueOf(data.get(QueryService.LINK_STATUS).toString()));
        }
        if (data.containsKey(QueryService.LINK_SECTION_ID) && !BarcoUtil.isNull(data.get(QueryService.LINK_SECTION_ID))) {
            formLinkSectionResponse.setFormLinkSection(Long.valueOf(data.get(QueryService.LINK_SECTION_ID).toString()));
        }
        if (data.containsKey(QueryService.SECTION_ORDER) && !BarcoUtil.isNull(data.get(QueryService.SECTION_ORDER))) {
            formLinkSectionResponse.setSectionOrder(Long.valueOf(data.get(QueryService.SECTION_ORDER).toString()));
        }
        return formLinkSectionResponse;
    }

    /**
     * Method use to fetch link form with stt
     * @param data
     * @return FormLinkSourceTaskTypeResponse
     * */
    public default FormLinkSourceTaskTypeResponse getFormLinkSourceTaskTypeResponse(HashMap<String, Object> data, LookupDataCacheService lookupDataCacheService) {
        FormLinkSourceTaskTypeResponse formLinkSourceTaskTypeResponse = new FormLinkSourceTaskTypeResponse();
        if (data.containsKey(QueryService.ID)) {
            formLinkSourceTaskTypeResponse.setId(Long.valueOf(data.get(QueryService.ID).toString()));
        }
        if (data.containsKey(QueryService.SERVICE_NAME) && !BarcoUtil.isNull(data.get(QueryService.SERVICE_NAME))) {
            formLinkSourceTaskTypeResponse.setServiceName(data.get(QueryService.SERVICE_NAME).toString());
        }
        if (data.containsKey(QueryService.TASK_TYPE) && !BarcoUtil.isNull(data.get(QueryService.TASK_TYPE))) {
            GLookup formType = GLookup.getGLookup(lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
                TASK_TYPE.getName(), Long.valueOf(data.get(QueryService.TASK_TYPE).toString())));
            formLinkSourceTaskTypeResponse.setTaskType(formType);
        }
        if (data.containsKey(QueryService.STATUS) && !BarcoUtil.isNull(data.get(QueryService.STATUS))) {
            formLinkSourceTaskTypeResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(Long.valueOf(data.get(QueryService.STATUS).toString())));
        }
        if (data.containsKey(QueryService.LINK_STATUS) && !BarcoUtil.isNull(data.get(QueryService.LINK_STATUS))) {
            formLinkSourceTaskTypeResponse.setLinkStatus(Boolean.valueOf(data.get(QueryService.LINK_STATUS).toString()));
        }
        if (data.containsKey(QueryService.LINK_STT_ID) && !BarcoUtil.isNull(data.get(QueryService.LINK_STT_ID))) {
            formLinkSourceTaskTypeResponse.setFormLinkStt(Long.valueOf(data.get(QueryService.LINK_STT_ID).toString()));
        }
        return formLinkSourceTaskTypeResponse;
    }

    /**
     * Method use to fetch link form with stt
     * @param data
     * @return SourceTaskTypeLinkFormResponse
     * */
    public default SourceTaskTypeLinkFormResponse getSourceTaskTypeLinkFormResponse(HashMap<String, Object> data, LookupDataCacheService lookupDataCacheService) {
        SourceTaskTypeLinkFormResponse sourceTaskTypeLinkFormResponse = new SourceTaskTypeLinkFormResponse();
        if (data.containsKey(QueryService.ID)) {
            sourceTaskTypeLinkFormResponse.setId(Long.valueOf(data.get(QueryService.ID).toString()));
        }
        if (data.containsKey(QueryService.FORM_NAME) && !BarcoUtil.isNull(data.get(QueryService.FORM_NAME))) {
            sourceTaskTypeLinkFormResponse.setFormName(data.get(QueryService.FORM_NAME).toString());
        }
        if (data.containsKey(QueryService.SERVICE_ID) && !BarcoUtil.isNull(data.get(QueryService.SERVICE_ID))) {
            sourceTaskTypeLinkFormResponse.setServiceId(data.get(QueryService.SERVICE_ID).toString());
        }
        if (data.containsKey(QueryService.FORM_TYPE) && !BarcoUtil.isNull(data.get(QueryService.FORM_TYPE))) {
            GLookup formType = GLookup.getGLookup(lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
                FORM_TYPE.getName(), Long.valueOf(data.get(QueryService.FORM_TYPE).toString())));
            sourceTaskTypeLinkFormResponse.setFormType(formType);
        }
        if (data.containsKey(QueryService.STATUS) && !BarcoUtil.isNull(data.get(QueryService.STATUS))) {
            sourceTaskTypeLinkFormResponse.setStatus(APPLICATION_STATUS.getStatusByLookupCode(Long.valueOf(data.get(QueryService.STATUS).toString())));
        }
        if (data.containsKey(QueryService.LINK_STATUS) && !BarcoUtil.isNull(data.get(QueryService.LINK_STATUS))) {
            sourceTaskTypeLinkFormResponse.setLinkStatus(Boolean.valueOf(data.get(QueryService.LINK_STATUS).toString()));
        }
        if (data.containsKey(QueryService.LINK_FORM_ID) && !BarcoUtil.isNull(data.get(QueryService.LINK_FORM_ID))) {
            sourceTaskTypeLinkFormResponse.setSttLinkForm(Long.valueOf(data.get(QueryService.LINK_FORM_ID).toString()));
        }
        return sourceTaskTypeLinkFormResponse;
    }

    /**
     * Method use to get the action user as response
     * @param appUser
     * */
    public default ActionByUser getActionUser(AppUser appUser) {
        ActionByUser actionByUser = new ActionByUser();
        actionByUser.setUuid(appUser.getUuid());
        actionByUser.setEmail(appUser.getEmail());
        actionByUser.setUsername(appUser.getUsername());
        return actionByUser;
    }

    /**
     * Method use to download template file
     * @param tempStoreDirectory
     * @param bulkExcel
     * @param sheetFiled
     * @return ByteArrayOutputStream
     * @throws Exception
     * */
    public default ByteArrayOutputStream downloadTemplateFile(String tempStoreDirectory, BulkExcel bulkExcel, SheetFiled sheetFiled) throws Exception {
        String basePath = tempStoreDirectory + File.separator;
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream inputStream = cl.getResourceAsStream(bulkExcel.BATCH);
        String fileUploadPath = basePath + System.currentTimeMillis()+bulkExcel.XLSX_EXTENSION;
        FileOutputStream fileOut = new FileOutputStream(fileUploadPath);
        IOUtils.copy(inputStream, fileOut);
        // after copy the stream into file close
        inputStream.close();
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
     * Method use to send notification
     * @param title
     * @param message
     * @param appUser
     * @throws Exception
     * @throws Exception
     * */
    public default void sendNotification(String title, String message, AppUser appUser,
        LookupDataCacheService lookupDataCacheService, NotificationService notificationService) throws Exception {
        LookupDataResponse notificationTime = lookupDataCacheService.getParentLookupDataByParentLookupType(LookupUtil.NOTIFICATION_DISAPPEAR_TIME);
        notificationService.addNotification(new NotificationRequest(new MessageRequest(title, message),
            NOTIFICATION_TYPE.USER_NOTIFICATION.getLookupCode(), ModelUtil.addDays(new Timestamp(System.currentTimeMillis()),
            Long.valueOf(notificationTime.getLookupValue())), NOTIFICATION_STATUS.UNREAD.getLookupCode()), appUser);
    }

    /**
     * Method use to get the db lookup
     * @param lookupData
     * @return GLookup
     * */
    public default GLookup getDBLoopUp(Optional<LookupData> lookupData) {
        return lookupData.map(data -> new GLookup(data.getLookupType(), data.getLookupCode(), data.getLookupValue()))
            .orElseGet(() -> (GLookup) BarcoUtil.NULL);
    }

    /***
     * Method use to get the env variable
     * @param envVariables
     * @return EnVariablesResponse
     * */
    public default EnVariablesResponse getEnVariablesResponse(EnvVariables envVariables) {
        EnVariablesResponse enVariablesResponse = new EnVariablesResponse();
        enVariablesResponse.setUuid(envVariables.getUuid());
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
     * Method use to get the templateReg response
     * @param templateReg
     * @return TemplateRegResponse
     * */
    public default TemplateRegResponse getTemplateRegResponse(TemplateReg templateReg) {
        TemplateRegResponse templateRegResponse = new TemplateRegResponse();
        templateRegResponse.setUuid(templateReg.getUuid());
        templateRegResponse.setTemplateName(templateReg.getTemplateName());
        templateRegResponse.setDescription(templateReg.getDescription());
        templateRegResponse.setTemplateContent(templateReg.getTemplateContent());
        templateRegResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(templateReg.getStatus().getLookupType()));
        templateRegResponse.setCreatedBy(getActionUser(templateReg.getCreatedBy()));
        templateRegResponse.setUpdatedBy(getActionUser(templateReg.getUpdatedBy()));
        templateRegResponse.setDateUpdated(templateReg.getDateUpdated());
        templateRegResponse.setDateCreated(templateReg.getDateCreated());
        return templateRegResponse;
    }

    /**
     * Method use to get the queryInquiry response
     * @param queryInquiry
     * @return QueryInquiryResponse
     * */
    public default QueryInquiryResponse getQueryInquiryResponse(QueryInquiry queryInquiry) {
        QueryInquiryResponse queryInquiryResponse = new QueryInquiryResponse();
        queryInquiryResponse.setUuid(queryInquiry.getUuid());
        queryInquiryResponse.setName(queryInquiry.getName());
        queryInquiryResponse.setDescription(queryInquiry.getDescription());
        queryInquiryResponse.setQuery(queryInquiry.getQuery());
        queryInquiryResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(queryInquiry.getStatus().getLookupType()));
        queryInquiryResponse.setCreatedBy(getActionUser(queryInquiry.getCreatedBy()));
        queryInquiryResponse.setUpdatedBy(getActionUser(queryInquiry.getUpdatedBy()));
        queryInquiryResponse.setDateUpdated(queryInquiry.getDateUpdated());
        queryInquiryResponse.setDateCreated(queryInquiry.getDateCreated());
        return queryInquiryResponse;
    }

    /**
     * Method use to convert source kafka task type to kafka task type resposne
     * @param kafkaTaskType
     * @return kafkaTaskTypeResponse
     * **/
    public default KafkaTaskTypeResponse getKafkaTaskTypeResponse(KafkaTaskType kafkaTaskType) {
        KafkaTaskTypeResponse kafkaTaskTypeResponse = new KafkaTaskTypeResponse();
        kafkaTaskTypeResponse.setKafkaId(kafkaTaskType.getId());
        kafkaTaskTypeResponse.setServiceUrl(kafkaTaskType.getServiceUrl());
        kafkaTaskTypeResponse.setTopicName(kafkaTaskType.getTopicName());
        kafkaTaskTypeResponse.setNumPartitions(kafkaTaskType.getNumPartitions());
        kafkaTaskTypeResponse.setTopicPattern(kafkaTaskType.getTopicPattern());
        return kafkaTaskTypeResponse;
    }

    /**
     * Method use to get api task type response
     * @param apiTaskType
     * @return ApiTaskTypeResponse
     * */
    public default ApiTaskTypeResponse getApiTaskTypeResponse(ApiTaskType apiTaskType, LookupDataCacheService lookupDataCacheService) {
        ApiTaskTypeResponse apiTaskTypeResponse = new ApiTaskTypeResponse();
        apiTaskTypeResponse.setApiTaskTypeId(apiTaskType.getId());
        apiTaskTypeResponse.setApiUrl(apiTaskType.getApiUrl());
        apiTaskTypeResponse.setHttpMethod(GLookup.getGLookup(lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
            REQUEST_METHOD.getName(), Long.valueOf(apiTaskType.getHttpMethod().ordinal()))));
        return apiTaskTypeResponse;
    }

    /**
     * Method us to get the lookupData
     * @param lookupData
     * */
    public default LookupDataResponse getLookupDataDetail(LookupData lookupData) {
        LookupDataResponse parentLookupData = new LookupDataResponse();
        parentLookupData = this.fillLookupDataResponse(lookupData, parentLookupData, false);
        if (!BarcoUtil.isNull(lookupData.getLookupChildren()) && !lookupData.getLookupChildren().isEmpty()) {
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
    public default LookupDataResponse fillLookupDataResponse(LookupData lookupData, LookupDataResponse lookupDataResponse, Boolean isFull) {
        lookupDataResponse.setUuid(lookupData.getUuid());
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

    /***
     * Method use to get the credential detail
     * @param credential
     * @return CredentialResponse
     * */
    public default CredentialResponse getCredentialResponse(Credential credential) {
        CredentialResponse credentialResponse = new CredentialResponse();
        credentialResponse.setUuid(credential.getUuid());
        credentialResponse.setName(credential.getName());
        credentialResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(credential.getStatus().getLookupType()));
        return credentialResponse;
    }

    /**
     * Method use to get app ser env
     * @param superAdmin
     * @param appUser
     * @param envVariables
     * */
    public default AppUserEnv getAppUserEnv(AppUser superAdmin, AppUser appUser, EnvVariables envVariables) {
        AppUserEnv appUserEnv = new AppUserEnv();
        appUserEnv.setCreatedBy(superAdmin);
        appUserEnv.setUpdatedBy(superAdmin);
        appUserEnv.setAppUser(appUser);
        appUserEnv.setEnvVariables(envVariables);
        appUserEnv.setStatus(APPLICATION_STATUS.ACTIVE);
        if (envVariables.getStatus().equals(APPLICATION_STATUS.INACTIVE) || appUser.getStatus().equals(APPLICATION_STATUS.INACTIVE)) {
            appUserEnv.setStatus(APPLICATION_STATUS.INACTIVE);
        }
        return appUserEnv;
    }

    /**
     * Method use to get app ser env
     * @param superAdmin
     * @param appUser
     * @param eventBridge
     * */
    public default AppUserEventBridge getAppUserEventBridge(AppUser superAdmin, AppUser appUser, EventBridge eventBridge) {
        AppUserEventBridge appUserEventBridge = new AppUserEventBridge();
        appUserEventBridge.setCreatedBy(superAdmin);
        appUserEventBridge.setUpdatedBy(superAdmin);
        appUserEventBridge.setAppUser(appUser);
        appUserEventBridge.setEventBridge(eventBridge);
        appUserEventBridge.setTokenId(UUID.randomUUID().toString());
        appUserEventBridge.setStatus(APPLICATION_STATUS.ACTIVE);
        if (eventBridge.getStatus().equals(APPLICATION_STATUS.INACTIVE) || appUser.getStatus().equals(APPLICATION_STATUS.INACTIVE)) {
            appUserEventBridge.setStatus(APPLICATION_STATUS.INACTIVE);
        }
        return appUserEventBridge;
    }

    /**
     * Method use to get dashboard setting
     * @param payload
     * @param adminUser
     * */
    public default DashboardSetting getDashboardSetting(DashboardSettingRequest payload, AppUser adminUser) {
        DashboardSetting dashboardSetting = new DashboardSetting();
        dashboardSetting.setName(payload.getName());
        dashboardSetting.setDescription(payload.getDescription());
        dashboardSetting.setBoardType(DASHBOARD_TYPE.getByLookupCode(payload.getBoardType()));
        dashboardSetting.setIframe(UI_LOOKUP.getByLookupCode(payload.getIframe()));
        dashboardSetting.setDashboardUrl(payload.getDashboardUrl());
        dashboardSetting.setStatus(APPLICATION_STATUS.ACTIVE);
        dashboardSetting.setCreatedBy(adminUser);
        dashboardSetting.setUpdatedBy(adminUser);
        return dashboardSetting;
    }

    /**
     * Method use to get dashboard setting
     * @param dashboardSetting
     * @return DashboardSettingResponse
     * */
    public default DashboardSettingResponse getDashboardSettingResponse(DashboardSetting dashboardSetting) {
        DashboardSettingResponse dashboardSettingResponse = new DashboardSettingResponse();
        dashboardSettingResponse.setId(dashboardSetting.getId());
        dashboardSettingResponse.setName(dashboardSetting.getName());
        dashboardSettingResponse.setDescription(dashboardSetting.getDescription());
        dashboardSettingResponse.setDashboardUrl(dashboardSetting.getDashboardUrl());
        dashboardSettingResponse.setIframe(UI_LOOKUP.getStatusByLookupType(dashboardSetting.getIframe().getLookupType()));
        dashboardSettingResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(dashboardSetting.getStatus().getLookupType()));
        dashboardSettingResponse.setCreatedBy(getActionUser(dashboardSetting.getCreatedBy()));
        dashboardSettingResponse.setUpdatedBy(getActionUser(dashboardSetting.getUpdatedBy()));
        dashboardSettingResponse.setDateUpdated(dashboardSetting.getDateUpdated());
        dashboardSettingResponse.setDateCreated(dashboardSetting.getDateCreated());
        return dashboardSettingResponse;
    }

    /**
     * Method use to validate the link EventBridge payload
     * @param payload
     * @return AppResponse
     * */
    public default AppResponse validateLinkEventBridgePayload(LinkEBURequest payload) {
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getAppUserId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APP_USER_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getLinked())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LINKED_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    /**
     * Method use to create the credential from private key
     * @param credential
     * @return String
     * */
    public default String getCredentialPrivateKey(Credential credential) {
        String credJsonStr = new String(Base64.getDecoder().decode(credential.getContent().getBytes()));
        JsonObject jsonObject = JsonParser.parseString(credJsonStr).getAsJsonObject();
        return jsonObject.get("priKey").getAsString();
    }

    /**
     * Method give one year from today
     * @return Timestamp
     * */
    public default Timestamp getOneYearFromNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * Method use to get the link event bridge count
     * @param eventBridge
     * @return Integer
     * */
    public default Integer getLinkEventBridgeCount(EventBridge eventBridge) {
        Integer totalCount = 0;
        if (!BarcoUtil.isNull(eventBridge.getReportPdfBridgeSettings()) && !eventBridge.getReportPdfBridgeSettings().isEmpty()) {
            totalCount += eventBridge.getReportPdfBridgeSettings().size();
        }
        if (!BarcoUtil.isNull(eventBridge.getReportXlsxBridgeSettings()) && !eventBridge.getReportXlsxBridgeSettings().isEmpty()) {
            totalCount += eventBridge.getReportXlsxBridgeSettings().size();
        }
        if (!BarcoUtil.isNull(eventBridge.getReportCsvBridgeSettings()) && !eventBridge.getReportCsvBridgeSettings().isEmpty()) {
            totalCount += eventBridge.getReportCsvBridgeSettings().size();
        }
        if (!BarcoUtil.isNull(eventBridge.getReportDataBridgeSettings()) && !eventBridge.getReportDataBridgeSettings().isEmpty()) {
            totalCount += eventBridge.getReportDataBridgeSettings().size();
        }
        if (!BarcoUtil.isNull(eventBridge.getReportFistDimBridgeSettings()) && !eventBridge.getReportFistDimBridgeSettings().isEmpty()) {
            totalCount += eventBridge.getReportFistDimBridgeSettings().size();
        }
        if (!BarcoUtil.isNull(eventBridge.getReportSecDimBridgeSettings()) && !eventBridge.getReportSecDimBridgeSettings().isEmpty()) {
            totalCount += eventBridge.getReportSecDimBridgeSettings().size();
        }
        return totalCount;
    }

    /**
     * Method use to null the report setting reference
     * @param eventBridge
     * */
    public default void nullifyReportSettingReferences(EventBridge eventBridge) {
        // null all event id for pdf
        if (!BarcoUtil.isNull(eventBridge.getReportPdfBridgeSettings()) && !eventBridge.getReportPdfBridgeSettings().isEmpty()) {
            eventBridge.getReportPdfBridgeSettings().stream()
            .map(reportSetting -> {
                reportSetting.setPdfBridge((EventBridge) BarcoUtil.NULL);
                return reportSetting;
            });
        }
        // null all event id for xlsx
        if (!BarcoUtil.isNull(eventBridge.getReportXlsxBridgeSettings()) && !eventBridge.getReportXlsxBridgeSettings().isEmpty()) {
            eventBridge.getReportXlsxBridgeSettings().stream()
            .map(reportSetting -> {
                reportSetting.setXlsxBridge((EventBridge) BarcoUtil.NULL);
                return reportSetting;
            });
        }
        // null all event id for csv
        if (!BarcoUtil.isNull(eventBridge.getReportCsvBridgeSettings()) && !eventBridge.getReportCsvBridgeSettings().isEmpty()) {
            eventBridge.getReportCsvBridgeSettings().stream()
            .map(reportSetting -> {
                reportSetting.setCsvBridge((EventBridge) BarcoUtil.NULL);
                return reportSetting;
            });
        }
        // null all event id for data
        if (!BarcoUtil.isNull(eventBridge.getReportDataBridgeSettings()) && !eventBridge.getReportDataBridgeSettings().isEmpty()) {
            eventBridge.getReportDataBridgeSettings().stream()
            .map(reportSetting -> {
                reportSetting.setDataBridge((EventBridge) BarcoUtil.NULL);
                return reportSetting;
            });
        }
        // null all event id for fist dim
        if (!BarcoUtil.isNull(eventBridge.getReportFistDimBridgeSettings()) && !eventBridge.getReportFistDimBridgeSettings().isEmpty()) {
            eventBridge.getReportFistDimBridgeSettings().stream()
            .map(reportSetting -> {
                reportSetting.setFirstDimensionBridge((EventBridge) BarcoUtil.NULL);
                return reportSetting;
            });
        }
        // null all event id for sec dim
        if (!BarcoUtil.isNull(eventBridge.getReportSecDimBridgeSettings()) && !eventBridge.getReportSecDimBridgeSettings().isEmpty()) {
            eventBridge.getReportSecDimBridgeSettings().stream()
            .map(reportSetting -> {
                reportSetting.setSecondDimensionBridge((EventBridge) BarcoUtil.NULL);
                return reportSetting;
            });
        }
    }

    /**
     * Method use to convert the source task to source task response
     * @param sourceTask
     * @return SourceTaskResponse
     * */
    public default SourceTaskResponse getSourceTaskResponse(SourceTask sourceTask) {
        SourceTaskResponse sourceTaskResponse = new SourceTaskResponse();
        sourceTaskResponse.setId(sourceTask.getId());
        sourceTaskResponse.setTaskName(sourceTask.getTaskName());
        sourceTaskResponse.setDescription(sourceTask.getDescription());
        sourceTaskResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(sourceTask.getStatus().getLookupType()));
        sourceTaskResponse.setCreatedBy(getActionUser(sourceTask.getCreatedBy()));
        sourceTaskResponse.setUpdatedBy(getActionUser(sourceTask.getUpdatedBy()));
        sourceTaskResponse.setDateUpdated(sourceTask.getDateUpdated());
        sourceTaskResponse.setDateCreated(sourceTask.getDateCreated());
        if (!BarcoUtil.isNull(sourceTask.getSourceTaskType())) {
            SourceTaskTypeResponse sourceTaskTypeResponse = new SourceTaskTypeResponse();
            sourceTaskTypeResponse.setId(sourceTask.getSourceTaskType().getId());
            sourceTaskTypeResponse.setServiceName(sourceTask.getSourceTaskType().getServiceName());
            sourceTaskTypeResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(sourceTask.getStatus().getLookupType()));
            sourceTaskResponse.setLinkStt(sourceTaskTypeResponse);
        }
        if (!BarcoUtil.isNull(sourceTask.getGenForm())) {
            FormResponse formResponse = new FormResponse();
            formResponse.setId(sourceTask.getGenForm().getId());
            formResponse.setFormName(sourceTask.getGenForm().getFormName());
            formResponse.setServiceId(sourceTask.getGenForm().getServiceId());
            formResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(sourceTask.getStatus().getLookupType()));
            sourceTaskResponse.setLinkForm(formResponse);
        }
        return sourceTaskResponse;
    }

    /**
     * Method use to get kafka task type
     * @param kafkaTaskTypeRequest
     * @param adminUser
     * @return ApiTaskType
     * */
    public default KafkaTaskType getKafkaTaskType(
        KafkaTaskTypeRequest kafkaTaskTypeRequest, Optional<AppUser> adminUser) {
        KafkaTaskType kafkaTaskType = new KafkaTaskType();
        kafkaTaskType.setServiceUrl(kafkaTaskTypeRequest.getServiceUrl());
        kafkaTaskType.setNumPartitions(kafkaTaskTypeRequest.getNumPartitions());
        kafkaTaskType.setTopicName(kafkaTaskTypeRequest.getTopicName());
        kafkaTaskType.setTopicPattern(kafkaTaskTypeRequest.getTopicPattern());
        kafkaTaskType.setStatus(APPLICATION_STATUS.ACTIVE);
        kafkaTaskType.setCreatedBy(adminUser.get());
        kafkaTaskType.setUpdatedBy(adminUser.get());
        return kafkaTaskType;
    }

    /**
     * Method use to get api task type
     * @param apiTaskTypeRequest
     * @param adminUser
     * @return ApiTaskType
     * */
    public default ApiTaskType getApiTaskType(
        ApiTaskTypeRequest apiTaskTypeRequest, Optional<AppUser> adminUser) {
        ApiTaskType apiTaskType = new ApiTaskType();
        apiTaskType.setApiUrl(apiTaskTypeRequest.getApiUrl());
        apiTaskType.setHttpMethod(apiTaskTypeRequest.getHttpMethod());
        apiTaskType.setStatus(APPLICATION_STATUS.ACTIVE);
        apiTaskType.setCreatedBy(adminUser.get());
        apiTaskType.setUpdatedBy(adminUser.get());
        return apiTaskType;
    }

    /**
     * Method use to action on link source task with user
     * @param sourceTaskType
     * @param adminUser
     * */
    public default void actionAppUserLinkSourceTaskTypes(SourceTaskType sourceTaskType, AppUser adminUser) {
        sourceTaskType.getAppUserLinkSourceTaskTypes().stream()
            .filter(appUserLinkStt -> !appUserLinkStt.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(appUserLinkStt -> {
                appUserLinkStt.setStatus(sourceTaskType.getStatus());
                appUserLinkStt.setUpdatedBy(adminUser);
                return appUserLinkStt;
            });
    }

    /**
     * Method use to action on link source task with form
     * @param sourceTaskType
     * @param adminUser
     * */
    public default void actionGenFormLinkSourceTaskTypes(SourceTaskType sourceTaskType, AppUser adminUser) {
        sourceTaskType.getGenFormLinkSourceTaskTypes().stream()
            .filter(genFormLinkStt -> !genFormLinkStt.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(genFormLinkStt -> {
                genFormLinkStt.setStatus(sourceTaskType.getStatus());
                genFormLinkStt.setUpdatedBy(adminUser);
                return genFormLinkStt;
            });
    }

    /**
     * Method use to action on gen form link source task types
     * @param genForm
     * @param appUser
     * */
    public default void actionOnGenFormLinkSourceTaskTypes(GenForm genForm, AppUser appUser) {
        genForm.getGenFormLinkSourceTaskTypes().stream()
            .filter(genFormLinkStt -> !genFormLinkStt.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(genFormLinkStt -> {
                genFormLinkStt.setStatus(genForm.getStatus());
                genFormLinkStt.setUpdatedBy(appUser);
                return genFormLinkStt;
            });
    }

    /***
     * Method use to action on gen section link gen from
     * @param genForm
     * @param appUser
     * */
    public default void actionOnGenSectionLinkGenForms(GenForm genForm, AppUser appUser) {
        genForm.getGenSectionLinkGenForms().stream()
            .filter(genFormLinkSection -> !genFormLinkSection.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(genFormLinkSection -> {
                genFormLinkSection.setStatus(genForm.getStatus());
                genFormLinkSection.setUpdatedBy(appUser);
                return genFormLinkSection;
            });
    }

    /***
     * Method use to action on link report setting with gen form
     * @param genForm
     * @param appUser
     * */
    public default void actionOnReportSettingLinkGenForms(GenForm genForm, AppUser appUser) {
        genForm.getReportSettings().stream()
            .filter(reportSetting -> !reportSetting.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(reportSetting -> {
                reportSetting.setGenForm((GenForm) BarcoUtil.NULL);
                reportSetting.setUpdatedBy(appUser);
                return reportSetting;
            });
    }

    /**
     * Method use to action on gen section link gen from
     * @param genSection
     * @param appUser
     * */
    public default void actionOnGenSectionLinkGenForms(GenSection genSection, AppUser appUser) {
        genSection.getGenSectionLinkGenForms().stream()
            .filter(genFormLinkSection -> !genFormLinkSection.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(genFormLinkSection -> {
                genFormLinkSection.setStatus(genSection.getStatus());
                genFormLinkSection.setUpdatedBy(appUser);
                return genFormLinkSection;
            });
    }

    /**
     * Method use to action on gen control link gen sections
     * @param genSection
     * @param appUser
     * */
    public default void actionOnGenSectionsLinkGenControl(GenSection genSection, AppUser appUser) {
        genSection.getGenControlLinkGenSections().stream()
            .filter(genControlLinkGenSections -> !genControlLinkGenSections.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(genControlLinkGenSections -> {
                genControlLinkGenSections.setStatus(genSection.getStatus());
                genControlLinkGenSections.setUpdatedBy(appUser);
                return genControlLinkGenSections;
            });
    }

    /**
     * Method use to action on gen control link gen sections
     * @param genControl
     * @param appUser
     * */
    public default void actionOnGenControlLinkGenSections(GenControl genControl, AppUser appUser) {
        genControl.getGenControlLinkGenSections().stream()
            .filter(genControlLinkGenSections -> !genControlLinkGenSections.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(genControlLinkGenSections -> {
                genControlLinkGenSections.setStatus(genControl.getStatus());
                genControlLinkGenSections.setUpdatedBy(appUser);
                return genControlLinkGenSections;
            });
    }

    /**
     * Method using to delete the event bridge credential
     * @param credential
     * @return void
     * */
    public default void deleteEventBridgesCredential(Credential credential) {
        if (!BarcoUtil.isNull(credential.getEventBridges())) {
            credential.getEventBridges().stream()
                .filter(eventBridge -> !eventBridge.getStatus().equals(APPLICATION_STATUS.DELETE))
                .map(eventBridge -> {
                    eventBridge.setCredential((Credential) BarcoUtil.NULL);
                    // if Credential is deleted from the event bridge the delete the all app user event bridge
                    if (!BarcoUtil.isNull(eventBridge.getAppUserEventBridges())) {
                        eventBridge.getAppUserEventBridges().clear();
                    }
                    return eventBridge;
                });
        }
    }

    /**
     * Method using to delete the source credential
     * @param credential
     * @return void
     * */
    public default void deleteSourceTaskCredential(Credential credential) {
        if (!BarcoUtil.isNull(credential.getSourceTaskTypes())) {
            credential.getSourceTaskTypes().stream()
                .filter(sourceTaskTypes -> !sourceTaskTypes.getStatus().equals(APPLICATION_STATUS.DELETE))
                .map(sourceTaskTypes -> {
                    sourceTaskTypes.setCredential((Credential) BarcoUtil.NULL);
                    return sourceTaskTypes;
                });
        }
    }

}

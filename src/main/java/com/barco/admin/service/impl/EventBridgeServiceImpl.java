package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.EventBridgeService;
import com.barco.common.security.JwtUtils;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.common.utility.excel.SheetFiled;
import com.barco.common.utility.validation.EventBridgeValidation;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.LinkEBURequest;
import com.barco.model.dto.request.EventBridgeRequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.*;
import com.barco.model.pojo.*;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.AppUserEventBridgeRepository;
import com.barco.model.repository.CredentialRepository;
import com.barco.model.repository.EventBridgeRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.*;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class EventBridgeServiceImpl implements EventBridgeService {

    private Logger logger = LoggerFactory.getLogger(EventBridgeServiceImpl.class);

    @Value("${storage.efsFileDire}")
    private String tempStoreDirectory;

    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private QueryService queryService;
    @Autowired
    private EventBridgeRepository eventBridgeRepository;
    @Autowired
    private AppUserEventBridgeRepository appUserEventBridgeRepository;
    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private AppUserRepository appUserRepository;

    public EventBridgeServiceImpl() {}

    /**
     * Method use to add event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addEventBridge(EventBridgeRequest payload) throws Exception {
        logger.info("Request addEventBridge :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getBridgeUrl())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_URL_MISSING);
        } else if (BarcoUtil.isNull(payload.getHttpMethod())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.HTTP_METHOD_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getBridgeType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getCredentialId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_CREDENTIAL_MISSING);
        }
        Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatus(
            payload.getCredentialId(), adminUser.get().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!credential.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        }
        EventBridge eventBridge = new EventBridge();
        eventBridge.setName(payload.getName());
        eventBridge.setBridgeUrl(payload.getBridgeUrl());
        eventBridge.setHttpMethod(payload.getHttpMethod());
        eventBridge.setDescription(payload.getDescription());
        eventBridge.setBridgeType(EVENT_BRIDGE_TYPE.getByLookupCode(payload.getBridgeType()));
        eventBridge.setCredential(credential.get());
        eventBridge.setCreatedBy(adminUser.get());
        eventBridge.setUpdatedBy(adminUser.get());
        eventBridge.setStatus(APPLICATION_STATUS.ACTIVE);
        this.eventBridgeRepository.save(eventBridge);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, eventBridge.getId().toString()), payload);
    }

    /**
     * Method use to update event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateEventBridge(EventBridgeRequest payload) throws Exception {
        logger.info("Request updateEventBridge :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getBridgeUrl())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_URL_MISSING);
        } else if (BarcoUtil.isNull(payload.getHttpMethod())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.HTTP_METHOD_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getBridgeType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getCredentialId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_CREDENTIAL_MISSING);
        }
        Optional<Credential> credential = this.credentialRepository.findByIdAndUsernameAndStatus(
            payload.getCredentialId(), adminUser.get().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!credential.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        }
        Optional<EventBridge> eventBridge = this.eventBridgeRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!eventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_NOT_FOUND);
        }
        eventBridge.get().setName(payload.getName());
        eventBridge.get().setDescription(payload.getDescription());
        eventBridge.get().setBridgeUrl(payload.getBridgeUrl());
        eventBridge.get().setHttpMethod(payload.getHttpMethod());
        eventBridge.get().setBridgeType(EVENT_BRIDGE_TYPE.getByLookupCode(payload.getBridgeType()));
        eventBridge.get().setCredential(credential.get());
        eventBridge.get().setUpdatedBy(adminUser.get());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            // if status is in-active & delete then we have filter the role and show only those role in user detail
            eventBridge.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            eventBridge.get().getAppUserEventBridges().stream()
            .map(appUserEventBridge -> {
                appUserEventBridge.setStatus(eventBridge.get().getStatus());
                return appUserEventBridge;
            }).collect(Collectors.toList());
        }
        this.eventBridgeRepository.save(eventBridge.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()), payload);
    }

    /**
     * Method use to fetch all event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllEventBridge(EventBridgeRequest payload) throws Exception {
        logger.info("Request fetchAllEventBridge :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.eventBridgeRepository.findAllByCreatedByAndStatusNotOrderByDateCreatedDesc(
            adminUser.get(), APPLICATION_STATUS.DELETE).stream()
            .map(eventBridge -> {
                EventBridgeResponse eventBridgeResponse = this.getEventBridgeResponse(eventBridge);
                eventBridgeResponse.setTotalLinkCount(this.getLinkEventBridgeCount(eventBridge));
                return eventBridgeResponse;
            }).collect(Collectors.toList()));
    }

    /**
     * Method use to fetch all event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchEventBridgeById(EventBridgeRequest payload) throws Exception {
        logger.info("Request fetchEventBridgeById :- " + payload);
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_ID_MISSING);
        }
        Optional<EventBridge> eventBridge = this.eventBridgeRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!eventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.EVENT_BRIDGE_NOT_FOUND_WITH_ID, payload.getId().toString()));
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getEventBridgeResponse(eventBridge.get()));
    }

    /**
     * Method use to fetch all event bridge by bridge type
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchEventBridgeByBridgeType(EventBridgeRequest payload) throws Exception {
        logger.info("Request fetchEventBridgeByBridgeType :- " + payload);
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getBridgeType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_TYPE_MISSING);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            appUser.get().getAppUserEventBridges().stream()
            .filter(appUserEventBridge -> !appUserEventBridge.getStatus().equals(APPLICATION_STATUS.DELETE))
            .map(appUserEventBridge -> appUserEventBridge.getEventBridge())
            .filter(eventBridge -> eventBridge.getBridgeType().getLookupCode().equals(payload.getBridgeType()))
            .map(eventBridge -> this.getEventBridgeResponse(eventBridge))
            .collect(Collectors.toList()));
    }

    /**
     * Method use to delete event bridge by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteEventBridgeById(EventBridgeRequest payload) throws Exception {
        logger.info("Request deleteEventBridgeById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_ID_MISSING);
        }
        Optional<EventBridge> eventBridge = this.eventBridgeRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE);
        if (!eventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.EVENT_BRIDGE_NOT_FOUND, payload.getId().toString()));
        }
        this.nullifyReportSettingReferences(eventBridge.get());
        this.eventBridgeRepository.delete(eventBridge.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()), payload);
    }

    /**
     * Method use to delete all Event Bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllEventBridge(EventBridgeRequest payload) throws Exception {
        logger.info("Request deleteAllEventBridge :- " + payload);
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
        this.eventBridgeRepository.deleteAll(
            this.eventBridgeRepository.findAllByIdIn(payload.getIds())
            .stream().map(eventBridge -> {
                this.nullifyReportSettingReferences(eventBridge);
                return eventBridge;
            }).collect(Collectors.toList()));
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_DELETED_ALL, payload);
    }

    /**
     * Method use to fetch the link event bridge with user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchLinkEventBridgeWitUser(EventBridgeRequest payload) throws Exception {
        logger.info("Request fetchLinkEventBridgeWitUser :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_ID_MISSING);
        }
        Optional<EventBridge> eventBridge = this.eventBridgeRepository.findByIdAndCreatedByAndStatusNot(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.DELETE);
        if (!eventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.EVENT_BRIDGE_NOT_FOUND_WITH_ID, payload.getId()), payload);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(String.format(QueryService.FETCH_LINK_EVENT_BRIDGE_WITH_USER,
            eventBridge.get().getId(), payload.getStartDate().concat(BarcoUtil.START_DATE), payload.getEndDate().concat(BarcoUtil.END_DATE), APPLICATION_STATUS.DELETE.getLookupCode()));
        List<LinkRPUResponse> linkRPUResponses = new ArrayList<>();
        if (!BarcoUtil.isNull(queryResponse.getData())) {
            for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
                linkRPUResponses.add(this.getLinkRPUResponse(data, eventBridge.get().getStatus()));
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, linkRPUResponses);
    }

    /**
     * Method to link the EventBridge with user
     * @param payload
     * @return AppResponse
     * @throws Exception
     */
    @Override
    public AppResponse linkEventBridgeWithUser(LinkEBURequest payload) throws Exception {
        logger.info("Request linkEventBridgeWithUser :- " + payload);
        Optional<AppUser> superAdmin = this.getAppUser(payload.getSessionUser().getUsername());
        if (!superAdmin.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        AppResponse validationResponse = this.validateLinkEventBridgePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        Optional<EventBridge> eventBridge = this.eventBridgeRepository.findById(payload.getId());
        if (!eventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.EVENT_BRIDGE_NOT_FOUND_WITH_ID, payload.getId()), payload);
        } else if (BarcoUtil.isNull(eventBridge.get().getCredential())) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.EVENT_BRIDGE_NOT_FOUND_LINK_CREDENTIAL_WITH_ID, payload.getId()), payload);
        }
        Optional<AppUser> appUser = this.appUserRepository.findById(payload.getAppUserId());
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.APPUSER_NOT_FOUND, payload.getAppUserId()), payload);
        } else if (payload.getLinked()) {
            return linkEventBridge(superAdmin.get(), appUser.get(), eventBridge.get(), payload);
        } else {
            this.queryService.deleteQuery(String.format(QueryService.DELETE_APP_USER_EVENT_BRIDGE_BY_EVENT_BRIDGE_ID_AND_APP_USER_ID,
                eventBridge.get().getId(), appUser.get().getId()));
        }
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, ""), payload);
    }

    /**
     * Method to generate the event bridge token
     * @param payload
     * @return AppResponse
     * @throws Exception
     */
    @Override
    public AppResponse genEventBridgeToken(LinkEBURequest payload) throws Exception {
        logger.info("Request genEventBridgeToken :- " + payload);
        Optional<AppUser> appUser = getAppUser(payload.getSessionUser().getUsername());
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getTokenId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_GEN_TOKEN_MISSING);
        }
        Optional<AppUserEventBridge> linkEventBridge = this.appUserEventBridgeRepository.findByTokenId(payload.getTokenId());
        if (!linkEventBridge.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.EVENT_BRIDGE_NOT_FOUND_WITH_GEN_TOKEN);
        }
        EventBridge eventBridge = linkEventBridge.get().getEventBridge();
        if (BarcoUtil.isNull(eventBridge.getCredential())) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.EVENT_BRIDGE_NOT_FOUND_LINK_CREDENTIAL_WITH_ID, payload.getId()), payload);
        }
        return this.generateTokenForEventBridge(linkEventBridge.get(), eventBridge, payload);
    }

    /**
     * Method use to download the event bridge template file
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadEventBridgeTemplateFile() throws Exception {
        logger.info("Request downloadEventBridgeTemplateFile ");
        return this.downloadTemplateFile(this.tempStoreDirectory, this.bulkExcel,
            this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.EVENT_BRIDGE));
    }

    /**
     * Method use to download the event bridge
     * @param payload
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadEventBridge(EventBridgeRequest payload) throws Exception {
        logger.info("Request downloadEventBridge " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            throw new Exception(MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            throw new Exception(MessageUtil.APPUSER_NOT_FOUND);
        }
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.EVENT_BRIDGE);
        XSSFWorkbook workbook = new XSSFWorkbook();
        this.bulkExcel.setWb(workbook);
        XSSFSheet xssfSheet = workbook.createSheet(sheetFiled.getSheetName());
        this.bulkExcel.setSheet(xssfSheet);
        AtomicInteger rowCount = new AtomicInteger();
        this.bulkExcel.fillBulkHeader(rowCount.get(), sheetFiled.getColTitle());
        List<EventBridge> eventBridges;
        if (!BarcoUtil.isNull(payload.getIds()) && payload.getIds().size() > 0) {
            eventBridges = this.eventBridgeRepository.findAllByIdInAndCreatedByAndStatusNotOrderByDateCreatedDesc(
                payload.getIds(), appUser.get(), APPLICATION_STATUS.DELETE);
        } else {
            eventBridges = this.eventBridgeRepository.findAllByCreatedByAndStatusNotOrderByDateCreatedDesc(
                appUser.get(), APPLICATION_STATUS.DELETE);
        }
        // event-bridges
        eventBridges.stream().filter(eventBridge -> !eventBridge.getStatus().equals(APPLICATION_STATUS.DELETE))
            .forEach(eventBridge -> {
                int currentRowCount = rowCount.incrementAndGet();
                List<String> dataCellValues = Arrays.asList(
                    eventBridge.getName(),
                    eventBridge.getBridgeUrl(),
                    eventBridge.getHttpMethod().name(),
                    eventBridge.getDescription(),
                    eventBridge.getBridgeType().name()
                );
                this.bulkExcel.fillBulkBody(dataCellValues, currentRowCount);
            });
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream;
    }

    /**
     * Method se the upload the event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse uploadEventBridge(FileUploadRequest payload) throws Exception {
        logger.info("Request uploadEventBridge :- " + payload);
        LookupDataRequest lookupDataRequest = new Gson().fromJson((String) payload.getData(), LookupDataRequest.class);
        if (BarcoUtil.isNull(lookupDataRequest.getSessionUser().getUsername())) {
            throw new Exception(MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            lookupDataRequest.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            throw new Exception(MessageUtil.APPUSER_NOT_FOUND);
        } else if (!payload.getFile().getContentType().equalsIgnoreCase(this.bulkExcel.SHEET_TYPE)) {
            logger.info("File Type " + payload.getFile().getContentType());
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.XLSX_FILE_ONLY);
        }
        // fill the stream with file into work-book
        LookupDataResponse uploadLimit = this.lookupDataCacheService.getParentLookupDataByParentLookupType(LookupUtil.UPLOAD_LIMIT);
        XSSFWorkbook workbook = new XSSFWorkbook(payload.getFile().getInputStream());
        if (BarcoUtil.isNull(workbook) || workbook.getNumberOfSheets() == 0) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.YOU_UPLOAD_EMPTY_FILE);
        }
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.EVENT_BRIDGE);
        XSSFSheet sheet = workbook.getSheet(sheetFiled.getSheetName());
        if (BarcoUtil.isNull(sheet)) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.SHEET_NOT_FOUND, sheetFiled.getSheetName()));
        } else if (sheet.getLastRowNum() < 1) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.YOU_CANT_UPLOAD_EMPTY_FILE);
        } else if (sheet.getLastRowNum() > Long.valueOf(uploadLimit.getLookupValue())) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.FILE_SUPPORT_ROW_AT_TIME, uploadLimit.getLookupValue()));
        }
        List<EventBridgeValidation> eventBridgeValidations = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Iterator<Row> rows = sheet.iterator();
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (currentRow.getRowNum() == 0) {
                for (int i=0; i < sheetFiled.getColTitle().size(); i++) {
                    if (!currentRow.getCell(i).getStringCellValue().equals(sheetFiled.getColTitle().get(i))) {
                        return new AppResponse(BarcoUtil.ERROR, "File at row " + (currentRow.getRowNum() + 1)
                            + " " + sheetFiled.getColTitle().get(i) + " heading missing.");
                    }
                }
            } else if (currentRow.getRowNum() > 0) {
                EventBridgeValidation eventBridgeValidation = new EventBridgeValidation();
                try {
                    eventBridgeValidation.setRowCounter(currentRow.getRowNum()+1);
                    for (int i=0; i < sheetFiled.getColTitle().size(); i++) {
                        int index = 0;
                        if (i == index) {
                            eventBridgeValidation.setName(this.bulkExcel.getCellDetail(currentRow, i));
                        } else if (i == ++index) {
                            eventBridgeValidation.setBridgeUrl(this.bulkExcel.getCellDetail(currentRow, i));
                        } else if (i == ++index) {
                            eventBridgeValidation.setHttpMethod(HttpMethod.resolve(this.bulkExcel.getCellDetail(currentRow, i)));
                        } else if (i == ++index) {
                            eventBridgeValidation.setDescription(this.bulkExcel.getCellDetail(currentRow, i));
                        } else if (i == ++index) {
                            eventBridgeValidation.setBridgeType(this.bulkExcel.getCellDetail(currentRow, i));
                        }
                    }
                    eventBridgeValidation.isValidBatch();
                    EVENT_BRIDGE_TYPE.getByLookupCode(eventBridgeValidation.getBridgeType());
                } catch (RuntimeException ex) {
                    eventBridgeValidation.setErrorMsg(String.format("%s at row %s.<br>", ex.getMessage(),
                        eventBridgeValidation.getRowCounter()));
                }
                if (!BarcoUtil.isNull(eventBridgeValidation.getErrorMsg())) {
                    errors.add(eventBridgeValidation.getErrorMsg());
                    continue;
                }
                eventBridgeValidations.add(eventBridgeValidation);
            }
        }
        if (errors.size() > 0) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.TOTAL_INVALID, errors.size()), errors);
        }
        eventBridgeValidations.forEach(eventBridgeValidation -> {
            EventBridge eventBridge = new EventBridge();
            eventBridge.setName(eventBridgeValidation.getName());
            eventBridge.setBridgeUrl(eventBridgeValidation.getBridgeUrl());
            eventBridge.setHttpMethod(eventBridgeValidation.getHttpMethod());
            eventBridge.setBridgeType(EVENT_BRIDGE_TYPE.getByLookupCode(eventBridgeValidation.getBridgeType()));
            eventBridge.setDescription(eventBridgeValidation.getDescription());
            eventBridge.setCreatedBy(appUser.get());
            eventBridge.setUpdatedBy(appUser.get());
            eventBridge.setStatus(APPLICATION_STATUS.ACTIVE);
            this.eventBridgeRepository.save(eventBridge);
        });
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, ""));
    }

    /**
     * Method use to gernate the otken for EventBridge
     * @param linkEventBridge
     * @param eventBridge
     * @param payload
     * @return AppResponse
     * */
    private AppResponse generateTokenForEventBridge(AppUserEventBridge linkEventBridge,
        EventBridge eventBridge, LinkEBURequest payload) throws Exception {
        if (BarcoUtil.isNull(eventBridge.getCredential())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_FOUND);
        } else if (!eventBridge.getCredential().getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_NOT_ACTIVE);
        }
        String priKey = this.getCredentialPrivateKey(eventBridge.getCredential());
        linkEventBridge.setAccessToken(this.jwtUtils.generateToken(priKey, linkEventBridge.getTokenId()));
        linkEventBridge.setExpireTime(getOneYearFromNow());
        this.appUserEventBridgeRepository.save(linkEventBridge);
        payload.setAccessToken(linkEventBridge.getAccessToken());
        payload.setExpireTime(linkEventBridge.getExpireTime());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, ""), payload);
    }

    /**
     * Method use to get the appuser by username
     * @param username
     * @return AppUser
     * */
    private Optional<AppUser> getAppUser(String username) {
        return this.appUserRepository.findByUsernameAndStatus(username, APPLICATION_STATUS.ACTIVE);
    }

    /**
     * Method use to link with event bridge
     * @param superAdmin
     * @param appUser
     * @param eventBridge
     * @param payload
     * @return AppResponse
     *
     * */
    private AppResponse linkEventBridge(AppUser superAdmin, AppUser appUser,
        EventBridge eventBridge, LinkEBURequest payload) throws Exception {
        Credential credential = eventBridge.getCredential();
        if (BarcoUtil.isNull(credential.getContent())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.CREDENTIAL_CONTENT_MISSING);
        }
        String priKey = this.getCredentialPrivateKey(credential);
        AppUserEventBridge linkEventBridge = this.getAppUserEventBridge(superAdmin, appUser, eventBridge);
        linkEventBridge.setAccessToken(this.jwtUtils.generateToken(priKey, linkEventBridge.getTokenId()));
        linkEventBridge.setExpireTime(this.getOneYearFromNow());
        this.appUserEventBridgeRepository.save(linkEventBridge);
        payload.setAccessToken(linkEventBridge.getAccessToken());
        payload.setExpireTime(linkEventBridge.getExpireTime());
        payload.setTokenId(linkEventBridge.getTokenId());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, ""), payload);
    }

    /***
     * Method use to get the event bridge detail
     * @param eventBridge
     * @return EventBridgeResponse
     * */
    private EventBridgeResponse getEventBridgeResponse(EventBridge eventBridge) {
        EventBridgeResponse eventBridgeResponse = new EventBridgeResponse();
        eventBridgeResponse.setId(eventBridge.getId());
        eventBridgeResponse.setName(eventBridge.getName());
        eventBridgeResponse.setBridgeUrl(eventBridge.getBridgeUrl());
        eventBridgeResponse.setDescription(eventBridge.getDescription());
        // http method
        if (!BarcoUtil.isNull(eventBridge.getHttpMethod())) {
            GLookup httpMethod = GLookup.getGLookup(this.lookupDataCacheService
                .getChildLookupDataByParentLookupTypeAndChildLookupCode(REQUEST_METHOD.getName(),
                    Long.valueOf(eventBridge.getHttpMethod().ordinal())));
            eventBridgeResponse.setHttpMethod(httpMethod);
        }
        // bridge type
        if (!BarcoUtil.isNull(eventBridge.getBridgeType())) {
            GLookup bridgeType = GLookup.getGLookup(this.lookupDataCacheService
                .getChildLookupDataByParentLookupTypeAndChildLookupCode(EVENT_BRIDGE_TYPE.getName(),
                    eventBridge.getBridgeType().getLookupCode()));
            eventBridgeResponse.setBridgeType(bridgeType);
        }
        // credential
        if (!BarcoUtil.isNull(eventBridge.getCredential())) {
            eventBridgeResponse.setCredential(this.getCredentialResponse(eventBridge.getCredential()));
        }
        eventBridgeResponse.setDateCreated(eventBridge.getDateCreated());
        eventBridgeResponse.setCreatedBy(getActionUser(eventBridge.getCreatedBy()));
        eventBridgeResponse.setDateUpdated(eventBridge.getDateUpdated());
        eventBridgeResponse.setUpdatedBy(getActionUser(eventBridge.getUpdatedBy()));
        eventBridgeResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(eventBridge.getStatus().getLookupType()));
        return eventBridgeResponse;
    }

}

package com.barco.admin.service.impl;

import com.barco.admin.service.EVariableService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.common.utility.excel.SheetFiled;
import com.barco.common.utility.validation.RPPValidation;
import com.barco.model.dto.request.EnVariablesRequest;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.LinkEURequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.*;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.AppUserEnv;
import com.barco.model.pojo.EnvVariables;
import com.barco.model.repository.AppUserEnvRepository;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.EnvVariablesRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.LookupUtil;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class EVariableServiceImpl implements EVariableService {

    private Logger logger = LoggerFactory.getLogger(EVariableServiceImpl.class);

    @Value("${storage.efsFileDire}")
    private String tempStoreDirectory;
    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private QueryService queryService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private EnvVariablesRepository envVariablesRepository;
    @Autowired
    private AppUserEnvRepository appUserEnvRepository;

    /***
     * Method use to add env variable
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addEnVariable(EnVariablesRequest payload) throws Exception {
        logger.info("Request addEnVariable :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getEnvKey())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ENV_ENVKEY_REQUIRED);
        } else if (this.envVariablesRepository.findByEnvKeyAndStatusNot(
            payload.getEnvKey(), APPLICATION_STATUS.DELETE).isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ENV_ENVKEY_ALREADY_EXIST);
        }
        EnvVariables envVariables = new EnvVariables();
        envVariables.setEnvKey(payload.getEnvKey());
        envVariables.setDescription(payload.getDescription());
        envVariables.setCreatedBy(adminUser.get());
        envVariables.setUpdatedBy(adminUser.get());
        envVariables.setStatus(APPLICATION_STATUS.ACTIVE);
        envVariables = this.envVariablesRepository.save(envVariables);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, envVariables.getId().toString()), payload);
    }

    /**
     * Method use edit the env
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateEnVariable(EnVariablesRequest payload) throws Exception {
        logger.info("Request updateEnVariable :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ENV_KEYID_REQUIRED);
        } else if (BarcoUtil.isNull(payload.getEnvKey())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ENV_ENVKEY_REQUIRED);
        }
        Optional<EnvVariables> envVariables = this.envVariablesRepository.findById(payload.getId());
        if (!envVariables.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.ENV_NOT_FOUND_WITH_ID, payload.getId().toString()));
        } else if (!envVariables.get().getEnvKey().equals(payload.getEnvKey()) && this.envVariablesRepository
            .findByEnvKeyAndStatusNot(payload.getEnvKey(), APPLICATION_STATUS.DELETE).isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PROFILE_ALREADY_EXIST, payload);
        }
        envVariables.get().setEnvKey(payload.getEnvKey());
        envVariables.get().setDescription(payload.getDescription());
        envVariables.get().setUpdatedBy(adminUser.get());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            // if status is in-active & delete then we have filter the role and show only those role in user detail
            envVariables.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            envVariables.get().getAppUserEnvs().stream()
                .map(appUserRoleAccess -> {
                    appUserRoleAccess.setStatus(envVariables.get().getStatus());
                    return appUserRoleAccess;
                }).collect(Collectors.toList());
        }
        this.envVariablesRepository.save(envVariables.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()), payload);
    }

    /**
     * Method use fetch all the env
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllEnVariable(EnVariablesRequest payload) throws Exception {
        logger.info("Request fetchAllEnVariable :- " + payload);
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.envVariablesRepository.findAllByStatusNot(APPLICATION_STATUS.DELETE)
                .stream().map(envVariables -> getEnVariablesResponse(envVariables))
                .collect(Collectors.toList()));
    }

    /**
     * Method use to get the env variable detail by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchEnVariableById(EnVariablesRequest payload) throws Exception {
        logger.info("Request fetchEnVariableById :- " + payload);
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ENV_KEYID_REQUIRED);
        }
        Optional<EnvVariables> envVariables = this.envVariablesRepository.findById(payload.getId());
        if (!envVariables.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.ENV_NOT_FOUND_WITH_ID, payload.getId().toString()));
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, getEnVariablesResponse(envVariables.get()));
    }

    /**
     * Method use delete the env
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteEnVariableById(EnVariablesRequest payload) throws Exception {
        logger.info("Request deleteEnVariableById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ENV_KEYID_REQUIRED);
        }
        Optional<EnvVariables> envVariables = this.envVariablesRepository.findById(payload.getId());
        if (!envVariables.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.ENV_NOT_FOUND_WITH_ID, payload.getId().toString()));
        }
        this.queryService.deleteQuery(String.format(QueryService.DELETE_APP_USER_ENV_BY_ENV_KEY_ID, envVariables.get().getId()));
        this.envVariablesRepository.delete(envVariables.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()), payload);
    }

    /**
     * Method use to download e-variable template
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadEnVariableTemplateFile() throws Exception {
        logger.info("Request downloadLookupDataTemplateFile");
        return downloadTemplateFile(this.tempStoreDirectory, this.bulkExcel,
            this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.EVARIABLE));
    }

    /**
     * Method use to download e-variable data
     * @param payload
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadEnVariable(EnVariablesRequest payload) throws Exception {
        logger.info("Request downloadRole :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            throw new Exception(MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            throw new Exception(MessageUtil.APPUSER_NOT_FOUND);
        }
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.EVARIABLE);
        XSSFWorkbook workbook = new XSSFWorkbook();
        this.bulkExcel.setWb(workbook);
        XSSFSheet xssfSheet = workbook.createSheet(sheetFiled.getSheetName());
        this.bulkExcel.setSheet(xssfSheet);
        AtomicInteger rowCount = new AtomicInteger();
        this.bulkExcel.fillBulkHeader(rowCount.get(), sheetFiled.getColTitle());
        Iterator<EnvVariables> envVariables = this.envVariablesRepository.findAll().iterator();
        while (envVariables.hasNext()) {
            EnvVariables envVariable = envVariables.next();
            rowCount.getAndIncrement();
            List<String> dataCellValue = new ArrayList<>();
            dataCellValue.add(envVariable.getEnvKey());
            dataCellValue.add(envVariable.getDescription());
            this.bulkExcel.fillBulkBody(dataCellValue, rowCount.get());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream;
    }

    /**
     * Method use to upload role data
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse uploadEnVariable(FileUploadRequest payload) throws Exception {
        logger.info("Request for bulk uploading file!");
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
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.EVARIABLE);
        XSSFSheet sheet = workbook.getSheet(sheetFiled.getSheetName());
        if (BarcoUtil.isNull(sheet)) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.SHEET_NOT_FOUND, sheetFiled.getSheetName()));
        } else if (sheet.getLastRowNum() < 1) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.YOU_CANT_UPLOAD_EMPTY_FILE);
        } else if (sheet.getLastRowNum() > Long.valueOf(uploadLimit.getLookupValue())) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.FILE_SUPPORT_ROW_AT_TIME, uploadLimit.getLookupValue()));
        }
        List<RPPValidation> rppValidationsList = new ArrayList<>();
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
                RPPValidation rppValidation = new RPPValidation();
                rppValidation.setRowCounter(currentRow.getRowNum()+1);
                for (int i=0; i < sheetFiled.getColTitle().size(); i++) {
                    int index = 0;
                    if (i == index) {
                        rppValidation.setName(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        rppValidation.setDescription(this.bulkExcel.getCellDetail(currentRow, i));
                    }
                }
                rppValidation.isValidLookup();
                if (this.envVariablesRepository.findByEnvKeyAndStatusNot(rppValidation.getName(),
                    APPLICATION_STATUS.DELETE).isPresent()) {
                    rppValidation.setErrorMsg(String.format(MessageUtil.EVARIABLE_TYPE_ALREADY_USE_AT_ROW,
                        rppValidation.getName(), rppValidation.getRowCounter()));
                }
                if (!BarcoUtil.isNull(rppValidation.getErrorMsg())) {
                    errors.add(rppValidation.getErrorMsg());
                    continue;
                }
                rppValidationsList.add(rppValidation);
            }
        }
        if (errors.size() > 0) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.TOTAL_INVALID, errors.size()), errors);
        }
        rppValidationsList.forEach(rppValidation -> {
            EnvVariables envVariable = new EnvVariables();
            envVariable.setEnvKey(rppValidation.getName());
            envVariable.setDescription(rppValidation.getDescription());
            envVariable.setCreatedBy(appUser.get());
            envVariable.setUpdatedBy(appUser.get());
            envVariable.setStatus(APPLICATION_STATUS.ACTIVE);
            this.envVariablesRepository.save(envVariable);
        });
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, ""));
    }

    /***
     * Method use to link variable with root user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchLinkEVariableWitUser(LinkEURequest payload) throws Exception {
        logger.info("Request fetchLinkEVariableWithRootUser :- " + payload);
        if (BarcoUtil.isNull(payload.getEnvId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ENV_KEYID_REQUIRED);
        }
        Optional<EnvVariables> envVariables = this.envVariablesRepository.findById(payload.getEnvId());
        if (!envVariables.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.ENV_NOT_FOUND_WITH_ID, payload.getEnvId()), payload);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(String.format(QueryService.FETCH_LINK_ENVIRONMENT_VARIABLE_WITH_USER,
            envVariables.get().getId(), APPLICATION_STATUS.DELETE.getLookupCode()));
        List<LinkRPUResponse> linkRPUResponses = new ArrayList<>();
        if (!BarcoUtil.isNull(queryResponse.getData())) {
            for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
                linkRPUResponses.add(getLinkRPUResponse(data, envVariables.get().getStatus()));
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, linkRPUResponses);
    }

    /***
     * Method use to link variable with root user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkEVariableWithUser(LinkEURequest payload) throws Exception {
        logger.info("Request linkRoleWithRootUser :- " + payload);
        Optional<AppUser> superAdmin = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!superAdmin.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getEnvId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ENV_KEYID_REQUIRED);
        } else if (BarcoUtil.isNull(payload.getAppUserId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APP_USER_ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getLinked())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LINKED_MISSING);
        }
        Optional<EnvVariables> envVariables = this.envVariablesRepository.findById(payload.getEnvId());
        if (!envVariables.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.ENV_NOT_FOUND_WITH_ID, payload.getEnvId()), payload);
        }
        Optional<AppUser> appUser = this.appUserRepository.findById(payload.getAppUserId());
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.APPUSER_NOT_FOUND, payload.getAppUserId()), payload);
        }
        // add operation de-link
        if (payload.getLinked()) {
            this.appUserEnvRepository.save(getAppUserEnv(superAdmin.get(), appUser.get(), envVariables.get()));
        } else {
            this.queryService.deleteQuery(String.format(QueryService.DELETE_APP_USER_ENV_BY_ENV_KEY_ID_AND_APP_USER_ID,
                envVariables.get().getId(), appUser.get().getId()));
        }
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, ""), payload);
    }

    private static AppUserEnv getAppUserEnv(AppUser superAdmin, AppUser appUser, EnvVariables envVariables) {
        AppUserEnv appUserEnv = new AppUserEnv();
        appUserEnv.setCreatedBy(superAdmin);
        appUserEnv.setUpdatedBy(superAdmin);
        appUserEnv.setAppUser(appUser);
        appUserEnv.setEnvVariables(envVariables);
        appUserEnv.setStatus(APPLICATION_STATUS.ACTIVE);
        if (envVariables.getStatus().getLookupType().equals(APPLICATION_STATUS.INACTIVE.getLookupType()) ||
            appUser.getStatus().getLookupType().equals(APPLICATION_STATUS.INACTIVE.getLookupType())) {
            appUserEnv.setStatus(APPLICATION_STATUS.INACTIVE);
        }
        return appUserEnv;
    }

    /***
     * Method use to get the env variable
     * @param envVariables
     * @return EnVariablesResponse
     * */
    private EnVariablesResponse getEnVariablesResponse(EnvVariables envVariables) {
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

}

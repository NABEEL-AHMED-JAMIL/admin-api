package com.barco.admin.service.impl;

import com.barco.common.utility.validation.LookupDataValidation;
import com.barco.model.util.lookup.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.common.utility.excel.SheetFiled;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.LookupDataResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.LookupData;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.LookupDataRepository;
import com.barco.model.util.MessageUtil;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class LookupDataCacheServiceImpl implements LookupDataCacheService {

    private Logger logger = LoggerFactory.getLogger(LookupDataCacheServiceImpl.class);

    @Value("${storage.efsFileDire}")
    private String tempStoreDirectory;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private Map<String, LookupDataResponse> lookupCacheMap = new HashMap<>();
    private Map<String, SheetFiled> sheetFiledMap = new HashMap<>();

    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private LookupDataRepository lookupDataRepository;

    public LookupDataCacheServiceImpl() {}

    /**
     * Method use to cache the data
     * */
    @PostConstruct
    public void initialize() {
        this.writeLock.lock();
        try {
            logger.info("****************Cache-Lookup-Start***************************");
            this.lookupCacheMap = new HashMap<>();
            Iterable<LookupData> lookupDataList = this.lookupDataRepository.findByParentLookupIsNull();
            lookupDataList.forEach(lookupData -> {
                if (this.lookupCacheMap.containsKey(lookupData.getLookupType())) {
                    this.lookupCacheMap.put(lookupData.getLookupType(), getLookupDataDetail(lookupData));
                } else {
                    this.lookupCacheMap.put(lookupData.getLookupType(), getLookupDataDetail(lookupData));
                }
            });
            logger.info("***************Cache-Lookup-End********************************");
        } finally {
            this.writeLock.unlock();
        }
    }

    /**
     * Method use to cache the data
     * */
    @PostConstruct
    private void initSheetData() throws IOException {
        logger.info("****************Sheet-Start***************************");
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream inputStream = cl.getResourceAsStream(this.bulkExcel.SHEET_COL);
        String result = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        Type type = new TypeToken<Map<String, SheetFiled>>(){}.getType();
        this.sheetFiledMap = new Gson().fromJson(result, type);
        logger.info("Sheet Map " + this.sheetFiledMap.size());
        logger.info("****************Sheet-End***************************");
    }

    /**
     * Method use to filter only ui lookup
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchCacheData() throws Exception {
        logger.info("Request fetchCacheData ");
        Map<String, LookupDataResponse> lookupDataResponseMap = new HashMap<>();
        for (Map.Entry<String, LookupDataResponse> item: this.lookupCacheMap.entrySet()) {
            if (item.getValue().getUiLookup().equals(UI_LOOKUP.TRUE.getLookupCode())) {
                lookupDataResponseMap.put(item.getKey(), item.getValue());
            }
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, lookupDataResponseMap);
    }

    /**
     * @apiName :- addLookupData
     * Api use to add the lookup data
     * @param payload
     * @return ResponseEntity<?> addLookupData
     * */

    @Override
    public AppResponse addLookupData(LookupDataRequest payload) throws Exception {
        logger.info("Request addLookupData :- " + payload);
        if (BarcoUtil.isNull(payload.getLookupCode())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LOOKUP_DATA_LOOKUP_CODE_MISSING);
        } else if (BarcoUtil.isNull(payload.getLookupValue())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LOOKUP_DATA_LOOKUP_VALUE_MISSING);
        } else if (BarcoUtil.isNull(payload.getLookupType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LOOKUP_DATA_LOOKUP_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LOOKUP_DATA_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getUiLookup())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LOOKUP_DATA_UI_LOOKUP_MISSING);
        } else if (this.lookupDataRepository.findByLookupType(payload.getLookupType()).isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LOOKUP_DATA_ALREADY_EXIST);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        LookupData lookupData = new LookupData();
        lookupData.setLookupType(payload.getLookupType());
        lookupData.setLookupCode(payload.getLookupCode());
        lookupData.setLookupValue(payload.getLookupValue());
        lookupData.setUiLookup(UI_LOOKUP.getByLookupCode(payload.getUiLookup()));
        lookupData.setDescription(payload.getDescription());
        lookupData.setStatus(APPLICATION_STATUS.ACTIVE);
        lookupData.setCreatedBy(appUser.get());
        lookupData.setUpdatedBy(appUser.get());
        if (!BarcoUtil.isNull(payload.getParentLookupId())) {
            Optional<LookupData> parentLookupData = this.lookupDataRepository.findById(payload.getParentLookupId());
            if (parentLookupData.isPresent()) {
                lookupData.setParentLookup(parentLookupData.get());
            }
        }
        this.addNewLookupData(this.lookupDataRepository.save(lookupData));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, lookupData.getId()), payload);
    }

    /**
     * Method use to update new filed into db & cache
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateLookupData(LookupDataRequest payload) throws Exception {
        logger.info("Request updateLookupData :- " + payload);
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Optional<LookupData> lookupData = this.lookupDataRepository.findById(payload.getId());
        if (!lookupData.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.DATA_NOT_FOUND, payload.getId()));
        }
        if (!BarcoUtil.isNull(payload.getLookupCode())) {
            lookupData.get().setLookupCode(payload.getLookupCode());
        }
        if (!BarcoUtil.isNull(payload.getLookupType())) {
            lookupData.get().setLookupType(payload.getLookupType());
        }
        if (!BarcoUtil.isNull(payload.getLookupValue())) {
            lookupData.get().setLookupValue(payload.getLookupValue());
        }
        if (!BarcoUtil.isNull(payload.getUiLookup())) {
            lookupData.get().setUiLookup(UI_LOOKUP.getByLookupCode(payload.getUiLookup()));
        }
        if (!BarcoUtil.isNull(payload.getDescription())) {
            lookupData.get().setDescription(payload.getDescription());
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            lookupData.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        lookupData.get().setUpdatedBy(appUser.get());
        this.lookupDataRepository.save(lookupData.get());
        this.initialize();
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
    }

    /**
     * Method use to fetch all lookup
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse findAllParentLookupByUsername(LookupDataRequest payload) throws Exception {
        logger.info("Request findAllParentLookupByUsername ");
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Iterator<LookupData> lookupDataIterator = this.lookupDataRepository.findAllParentLookupByUsernameOrderByDateCreatedDesc(
            appUser.get().getUsername()).iterator();
        List<LookupDataResponse> lookupDataResponse = new ArrayList<>();
        while (lookupDataIterator.hasNext()) {
            LookupData lookupData = lookupDataIterator.next();
            if (!lookupData.getStatus().equals(APPLICATION_STATUS.DELETE)) {
                lookupDataResponse.add(this.fillLookupDataResponse(lookupData, new LookupDataResponse(), true));
            }
        }
        if (!BarcoUtil.isNull(payload.getUiLookup())) {
            lookupDataResponse = lookupDataResponse.stream().filter(lkupDataRes -> lkupDataRes.getUiLookup()
                .getLookupCode().equals(UI_LOOKUP.TRUE.getLookupCode())).collect(Collectors.toList());
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, lookupDataResponse);
    }

    /**
     * Method use to fetch sub lookup by Parent lookup uuid
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchSubLookupDataByParentLookupDataId(LookupDataRequest payload) throws Exception {
        logger.info("Request fetchSubLookupDataByParentLookupDataId :- " + payload);
        if (BarcoUtil.isNull(payload.getParentLookupId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Optional<LookupData> parentLookupData= this.lookupDataRepository.findOneByParentLookupIdAndUsername(
            payload.getParentLookupId(), appUser.get().getUsername());
        if (parentLookupData.isPresent()) {
            Map<String, Object> appSettingDetail = new HashMap<>();
            appSettingDetail.put(PARENT_LOOKUP_DATA, this.fillLookupDataResponse(parentLookupData.get(), new LookupDataResponse(), true));
            if (!BarcoUtil.isNull(parentLookupData.get().getLookupChildren())) {
                List<LookupDataResponse> lookupDataResponses = new ArrayList<>();
                for (LookupData childLookupData: parentLookupData.get().getLookupChildren()) {
                    if (!childLookupData.getStatus().equals(APPLICATION_STATUS.DELETE)) {
                        lookupDataResponses.add(this.fillLookupDataResponse(childLookupData, new LookupDataResponse(), true));
                    }
                }
                appSettingDetail.put(SUB_LOOKUP_DATA, lookupDataResponses);
            }
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, appSettingDetail);
        }
        return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.DATA_NOT_FOUND, payload.getParentLookupId()));
    }

    /**
     * Method use to fetch lookup by lookup type
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchLookupDataByLookupType(LookupDataRequest payload) throws Exception {
        logger.info("Request fetchLookupDataByLookupType :- " + payload);
        if (BarcoUtil.isNull(payload.getLookupType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.LOOKUP_DATA_LOOKUP_TYPE_MISSING);
        }
        Map<String, Object> appSettingDetail = new HashMap<>();
        Optional<LookupData> parentLookupData = this.lookupDataRepository.findByLookupType(payload.getLookupType());
        if (!parentLookupData.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.DATA_NOT_FOUND, payload.getLookupType()));
        }
        appSettingDetail.put(PARENT_LOOKUP_DATA, this.fillLookupDataResponse(parentLookupData.get(), new LookupDataResponse(), false));
        if (!BarcoUtil.isNull(parentLookupData.get().getLookupChildren())) {
            List<LookupDataResponse> lookupDataResponses = new ArrayList<>();
            for (LookupData childLookupData: parentLookupData.get().getLookupChildren()) {
                if (childLookupData.getStatus().equals(APPLICATION_STATUS.ACTIVE)) {
                    lookupDataResponses.add(this.fillLookupDataResponse(childLookupData, new LookupDataResponse(), true));
                }
            }
            appSettingDetail.put(SUB_LOOKUP_DATA, lookupDataResponses);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, appSettingDetail);
    }

    /**
     * Method use to delete the lookUp by lookup id and username
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteLookupData(LookupDataRequest payload) throws Exception {
        logger.info("Request deleteLookupData :- " + payload);
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        this.lookupDataRepository.deleteById(payload.getId());
        this.initialize();
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()));
    }

    /**
     * Method use to download the template
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadLookupDataTemplateFile() throws Exception {
        logger.info("Request downloadLookupDataTemplateFile");
        return downloadTemplateFile(this.tempStoreDirectory, this.bulkExcel, this.sheetFiledMap.get(this.bulkExcel.LOOKUP));
    }

    /**
     * Method use to download the data
     * @param payload
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadLookupData(LookupDataRequest payload) throws Exception {
        logger.info("Request downloadLookupData :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            throw new Exception(MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            throw new Exception(MessageUtil.APPUSER_NOT_FOUND);
        }
        List<LookupData> lookupDataList;
        if (BarcoUtil.isNull(payload.getParentLookupId())) {
            lookupDataList = this.lookupDataRepository.findAllParentLookupByUsernameOrderByDateCreatedDesc(appUser.get().getUsername());
        } else {
            Optional<LookupData> parentLookupData = this.lookupDataRepository.findOneByParentLookupIdAndUsername(
                payload.getParentLookupId(), appUser.get().getUsername());
            if (!parentLookupData.isPresent()) {
                throw new Exception(MessageUtil.PARENT_LOOKUP_NOT_FOUND);
            }
            lookupDataList = parentLookupData.get().getLookupChildren().stream().collect(Collectors.toList());
        }
        SheetFiled sheetFiled = this.sheetFiledMap.get(this.bulkExcel.LOOKUP);
        XSSFWorkbook workbook = new XSSFWorkbook();
        this.bulkExcel.setWb(workbook);
        XSSFSheet xssfSheet = workbook.createSheet(sheetFiled.getSheetName());
        this.bulkExcel.setSheet(xssfSheet);
        AtomicInteger rowCount = new AtomicInteger();
        this.bulkExcel.fillBulkHeader(rowCount.get(), sheetFiled.getColTitle());
        lookupDataList.forEach(lookupData -> {
            if (!lookupData.getStatus().equals(APPLICATION_STATUS.DELETE)) {
                rowCount.getAndIncrement();
                List<String> dataCellValue = new ArrayList<>();
                dataCellValue.add(lookupData.getLookupType());
                dataCellValue.add(!BarcoUtil.isNull(lookupData.getLookupCode()) ? String.valueOf(lookupData.getLookupCode()) : "");
                dataCellValue.add(lookupData.getLookupValue());
                dataCellValue.add(lookupData.getUiLookup().getLookupType());
                dataCellValue.add(lookupData.getDescription());
                this.bulkExcel.fillBulkBody(dataCellValue, rowCount.get());
            }
        });
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream;
    }

    /**
     * Method use to download the data
     * @param payload
     * @return ByteArrayOutputStream
     * */
    @Override
    public AppResponse uploadLookupData(FileUploadRequest payload) throws Exception {
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
        LookupDataResponse uploadLimit = this.getParentLookupDataByParentLookupType(LookupUtil.UPLOAD_LIMIT);
        XSSFWorkbook workbook = new XSSFWorkbook(payload.getFile().getInputStream());
        if (BarcoUtil.isNull(workbook) || workbook.getNumberOfSheets() == 0) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.YOU_UPLOAD_EMPTY_FILE);
        }
        SheetFiled sheetFiled = this.sheetFiledMap.get(this.bulkExcel.LOOKUP);
        XSSFSheet sheet = workbook.getSheet(sheetFiled.getSheetName());
        if (BarcoUtil.isNull(sheet)) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.SHEET_NOT_FOUND, sheetFiled.getSheetName()));
        } else if (sheet.getLastRowNum() < 1) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.YOU_CANT_UPLOAD_EMPTY_FILE);
        } else if (sheet.getLastRowNum() > Long.valueOf(uploadLimit.getLookupValue())) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.FILE_SUPPORT_ROW_AT_TIME, uploadLimit.getLookupValue()));
        }
        List<LookupDataValidation> lookupDataValidations = new ArrayList<>();
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
                LookupDataValidation lookupDataValidation = new LookupDataValidation();
                lookupDataValidation.setRowCounter(currentRow.getRowNum()+1);
                for (int i=0; i < sheetFiled.getColTitle().size(); i++) {
                    int index = 0;
                    if (i == index) {
                        lookupDataValidation.setLookupType(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        lookupDataValidation.setLookupCode(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        lookupDataValidation.setLookupValue(this.bulkExcel.getCellDetail(currentRow, i));
                    }  else if (i == ++index) {
                        lookupDataValidation.setUiLookup(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        lookupDataValidation.setDescription(this.bulkExcel.getCellDetail(currentRow, i));
                    }
                }
                lookupDataValidation.setParentLookupId(lookupDataRequest.getParentLookupId());
                lookupDataValidation.isValidBatch();
                Optional<LookupData> isAlreadyExistLookup = this.lookupDataRepository.findByLookupType(lookupDataValidation.getLookupType());
                if (isAlreadyExistLookup.isPresent()) {
                    lookupDataValidation.setErrorMsg(String.format(MessageUtil.LOOKUP_TYPE_ALREADY_USE_AT_ROW,
                        lookupDataValidation.getLookupType(), lookupDataValidation.getRowCounter()));
                }
                if (!BarcoUtil.isNull(lookupDataValidation.getErrorMsg())) {
                    errors.add(lookupDataValidation.getErrorMsg());
                    continue;
                }
                lookupDataValidations.add(lookupDataValidation);
            }
        }
        if (errors.size() > 0) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.TOTAL_INVALID, errors.size()), errors);
        }
        lookupDataValidations.forEach(lookupDataValidation -> {
            LookupData lookupData = new LookupData();
            if (!BarcoUtil.isNull(lookupDataValidation.getLookupCode())) {
                lookupData.setLookupCode(Long.valueOf(lookupDataValidation.getLookupCode()));
            }
            if (!BarcoUtil.isNull(lookupDataValidation.getUiLookup())) {
                GLookup gUI_LOOKUP = UI_LOOKUP.getStatusByLookupType(lookupDataValidation.getUiLookup());
                lookupData.setUiLookup(UI_LOOKUP.getByLookupCode(Long.parseLong(gUI_LOOKUP.getLookupCode().toString())));
            }
            lookupData.setLookupValue(lookupDataValidation.getLookupValue());
            lookupData.setLookupType(lookupDataValidation.getLookupType());
            if (!BarcoUtil.isNull(lookupDataValidation.getDescription())) {
                lookupData.setDescription(lookupDataValidation.getDescription());
            }
            if (!BarcoUtil.isNull(lookupDataRequest.getParentLookupId())) {
                Optional<LookupData> parentLookupData = this.lookupDataRepository.findById(lookupDataRequest.getParentLookupId());
                if (parentLookupData.isPresent()) {
                    lookupData.setParentLookup(parentLookupData.get());
                }
            }
            lookupData.setCreatedBy(appUser.get());
            lookupData.setUpdatedBy(appUser.get());
            lookupData.setStatus(APPLICATION_STATUS.ACTIVE);
            this.addNewLookupData(this.lookupDataRepository.save(lookupData));
        });
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, ""));
    }

    /**
     * Method use to get the parent lookup by parent lookup type
     * @param parentLookupType
     * @return LookupDataResponse
     * */
    @Override
    public LookupDataResponse getParentLookupDataByParentLookupType(String parentLookupType) {
        return this.lookupCacheMap.get(parentLookupType);
    }

    /**
     * Method use to get the child lookup by parent lookup type and child lookup code
     * @param parentLookupType
     * @param childLookupCode
     * @return LookupDataResponse
     * */
    @Override
    public LookupDataResponse getChildLookupDataByParentLookupTypeAndChildLookupCode(String parentLookupType, Long childLookupCode) {
        return this.getParentLookupDataByParentLookupType(parentLookupType).getLookupChildren().stream()
            .filter(childLookup -> childLookupCode.equals(childLookup.getLookupCode())).findAny().orElse(null);
    }


    public Map<String, LookupDataResponse> getLookupCacheMap() {
        return lookupCacheMap;
    }

    public void setLookupCacheMap(Map<String, LookupDataResponse> lookupCacheMap) {
        this.lookupCacheMap = lookupCacheMap;
    }

    public Map<String, SheetFiled> getSheetFiledMap() {
        return sheetFiledMap;
    }

    public void setSheetFiledMap(Map<String, SheetFiled> sheetFiledMap) {
        this.sheetFiledMap = sheetFiledMap;
    }

    /**
     * Method use to add new field into cache
     * @param payload
     * */
    private void addNewLookupData(LookupData payload) {
        this.writeLock.lock();
        try {
            this.lookupCacheMap.put(payload.getLookupType(), getLookupDataDetail(payload));
        } finally {
            this.writeLock.unlock();
        }
    }

}

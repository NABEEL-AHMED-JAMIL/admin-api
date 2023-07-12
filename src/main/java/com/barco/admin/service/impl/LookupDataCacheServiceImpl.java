package com.barco.admin.service.impl;

import com.amazonaws.util.IOUtils;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.common.utility.validation.LookupValidation;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.LookupDataResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.LookupData;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.LookupDataRepository;
import com.barco.model.util.ProcessUtil;
import com.barco.model.util.lookuputil.APPLICATION_STATUS;
import com.barco.model.util.lookuputil.GLookup;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
@Transactional
public class LookupDataCacheServiceImpl implements LookupDataCacheService {

    private Logger logger = LoggerFactory.getLogger(LookupDataCacheServiceImpl.class);

    private final String PARENT_LOOKUP_DATA = "parentLookupData";
    private final String SUB_LOOKUP_DATA = "subLookupData";

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();

    @Value("${storage.efsFileDire}")
    private String tempStoreDirectory;

    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private LookupDataRepository lookupDataRepository;

    private Map<String, LookupDataResponse> lookupCacheMap = new HashMap<>();

    /**
     * Method use to cache the data
     * */
    @PostConstruct
    public void initialize() {
        this.writeLock.lock();
        try {
            logger.info("****************Cache-Lookup-Start***************************");
            this.lookupCacheMap = new HashMap<>();
            this.lookupDataRepository.findByParentLookupIsNull()
            .forEach(lookupData -> {
                if (this.lookupCacheMap.containsKey(lookupData.getLookupType())) {
                    this.lookupCacheMap.put(lookupData.getLookupType(),
                        this.getLookupDataDetail(lookupData));
                } else {
                    this.lookupCacheMap.put(lookupData.getLookupType(),
                        this.getLookupDataDetail(lookupData));
                }
            });
            logger.info("***************Cache-Lookup-End********************************");
        } finally {
            this.writeLock.unlock();
        }
    }

    /**
     * Method use to add new filed into cache
     * */
    private void addNewLookupData(LookupData lookupData) {
        this.writeLock.lock();
        try {
            this.lookupCacheMap.put(lookupData.getLookupType(),
                this.getLookupDataDetail(lookupData));
        } finally {
            this.writeLock.unlock();
        }
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
            if (item.getValue().getUiLookup()) {
                lookupDataResponseMap.put(item.getKey(), item.getValue());
            }
        }
        return new AppResponse(ProcessUtil.SUCCESS, "LookupData.", lookupDataResponseMap);
    }

    /**
     * Method use to add new filed into db & cache
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse addLookupData(LookupDataRequest requestPayload) throws Exception {
        logger.info("Request addLookupData :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getLookupCode())) {
            return new AppResponse(ProcessUtil.ERROR, "LookupCode missing.");
        } else if (BarcoUtil.isNull(requestPayload.getLookupValue())) {
            return new AppResponse(ProcessUtil.ERROR, "LookupValue missing.");
        } else if (BarcoUtil.isNull(requestPayload.getLookupType())) {
            return new AppResponse(ProcessUtil.ERROR, "LookupType missing.");
        } else if (BarcoUtil.isNull(requestPayload.getDescription())) {
            return new AppResponse(ProcessUtil.ERROR, "Description missing.");
        } else if (BarcoUtil.isNull(requestPayload.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        } else if (this.lookupDataRepository.findByLookupType(requestPayload.getLookupType()).isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "LookupType already exist.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            requestPayload.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        LookupData lookupData = new LookupData();
        lookupData.setLookupValue(requestPayload.getLookupValue());
        lookupData.setLookupCode(requestPayload.getLookupCode());
        lookupData.setLookupType(requestPayload.getLookupType());
        lookupData.setUiLookup(requestPayload.isUiLookup());
        if (!BarcoUtil.isNull(requestPayload.getDescription())) {
            lookupData.setDescription(requestPayload.getDescription());
        }
        if (!BarcoUtil.isNull(requestPayload.getParentLookupId())) {
            Optional<LookupData> parentLookupData = this.lookupDataRepository.findById(
                requestPayload.getParentLookupId());
            if (parentLookupData.isPresent()) {
                lookupData.setParentLookup(parentLookupData.get());
            }
        }
        lookupData.setAppUser(appUser.get());
        this.addNewLookupData(this.lookupDataRepository.save(lookupData));
        return new AppResponse(ProcessUtil.SUCCESS, String.format(
            "LookupData save with %d.",lookupData.getLookupId()));
    }


    /**
     * Method use to update new filed into db & cache
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateLookupData(LookupDataRequest requestPayload) throws Exception {
        logger.info("Request updateLookupData :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getLookupId())) {
            return new AppResponse(ProcessUtil.ERROR, "LookupId missing.");
        } else if (BarcoUtil.isNull(requestPayload.getLookupCode())) {
            return new AppResponse(ProcessUtil.ERROR, "LookupCode missing.");
        } else if (BarcoUtil.isNull(requestPayload.getLookupValue())) {
            return new AppResponse(ProcessUtil.ERROR, "LookupValue missing.");
        } else if (BarcoUtil.isNull(requestPayload.getLookupType())) {
            return new AppResponse(ProcessUtil.ERROR, "LookupType missing.");
        } else if (BarcoUtil.isNull(requestPayload.getDescription())) {
            return new AppResponse(ProcessUtil.ERROR, "Description missing.");
        } else if (BarcoUtil.isNull(requestPayload.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            requestPayload.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        Optional<LookupData> lookupData = this.lookupDataRepository.findById(requestPayload.getLookupId());
        if (lookupData.isPresent()) {
            lookupData.get().setLookupValue(requestPayload.getLookupValue());
            lookupData.get().setLookupCode(requestPayload.getLookupCode());
            lookupData.get().setLookupType(requestPayload.getLookupType());
            lookupData.get().setUiLookup(requestPayload.isUiLookup());
            if (!BarcoUtil.isNull(requestPayload.getDescription())) {
                lookupData.get().setDescription(requestPayload.getDescription());
            }
            this.lookupDataRepository.save(lookupData.get());
            this.initialize(); // its update and add new also
            return new AppResponse(ProcessUtil.SUCCESS, String.format(
                "LookupData update with %d.", requestPayload.getLookupId()));
        }
        return new AppResponse(ProcessUtil.ERROR, String.format(
            "LookupData not found with %d.", requestPayload.getLookupId()));
    }

    /**
     * Method use to fetch sub lookup by ParentId
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchSubLookupByParentId(LookupDataRequest requestPayload) throws Exception {
        logger.info("Request fetchSubLookupByParentId :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getParentLookupId())) {
            return new AppResponse(ProcessUtil.ERROR, "ParentLookupId missing.");
        } else if (BarcoUtil.isNull(requestPayload.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            requestPayload.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        Map<String, Object> appSettingDetail = new HashMap<>();
        List<LookupDataResponse> lookupDataResponses = new ArrayList<>();
        Optional<LookupData> parentLookup = this.lookupDataRepository.findByParentLookupAndAppUserUsername(
            requestPayload.getParentLookupId(), requestPayload.getAccessUserDetail().getUsername());
        if (!parentLookup.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, String.format(
                "LookupData not found with %s.", requestPayload));
        }
        LookupDataResponse parentLookupDataResponse = new LookupDataResponse();
        this.fillLookupDateDto(parentLookup.get(), parentLookupDataResponse);
        appSettingDetail.put(PARENT_LOOKUP_DATA, parentLookupDataResponse);
        if (!BarcoUtil.isNull(parentLookup.get().getLookupChildren())) {
            for (LookupData lookup: parentLookup.get().getLookupChildren()) {
                LookupDataResponse childLookupDataResponse = new LookupDataResponse();
                this.fillLookupDateDto(lookup, childLookupDataResponse);
                lookupDataResponses.add(childLookupDataResponse);
            }
        }
        appSettingDetail.put(SUB_LOOKUP_DATA, lookupDataResponses);
        return new AppResponse(ProcessUtil.SUCCESS,
            "Data fetch successfully.", appSettingDetail);
    }

    /**
     * Method use to fetch lookup by lookup type
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchLookupByLookupType(LookupDataRequest requestPayload) throws Exception {
        logger.info("Request fetchLookupByLookupType :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getLookupType())) {
            return new AppResponse(ProcessUtil.ERROR, "LookupType missing.");
        }
        if (requestPayload.isValidate()) {
            if (BarcoUtil.isNull(requestPayload.getAccessUserDetail().getUsername())) {
                return new AppResponse(ProcessUtil.ERROR, "Username missing.");
            } else if (!this.appUserRepository.findByUsernameAndStatus(requestPayload.getAccessUserDetail()
                .getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode()).isPresent()) {
                return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
            }
        }
        Map<String, Object> appSettingDetail = new HashMap<>();
        List<LookupDataResponse> lookupDataResponses = new ArrayList<>();
        Optional<LookupData> parentLookup = this.lookupDataRepository.findByLookupType(requestPayload.getLookupType());
        if (!parentLookup.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, String.format(
                "LookupData not found with %s.", requestPayload));
        }
        LookupDataResponse parentLookupDataResponse = new LookupDataResponse();
        this.fillLookupDateDto(parentLookup.get(), parentLookupDataResponse);
        appSettingDetail.put(PARENT_LOOKUP_DATA, parentLookupDataResponse);
        if (!BarcoUtil.isNull(parentLookup.get().getLookupChildren())) {
            for (LookupData lookup: parentLookup.get().getLookupChildren()) {
                LookupDataResponse childLookupDataResponse = new LookupDataResponse();
                this.fillLookupDateDto(lookup, childLookupDataResponse);
                lookupDataResponses.add(childLookupDataResponse);
            }
        }
        appSettingDetail.put(SUB_LOOKUP_DATA, lookupDataResponses);
        return new AppResponse(ProcessUtil.SUCCESS, "Data fetch successfully.", appSettingDetail);
    }

    /**
     * Method use to fetch all lookup by user id
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllLookup(LookupDataRequest requestPayload) throws Exception {
        logger.info("Request fetchAllLookup :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser username missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            requestPayload.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        List<LookupData> lookupDataList = this.lookupDataRepository.fetchAllLookup(appUser.get().getUsername());
        List<LookupDataResponse> lookupDataResponses = new ArrayList<>();
        if (!lookupDataList.isEmpty()) {
            for (LookupData lookup: lookupDataList) {
                LookupDataResponse lookupDataResponse = new LookupDataResponse();
                this.fillLookupDateDto(lookup, lookupDataResponse);
                lookupDataResponses.add(lookupDataResponse);
            }
        }
        return new AppResponse(ProcessUtil.SUCCESS, "Data fetch successfully.", lookupDataResponses);
    }

    /**
     * Method use to delete the lookUp by lookup id and username
     * @param requestPayload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteLookupData(LookupDataRequest requestPayload) throws Exception {
        logger.info("Request deleteLookupData :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getLookupId())) {
            return new AppResponse(ProcessUtil.ERROR, "LookupData id missing.");
        } else if (BarcoUtil.isNull(requestPayload.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser username missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            requestPayload.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        this.lookupDataRepository.deleteById(requestPayload.getLookupId());
        this.initialize(); // its update and add new also
        return new AppResponse(ProcessUtil.SUCCESS, String.format(
            "LookupData delete with %d.", requestPayload.getLookupId()));
    }

    /**
     * Method use to download lookup template
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadLookupTemplateFile() throws Exception {
        String basePath = this.tempStoreDirectory + File.separator;
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream inputStream = cl.getResourceAsStream(this.bulkExcel.BATCH);
        String fileUploadPath = basePath + System.currentTimeMillis() + this.bulkExcel.XLSX_EXTENSION;
        FileOutputStream fileOut = new FileOutputStream(fileUploadPath);
        IOUtils.copy(inputStream, fileOut);
        // after copy the stream into file close
        if (inputStream != null) {
            inputStream.close();
        }
        // 2nd insert data to newly copied file. So that template couldn't be changed.
        XSSFWorkbook workbook = new XSSFWorkbook(new File(fileUploadPath));
        this.bulkExcel.setWb(workbook);
        XSSFSheet sheet = workbook.getSheet(this.bulkExcel.LOOKUP);
        this.bulkExcel.setSheet(sheet);
        this.bulkExcel.fillBulkHeader(0, this.bulkExcel.LOOKUP_HEADER_FILED_BATCH_FILE);
        // Priority
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        // read the file
        File file = new File(fileUploadPath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(Files.readAllBytes(file.toPath()));
        file.delete();
        return byteArrayOutputStream;
    }

    /**
     * Method use to download lookup file with content
     * @param requestPayload
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadLookup(LookupDataRequest requestPayload) throws Exception {
        logger.info("Request deleteLookupData :- " + requestPayload);
        if (BarcoUtil.isNull(requestPayload.getAccessUserDetail().getUsername())) {
            throw new Exception("AppUser username missing");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            requestPayload.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            throw new Exception("AppUser not found");
        }
        List<LookupData> lookupDataList;
        if (BarcoUtil.isNull(requestPayload.getParentLookupId())) {
            lookupDataList = this.lookupDataRepository.fetchAllLookup(appUser.get().getUsername());
        } else {
            Optional<LookupData> parentLookup = this.lookupDataRepository.findByParentLookupAndAppUserUsername(
                requestPayload.getParentLookupId(), appUser.get().getUsername());
            if (!parentLookup.isPresent()) {
                throw new Exception("ParentLookup not found");
            }
            lookupDataList = parentLookup.get().getLookupChildren().stream().collect(Collectors.toList());
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        this.bulkExcel.setWb(workbook);
        XSSFSheet xssfSheet = workbook.createSheet(this.bulkExcel.LOOKUP);
        this.bulkExcel.setSheet(xssfSheet);
        AtomicInteger rowCount = new AtomicInteger();
        this.bulkExcel.fillBulkHeader(rowCount.get(), this.bulkExcel.LOOKUP_HEADER_FILED_BATCH_FILE);
        lookupDataList.forEach(sourceJob -> {
            rowCount.getAndIncrement();
            List<String> dataCellValue = new ArrayList<>();
            dataCellValue.add(!BarcoUtil.isNull(sourceJob.getLookupCode()) ?
                String.valueOf(sourceJob.getLookupCode()) : "");
            dataCellValue.add(sourceJob.getLookupType());
            dataCellValue.add(sourceJob.getLookupValue());
            dataCellValue.add(sourceJob.getDescription());
            this.bulkExcel.fillBulkBody(dataCellValue, rowCount.get());
        });
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream;
    }

    /**
     * Method use to upload lookup file with content
     * @param fileObject
     * @return ByteArrayOutputStream
     * */
    @Override
    public AppResponse uploadLookup(FileUploadRequest fileObject) throws Exception {
        logger.info("Request for bulk uploading file!");
        LookupDataRequest lookupDataRequest = new Gson().fromJson((String)
            fileObject.getData(), LookupDataRequest.class);
        if (BarcoUtil.isNull(fileObject.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser username missing.");
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            fileObject.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!appUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        } else if (!fileObject.getFile().getContentType().equalsIgnoreCase(this.bulkExcel.SHEET_NAME)) {
            logger.info("File Type " + fileObject.getFile().getContentType());
            return new AppResponse(ProcessUtil.ERROR, "You can upload only .xlsx extension file.");
        }
        // fill the stream with file into work-book
        LookupDataResponse lookupDataResponse = this.getParentLookupById(ProcessUtil.UPLOAD_LIMIT);
        XSSFWorkbook workbook = new XSSFWorkbook(fileObject.getFile().getInputStream());
        if (BarcoUtil.isNull(workbook) || workbook.getNumberOfSheets() == 0) {
            return new AppResponse(ProcessUtil.ERROR,  "You uploaded empty file.");
        }
        XSSFSheet sheet = workbook.getSheet(this.bulkExcel.LOOKUP);
        if (BarcoUtil.isNull(sheet)) {
            return new AppResponse(ProcessUtil.ERROR, "Sheet not found with (LookupTemplate)");
        } else if (sheet.getLastRowNum() < 1) {
            return new AppResponse(ProcessUtil.ERROR,  "You can't upload empty file.");
        } else if (sheet.getLastRowNum() > Long.valueOf(lookupDataResponse.getLookupValue())) {
            return new AppResponse(ProcessUtil.ERROR, String.format("File support %s rows at a time.",
                lookupDataResponse.getLookupValue()));
        }
        List<LookupValidation> lookupValidations = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Iterator<Row> rows = sheet.iterator();
        while (rows.hasNext()) {
            Row currentRow = rows.next();
            if (currentRow.getRowNum() == 0) {
                for (int i=0; i < this.bulkExcel.LOOKUP_HEADER_FILED_BATCH_FILE.length; i++) {
                    if (!currentRow.getCell(i).getStringCellValue().equals(this.bulkExcel.LOOKUP_HEADER_FILED_BATCH_FILE[i])) {
                        return new AppResponse(ProcessUtil.ERROR, "File at row " + (currentRow.getRowNum() + 1) + " " +
                            this.bulkExcel.LOOKUP_HEADER_FILED_BATCH_FILE[i] + " heading missing.");
                    }
                }
            } else if (currentRow.getRowNum() > 0) {
                LookupValidation lookupValidation = new LookupValidation();
                lookupValidation.setRowCounter(currentRow.getRowNum()+1);
                for (int i=0; i < this.bulkExcel.LOOKUP_HEADER_FILED_BATCH_FILE.length; i++) {
                    int index = 0;
                    if (i == index) {
                        lookupValidation.setLookupCode(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        lookupValidation.setLookupType(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        lookupValidation.setLookupValue(this.bulkExcel.getCellDetail(currentRow, i));
                    } else if (i == ++index) {
                        lookupValidation.setDescription(this.bulkExcel.getCellDetail(currentRow, i));
                    }
                }
                lookupValidation.setParentLookupId(lookupDataRequest.getParentLookupId());
                lookupValidation.isValidLookup();
                Optional<LookupData> isAlreadyExistLookup = this.lookupDataRepository.findByLookupType(
                    lookupValidation.getLookupType());
                if (isAlreadyExistLookup.isPresent()) {
                    lookupValidation.setErrorMsg(String.format("LookupType %s already in use at row %s.<br>",
                        lookupValidation.getLookupType(), lookupValidation.getRowCounter()));
                }
                if (!BarcoUtil.isNull(lookupValidation.getErrorMsg())) {
                    errors.add(lookupValidation.getErrorMsg());
                    continue;
                }
                lookupValidations.add(lookupValidation);
            }
        }
        if (errors.size() > 0) {
            return new AppResponse(ProcessUtil.ERROR, String.format(
                "Total %d source jobs invalid.", errors.size()), errors);
        }
        lookupValidations.forEach(lookupValidation -> {
            LookupData lookupData = new LookupData();
            if (!BarcoUtil.isNull(lookupValidation.getLookupCode())) {
                lookupData.setLookupCode(Long.valueOf(lookupValidation.getLookupCode()));
            }
            lookupData.setLookupValue(lookupValidation.getLookupValue());
            lookupData.setLookupType(lookupValidation.getLookupType());
            if (!BarcoUtil.isNull(lookupValidation.getDescription())) {
                lookupData.setDescription(lookupValidation.getDescription());
            }
            if (!BarcoUtil.isNull(lookupDataRequest.getParentLookupId())) {
                Optional<LookupData> parentLookupData = this.lookupDataRepository
                    .findById(lookupDataRequest.getParentLookupId());
                if (parentLookupData.isPresent()) {
                    lookupData.setParentLookup(parentLookupData.get());
                }
            }
            lookupData.setAppUser(appUser.get());
            this.addNewLookupData(this.lookupDataRepository.save(lookupData));
        });
        return new AppResponse(ProcessUtil.SUCCESS, "Data save successfully.");
    }

    private void fillLookupDateDto(LookupData lookupData, LookupDataResponse lookupDataResponse) {
        lookupDataResponse.setLookupId(lookupData.getLookupId());
        lookupDataResponse.setLookupCode(lookupData.getLookupCode());
        lookupDataResponse.setLookupValue(lookupData.getLookupValue());
        lookupDataResponse.setLookupType(lookupData.getLookupType());
        lookupDataResponse.setDescription(lookupData.getDescription());
        lookupDataResponse.setDateCreated(lookupData.getDateCreated());
        lookupDataResponse.setUiLookup(lookupData.getUiLookup());
    }

    private LookupDataResponse getLookupDataDetail(LookupData lookupData) {
        LookupDataResponse parentLookupData = new LookupDataResponse();
        parentLookupData.setLookupId(lookupData.getLookupId());
        parentLookupData.setLookupCode(lookupData.getLookupCode());
        parentLookupData.setLookupType(lookupData.getLookupType());
        parentLookupData.setLookupValue(lookupData.getLookupValue());
        parentLookupData.setDescription(lookupData.getDescription());
        parentLookupData.setDateCreated(lookupData.getDateCreated());
        parentLookupData.setUiLookup(lookupData.getUiLookup());
        if (!BarcoUtil.isNull(lookupData.getLookupChildren()) &&
            lookupData.getLookupChildren().size() > 0) {
            parentLookupData.setLookupChildren(lookupData.getLookupChildren()
            .stream().map(childLookup -> {
                LookupDataResponse childLookupData =new LookupDataResponse();
                childLookupData.setLookupId(childLookup.getLookupId());
                childLookupData.setLookupCode(childLookup.getLookupCode());
                childLookupData.setLookupType(childLookup.getLookupType());
                childLookupData.setLookupValue(childLookup.getLookupValue());
                childLookupData.setDescription(childLookup.getDescription());
                childLookupData.setDateCreated(childLookup.getDateCreated());
                childLookupData.setDateCreated(childLookup.getDateCreated());
                childLookupData.setUiLookup(childLookup.getUiLookup());
                return childLookupData;
            }).collect(Collectors.toSet()));
        }
        return parentLookupData;
    }

    public LookupDataResponse getParentLookupById(String lookupType) {
        return this.lookupCacheMap.get(lookupType);
    }

    public LookupDataResponse getChildLookupById(String parentLookupType, Long childLookupCode) {
        return this.getParentLookupById(parentLookupType).getLookupChildren().stream()
            .filter(childLookup -> childLookupCode.equals(childLookup.getLookupCode()))
            .findAny().orElse(null);
    }

    public GLookup<Long, String> getGLookup(String parentLookup, Long childLookupCode) {
        LookupDataResponse lookupDataResponse = this.getChildLookupById(parentLookup, childLookupCode);
        return lookupDataResponse != null ? new GLookup<>(lookupDataResponse.getLookupType(),
                lookupDataResponse.getLookupCode(), lookupDataResponse.getLookupValue()) : null;
    }

    public Map<String, LookupDataResponse> getLookupCacheMap() {
        return lookupCacheMap;
    }

    public void setLookupCacheMap(Map<String, LookupDataResponse> lookupCacheMap) {
        this.lookupCacheMap = lookupCacheMap;
    }

}
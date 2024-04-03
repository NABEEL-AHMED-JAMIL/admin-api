package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.MgGroupService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.common.utility.excel.SheetFiled;
import com.barco.common.utility.validation.RPPValidation;
import com.barco.model.dto.request.EnVariablesRequest;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.GroupRequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.GroupResponse;
import com.barco.model.dto.response.LookupDataResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.Groups;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.GroupsRepository;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class MgGroupServiceImpl implements MgGroupService {

    private Logger logger = LoggerFactory.getLogger(MgGroupServiceImpl.class);

    @Value("${storage.efsFileDire}")
    private String tempStoreDirectory;
    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    /***
     * Method use to add group
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addGroup(GroupRequest payload) throws Exception {
        logger.info("Request addGroup :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.GROUP_NAME_MISSING);
        }
        Groups groups = new Groups();
        groups.setName(payload.getName());
        groups.setDescription(payload.getDescription());
        groups.setCreatedBy(adminUser.get());
        groups.setUpdatedBy(adminUser.get());
        groups.setStatus(APPLICATION_STATUS.ACTIVE);
        groups = this.groupsRepository.save(groups);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, groups.getId().toString()), payload);
    }

    /***
     * Method use to edit group
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateGroup(GroupRequest payload) throws Exception {
        logger.info("Request updateGroup :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.GROUP_ID_REQUIRED);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.GROUP_NAME_MISSING);
        }
        Optional<Groups> groups = this.groupsRepository.findByIdAndStatusNot(
            payload.getId(), APPLICATION_STATUS.DELETE);
        if (!groups.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.GROUP_NOT_FOUND);
        }
        groups.get().setName(payload.getName());
        groups.get().setDescription(payload.getDescription());
        groups.get().setUpdatedBy(adminUser.get());
        groups.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        this.groupsRepository.save(groups.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(
            MessageUtil.DATA_UPDATE, payload.getId().toString()), payload);
    }

    /***
     * Method use to fetch all group
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllGroup(GroupRequest payload) throws Exception {
        logger.info("Request fetchAllEnVariable :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.groupsRepository.findAllByCreatedByAndStatusNot(adminUser.get(), APPLICATION_STATUS.DELETE)
                .stream().map(groups -> getGroupResponse(groups)).collect(Collectors.toList()));
    }

    /**
     * Method use to fetch group by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchGroupById(GroupRequest payload) throws Exception {
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.PERMISSION_ID_MISSING, payload);
        }
        Optional<Groups> groups = this.groupsRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE);
        if (!groups.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.GROUP_NOT_FOUND_WITH_ID, payload.getId()), payload);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, getGroupResponse(groups.get()));
    }

    /**
     * Method use to delete group by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteGroupById(GroupRequest payload) throws Exception {
        logger.info("Request deleteGroupById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.GROUP_ID_REQUIRED);
        }
        Optional<Groups> groups = this.groupsRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE);
        if (!groups.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.GROUP_NOT_FOUND);
        }
        groups.get().setStatus(APPLICATION_STATUS.DELETE);
        this.groupsRepository.save(groups.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(
            MessageUtil.DATA_DELETED, payload.getId().toString()), payload);
    }

    @Override
    public AppResponse addGroupTeamLead(GroupRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse fetchLinkGroupWithUser(GroupRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use to link group => user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkGroupWithUser(GroupRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use to download e-variable template
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadGroupTemplateFile() throws Exception {
        logger.info("Request downloadGroupTemplateFile");
        return downloadTemplateFile(this.tempStoreDirectory, this.bulkExcel,
            this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.GROUP));
    }

    /**
     * Method use to download e-variable data
     * @param payload
     * @return ByteArrayOutputStream
     * */
    @Override
    public ByteArrayOutputStream downloadGroup(EnVariablesRequest payload) throws Exception {
        logger.info("Request downloadGroup :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            throw new Exception(MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            throw new Exception(MessageUtil.APPUSER_NOT_FOUND);
        }
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.GROUP);
        XSSFWorkbook workbook = new XSSFWorkbook();
        this.bulkExcel.setWb(workbook);
        XSSFSheet xssfSheet = workbook.createSheet(sheetFiled.getSheetName());
        this.bulkExcel.setSheet(xssfSheet);
        AtomicInteger rowCount = new AtomicInteger();
        this.bulkExcel.fillBulkHeader(rowCount.get(), sheetFiled.getColTitle());
        Iterator<Groups> groupsIterator = this.groupsRepository.findAllByCreatedByAndStatusNot(appUser.get(),
            APPLICATION_STATUS.DELETE).iterator();
        while (groupsIterator.hasNext()) {
            Groups groups = groupsIterator.next();
            rowCount.getAndIncrement();
            List<String> dataCellValue = new ArrayList<>();
            dataCellValue.add(groups.getName());
            dataCellValue.add(groups.getDescription());
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
    public AppResponse uploadGroup(FileUploadRequest payload) throws Exception {
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
        SheetFiled sheetFiled = this.lookupDataCacheService.getSheetFiledMap().get(this.bulkExcel.GROUP);
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
                if (!BarcoUtil.isNull(rppValidation.getErrorMsg())) {
                    errors.add(rppValidation.getErrorMsg());
                    continue;
                }
                rppValidationsList.add(rppValidation);
            }
        }
        if (errors.size() > 0) {
            return new AppResponse(BarcoUtil.ERROR, String.format(
                MessageUtil.TOTAL_INVALID, errors.size()), errors);
        }
        rppValidationsList.forEach(rppValidation -> {
            Groups groups = new Groups();
            groups.setName(rppValidation.getName());
            groups.setDescription(rppValidation.getDescription());
            groups.setCreatedBy(appUser.get());
            groups.setUpdatedBy(appUser.get());
            groups.setStatus(APPLICATION_STATUS.ACTIVE);
            this.groupsRepository.save(groups);
        });
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, ""));
    }

    private GroupResponse getGroupResponse(Groups groups) {
        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setId(groups.getId());
        groupResponse.setName(groups.getName());
        groupResponse.setDescription(groups.getDescription());
        groupResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(groups.getStatus().getLookupType()));
        groupResponse.setCreatedBy(getActionUser(groups.getCreatedBy()));
        groupResponse.setUpdatedBy(getActionUser(groups.getUpdatedBy()));
        groupResponse.setDateUpdated(groups.getDateUpdated());
        groupResponse.setDateCreated(groups.getDateCreated());
        return groupResponse;
    }
}

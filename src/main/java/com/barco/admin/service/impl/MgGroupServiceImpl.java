package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.MgGroupService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.common.utility.excel.SheetFiled;
import com.barco.common.utility.validation.RPPValidation;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.GroupResponse;
import com.barco.model.dto.response.LookupDataResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.GroupUser;
import com.barco.model.pojo.Groups;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.GroupsRepository;
import com.barco.model.repository.GroupUserRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.GROUP_USER_TYPE;
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
import java.sql.Timestamp;
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
    private GroupUserRepository groupUserRepository;
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
        Optional<Groups> groups = this.groupsRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE);
        if (!groups.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.GROUP_NOT_FOUND);
        }
        groups.get().setName(payload.getName());
        groups.get().setDescription(payload.getDescription());
        groups.get().setUpdatedBy(adminUser.get());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            groups.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
            groups.get().getGroupUsers().stream()
           .map(groupUser -> {
               if (!groupUser.getStatus().equals(APPLICATION_STATUS.DELETE)) {
                   groupUser.setStatus(groups.get().getStatus());
                   groupUser.setUpdatedBy(adminUser.get());
               }
               return groupUser;
           }).collect(Collectors.toList());
        }
        this.groupsRepository.save(groups.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()), payload);
    }

    /***
     * Method use to fetch all group
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllGroup(GroupRequest payload) throws Exception {
        logger.info("Request fetchAllGroup :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
        List<GroupResponse> groupResponses = this.groupsRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
            startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE)
            .stream().map(groups -> {
                GroupResponse groupResponse = getGroupResponse(groups);
                groupResponse.setTotalUser(groups.getGroupUsers().stream()
                    .filter(groupUser -> !groupUser.getUserType().equals(GROUP_USER_TYPE.MANAGER)
                        && !groupUser.getStatus().equals(APPLICATION_STATUS.DELETE)).count());
                return groupResponse;
            }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, groupResponses);
    }

    /**
     * Method use to fetch group by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchGroupById(GroupRequest payload) throws Exception {
        logger.info("Request fetchGroupById :- " + payload);
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
        groups.get().getGroupUsers().stream().map(groupUser -> {
            if (!groupUser.getStatus().equals(APPLICATION_STATUS.DELETE)) {
                groupUser.setStatus(groups.get().getStatus());
                groupUser.setUpdatedBy(adminUser.get());
            }
            return groupUser;
        }).collect(Collectors.toList());
        this.groupsRepository.save(groups.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()), payload);
    }

    /**
     * Method use to delete all group
     * */
    @Override
    public AppResponse deleteAllGroup(GroupRequest payload) throws Exception {
        logger.info("Request deleteAllGroup :- " + payload);
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
        List<Groups> groups = this.groupsRepository.findAllByIdInAndStatusNot(payload.getIds(), APPLICATION_STATUS.DELETE);
        if (groups.size() == 0) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.GROUP_NOT_FOUND);
        }
        // delete group and delete user
        groups.stream().map(group -> {
            group.setStatus(APPLICATION_STATUS.DELETE);
            group.getGroupUsers().stream().map(groupUser -> {
                if (!groupUser.getStatus().equals(APPLICATION_STATUS.DELETE)) {
                    groupUser.setStatus(group.getStatus());
                    groupUser.setUpdatedBy(adminUser.get());
                }
                return groupUser;
            }).collect(Collectors.toList());
            return group;
        });
        this.groupsRepository.saveAll(groups);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, ""), payload);
    }

    /**
     * Method use to add group team user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addGroupTeamLead(GroupUserRequest payload) throws Exception {
        logger.info("Request addGroupTeamLead :- " + payload);
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
        Optional<AppUser> teamLead = this.appUserRepository.findByIdAndStatus(payload.getTeamLeadId(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Optional<Groups> groups = this.groupsRepository.findByIdAndStatusNot(payload.getId(), APPLICATION_STATUS.DELETE);
        if (!groups.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.GROUP_NOT_FOUND);
        }
        if (BarcoUtil.isNull(groups.get().getGroupUsers())) {
            groups.get().setGroupUsers(new ArrayList<>());
            groups.get().getGroupUsers().add(getGroupUser(groups.get(), adminUser.get(), teamLead.get(), GROUP_USER_TYPE.MANAGER));
            this.groupsRepository.save(groups.get());
            return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId().toString()), payload);
        } else {
            // check if the team lead id change then delete the old one and add new one
            Optional<GroupUser> groupUser = this.groupUserRepository.findByAppUserAndGroupsAndUserTypeAndStatusNot(
                teamLead.get(), groups.get(), GROUP_USER_TYPE.MANAGER, APPLICATION_STATUS.DELETE);
            // its mean the team lead change so change the old one to delete and add new one
            if (!groupUser.isPresent()) {
                groups.get().getGroupUsers().stream().map(updateGrpUser -> {
                    if (updateGrpUser.getStatus() != APPLICATION_STATUS.DELETE &&
                        updateGrpUser.getUserType() == GROUP_USER_TYPE.MANAGER) {
                        updateGrpUser.setStatus(APPLICATION_STATUS.DELETE);
                        updateGrpUser.setUpdatedBy(adminUser.get());
                    }
                    return updateGrpUser;
                }).collect(Collectors.toList());
                groups.get().getGroupUsers().add(getGroupUser(groups.get(), adminUser.get(), teamLead.get(), GROUP_USER_TYPE.MANAGER));
                this.groupsRepository.save(groups.get());
            }
            return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()), payload);
        }
    }

    /**
     * Method use to fetch link group => team user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchLinkGroupWithUser(GroupRequest payload) throws Exception {
        logger.info("Request fetchLinkGroupWithUser");
        return null;
    }

    /**
     * Method use to link group => user
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse linkGroupWithUser(GroupRequest payload) throws Exception {
        logger.info("Request linkGroupWithUser");
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
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
        Iterator<Groups> groups;
        if (!BarcoUtil.isNull(payload.getIds()) && payload.getIds().size() > 0) {
            groups = this.groupsRepository.findAllByDateCreatedBetweenAndCreatedByAndIdInAndStatusNotOrderByDateCreatedDesc(
                startDate, endDate, appUser.get(), payload.getIds(), APPLICATION_STATUS.DELETE).iterator();
        } else {
            groups = this.groupsRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
                startDate, endDate, appUser.get(), APPLICATION_STATUS.DELETE).iterator();
        }
        while (groups.hasNext()) {
            Groups group = groups.next();
            rowCount.getAndIncrement();
            List<String> dataCellValue = new ArrayList<>();
            dataCellValue.add(group.getName());
            dataCellValue.add(group.getDescription());
            this.bulkExcel.fillBulkBody(dataCellValue, rowCount.get());
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        return outStream;
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
            return new AppResponse(BarcoUtil.ERROR, String.format(MessageUtil.TOTAL_INVALID, errors.size()), errors);
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

}

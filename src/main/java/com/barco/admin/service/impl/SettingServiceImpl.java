package com.barco.admin.service.impl;

import com.barco.admin.service.SettingService;
import com.barco.model.dto.request.QueryInquiryRequest;
import com.barco.model.dto.request.SessionUser;
import com.barco.model.dto.response.QueryInquiryResponse;
import com.barco.model.dto.response.QueryResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.QueryInquiry;
import com.barco.model.repository.ETLCountryRepository;
import com.barco.model.repository.QueryInquiryRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.model.dto.request.QueryRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.repository.AppUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 * TemplateReg can be email and etc
 */
@Service
public class SettingServiceImpl implements SettingService {

    private Logger logger = LoggerFactory.getLogger(SettingServiceImpl.class);

    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private QueryService queryService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private ETLCountryRepository etlCountryRepository;
    @Autowired
    private QueryInquiryRepository queryInquiryRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    public SettingServiceImpl() {}

    /**
     * Method use to fetch the detail for dashboard
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse fetchStatisticsDashboard(SessionUser payload) throws Exception {
        logger.info("Request fetchStatisticsDashboard :- {}.", payload);
        if (BarcoUtil.isNull(payload.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(payload.getUsername(), APPLICATION_STATUS.ACTIVE);
        Map<String, Object> settingDashboard = new HashMap<>();
        // APP_SETTING_STATISTICS
        String APP_SETTING_STATISTICS = "APP_SETTING_STATISTICS";
        settingDashboard.put(APP_SETTING_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.APP_SETTING_STATISTICS, appUser.get().getId())));
        // PROFILE_SETTING_STATISTICS
        String PROFILE_SETTING_STATISTICS = "PROFILE_SETTING_STATISTICS";
        settingDashboard.put(PROFILE_SETTING_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.PROFILE_SETTING_STATISTICS, appUser.get().getId())));
        // FORM_SETTING_STATISTICS
        String FORM_SETTING_STATISTICS = "FORM_SETTING_STATISTICS";
        settingDashboard.put(FORM_SETTING_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.FORM_SETTING_STATISTICS, appUser.get().getId())));
        // DASHBOARD_AND_REPORT_SETTING_STATISTICS
        String DASHBOARD_AND_REPORT_SETTING_STATISTICS = "DASHBOARD_AND_REPORT_SETTING_STATISTICS";
        settingDashboard.put(DASHBOARD_AND_REPORT_SETTING_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.DASHBOARD_AND_REPORT_SETTING_STATISTICS, appUser.get().getId())));
        // SERVICE_SETTING_STATISTICS
        String SERVICE_SETTING_STATISTICS = "SERVICE_SETTING_STATISTICS";
        settingDashboard.put(SERVICE_SETTING_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.SERVICE_SETTING_STATISTICS, appUser.get().getId())));
        // SESSION_COUNT_STATISTICS
        String SESSION_COUNT_STATISTICS = "SESSION_COUNT_STATISTICS";
        settingDashboard.put(SESSION_COUNT_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.SESSION_COUNT_STATISTICS, appUser.get().getId())));
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, settingDashboard);
    }

    /**
     * Method use to fetch the detail for country
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse fetchCountryData(SessionUser payload) throws Exception {
        logger.info("Request fetchCountryData :- {}.", payload);
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.etlCountryRepository.findAll());
    }

    /**
     * Method use to query the data access only for super admin
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse dynamicQueryResponse(QueryRequest payload) throws Exception {
        logger.info("Request dynamicQueryResponse :- {}.", payload);
        if (BarcoUtil.isNull(payload.getQuery())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.QUERY_MISSING);
        }
        payload.setQuery(payload.getQuery().trim());
        if (!payload.getQuery().toLowerCase().startsWith(BarcoUtil.SELECT)) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ONLY_SELECT_QUERY_EXECUTE);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.queryService.executeQueryResponse(payload.getQuery()));
    }

    /**
     * Method use to query & download result
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public ByteArrayOutputStream downloadDynamicQueryFile(QueryRequest payload) throws Exception {
        logger.info("Request downloadDynamicQueryFile :- {}.", payload);
        if (BarcoUtil.isNull(payload.getQuery())) {
            throw new Exception(MessageUtil.QUERY_MISSING);
        }
        payload.setQuery(payload.getQuery().trim());
        if (!payload.getQuery().toLowerCase().startsWith(BarcoUtil.SELECT)) {
            throw new Exception(MessageUtil.ONLY_SELECT_QUERY_EXECUTE);
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(payload.getQuery());
        XSSFWorkbook workbook = new XSSFWorkbook();
        this.bulkExcel.setWb(workbook);
        this.bulkExcel.setSheet(workbook.createSheet(this.bulkExcel.QUERY_RESPONSE));
        AtomicInteger rowCount = new AtomicInteger();
        Set<String> column = queryResponse.getColumn();
        String[] header = column.toArray(new String[0]);
        this.bulkExcel.fillBulkHeader(rowCount.get(), Arrays.asList(header));
        for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
            rowCount.getAndIncrement();
            List<String> dataCellValue = new ArrayList<>();
            column.forEach(col -> dataCellValue.add(String.valueOf(!BarcoUtil.isNull(data.get(col)) ? data.get(col): "")));
            this.bulkExcel.fillBulkBody(dataCellValue, rowCount.get());
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        workbook.write(outStream);
        return outStream;
    }

    /**
     * Method use to add the query inquiry
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse addQueryInquiry(QueryInquiryRequest payload) throws Exception {
        logger.info("Request addQueryInquiry :- {}.", payload);
        AppResponse validationResponse = this.validateAddOrUpdatePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        QueryInquiry queryInquiry = this.createQueryInquiry(payload);
        this.queryInquiryRepository.save(queryInquiry);
        payload.setId(queryInquiry.getId());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId().toString()));
    }

    /**
     * Method use to update the query inquiry
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse updateQueryInquiry(QueryInquiryRequest payload) throws Exception {
        logger.info("Request updateQueryInquiry :- {}.", payload);
        AppResponse validationResponse = this.validateAddOrUpdatePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        Optional<QueryInquiry> queryInquiry = this.queryInquiryRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (queryInquiry.isEmpty()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.QUERY_INQUIRY_NOT_FOUND);
        }
        this.queryInquiryRepository.save(this.updateQueryInquiryPayload(queryInquiry.get(), payload));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

    /**
     * Method use to fetch by id the query inquiry
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse fetchQueryInquiryById(QueryInquiryRequest payload) throws Exception {
        logger.info("Request fetchQueryInquiryById :- {}.", payload);
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        return this.queryInquiryRepository.findByIdAndUsernameAndStatusNot(payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE)
            .map(inquiry -> new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getQueryInquiryResponse(inquiry)))
            .orElseGet(() -> new AppResponse(BarcoUtil.ERROR, MessageUtil.QUERY_INQUIRY_NOT_FOUND));
    }

    /**
     * Method use to fetch all the query inquiry
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse fetchAllQueryInquiry(QueryInquiryRequest payload) throws Exception {
        logger.info("Request fetchAllQueryInquiry :- {}.", payload);
        if (!BarcoUtil.isNull(payload.getStartDate()) && !BarcoUtil.isNull(payload.getEndDate())) {
            Timestamp startDate = Timestamp.valueOf(payload.getStartDate().concat(BarcoUtil.START_DATE));
            Timestamp endDate = Timestamp.valueOf(payload.getEndDate().concat(BarcoUtil.END_DATE));
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
                this.queryInquiryRepository.findAllByDateCreatedBetweenAndUsernameAndStatusNot(startDate, endDate,
                    payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE)
                    .stream().map(this::getQueryInquiryResponse).collect(Collectors.toList()));
        } else {
            return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
                this.queryInquiryRepository.findAllByUsernameAndStatusNotInOrderByDateCreatedDesc(payload.getSessionUser().getUsername(),
                    Arrays.asList(APPLICATION_STATUS.DELETE, APPLICATION_STATUS.INACTIVE))
                    .stream().map(queryInquiry -> {
                        QueryInquiryResponse queryInquiryResponse = new QueryInquiryResponse();
                        queryInquiryResponse.setId(queryInquiry.getId());
                        queryInquiryResponse.setName(queryInquiry.getName());
                        queryInquiryResponse.setQuery(queryInquiry.getQuery());
                        return queryInquiryResponse;
                    }).collect(Collectors.toList()));
        }
    }

    /**
     * Method use to delete by id the query inquiry
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse deleteQueryInquiryById(QueryInquiryRequest payload) throws Exception {
        logger.info("Request deleteQueryInquiryById :- {}.", payload);
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        Optional<QueryInquiry> queryInquiry = this.queryInquiryRepository.findByIdAndUsername(payload.getId(), payload.getSessionUser().getUsername());
        if (queryInquiry.isEmpty()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.QUERY_INQUIRY_NOT_FOUND);
        }
        this.queryInquiryRepository.delete(queryInquiry.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()), payload);
    }

    /**
     * Method use to delete all the query inquiry
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse deleteAllQueryInquiry(QueryInquiryRequest payload) throws Exception {
        logger.info("Request deleteAllQueryInquiry :- {}.", payload);
        if (BarcoUtil.isNull(payload.getIds())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        this.queryInquiryRepository.deleteAll(this.queryInquiryRepository.findAllByIdIn(payload.getIds()));
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_DELETED_ALL, payload);
    }

    /**
     * Method use to create the queryInquiry reg
     * @param payload
     * @return QueryInquiry
     * @throws Exception
     * */
    private QueryInquiry createQueryInquiry(QueryInquiryRequest payload) throws Exception {
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        QueryInquiry queryInquiry = new QueryInquiry();
        if (appUserOpt.isPresent()) {
            AppUser appUser = appUserOpt.get();
            queryInquiry.setName(payload.getName());
            queryInquiry.setDescription(payload.getDescription());
            queryInquiry.setQuery(payload.getQuery());
            queryInquiry.setCreatedBy(appUser);
            queryInquiry.setUpdatedBy(appUser);
            queryInquiry.setStatus(APPLICATION_STATUS.ACTIVE);
        }
        return queryInquiry;
    }

    /**
     * Method use to update the queryInquiry reg
     * @param queryInquiry
     * @param payload
     * @return QueryInquiry
     * @throws Exception
     * */
    private QueryInquiry updateQueryInquiryPayload(QueryInquiry queryInquiry, QueryInquiryRequest payload) throws Exception {
        if (!BarcoUtil.isNull(payload.getName())) {
            queryInquiry.setName(payload.getName());
        }
        if (!BarcoUtil.isNull(payload.getDescription())) {
            queryInquiry.setDescription(payload.getDescription());
        }
        if (!BarcoUtil.isNull(payload.getQuery())) {
            queryInquiry.setQuery(payload.getQuery());
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            queryInquiry.setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        appUserOpt.ifPresent(queryInquiry::setUpdatedBy);
        return queryInquiry;
    }

    /**
     * Method use to validate the payload
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    private AppResponse validateAddOrUpdatePayload(QueryInquiryRequest payload) throws Exception {
        if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.QUERY_INQUIRY_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.QUERY_INQUIRY_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getQuery())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.QUERY_INQUIRY_QUERY_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }


}

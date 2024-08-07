package com.barco.admin.service.impl;

import com.barco.admin.service.SettingService;
import com.barco.model.dto.request.SessionUser;
import com.barco.model.dto.response.QueryResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.model.dto.request.QueryRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.repository.AppUserRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nabeel Ahmed
 * TemplateReg can be email and etc
 */
@Service
public class SettingServiceImpl implements SettingService {

    private Logger logger = LoggerFactory.getLogger(SettingServiceImpl.class);

    private String APP_SETTING_STATISTICS = "APP_SETTING_STATISTICS";
    private String PROFILE_SETTING_STATISTICS = "PROFILE_SETTING_STATISTICS";
    private String FORM_SETTING_STATISTICS = "FORM_SETTING_STATISTICS";
    private String DASHBOARD_AND_REPORT_SETTING_STATISTICS = "DASHBOARD_AND_REPORT_SETTING_STATISTICS";
    private String SERVICE_SETTING_STATISTICS = "SERVICE_SETTING_STATISTICS";
    private String SESSION_COUNT_STATISTICS = "SESSION_COUNT_STATISTICS";

    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private QueryService queryService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    public SettingServiceImpl() {}

    /**
     * Method use to fetch the detail for dashboard
     * @param sessionUser
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchSettingDashboard(SessionUser sessionUser) throws Exception {
        logger.info("Request dynamicQueryResponse :- " + sessionUser);
        if (BarcoUtil.isNull(sessionUser.getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            sessionUser.getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        Map<String, Object> settingDashboard = new HashMap<>();
        settingDashboard.put(APP_SETTING_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.APP_SETTING_STATISTICS, appUser.get().getId())));
        settingDashboard.put(PROFILE_SETTING_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.PROFILE_SETTING_STATISTICS, appUser.get().getId())));
        settingDashboard.put(FORM_SETTING_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.FORM_SETTING_STATISTICS, appUser.get().getId())));
        settingDashboard.put(DASHBOARD_AND_REPORT_SETTING_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.DASHBOARD_AND_REPORT_SETTING_STATISTICS, appUser.get().getId())));
        settingDashboard.put(SERVICE_SETTING_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.SERVICE_SETTING_STATISTICS, appUser.get().getId())));
        settingDashboard.put(SESSION_COUNT_STATISTICS, this.queryService.executeQueryResponse(
            String.format(QueryService.SESSION_COUNT_STATISTICS, appUser.get().getId())));
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, settingDashboard);
    }

    /**
     * Method use to query the data access only for super admin
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse dynamicQueryResponse(QueryRequest payload) throws Exception {
        logger.info("Request dynamicQueryResponse :- " + payload);
        if (BarcoUtil.isNull(payload.getQuery())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.QUERY_MISSING);
        }
        payload.setQuery(payload.getQuery().trim());
        if (!payload.getQuery().toLowerCase().startsWith(BarcoUtil.SELECT)) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ONLY_SELECT_QUERY_EXECUTE);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.queryService.executeQueryResponse(payload.getQuery()));
    }

    /**
     * Method use to query & download result
     * @param payload
     * @return AppResponse
     * */
    @Override
    public ByteArrayOutputStream downloadDynamicQueryFile(QueryRequest payload) throws Exception {
        logger.info("Request downloadDynamicQueryFile :- " + payload);
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
}

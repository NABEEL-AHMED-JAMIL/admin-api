package com.barco.admin.service.impl;

import com.barco.admin.service.SettingService;
import com.barco.model.dto.response.QueryResponse;
import com.barco.model.util.MessageUtil;
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

    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private QueryService queryService;
    @Autowired
    private AppUserRepository appUserRepository;

    public SettingServiceImpl() {}

    /**
     * Method use to query the data access only for super admin
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse dynamicQueryResponse(QueryRequest payload) {
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

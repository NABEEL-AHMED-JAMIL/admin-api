package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.SettingApiService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.excel.BulkExcel;
import com.barco.model.dto.request.QueryRequest;
import com.barco.model.dto.request.TemplateRegRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.QueryResponse;
import com.barco.model.dto.response.TemplateRegResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.TemplateReg;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.TemplateRegRepository;
import com.barco.model.util.ProcessUtil;
import com.barco.model.util.lookuputil.APPLICATION_STATUS;
import com.barco.model.util.lookuputil.EMAIL_TEMPLATE;
import com.barco.model.util.lookuputil.GLookup;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 * TemplateReg can be email and etc
 */
@Service
@Transactional
public class SettingApiServiceImpl implements SettingApiService {

    private Logger logger = LoggerFactory.getLogger(SettingApiServiceImpl.class);

    @Autowired
    private BulkExcel bulkExcel;
    @Autowired
    private QueryService queryService;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private TemplateRegRepository templateRegRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    /**
     * Method use to query the data access only for super admin
     * @param queryRequest
     * @return AppResponse
     * */
    @Override
    public AppResponse dynamicQueryResponse(QueryRequest queryRequest) {
        logger.info("Request dynamicQueryResponse :- " + queryRequest);
        if (BarcoUtil.isNull(queryRequest.getQuery())) {
            return new AppResponse(ProcessUtil.ERROR, "Query missing.");
        }
        queryRequest.setQuery(queryRequest.getQuery().trim());
        if (!queryRequest.getQuery().toLowerCase().startsWith("select")) {
            return new AppResponse(ProcessUtil.ERROR, "Only select query execute.");
        }
        return new AppResponse(ProcessUtil.SUCCESS, "Data fetch successfully.",
            this.queryService.executeQueryResponse(queryRequest.getQuery()));
    }

    /**
     * Method use to query & download result
     * @param queryRequest
     * @return AppResponse
     * */
    @Override
    public ByteArrayOutputStream downloadDynamicQueryFile(QueryRequest queryRequest) throws Exception {
        logger.info("Request downloadDynamicQueryFile :- " + queryRequest);
        if (BarcoUtil.isNull(queryRequest.getQuery())) {
            throw new Exception("Query missing.");
        }
        queryRequest.setQuery(queryRequest.getQuery().trim());
        if (!queryRequest.getQuery().toLowerCase().startsWith("select")) {
            throw new Exception("Only select query execute.");
        }
        QueryResponse queryResponse = this.queryService.executeQueryResponse(queryRequest.getQuery());
        XSSFWorkbook workbook = new XSSFWorkbook();
        this.bulkExcel.setWb(workbook);
        this.bulkExcel.setSheet(workbook.createSheet(this.bulkExcel.QUERY_RESPONSE));
        AtomicInteger rowCount = new AtomicInteger();
        Set<String> column = queryResponse.getColumn();
        String[] header = column.toArray(new String[0]);
        this.bulkExcel.fillBulkHeader(rowCount.get(), header);
        for (HashMap<String, Object> data : (List<HashMap<String, Object>>) queryResponse.getData()) {
            rowCount.getAndIncrement();
            List<String> dataCellValue = new ArrayList<>();
            column.stream().forEach(col -> {
                dataCellValue.add(String.valueOf(!BarcoUtil.isNull(data.get(col)) ? data.get(col): ""));
            });
            this.bulkExcel.fillBulkBody(dataCellValue, rowCount.get());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream;
    }

    /**
     * Method use to add the email template in db
     * @param templateRegRequest
     * @return AppResponse
     * */
    @Override
    public AppResponse addTemplateReg(TemplateRegRequest templateRegRequest) throws Exception {
        logger.info("Request addTemplateReg :- " + templateRegRequest);
        if (BarcoUtil.isNull(templateRegRequest.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            templateRegRequest.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!adminUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        } else if (BarcoUtil.isNull(templateRegRequest.getTemplateName())) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateName missing.");
        } else if (BarcoUtil.isNull(templateRegRequest.getTemplateType())) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateType missing.");
        } else if (BarcoUtil.isNull(templateRegRequest.getTemplateContent())) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateContent missing.");
        } else if (this.templateRegRepository.findFirstByTemplateTypeAndStatusNotIn(templateRegRequest.getTemplateType(),
            APPLICATION_STATUS.DELETE.getLookupCode()).isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateType already exist.");
        }
        TemplateReg templateReg = new TemplateReg();
        templateReg.setTemplateName(templateRegRequest.getTemplateName());
        templateReg.setTemplateType(templateRegRequest.getTemplateType());
        templateReg.setTemplateContent(templateRegRequest.getTemplateContent());
        templateReg.setAppUser(adminUser.get());
        templateReg.setStatus(APPLICATION_STATUS.ACTIVE.getLookupCode());
        this.templateRegRepository.save(templateReg);
        return new AppResponse(ProcessUtil.SUCCESS, String.format(
            "TemplateReg save with %d.", templateReg.getTemplateId()));
    }

    /**
     * Method use to edit the email template in db
     * @param templateRegRequest
     * @return AppResponse
     * */
    @Override
    public AppResponse editTemplateReg(TemplateRegRequest templateRegRequest) throws Exception {
        logger.info("Request editTemplateReg :- " + templateRegRequest);
        if (BarcoUtil.isNull(templateRegRequest.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            templateRegRequest.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!adminUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        } else if (BarcoUtil.isNull(templateRegRequest.getTemplateId())) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateId missing.");
        } else if (BarcoUtil.isNull(templateRegRequest.getTemplateName())) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateName missing.");
        } else if (BarcoUtil.isNull(templateRegRequest.getTemplateType())) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateType missing.");
        } else if (BarcoUtil.isNull(templateRegRequest.getTemplateContent())) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateContent missing.");
        }
        Optional<TemplateReg> templateRegOptional = this.templateRegRepository.findByTemplateIdAndUsernameAndStatusNotIn(
            templateRegRequest.getTemplateId(), adminUser.get().getUsername(), APPLICATION_STATUS.DELETE.getLookupCode());
        if (!templateRegOptional.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateReg not found.");
        }
        templateRegOptional.get().setTemplateName(templateRegRequest.getTemplateName());
        templateRegOptional.get().setTemplateType(templateRegRequest.getTemplateType());
        templateRegOptional.get().setTemplateContent(templateRegRequest.getTemplateContent());
        templateRegOptional.get().setStatus(templateRegRequest.getStatus());
        this.templateRegRepository.save(templateRegOptional.get());
        return new AppResponse(ProcessUtil.SUCCESS, String.format("TemplateReg save with %d.", templateRegRequest.getTemplateId()));
    }

    /**
     * Method use to find the email template from db by template id
     * @param templateRegRequest
     * @return AppResponse
     * */
    @Override
    public AppResponse findTemplateRegByTemplateId(TemplateRegRequest templateRegRequest) throws Exception {
        logger.info("Request findTemplateRegByTemplateId :- " + templateRegRequest);
        if (BarcoUtil.isNull(templateRegRequest.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            templateRegRequest.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!adminUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        } else if (BarcoUtil.isNull(templateRegRequest.getTemplateId())) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateId missing.");
        }
        Optional<TemplateReg> templateRegOptional = this.templateRegRepository.findByTemplateIdAndUsernameAndStatusNotIn(
            templateRegRequest.getTemplateId(), adminUser.get().getUsername(), APPLICATION_STATUS.DELETE.getLookupCode());
        if (!templateRegOptional.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateReg not found.");
        }
        TemplateRegResponse templateRegResponse = new TemplateRegResponse();
        templateRegResponse.setTemplateId(templateRegOptional.get().getTemplateId());
        templateRegResponse.setTemplateName(templateRegOptional.get().getTemplateName());
        templateRegResponse.setTemplateContent(templateRegOptional.get().getTemplateContent());
        templateRegResponse.setTemplateType(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupById(
            EMAIL_TEMPLATE.getName(), templateRegOptional.get().getTemplateType())));
        templateRegResponse.setStatus(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupById(
            APPLICATION_STATUS.getName(), templateRegOptional.get().getStatus())));
        return new AppResponse(ProcessUtil.SUCCESS, "Data fetch successfully.", templateRegResponse);
    }

    /**
     * Method use to find all email template from db by username
     * @param templateRegRequest
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchTemplateReg(TemplateRegRequest templateRegRequest) throws Exception {
        logger.info("Request fetchTemplateReg :- " + templateRegRequest);
        if (BarcoUtil.isNull(templateRegRequest.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            templateRegRequest.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!adminUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        }
        List<TemplateRegResponse> credentialResponseList = this.templateRegRepository.findAllByUsernameAndStatusNotIn(
            adminUser.get().getUsername(), APPLICATION_STATUS.DELETE.getLookupCode())
            .stream().map(templateReg -> {
                TemplateRegResponse templateRegResponse = new TemplateRegResponse();
                templateRegResponse.setTemplateId(templateReg.getTemplateId());
                templateRegResponse.setTemplateName(templateReg.getTemplateName());
                templateRegResponse.setTemplateContent(templateReg.getTemplateContent());
                templateRegResponse.setTemplateType(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupById(
                    EMAIL_TEMPLATE.getName(), templateReg.getTemplateType())));
                templateRegResponse.setStatus(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupById(
                    APPLICATION_STATUS.getName(), templateReg.getStatus())));
                templateRegResponse.setDateCreated(templateReg.getDateCreated());
                return templateRegResponse;
            }).collect(Collectors.toList());
        return new AppResponse(ProcessUtil.SUCCESS, "Data fetch successfully.", credentialResponseList);
    }

    /**
     * Method use to delete template by template id and username
     * @param templateRegRequest
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteTemplateReg(TemplateRegRequest templateRegRequest) throws Exception {
        logger.info("Request deleteTemplateReg :- " + templateRegRequest);
        if (BarcoUtil.isNull(templateRegRequest.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            templateRegRequest.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!adminUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found.");
        } else if (BarcoUtil.isNull(templateRegRequest.getTemplateId())) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateId missing.");
        }
        Optional<TemplateReg> templateRegOptional = this.templateRegRepository.findByTemplateIdAndUsernameAndStatusNotIn(
            templateRegRequest.getTemplateId(), adminUser.get().getUsername(), APPLICATION_STATUS.DELETE.getLookupCode());
        if (!templateRegOptional.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "TemplateRef not found.");
        }
        templateRegOptional.get().setStatus(APPLICATION_STATUS.DELETE.getLookupCode());
        return new AppResponse(ProcessUtil.SUCCESS, "Data delete successfully.");
    }

}

package com.barco.admin.controller;

import com.barco.admin.service.ICompanyService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.CompanyDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/company.json", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyRestApi {

    public Logger logger = LogManager.getLogger(CompanyRestApi.class);

    @Autowired
    private ICompanyService companyService;

    @RequestMapping(value = "/createCompanyRequest", method = RequestMethod.POST)
    public ResponseDTO createCompanyRequest(@RequestBody CompanyDto companyDto) {
        ResponseDTO response = null;
        try {
            logger.info("Request for createCompanyRequest " + companyDto);
            response = this.companyService.createCompanyRequest(companyDto);
        } catch (Exception ex) {
            logger.info("Error during createCompanyRequest " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    @RequestMapping(value = "/acceptCompanyRequest", method = RequestMethod.POST)
    public ResponseDTO acceptCompanyRequest(@RequestBody CompanyDto companyDto) {
        ResponseDTO response = null;
        try {
            logger.info("Request for acceptCompanyRequest " + companyDto);
            response = this.companyService.acceptCompanyRequest(companyDto);
        } catch (Exception ex) {
            logger.info("Error during acceptCompanyRequest " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }

    @RequestMapping(value = "/createCompany", method = RequestMethod.POST)
    public ResponseDTO createCompany(@RequestBody CompanyDto companyDto) {
        ResponseDTO response = null;
        try {
            logger.info("Request for createCompany " + companyDto);
            response = this.companyService.createCompany(companyDto);
        } catch (Exception ex) {
            logger.info("Error during createCompany " + ExceptionUtil.getRootCause(ex));
            response = new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR);
        }
        return response;
    }


}
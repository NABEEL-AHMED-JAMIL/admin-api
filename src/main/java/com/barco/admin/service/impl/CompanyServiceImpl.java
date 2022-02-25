package com.barco.admin.service.impl;

import com.barco.admin.service.ICompanyService;
import com.barco.model.dto.CompanyDto;
import com.barco.model.dto.ResponseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Nabeel Ahmed
 */
@Service
@Transactional
public class CompanyServiceImpl implements ICompanyService {

    public Logger logger = LogManager.getLogger(CompanyServiceImpl.class);

    @Override
    public ResponseDTO createCompanyRequest(CompanyDto company) {
        return null;
    }

    @Override
    public ResponseDTO acceptCompanyRequest(CompanyDto companyDto) {
        return null;
    }

    @Override
    public ResponseDTO createCompany(CompanyDto company) {
        return null;
    }
}

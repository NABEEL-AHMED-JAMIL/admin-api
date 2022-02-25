package com.barco.admin.service;

import com.barco.model.dto.CompanyDto;
import com.barco.model.dto.ResponseDTO;

/**
 * @author Nabeel Ahmed
 */
public interface ICompanyService {

    public ResponseDTO createCompanyRequest(CompanyDto company);

    public ResponseDTO acceptCompanyRequest(CompanyDto companyDto);

    public ResponseDTO createCompany(CompanyDto company);
}

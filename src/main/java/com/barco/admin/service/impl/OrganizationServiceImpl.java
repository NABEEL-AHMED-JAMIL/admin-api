package com.barco.admin.service.impl;

import com.barco.admin.service.OrganizationService;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.OrganizationRequest;
import com.barco.model.dto.response.AppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Override
    public AppResponse addOrg(OrganizationRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse updateOrg(OrganizationRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse fetchOrgById(OrganizationRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse fetchAllOrg(OrganizationRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse deleteOrgById(OrganizationRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse deleteAllOrg(OrganizationRequest payload) throws Exception {
        return null;
    }

    @Override
    public ByteArrayOutputStream downloadOrgTemplateFile() throws Exception {
        return null;
    }

    @Override
    public ByteArrayOutputStream downloadOrg(OrganizationRequest payload) throws Exception {
        return null;
    }

    @Override
    public AppResponse uploadOrg(FileUploadRequest payload) throws Exception {
        return null;
    }
}

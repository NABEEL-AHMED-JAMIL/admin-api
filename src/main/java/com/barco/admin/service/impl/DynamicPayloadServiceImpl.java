package com.barco.admin.service.impl;

import com.barco.admin.service.DynamicPayloadService;
import com.barco.model.dto.request.DynamicPayloadRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.repository.DynamicPayloadRepository;
import com.barco.model.repository.DynamicPayloadTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Nabeel Ahmed
 */
@Service
public class DynamicPayloadServiceImpl implements DynamicPayloadService {

    private Logger logger = LoggerFactory.getLogger(DynamicPayloadServiceImpl.class);

    @Autowired
    private DynamicPayloadRepository dynamicPayloadRepository;
    @Autowired
    private DynamicPayloadTagRepository dynamicPayloadTagRepository;

    /**
     * Method use to add event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use to add event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use to add event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use to add event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchDynamicPayloadById(DynamicPayloadRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use to add event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteDynamicPayloadById(DynamicPayloadRequest payload) throws Exception {
        return null;
    }

    /**
     * Method use to add event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        return null;
    }

}

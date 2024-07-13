package com.barco.admin.service.impl;

import com.barco.admin.service.DynamicPayloadService;
import com.barco.common.request.ConfigurationMakerRequest;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.JsonOutTagInfoUtil;
import com.barco.common.utility.XmlOutTagInfoUtil;
import com.barco.model.dto.request.DynamicPayloadRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.repository.DynamicPayloadRepository;
import com.barco.model.repository.DynamicPayloadTagRepository;
import com.barco.model.util.MessageUtil;
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
    private XmlOutTagInfoUtil xmlOutTagInfoUtil;
    @Autowired
    private JsonOutTagInfoUtil jsonOutTagInfoUtil;

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
        logger.info("Request addDynamicPayload :- " + payload);
        return null;
    }

    /**
     * Method use to update event bridge
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request updateDynamicPayload :- " + payload);
        return null;
    }

    /**
     * Method use to fetch all dynamic payload
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request fetchAllDynamicPayload :- " + payload);
        return null;
    }

    /**
     * Method use to fetch dynamic payload by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchDynamicPayloadById(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request fetchDynamicPayloadById :- " + payload);
        return null;
    }

    /**
     * Method use to delete dynamic payload by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteDynamicPayloadById(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request deleteDynamicPayloadById :- " + payload);
        return null;
    }

    /**
     * Method use to delete all dynamic payload
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllDynamicPayload(DynamicPayloadRequest payload) throws Exception {
        logger.info("Request deleteAllDynamicPayload :- " + payload);
        return null;
    }

    /**
     * Method use to convert the info to xlm
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse xmlCreateChecker(ConfigurationMakerRequest payload) throws Exception {
        logger.info("Request xmlCreateChecker :- " + payload);
        if (!BarcoUtil.isNull(payload.getXmlTagsInfo())) {
            return new AppResponse(BarcoUtil.SUCCESS, this.xmlOutTagInfoUtil.makeXml(payload));
        } else {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WRONG_INPUT);
        }
    }

    /**
     * Method use to convert the info to json
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse jsonCreateChecker(ConfigurationMakerRequest payload) throws Exception {
        logger.info("Request jsonCreateChecker :- " + payload);
        if (!BarcoUtil.isNull(payload.getXmlTagsInfo())) {
            return new AppResponse(BarcoUtil.SUCCESS, this.jsonOutTagInfoUtil.makeJson(payload));
        } else {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.WRONG_INPUT);
        }
    }
}

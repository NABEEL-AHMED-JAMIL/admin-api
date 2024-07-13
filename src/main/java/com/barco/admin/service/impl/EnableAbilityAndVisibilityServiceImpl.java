package com.barco.admin.service.impl;

import com.barco.admin.service.EnableAbilityAndVisibilityService;
import com.barco.model.dto.request.EnableAndVisibilityConfigRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Nabeel Ahmed
 */
@Service
public class EnableAbilityAndVisibilityServiceImpl implements EnableAbilityAndVisibilityService {

    private Logger logger = LoggerFactory.getLogger(DynamicFormService.class);

    @Autowired
    private EnableAndVisibilityConfigRepository enableAndVisibilityConfigRepository;
    @Autowired
    private ConditionalLogicRepository conditionalLogicRepository;
    @Autowired
    private CaseConditionRepository caseConditionRepository;
    @Autowired
    private ThenConditionRepository thenConditionRepository;

    /**
     * Method use to add enable ability
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addEnableAndVisibilityConfig(EnableAndVisibilityConfigRequest payload) throws Exception {
        logger.info("Request addEnableAndVisibilityConfig :- " + payload);
        return null;
    }

    /**
     * Method use to edit enable ability
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse editEnableAndVisibilityConfig(EnableAndVisibilityConfigRequest payload) throws Exception {
        logger.info("Request editEnableAndVisibilityConfig :- " + payload);
        return null;
    }

    /**
     * Method use to fetch enable ability
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllEnableAndVisibilityConfig(EnableAndVisibilityConfigRequest payload) throws Exception {
        logger.info("Request fetchAllEnableAndVisibilityConfig :- " + payload);
        return null;
    }

    /**
     * Method use to fetch enable ability by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchEnableAndVisibilityConfigById(EnableAndVisibilityConfigRequest payload) throws Exception {
        logger.info("Request fetchEnableAndVisibilityConfigById :- " + payload);
        return null;
    }

    /**
     * Method use to delete enable ability by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteEnableAndVisibilityConfigById(EnableAndVisibilityConfigRequest payload) throws Exception {
        logger.info("Request deleteEnableAndVisibilityConfigById :- " + payload);
        return null;
    }

    /**
     * Method use to delete all enable ability
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllEnableAndVisibilityConfig(EnableAndVisibilityConfigRequest payload) throws Exception {
        logger.info("Request deleteAllEnableAndVisibilityConfig :- " + payload);
        return null;
    }

}

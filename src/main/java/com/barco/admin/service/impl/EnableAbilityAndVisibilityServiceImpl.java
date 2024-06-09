package com.barco.admin.service.impl;

import com.barco.admin.service.EnableAbilityAndVisibilityService;
import com.barco.model.dto.request.EnableAbilityRequest;
import com.barco.model.dto.request.VisibilityRequest;
import com.barco.model.dto.response.AppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Nabeel Ahmed
 */
@Service
public class EnableAbilityAndVisibilityServiceImpl implements EnableAbilityAndVisibilityService {

    private Logger logger = LoggerFactory.getLogger(DynamicFormService.class);

    /**
     * Method use to add enable ability
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addEnableAbility(EnableAbilityRequest payload) throws Exception {
        logger.info("Request addEnableAbility :- " + payload);
        return null;
    }

    /**
     * Method use to edit enable ability
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse editEnableAbility(EnableAbilityRequest payload) throws Exception {
        logger.info("Request editEnableAbility :- " + payload);
        return null;
    }

    /**
     * Method use to fetch enable ability
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllEnableAbility(EnableAbilityRequest payload) throws Exception {
        logger.info("Request fetchAllEnableAbility :- " + payload);
        return null;
    }

    /**
     * Method use to fetch enable ability by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchEnableAbilityById(EnableAbilityRequest payload) throws Exception {
        logger.info("Request fetchEnableAbilityById :- " + payload);
        return null;
    }

    /**
     * Method use to delete enable ability by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteEnableAbilityById(EnableAbilityRequest payload) throws Exception {
        logger.info("Request deleteEnableAbilityById :- " + payload);
        return null;
    }

    /**
     * Method use to delete all enable ability
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllEnableAbility(EnableAbilityRequest payload) throws Exception {
        logger.info("Request deleteAllEnableAbility :- " + payload);
        return null;
    }

    /**
     * Method use to add visibility
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addVisibility(VisibilityRequest payload) throws Exception {
        logger.info("Request addVisibility :- " + payload);
        return null;
    }

    /**
     * Method use to edit visibility
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse editVisibility(VisibilityRequest payload) throws Exception {
        logger.info("Request editVisibility :- " + payload);
        return null;
    }

    /**
     * Method use to fetch visibility
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllVisibility(VisibilityRequest payload) throws Exception {
        logger.info("Request fetchAllVisibility :- " + payload);
        return null;
    }

    /**
     * Method use to fetch visibility by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchVisibilityById(VisibilityRequest payload) throws Exception {
        logger.info("Request fetchVisibilityById :- " + payload);
        return null;
    }

    /**
     * Method use to delete visibility by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteVisibilityById(VisibilityRequest payload) throws Exception {
        logger.info("Request deleteVisibilityById :- " + payload);
        return null;
    }

    /**
     * Method use to delete all visibility
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllVisibility(VisibilityRequest payload) throws Exception {
        logger.info("Request deleteAllVisibility :- " + payload);
        return null;
    }
}

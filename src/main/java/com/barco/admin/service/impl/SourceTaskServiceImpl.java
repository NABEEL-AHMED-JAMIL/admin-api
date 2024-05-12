package com.barco.admin.service.impl;

import com.barco.admin.service.SourceTaskService;
import com.barco.model.dto.request.SourceTaskRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.repository.SourceTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Nabeel Ahmed
 */
@Service
public class SourceTaskServiceImpl implements SourceTaskService {

    private Logger logger = LoggerFactory.getLogger(SourceTaskServiceImpl.class);

    @Autowired
    private SourceTaskRepository sourceTaskRepository;

    /***
     * Method use to add new source task
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addSourceTask(SourceTaskRequest payload) throws Exception {
        logger.info("Request addSourceTask :- " + payload);
        return null;
    }

    /***
     * Method use to edit new source task
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse editSourceTask(SourceTaskRequest payload) throws Exception {
        logger.info("Request editSourceTask :- " + payload);
        return null;
    }

    /***
     * Method use to delete source task
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteSourceTask(SourceTaskRequest payload) throws Exception {
        logger.info("Request deleteSourceTask :- " + payload);
        return null;
    }

    /***
     * Method use to delete all source task
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllSourceTask(SourceTaskRequest payload) throws Exception {
        logger.info("Request deleteAllSourceTask :- " + payload);
        return null;
    }

    /***
     * Method use to fetch all source task
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllSourceTask(SourceTaskRequest payload) throws Exception {
        logger.info("Request fetchAllSourceTask :- " + payload);
        return null;
    }

    /***
     * Method use to fetch source task by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchSourceTaskById(SourceTaskRequest payload) throws Exception {
        logger.info("Request fetchSourceTaskById :- " + payload);
        return null;
    }
}

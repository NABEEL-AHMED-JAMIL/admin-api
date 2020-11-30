package com.barco.admin.service;

import com.barco.model.dto.ResponseDTO;
import com.barco.model.pojo.Scheduler;

/**
 * @author Nabeel Ahmed
 */
public interface IJobQueueService {

    // job queue method for auto
    public ResponseDTO addJobToQueue(Long jobId, Long appUserId, Scheduler scheduler) throws Exception;
}

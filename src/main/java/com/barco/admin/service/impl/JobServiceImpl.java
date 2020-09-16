package com.barco.admin.service.impl;

import com.barco.admin.service.IJobService;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.Status;
import com.barco.model.pojo.pagination.PaginationDetail;
import com.barco.model.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@Scope("prototype")
public class JobServiceImpl implements IJobService {

    private Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    private JobRepository jobRepository;

    @Override
    public ResponseDTO createJob() throws Exception {
        return null;
    }

    @Override
    public ResponseDTO getJob(Long jobId) throws Exception  {
        return null;
    }

    @Override
    public ResponseDTO statusChange(Long jobId, Status jobStatus) throws Exception  {
        return null;
    }

    @Override
    public ResponseDTO findAllJobByAppUserIdInPagination(Long appUserId, PaginationDetail paginationDetail) throws Exception  {
        return null;
    }

    @Override
    public ResponseDTO runJob(Long jobId, Long appUserId) throws Exception  {
        return null;
    }

    @Override
    public ResponseDTO skipNextOccurrence(Long jobId, Long appUserId) throws Exception  {
        return null;
    }

}

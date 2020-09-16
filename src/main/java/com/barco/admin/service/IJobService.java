package com.barco.admin.service;

import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.Status;
import com.barco.model.pojo.pagination.PaginationDetail;


public interface IJobService {

    // create job
    public ResponseDTO createJob() throws Exception;

    // get job by id
    public ResponseDTO getJob(Long jobId) throws Exception;

    // change status task by id Inactive(0), Active(1), Delete(3)
    public ResponseDTO statusChange(Long jobId, Status jobStatus) throws Exception;

    // fetch all job
    public ResponseDTO findAllJobByAppUserIdInPagination(Long appUserId, PaginationDetail paginationDetail) throws Exception;

    // run job
    public ResponseDTO runJob(Long jobId, Long appUserId) throws Exception;

    // skip next occurrence
    public ResponseDTO skipNextOccurrence(Long jobId, Long appUserId) throws Exception;
}

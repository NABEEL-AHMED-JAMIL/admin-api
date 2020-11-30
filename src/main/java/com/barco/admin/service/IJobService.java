package com.barco.admin.service;

import com.barco.model.dto.JobDto;
import com.barco.model.dto.PaggingDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.enums.Status;
import com.barco.model.searchspec.PaginationDetail;

/**
 * @author Nabeel Ahmed
 */
public interface IJobService {

    // create job
    public ResponseDTO createJob(JobDto jobDto) throws Exception;

    // get job by id
    public ResponseDTO getJobById(Long jobId, Long appUserId) throws Exception;

    // change status task by id
    // Inactive(0), Active(1), Delete(3),
    public ResponseDTO statusChange(Long jobId, Long appUserId, Status jobStatus) throws Exception;

    // fetch all job
    public ResponseDTO findAllJobByAppUserIdInPagination(PaggingDto pagging, Long adminId, SearchTextDto searchTextDto,
         String startDate, String endDate) throws Exception;

    // run job
    public ResponseDTO addJobToQueue(Long jobId, Long appUserId) throws Exception;

    // skip next occurrence
    public ResponseDTO skipNextOccurrence(Long jobId, Long appUserId) throws Exception;
}

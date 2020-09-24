package com.barco.admin.service.impl;

import com.barco.admin.mapper.JobMapper;
import com.barco.admin.service.IJobService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.model.dto.JobDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.pojo.*;
import com.barco.model.pojo.pagination.PaginationDetail;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.JobRepository;
import com.barco.model.repository.SchedulerRepository;
import com.barco.model.repository.TaskRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Scope("prototype")
public class JobServiceImpl implements IJobService {

    private Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SchedulerRepository schedulerRepository;

    @Autowired
    private AppUserRepository appUserRepository;


    @Override
    public ResponseDTO createJob(JobDto jobDto) throws Exception {
        if (StringUtils.isEmpty(jobDto.getJobName())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.JOB_NAME_MISSING);
        } else if (this.jobRepository.findByJobNameAndStatus(jobDto.getJobName(), Status.Active).isPresent() && jobDto.getId() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.JOB_NAME_ALREADY_EXIST);
        } else if (jobDto.getExecutionType() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.JOB_EXECUTION_TYPE);
        } else if (jobDto.getTask() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_FOR_JOB_MISSING);
        } else if (jobDto.getTask().getId() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_FOR_JOB_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByIdAndStatus(jobDto.getCreatedBy(), Status.Active);
        if (!appUser.isPresent()) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.USER_NOT_FOUND);
        }
        Job job = null;
        if (jobDto.getId() != null) {
            job = this.jobRepository.findByIdAndStatus(jobDto.getId(), Status.Active);
            if (job != null) {
                job.setModifiedBy(appUser.get().getId());
            } else {
                return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.JOB_NOT_FOUND);
            }
        } else {
            job = new Job();
            job.setCreatedBy(appUser.get().getId());
            job.setStatus(Status.Active);
        }
        job.setJobName(jobDto.getJobName());
        job.setDescription(jobDto.getDescription());
        job.setExecutionType(jobDto.getExecutionType());
        Task task = this.taskRepository.findByIdAndStatus(jobDto.getTask().getId(), Status.Active);
        if (task == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.TASK_NOT_FOUND);
        }
        job.setTask(task);
        // scheduler will impl after first flow complete
        job.setNotification(jobDto.getNotification());
        job = this.jobRepository.saveAndFlush(job);
        jobDto.setId(job.getId());
        return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, jobDto);
    }

    @Override
    public ResponseDTO getJobById(Long jobId, Long appUserId) throws Exception  {
        Optional<Job> job = this.jobRepository.findByIdAndCreatedByAndStatus(jobId, appUserId, Status.Active);
        if (job.isPresent()) {
            List<Scheduler> scheduler = this.schedulerRepository.findAllByJobIdAndCreatedByAndStatus(job.get(), appUserId, Status.Active);
            return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, JobMapper.jobToJobDto(job.get(), scheduler)); // convert to jobDto
        }
        return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG);
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
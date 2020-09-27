package com.barco.admin.service.impl;

import com.barco.admin.service.IJobQueueService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.JobStatus;
import com.barco.model.enums.Status;
import com.barco.model.pojo.Job;
import com.barco.model.pojo.JobQueue;
import com.barco.model.pojo.Scheduler;
import com.barco.model.repository.JobQueueRepository;
import com.barco.model.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Optional;

@Service
@Transactional
@Scope("prototype")
public class JobQueueServiceImpl implements IJobQueueService {

    private Logger logger = LoggerFactory.getLogger(JobQueueServiceImpl.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobQueueRepository jobQueueRepository;

    @Override
    public ResponseDTO addJobToQueue(Long jobId, Long appUserId, Scheduler scheduler) throws Exception {
        Optional<Job> job = this.jobRepository.findByIdAndCreatedByAndStatus(jobId, appUserId, Status.Active);
        if (job.isPresent()) {
            if (isJobEligible(job.get())) {
                logger.info(jobId + " : Job Added In Queue");
                JobQueue jobQueue = new JobQueue();
                jobQueue.setJob(job.get()); // job in queue added
                jobQueue.setStatus(Status.Active);
                jobQueue.setCreatedBy(appUserId);
                jobQueue.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                if (scheduler != null) {
                    jobQueue.setSchedulerId(scheduler.getId());
                    jobQueue.setSchedulerTime(scheduler.getTime());
                }
                // save the detail into the job queue
                this.jobQueueRepository.saveAndFlush(jobQueue);
                // --job-status added into the job--
                job.get().setJobStatus(JobStatus.Queue);
                job.get().setModifiedBy(appUserId);
                this.jobRepository.saveAndFlush(job.get());
                return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
            } else {
                logger.info(jobId + " : Job Not Added In Queue");
                return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.JOB_NOT_ELIGIBLE);
            }
        } else {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG);
        }
    }

    private Boolean isJobEligible(Job job) {
        if (job.getJobStatus() == null) {
            return true;
        } else if ((job.getJobStatus().compareTo(JobStatus.Queue) == 0) ||
                (job.getJobStatus().compareTo(JobStatus.Running) == 0)) {
            return false;
        }
        return true;
    }

}

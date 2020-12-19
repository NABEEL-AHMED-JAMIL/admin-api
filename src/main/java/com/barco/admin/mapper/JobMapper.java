package com.barco.admin.mapper;

import com.barco.model.dto.JobDto;
import com.barco.model.dto.SchedulerDto;
import com.barco.model.dto.TaskDto;
import com.barco.model.pojo.Job;
import com.barco.model.pojo.Scheduler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nabeel Ahmed
 */
public class JobMapper {

    private Logger logger = LoggerFactory.getLogger(JobMapper.class);

    public static JobDto jobToJobDto(Job job, List<Scheduler> schedulers) {
        JobDto jobDto = new JobDto();
        if (job.getId() != null) {
            jobDto.setId(job.getId());
        }
        if (StringUtils.isNotBlank(job.getJobName())) {
            jobDto.setJobName(job.getJobName());
        }
        if (StringUtils.isNotBlank(job.getDescription())) {
            jobDto.setDescription(job.getDescription());
        }
        if (job.getExecutionType() != null) {
            jobDto.setExecutionType(job.getExecutionType());
        }
        if (job.getStatus() != null) {
            jobDto.setJobStatus(job.getJobStatus());
        }
        if (job.getLastJobRun() != null) {
            jobDto.setLastJobRun(job.getLastJobRun());
        }
        if (job.getNextJobRun() != null) {
            jobDto.setLastJobRun(job.getNextJobRun());
        }
        if (job.getCreatedBy() != null) {
            jobDto.setCreatedBy(job.getCreatedBy());
        }
        // added task
        if (job.getTask() != null) {
            TaskDto taskDto = new TaskDto();
            taskDto.setId(job.getTask().getId());
            taskDto.setTaskName(job.getTask().getTaskName());
            jobDto.setTask(taskDto);
        }
        // add scheduler
        if (schedulers != null) {
            List<SchedulerDto> schedulerDtos = new ArrayList<>();
            for (Scheduler scheduler: schedulers) {
                SchedulerDto schedulerDto = new SchedulerDto();
                if (scheduler.getId() != null) {
                    schedulerDto.setId(scheduler.getId());
                }
                if (scheduler.getStartDate() != null) {
                    schedulerDto.setStartDate(scheduler.getStartDate());
                }
                if (scheduler.getEndDate() != null) {
                    schedulerDto.setEndDate(scheduler.getEndDate());
                }
                if (scheduler.getTime() != null) {
                    schedulerDto.setTime(scheduler.getTime());
                }
                if (StringUtils.isNotBlank(scheduler.getFrequency())) {
                    schedulerDto.setFrequency(scheduler.getFrequency());
                }
                if (StringUtils.isNotBlank(scheduler.getRecurrence())) {
                    schedulerDto.setRecurrence(scheduler.getRecurrence());
                }
                if (StringUtils.isNotBlank(scheduler.getTimeZone())) {
                    schedulerDto.setRecurrence(scheduler.getTimeZone());
                }
                schedulerDtos.add(schedulerDto);
            }
            jobDto.setSchedulers(schedulerDtos);
        }
        if (StringUtils.isNotBlank(job.getNotification())) {
            jobDto.setNotification(job.getNotification());
        }
        return jobDto;
    }
}

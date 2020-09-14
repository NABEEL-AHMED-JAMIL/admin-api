package com.barco.admin.service.impl;

import com.barco.admin.service.IJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class JobServiceImpl implements IJobService {

    private Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
}

package com.barco.admin.service.impl;

import com.barco.admin.service.ITaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class TaskServiceImpl implements ITaskService {

    private Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
}

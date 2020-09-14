package com.barco.admin.service.impl;

import com.barco.admin.service.IBatchFileProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class BatchFileProcessServiceImpl implements IBatchFileProcessService {

    private Logger logger = LoggerFactory.getLogger(BatchFileProcessServiceImpl.class);
}

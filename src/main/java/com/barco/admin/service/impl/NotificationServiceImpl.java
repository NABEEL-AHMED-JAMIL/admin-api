package com.barco.admin.service.impl;

import com.barco.admin.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@Scope("prototype")
public class NotificationServiceImpl implements INotificationService {

    private Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
}

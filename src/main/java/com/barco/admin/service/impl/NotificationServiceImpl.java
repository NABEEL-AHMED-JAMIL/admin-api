package com.barco.admin.service.impl;

import com.barco.admin.service.NotificationService;
import com.barco.model.dto.request.NotificationRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.pojo.NotificationAudit;
import com.barco.model.repository.NotificationAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

/**
 * @author Nabeel Ahmed
 * TemplateReg can be email and etc
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final String REPLAY = "/reply";

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private NotificationAuditRepository notificationAuditRepository;

    @Override
    public void addNotification(NotificationAudit notificationAudit) throws Exception {

    }

    @Override
    public AppResponse updateNotification(NotificationRequest requestPayload) throws Exception {
        return null;
    }

    @Override
    public AppResponse fetchAllNotification(NotificationRequest requestPayload) throws Exception {
        return null;
    }

    @Override
    public void sendNotificationToSpecificUser(NotificationRequest message) throws Exception {
        this.simpMessagingTemplate.convertAndSendToUser(message.getTo(), REPLAY, message.getPayload());
    }
}

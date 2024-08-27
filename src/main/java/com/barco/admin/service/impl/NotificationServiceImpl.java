package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.NotificationService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.NotificationRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.MessageResponse;
import com.barco.model.dto.response.NotificationResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.NotificationAudit;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.NotificationAuditRepository;
import com.barco.model.repository.specification.NotificationSpecification;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * @author Nabeel Ahmed
 * TemplateReg can be email and etc
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final String REPLAY = "/reply";
    private final String NOTIFY_ID = "id";

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;
    @Autowired
    private NotificationAuditRepository notificationAuditRepository;

    public NotificationServiceImpl() {}

    /**
     * Method use send notification to user session
     * @param payload
     * @param appUser
     * @throws Exception
     * */
    @Override
    public void addNotification(NotificationRequest payload, AppUser appUser) throws Exception {
        logger.info("Request addNotification :- {}.", payload);
        NotificationAudit notificationAudit = new NotificationAudit();
        notificationAudit.setSendTo(appUser);
        notificationAudit.setMessage(payload.getBody().toString());
        notificationAudit.setNotifyType(NOTIFICATION_TYPE.getByLookupCode(payload.getNotifyType()));
        notificationAudit.setMessageStatus(NOTIFICATION_STATUS.getByLookupCode(payload.getMessageStatus()));
        notificationAudit.setExpireTime(payload.getExpireTime());
        notificationAudit.setCreatedBy(appUser);
        notificationAudit.setUpdatedBy(appUser);
        notificationAudit.setStatus(APPLICATION_STATUS.ACTIVE);
        this.notificationAuditRepository.save(notificationAudit);
        payload.setId(notificationAudit.getId());
        payload.setDateCreated(notificationAudit.getDateCreated());
        this.sendNotificationToSpecificUser(this.getNotificationResponse(notificationAudit));
    }

    /**
     * updateNotification method use to change the notification status by update the status(read|unread)
     * @param payload
     * @throws Exception
     * */
    @Override
    public AppResponse updateNotification(NotificationRequest payload) throws Exception {
        logger.info("Request updateNotification :- {}.", payload);
        if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.NOTIFY_ID_MISSING);
        }
        Optional<NotificationAudit> notificationAudit = this.notificationAuditRepository.findById(payload.getId());
        if (notificationAudit.isEmpty()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.NOTIFICATION_AUDIT_NOT_FOUND);
        }
        notificationAudit.get().setMessageStatus(NOTIFICATION_STATUS.READ);
        this.notificationAuditRepository.save(notificationAudit.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

    /**
     * fetchAllNotification method use to fetch all notification by user
     * @param username
     * @throws Exception
     * */
    @Override
    public AppResponse fetchAllNotification(String username) throws Exception {
        logger.info("Request fetchAllNotification :- {}.", username);
        if (BarcoUtil.isNull(username)) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(username, APPLICATION_STATUS.ACTIVE);
        return appUser.map(user -> new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.notificationAuditRepository.findAll(new NotificationSpecification(user,
                Long.valueOf(this.lookupDataCacheService.getParentLookupDataByParentLookupType(LookupUtil.NOTIFICATION_DISAPPEAR_TIME).getLookupValue())),
                PageRequest.of(0, 5000, Sort.by(Sort.Order.asc(NOTIFY_ID)))).map(this::getNotificationResponse).getContent()))
                .orElseGet(() -> new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND));
    }

    /**
     * sendNotificationToSpecificUser method use to send the notification to specific user
     * @param payload
     * @throws Exception
     * */
    @Override
    public void sendNotificationToSpecificUser(NotificationResponse payload) throws Exception {
        logger.info("Request sendNotificationToSpecificUser :- {}.", payload);
        this.simpMessagingTemplate.convertAndSendToUser(payload.getSendTo(), REPLAY, payload);
    }

    /**
     * Method use to add notification response
     * @param payload
     * @return NotificationResponse
     * */
    private NotificationResponse getNotificationResponse(NotificationAudit payload) {
        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setId(payload.getId());
        notificationResponse.setSendTo(payload.getSendTo().getUsername());
        notificationResponse.setBody(new Gson().fromJson(payload.getMessage(), MessageResponse.class));
        notificationResponse.setNotifyType(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
            NOTIFICATION_TYPE.getName(), payload.getNotifyType().getLookupCode())));
        notificationResponse.setMessageStatus(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
            NOTIFICATION_STATUS.getName(), payload.getMessageStatus().getLookupCode())));
        notificationResponse.setExpireTime(payload.getExpireTime());
        notificationResponse.setDateCreated(payload.getDateCreated());
        notificationResponse.setDateUpdated(payload.getDateCreated());
        return notificationResponse;
    }
}

package com.barco.admin.service;


import com.barco.model.dto.request.NotificationRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.pojo.NotificationAudit;

/**
 * @author Nabeel Ahmed
 */
public interface NotificationService {

    // inside method
    public void addNotification(NotificationAudit notificationAudit) throws Exception;

    // api call
    public AppResponse updateNotification(NotificationRequest requestPayload) throws Exception ;

    // api call
    public AppResponse fetchAllNotification(NotificationRequest requestPayload) throws Exception ;

    // inside method
    public void sendNotificationToSpecificUser(NotificationRequest requestPayload) throws Exception;

}

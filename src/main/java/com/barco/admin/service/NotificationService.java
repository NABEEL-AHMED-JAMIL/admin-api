package com.barco.admin.service;

import com.barco.model.dto.request.NotificationRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.NotificationResponse;
import com.barco.model.pojo.AppUser;

/**
 * @author Nabeel Ahmed
 */
public interface NotificationService {

    public void addNotification(NotificationRequest payload, AppUser appUser) throws Exception;

    public AppResponse updateNotification(NotificationRequest payload) throws Exception ;

    public AppResponse fetchAllNotification(String username) throws Exception ;

    public void sendNotificationToSpecificUser(NotificationResponse payload) throws Exception;

}

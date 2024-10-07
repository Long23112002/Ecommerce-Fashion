package org.example.ecommercefashion.services;

import org.example.ecommercefashion.entities.Notification;
import org.example.ecommercefashion.enums.notification.NotificationCode;

public interface NotificationService {

    void sendNotificationAll(NotificationCode notificationCode, Long createBy);

}

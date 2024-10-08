package org.example.ecommercefashion.services;

import org.example.ecommercefashion.entities.Notification;
import org.example.ecommercefashion.enums.notification.NotificationCode;

public interface NotificationService {

    void sendNotificationAll(Long createBy, NotificationCode notificationCode, String... variables);
    void sendNotificationAll(NotificationCode notificationCode, String... variables);

}

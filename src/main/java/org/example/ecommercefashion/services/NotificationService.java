package org.example.ecommercefashion.services;

import org.example.ecommercefashion.entities.NotificationMessage;

public interface NotificationService {

    void sendNotificationAll(NotificationMessage notificationMessage);

}

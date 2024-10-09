package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.dtos.response.LoadMoreResponse;
import org.example.ecommercefashion.dtos.response.NotificationResponse;
import org.example.ecommercefashion.entities.Notification;
import org.example.ecommercefashion.enums.notification.NotificationCode;

public interface NotificationService {

    LoadMoreResponse<NotificationResponse> findAllNotificationsByUserId(Long id, int offset, int limit);

    void sendNotificationAll(Long createBy, NotificationCode notificationCode, Object... variables);
    void sendNotificationAll(NotificationCode notificationCode, Object... variables);
    void sendNotificationToUser(Long createBy, Long idReceiver, NotificationCode notificationCode, Object... variables);
    void sendNotificationToUser(Long idReceiver, NotificationCode notificationCode, Object... variables);

}

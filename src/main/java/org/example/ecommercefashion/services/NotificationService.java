package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.response.LoadMoreResponse;
import org.example.ecommercefashion.dtos.response.NotificationResponse;
import org.example.ecommercefashion.enums.notification.NotificationCode;

import java.util.List;

public interface NotificationService {

    LoadMoreResponse<NotificationResponse> findAllNotificationsByUserId(Long id, int offset, int limit);
    LoadMoreResponse<NotificationResponse> findAllUnSeenNotificationByIdUser(Long id, Integer offset, Integer limit);
    List<NotificationResponse> markSeenAll (Long idReceiver);
    List<NotificationResponse> markSeenById (String idNoti);

    void sendNotificationAll(Long createBy, NotificationCode notificationCode, Object... variables);
    void sendNotificationAll(NotificationCode notificationCode, Object... variables);
    void sendNotificationToUser(Long createBy, Long idReceiver, NotificationCode notificationCode, Object... variables);
    void sendNotificationToUser(Long idReceiver, NotificationCode notificationCode, Object... variables);

}

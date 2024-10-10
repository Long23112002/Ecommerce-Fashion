package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.response.LoadMoreResponse;
import org.example.ecommercefashion.dtos.response.NotificationResponse;
import org.example.ecommercefashion.enums.notification.NotificationCode;

import java.util.Collection;
import java.util.List;

public interface NotificationService {

    LoadMoreResponse<NotificationResponse> findAllNotificationsByUserId(Long id, int offset, int limit);
    LoadMoreResponse<NotificationResponse> findAllUnSeenNotificationByIdUser(Long id, Integer offset, Integer limit);
    List<NotificationResponse> markSeenAll (Long idReceiver, String token);
    List<NotificationResponse> markSeenById (String idNoti, String token);
    NotificationResponse deleteById(String idNoti, String token);

    // Gửi realtime cho tất cả người trong server
    void sendNotificationAll(Long createBy, NotificationCode notificationCode, Object... variables);

    // Gửi realtime cho tất cả người có permission trong NotificationCode.permission
    void sendNotificationToUsersWithPermission(Long createBy, NotificationCode notificationCode, Object... variables);

    // Gửi realtime cho tất cả danh sách người được chỉ định
    void sendNotificationToUsers(Long createBy, Collection<Long> idReceivers, NotificationCode notificationCode, Object... variables);

    // Gửi realtime cho người duy nhất được chỉ định
    void sendNotificationToUser(Long createBy, Long idReceiver, NotificationCode notificationCode, Object... variables);

}

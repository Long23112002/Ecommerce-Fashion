package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.WebSocketDestination;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.response.NotificationResponse;
import org.example.ecommercefashion.entities.Notification;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.notification.NotificationCode;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.NotificationRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketService webSocketService;
    private final UserRepository userRepository;

    @Override
    public void sendNotificationAll(Long createBy, NotificationCode notificationCode, String... variables) {
        Optional<User> createByUserOptional = userRepository.findById(createBy);
        if (createByUserOptional.isPresent()) {
            User createByUser = createByUserOptional.get();
            List<User> users = userRepository.findAllUserByPermission(notificationCode.getPermission());
            List<Notification> notifications = buildAndSendNotifications(users, notificationCode, createByUser, variables);
            notificationRepository.saveAll(notifications);
        }
    }

    @Override
    public void sendNotificationAll(NotificationCode notificationCode, String... variables) {
        List<User> users = userRepository.findAllUserByPermission(notificationCode.getPermission());
        List<Notification> notifications = buildAndSendNotifications(users, notificationCode, null, variables);
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void sendNotificationToUser(Long createBy, Long idReceiver, NotificationCode notificationCode, String... variables) {
        User createByUser = getUserById(createBy);
        User receiver = getUserById(idReceiver);
        Notification notification = buildNotification(notificationCode, idReceiver, variables);
        notificationRepository.save(notification);
        sendRealtimeNotification(notification, createByUser);
    }

    @Override
    public void sendNotificationToUser(Long idReceiver, NotificationCode notificationCode, String... variables) {
        User receiver = getUserById(idReceiver);
        Notification notification = buildNotification(notificationCode, idReceiver, variables);
        notificationRepository.save(notification);
        sendRealtimeNotification(notification, null);
    }

    private List<Notification> buildAndSendNotifications(List<User> users, NotificationCode notificationCode, User createByUser, String... variables) {
        List<Notification> notifications = new ArrayList<>();
        for (User user : users) {
            Notification notification = buildNotification(notificationCode, user.getId(), variables);
            notifications.add(notification);
            sendRealtimeNotification(notification, createByUser);
        }
        return notifications;
    }

    private Notification buildNotification(NotificationCode notificationCode, Long idReceiver, String... variables) {
        String title = notificationCode.getDefaultTitle();
        String content = notificationCode.getContentWithInfor(variables);
        return buildEntity(title, content, idReceiver);
    }

    private void sendRealtimeNotification(Notification notification, User createByUser) {
        String destination = WebSocketDestination.NOTIFICATION.getDestinationWithSlash() + notification.getIdReceiver();
        NotificationResponse response = createByUser == null ? toDto(notification) : toDto(notification, createByUser);
        webSocketService.responseRealtime(destination, response);
    }

    private Notification buildEntity(String title, String content, Long idReceiver) {
        return Notification.builder()
                .id(UUID.randomUUID().toString())
                .title(title)
                .content(content)
                .idReceiver(idReceiver)
                .createAt(new Date())
                .deleted(false)
                .seen(false)
                .build();
    }

    private NotificationResponse toDto(Notification entity) {
        return FnCommon.copyProperties(NotificationResponse.class, entity);
    }

    private NotificationResponse toDto(Notification entity, User user) {
        NotificationResponse response = FnCommon.copyProperties(NotificationResponse.class, entity);
        response.setNameCreateBy(user.getFullName());
        response.setAvatar(user.getAvatar());
        return response;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
    }
}

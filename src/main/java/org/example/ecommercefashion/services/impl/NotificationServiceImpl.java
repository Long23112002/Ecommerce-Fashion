package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.WebSocketDestination;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.response.LoadMoreResponse;
import org.example.ecommercefashion.dtos.response.NotificationResponse;
import org.example.ecommercefashion.entities.Notification;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.notification.NotificationCode;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.NotificationRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.NotificationService;
import org.example.ecommercefashion.services.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketService webSocketService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MongoTemplate mongoTemplate;

    @Override
    public LoadMoreResponse<NotificationResponse> findAllNotificationsByUserId(Long id, int offset, int limit) {
        var entities = notificationRepository.findAllNotificationsByUserId(id, offset, limit);
        int count = notificationRepository.countByIdReceiver(id);
        var response = toDtos(entities);
        return new LoadMoreResponse("/api/v1/notification/user/", id, offset, limit, count, response);
    }

    @Override
    public LoadMoreResponse<NotificationResponse> findAllUnSeenNotificationByIdUser(Long id, Integer offset, Integer limit) {
        var entities = notificationRepository.findAllUnSeenNotificationByIdUser(id, offset, limit);
        int count = notificationRepository.countByIdReceiverAndSeenIsFalse(id);
        var response = toDtos(entities);
        return new LoadMoreResponse("/api/v1/notification/user/", id, offset, limit, count, response);
    }

    @Override
    @Transactional
    public List<NotificationResponse> markSeenAll(Long idReceiver) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id_receiver").is(idReceiver));

        List<Notification> entities = mongoTemplate.find(query, Notification.class);

        Update update = new Update();
        update.set("seen", true);
        mongoTemplate.updateMulti(query, update, Notification.class);

        var responses = toSeenDtos(entities);

        return responses;
    }

    @Override
    @Transactional
    public List<NotificationResponse> markSeenById(String idNoti) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(idNoti));

        List<Notification> entities = mongoTemplate.find(query, Notification.class);

        Update update = new Update();
        update.set("seen", true);
        mongoTemplate.updateMulti(query, update, Notification.class);

        var responses = toSeenDtos(entities);

        return responses;
    }

    @Override
    public void sendNotificationAll(Long createBy, NotificationCode notificationCode, Object... variables) {
        Optional<User> createByUserOptional = userRepository.findById(createBy);
        if (createByUserOptional.isPresent()) {
            User createByUser = createByUserOptional.get();
            List<User> users = userRepository.findAllUserByPermission(notificationCode.getPermission());
            List<Notification> notifications = buildAndSendNotifications(users, notificationCode, createByUser, variables);
            notificationRepository.saveAll(notifications);
        }
    }

    @Override
    public void sendNotificationAll(NotificationCode notificationCode, Object... variables) {
        List<User> users = userRepository.findAllUserByPermission(notificationCode.getPermission());
        List<Notification> notifications = buildAndSendNotifications(users, notificationCode, null, variables);
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void sendNotificationToUser(Long createBy, Long idReceiver, NotificationCode notificationCode, Object... variables) {
        User createByUser = getUserById(createBy);
        User receiver = getUserById(idReceiver);
        Notification notification = buildNotification(notificationCode, idReceiver, idReceiver, variables);
        notificationRepository.save(notification);
        sendRealtimeNotification(notification, createByUser);
    }

    @Override
    public void sendNotificationToUser(Long idReceiver, NotificationCode notificationCode, Object... variables) {
        User receiver = getUserById(idReceiver);
        Notification notification = buildNotification(notificationCode, idReceiver, null, variables);
        notificationRepository.save(notification);
        sendRealtimeNotification(notification, null);
    }

    private List<Notification> buildAndSendNotifications(List<User> users, NotificationCode notificationCode, User createByUser, Object... variables) {
        List<Notification> notifications = new ArrayList<>();
        for (User user : users) {
            Notification notification = buildNotification(notificationCode, user.getId(), createByUser.getId(), variables);
            notifications.add(notification);
            sendRealtimeNotification(notification, createByUser);
        }
        return notifications;
    }

    private Notification buildNotification(NotificationCode notificationCode, Long idReceiver, Long createBy, Object... variables) {
        String title = notificationCode.getDefaultTitle();
        String content = notificationCode.getContentWithInfor(Arrays.stream(variables)
                .map(obj -> obj.toString())
                .toArray());
        return buildEntity(title, content, idReceiver, createBy);
    }

    private void sendRealtimeNotification(Notification notification, User createByUser) {
        String destination = WebSocketDestination.NOTIFICATION.getDestinationWithSlash() + notification.getIdReceiver();
        NotificationResponse response = createByUser == null ? toDto(notification) : toDto(notification, createByUser);
        webSocketService.responseRealtime(destination, response);
    }

    private Notification buildEntity(String title, String content, Long idReceiver, Long createBy) {
        return Notification.builder()
                .id(UUID.randomUUID().toString())
                .title(title)
                .content(content)
                .idReceiver(idReceiver)
                .createBy(createBy)
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

    private List<NotificationResponse> toDtos(List<Notification> notis) {
        Set<Long> idUsers = notis.stream()
                .map(entity -> entity.getCreateBy())
                .collect(Collectors.toSet());
        Map<Long, User> mapUsers =
                userService.findAllEntityUserByIds(idUsers).stream()
                        .collect(Collectors.toMap(
                                user -> user.getId(),
                                user -> user
                        ));
        return notis.stream()
                .map(entity -> {
                    User user = mapUsers.get(entity.getCreateBy());
                    if (user == null) {
                        user = userService.getDeletedUser();
                    }
                    NotificationResponse response = FnCommon.copyProperties(NotificationResponse.class, entity);
                    response.setAvatar(user.getAvatar());
                    response.setNameCreateBy(user.getFullName());
                    return response;
                })
                .toList();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
    }

    private List<NotificationResponse> toSeenDtos (List<Notification> entities) {
        return toDtos(entities).stream()
                .map(entity -> {
                    entity.setSeen(true);
                    return entity;
                })
                .toList();
    }

}

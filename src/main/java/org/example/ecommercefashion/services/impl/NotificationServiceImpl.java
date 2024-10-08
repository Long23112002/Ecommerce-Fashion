package org.example.ecommercefashion.services.impl;

import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.WebSocketDestination;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.response.NotificationResponse;
import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.entities.Notification;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.notification.NotificationCode;
import org.example.ecommercefashion.repositories.NotificationMessageRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.NotificationService;
import org.example.ecommercefashion.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationMessageRepository notificationMessageRepository;
  private final WebSocketService webSocketService;
  private final UserRepository userRepository;

  @Override
  public void sendNotificationAll(NotificationCode notificationCode, Long createBy) {
    List<Notification> entities = new ArrayList<>();
    List<User> users = userRepository.findAllUserByPermission(notificationCode.getPermission());
    Optional<User> optional = userRepository.findById(createBy);
    if(optional.isPresent()){
      User createByUser = optional.get();
      for(User user : users){
        Notification entity = Notification.builder()
                .id(UUID.randomUUID().toString())
                .content(notificationCode.getDefaultContent())
                .title(notificationCode.getDefaultTitle())
                .idReceiver(user.getId())
                .createBy(createBy)
                .createAt(new Date())
                .deleted(false)
                .seen(false)
                .build();
        entities.add(entity);
        webSocketService.responseRealtime(WebSocketDestination.NOTIFICATION.getDestinationWithSlash()+entity.getIdReceiver(), toDto(entity, createByUser));
      }
      notificationMessageRepository.saveAll(entities);
    }
  }

  private NotificationResponse toDto(Notification entity, User user) {
    NotificationResponse response = FnCommon.copyProperties(NotificationResponse.class, entity);
    response.setNameCreateBy(user.getFullName());
    response.setAvatar(user.getAvatar());
    return response;
  }

}

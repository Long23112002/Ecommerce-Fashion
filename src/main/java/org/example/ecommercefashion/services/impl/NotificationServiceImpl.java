package org.example.ecommercefashion.services.impl;

import org.example.ecommercefashion.entities.NotificationMessage;
import org.example.ecommercefashion.repositories.NotificationMessageRepository;
import org.example.ecommercefashion.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired private NotificationMessageRepository notificationMessageRepository;

  @Override
  public void sendNotificationAll(NotificationMessage notificationMessage) {
    System.out.printf("Sending notification to all users: %s\n");
    notificationMessageRepository.save(notificationMessage);
  }


}

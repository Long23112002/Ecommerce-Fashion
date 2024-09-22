package org.example.ecommercefashion.entities;

import lombok.Data;
import org.example.ecommercefashion.enums.notification.NotificationCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Transient;

@Data
@Document(collection = "notification_message")
public class NotificationMessage {

  @Transient public static final String SEQUENCE = "notification";

  @Id private String id;

  private NotificationCode code;

  private String title;

  private String content;

  private String[] variables;

  private User user;

  private Long createdAt;
}

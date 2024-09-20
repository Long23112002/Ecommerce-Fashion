package org.example.ecommercefashion.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.Transient;

@Data
@Builder
@Document(collection = "user_notification")
public class UserNotification {

  @Transient public static final String SEQUENCE = "notification";

  @Id private String id;

  @Indexed private Long userId;

  private Notification notification;

  private Boolean read;

  private Long createdAt;

  private Long updatedAt;
}

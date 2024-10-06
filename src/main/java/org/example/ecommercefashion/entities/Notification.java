package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Map;

@Data
@Document(collection = "notification")
public class Notification {

  @Transient public static final String SEQUENCE = "notification";

  @Id private String id;

  @JsonIgnore private Map<String, Object> metaData;

  private String type;

  private Map<String, Object> data;

  private String title;

  private String content;

  private Long createdAt;
}

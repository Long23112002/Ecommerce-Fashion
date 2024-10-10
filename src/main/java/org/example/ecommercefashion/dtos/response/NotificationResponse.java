package org.example.ecommercefashion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private String id;

    private String title;

    private String content;

    private Long idReceiver;

    private String nameCreateBy;

    private String avatar;

    private Boolean seen;

    private Date createAt;
}

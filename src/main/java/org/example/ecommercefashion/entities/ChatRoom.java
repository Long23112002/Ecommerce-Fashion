package org.example.ecommercefashion.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_room")
public class ChatRoom {
    @Id
    private String id;

    @Field("id_client")
    private Long idClient;

    @Field("create_at")
    private Date createAt;

    @Field("deleted")
    private Boolean deleted;
}

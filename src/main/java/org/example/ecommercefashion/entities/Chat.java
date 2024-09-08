package org.example.ecommercefashion.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat")
public class Chat {
    @Id
    private String id;

    @Field("content")
    private String content;

    @Field("seen")
    private Boolean seen;

    @Field("create_by")
    private Long createBy;

    @Field("create_at")
    private Date createAt;

    @Field("deleted")
    private Boolean deleted;

    @Field("id_room")
    private String idRoom;
}

package org.example.ecommercefashion.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat")
public class Chat {
    @Id
    private String id;

    @Field("content")
    private String content;

    @Field("image")
    private String image;

    @Field("seen")
    @Indexed
    private Boolean seen;

    @Field("create_by")
    private Long createBy;

    @Field("create_at")
    @Indexed
    private Date createAt;

    @Field("deleted")
    private Boolean deleted;

    @Field("id_room")
    @Indexed
    private String idRoom;

    @Field("id_reply")
    private String idReply;
}

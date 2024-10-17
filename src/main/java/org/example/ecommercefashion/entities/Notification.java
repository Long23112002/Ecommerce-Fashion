package org.example.ecommercefashion.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "notification")
public class Notification {
    @Id
    private String id;

    @Field("title")
    private String title;

    @Field("content")
    private String content;

    @Field("id_receiver")
    @Indexed
    private Long idReceiver;

    @Field("seen")
    @Indexed
    private Boolean seen;

    @Field("create_by")
    private Long createBy;

    @Field("create_at")
    @Indexed
    private Date createAt;

    @Field("update_by")
    private Long updateBy;

    @Field("update_at")
    private Date updateAt;

    @Field("deleted")
    @Indexed
    private Boolean deleted;
}

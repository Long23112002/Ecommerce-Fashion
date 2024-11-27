package org.example.ecommercefashion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyResponse {

    private String id;

    private String content;

    private String image;

    private Long createBy;

    private String nameCreateBy;

    private Date createAt;

}

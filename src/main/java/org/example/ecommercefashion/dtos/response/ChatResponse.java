package org.example.ecommercefashion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {

  private String id;

  private String idRoom;

  private String content;

  private String image;

  private Boolean seen;

  private Long createBy;

  private String avatar;

  private Date createAt;

  private String nameCreateBy;

  private ReplyResponse reply;

}

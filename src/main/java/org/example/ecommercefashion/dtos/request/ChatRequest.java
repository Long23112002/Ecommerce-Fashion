package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequest {
  private String idRoom;
  private String content;
  private String image;
  private Long createBy;
  private String idReply;
}

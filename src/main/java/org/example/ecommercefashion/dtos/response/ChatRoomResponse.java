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
public class ChatRoomResponse {

    private String id;

    private Long idClient;

    private String nameClient;

    private String avatar;

    private Boolean seen;

    private String lastChatContent;

    private Long lastChatSendBy;

}

package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.ChatRoomRequest;
import org.example.ecommercefashion.dtos.response.ChatRoomResponse;

public interface ChatRoomService {
    String findIdChatRoomByUserId(Long id);
    ChatRoomResponse create(ChatRoomRequest request);
}

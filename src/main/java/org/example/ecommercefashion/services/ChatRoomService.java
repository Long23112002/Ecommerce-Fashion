package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.ChatRoomRequest;
import org.example.ecommercefashion.dtos.response.ChatRoomResponse;

import java.util.List;

public interface ChatRoomService {
    List<ChatRoomResponse> findAllChatRoom();
    String findIdChatRoomByUserId(Long id);
    ChatRoomResponse create(ChatRoomRequest request);
}

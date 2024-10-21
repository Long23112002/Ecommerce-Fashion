package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.dtos.request.ChatRoomRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.dtos.response.ChatRoomResponse;
import org.example.ecommercefashion.entities.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {
    List<ChatRoomResponse> findAllChatRoom();
    ChatRoomResponse findById(String id);
    String findIdChatRoomByUserId(Long id);
    Optional<Chat> findLastChatByIdChatRoom(String id);
    ChatRoomResponse create(ChatRoomRequest request);
    void delete(String id);
    List<ChatRoomResponse> findAllChatRoomByIdUsers(UserParam param);
}

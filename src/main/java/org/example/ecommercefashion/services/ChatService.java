package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

    ChatResponse createChat(ChatRequest request);

    List<ChatResponse> findAllChatsByRoomId(String id, int p);

    void markAllChatsAsSeen(String id, Long createBy);
}

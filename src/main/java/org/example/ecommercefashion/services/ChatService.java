package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

    ChatResponse create(ChatRequest request);

    List<ChatResponse> findAllChatByIdChatRoom(String id);

    void seenAllChatByIdChatRoom(String id, Long createBy);
}

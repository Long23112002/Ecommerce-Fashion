package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.dtos.response.LoadMoreResponse;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface ChatService {

    LoadMoreResponse<ChatResponse> findAllChatsByRoomId(String roomId, int offset, int limit);

    ChatResponse createChat(ChatRequest request);

    void markAllChatsAsSeen(String id, Long createBy);

    LoadMoreResponse<ChatResponse> findChatsUntilTarget(String id);

    List<ChatResponse> findAllLastChatByRoomIds(Collection<String> ids);
}

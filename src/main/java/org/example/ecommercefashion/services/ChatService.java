package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {

    ChatResponse create(ChatRequest request);
}

package org.example.ecommercefashion.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.services.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    final WebSocketService webSocketService;
    final ChatService chatService;

    @MessageMapping("/chat.sendMessage/{idRoom}")
    public void sendMessage(@DestinationVariable String idRoom,
                            @Payload ChatRequest request) {
        ChatResponse response = chatService.create(request);
        webSocketService.responseRealtime("/room/" + idRoom, response);
    }
}

package org.example.ecommercefashion.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.services.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

  final ChatService chatService;

  @MessageMapping("/chat.sendMessage/{idRoom}")
  public void sendMessage(@Payload ChatRequest request) {
    chatService.create(request);
  }
}

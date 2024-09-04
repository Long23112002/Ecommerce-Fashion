package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ChatRoomRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.dtos.response.ChatRoomResponse;
import org.example.ecommercefashion.services.ChatRoomService;
import org.example.ecommercefashion.services.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat_room")
@RequiredArgsConstructor
@Api(tags = "ChatRoom", value = "Endpoints for chat room management")
public class ChatRoomController {

    final ChatRoomService chatRoomService;
    final ChatService chatService;

    @GetMapping("/user/{id}")
    public String findIdChatRoomByUserId(@PathVariable("id") Long id) {
       return chatRoomService.findIdChatRoomByUserId(id);
    }

    @GetMapping("/chats/{id}")
    public List<ChatResponse> findAllChatByIdChatRoom(@PathVariable("id") String id) {
       return chatService.findAllChatByIdChatRoom(id);
    }

    @PostMapping
    public ChatRoomResponse create(@RequestBody ChatRoomRequest request) {
        return chatRoomService.create(request);
    }

}

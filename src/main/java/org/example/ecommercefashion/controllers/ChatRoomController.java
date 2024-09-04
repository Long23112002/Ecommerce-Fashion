package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ChatRoomRequest;
import org.example.ecommercefashion.dtos.response.ChatRoomResponse;
import org.example.ecommercefashion.services.ChatRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat_room")
@RequiredArgsConstructor
@Api(tags = "ChatRoom", value = "Endpoints for chat room management")
public class ChatRoomController {

    final ChatRoomService chatRoomService;

    @GetMapping("/user/{id}")
    public String findIdChatRoomByUserId(@PathVariable("id") Long id) {
       return chatRoomService.findIdChatRoomByUserId(id);
    }

    @PostMapping
    public ChatRoomResponse create(@RequestBody ChatRoomRequest request) {
        return chatRoomService.create(request);
    }

}

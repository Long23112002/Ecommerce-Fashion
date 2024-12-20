package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.dtos.request.ChatRoomRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.dtos.response.ChatRoomResponse;
import org.example.ecommercefashion.dtos.response.LoadMoreResponse;
import org.example.ecommercefashion.services.ChatRoomService;
import org.example.ecommercefashion.services.ChatService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat-room")
@RequiredArgsConstructor
@Api(tags = "ChatRoom", value = "Endpoints for chat room management")
public class ChatRoomController {

    final ChatRoomService chatRoomService;
    final ChatService chatService;

    @GetMapping
    public List<ChatRoomResponse> findAllChatRoom() {
        return chatRoomService.findAllChatRoom();
    }

    @GetMapping("/user/{id}")
    public String findIdChatRoomByUserId(@PathVariable("id") Long id) {
        return chatRoomService.findIdChatRoomByUserId(id);
    }

    @GetMapping("/chats/{id}")
    public LoadMoreResponse<ChatResponse> findAllChatByIdChatRoom(
            @PathVariable("id") String id,
            @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(name = "limit", defaultValue = "15") Integer limit
    ) {
        return chatService.findAllChatsByRoomId(id, offset, limit);
    }

    @PatchMapping("/chats/{idRoom}/{idUser}")
    public void seenAllChatByIdChatRoom(@PathVariable("idRoom") String idRoom,
                                        @PathVariable("idUser") Long idUser) {
        chatService.markAllChatsAsSeen(idRoom, idUser);
    }

    @PostMapping
    public ChatRoomResponse createChatRoom(@RequestBody ChatRoomRequest request) {
        return chatRoomService.create(request);
    }

    @DeleteMapping("/{id}")
    public void deleteRoomById(@PathVariable("id") String id) {
        chatRoomService.delete(id);
    }

    @GetMapping("/chats/before-target/{id}")
    public LoadMoreResponse<ChatResponse> findChatsUntilTarget(@PathVariable("id") String id) {
        return chatService.findChatsUntilTarget(id);
    }

    @GetMapping("/users")
    public List<ChatRoomResponse> findAllChatRoomByIdUsers(UserParam param) {
        return chatRoomService.findAllChatRoomByIdUsers(param);
    }

}

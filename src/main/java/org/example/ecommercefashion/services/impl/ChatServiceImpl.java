package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ChatRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.ChatRoomService;
import org.example.ecommercefashion.services.ChatService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    final ChatRepository chatRepository;
    final UserRepository userRepository;
    final WebSocketService webSocketService;
    final ChatRoomService chatRoomService;
    final MongoTemplate mongoTemplate;

    @Override
    public ChatResponse create(ChatRequest request) {
        try {
            Chat entity = FnCommon.copyProperties(Chat.class, request);
            defaultCreateValue(entity);
            Chat save = chatRepository.save(entity);
            ChatResponse response = toDto(save);
            return response;
        } finally {
            webSocketService.responseRealtime("/admin", chatRoomService.findAllChatRoom());
        }
    }

    @Override
    public List<ChatResponse> findAllChatByIdChatRoom(String id) {
        return chatRepository.findAllChatByIdChatRoom(id).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void seenAllChatByIdChatRoom(String id) {
        var responses = chatRoomService.findAllChatRoom().stream()
                .map(chatRoom -> {
                    if (chatRoom.getId().equals(id)) {
                        chatRoom.setSeen(true);
                    }
                    return chatRoom;
                })
                .toList();
        webSocketService.responseRealtime("/admin", responses);
        Query query = new Query(Criteria.where("id_room").is(id));
        Update update = new Update().set("seen", true);
        mongoTemplate.updateMulti(query, update, Chat.class);
    }

    private void defaultCreateValue(Chat entity) {
        entity.setId(UUID.randomUUID().toString());
        entity.setCreateAt(new Date());
        entity.setDeleted(false);
        entity.setSeen(false);
    }

    private ChatResponse toDto(Chat entity) {
        ChatResponse response = FnCommon.copyProperties(ChatResponse.class, entity);
        User user = userRepository.findById(entity.getCreateBy())
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
        response.setAvatar(user.getAvatar());
        return response;
    }

}

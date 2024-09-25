package org.example.ecommercefashion.services.impl;

import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.RoomSubscriptionService;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.repositories.ChatRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.ChatRoomService;
import org.example.ecommercefashion.services.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    final ChatRepository chatRepository;
    final UserRepository userRepository;
    final WebSocketService webSocketService;
    final ChatRoomService chatRoomService;
    final MongoTemplate mongoTemplate;
    final RoomSubscriptionService subscriptionService;

    @Override
    public ChatResponse create(ChatRequest request) {
        ChatResponse response = null;
        try {
            Chat entity = FnCommon.copyProperties(Chat.class, request);
            defaultCreateValue(entity);
            Chat save = chatRepository.save(entity);
            response = toDto(save);
            return response;
        } finally {
            if (response != null) {
                seenAllChatByIdChatRoom(request.getIdRoom(), request.getCreateBy());
                webSocketService.responseRealtime("/room/" + request.getIdRoom(), response);
            }
        }
    }

    @Override
    public List<ChatResponse> findAllChatByIdChatRoom(String id, int p) {
        int limit = 15;
        int offset = p*limit;
        return chatRepository.findAllChatByIdChatRoom(id, offset, limit).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void seenAllChatByIdChatRoom(String id, Long createBy) {
        boolean isSeen = subscriptionService.getUsersInRoom(id).stream()
                .anyMatch(idUser -> {
                    Optional<User> optional = userRepository.findById(idUser);
                    if (optional.isPresent()) {
                        return optional.get().getIsAdmin();
                    }
                    return false;
                }) || createBy == null;
        if (isSeen) {
            Query query = new Query(Criteria.where("id_room").is(id));
            Update update = new Update().set("seen", true);
            mongoTemplate.updateMulti(query, update, Chat.class);
        }
        var responses = chatRoomService.findAllChatRoom().stream()
                .map(chatRoom -> {
                    if (chatRoom.getId().equals(id)) {
                        chatRoom.setSeen(isSeen);
                    }
                    return chatRoom;
                })
                .toList();
        webSocketService.responseRealtime("/admin", responses);
    }

    private void defaultCreateValue(Chat entity) {
        entity.setId(UUID.randomUUID().toString());
        entity.setCreateAt(new Date());
        entity.setDeleted(false);
        entity.setSeen(false);
    }

    private ChatResponse toDto(Chat entity) {
        ChatResponse response = FnCommon.copyProperties(ChatResponse.class, entity);
        userRepository.findById(entity.getCreateBy())
                .ifPresent(ent -> {
                    response.setAvatar(ent.getAvatar());
                });
        return response;
    }

}

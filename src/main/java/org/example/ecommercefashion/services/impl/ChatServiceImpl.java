package org.example.ecommercefashion.services.impl;

import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.RoomSubscriptionService;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.dtos.response.ChatRoomResponse;
import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.repositories.ChatRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.ChatRoomService;
import org.example.ecommercefashion.services.ChatService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    private final ChatRoomService chatRoomService;
    private final MongoTemplate mongoTemplate;
    private final RoomSubscriptionService subscriptionService;

    @Override
    public ChatResponse createChat(ChatRequest request) {
        Chat chatEntity = FnCommon.copyProperties(Chat.class, request);
        setDefaultChatValues(chatEntity);
        Chat savedChat = chatRepository.save(chatEntity);
        ChatResponse chatResponse = toDto(savedChat);

        markAllChatsAsSeenAsync(request.getIdRoom());
        webSocketService.responseRealtime("/room/" + request.getIdRoom(), chatResponse);

        return chatResponse;
    }

    @Override
    public List<ChatResponse> findAllChatsByRoomId(String roomId, int page) {
        int limit = 15;
        int offset = page * limit;
        return chatRepository.findAllChatByIdChatRoom(roomId, offset, limit).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markAllChatsAsSeen(String roomId, Long userId) {
        updateSeenStatus(roomId, userId);
        var chatRooms = findAllChatRoomsCached();
        webSocketService.responseRealtime("/admin", chatRooms);
    }

    @Async
    public void markAllChatsAsSeenAsync(String roomId) {
        var usersInRoom = subscriptionService.getUsersInRoom(roomId);
        updateSeenStatus(roomId, usersInRoom);
        var chatRooms = findAllChatRoomsCached();
        webSocketService.responseRealtime("/admin", chatRooms);
    }

    private void updateSeenStatus(String roomId, Long creatorId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id_room").is(roomId));
        query.addCriteria(Criteria.where("create_by").ne(creatorId));

        Update update = new Update().set("seen", true);
        mongoTemplate.updateMulti(query, update, Chat.class);
    }

    private void updateSeenStatus(String roomId, Set<Long> userIds) {
        for(Long userId : userIds) {
            Query query = new Query();
            query.addCriteria(Criteria.where("id_room").is(roomId));
            query.addCriteria(Criteria.where("create_by").ne(userId));

            Update update = new Update().set("seen", true);
            mongoTemplate.updateMulti(query, update, Chat.class);
        }
    }

    @Cacheable("chatRooms")
    public List<ChatRoomResponse> findAllChatRoomsCached() {
        return chatRoomService.findAllChatRoom();
    }

    private void setDefaultChatValues(Chat chatEntity) {
        chatEntity.setId(UUID.randomUUID().toString());
        chatEntity.setCreateAt(new Date());
        chatEntity.setDeleted(false);
        chatEntity.setSeen(false);
    }

    private ChatResponse toDto(Chat chatEntity) {
        ChatResponse chatResponse = FnCommon.copyProperties(ChatResponse.class, chatEntity);
        userRepository.findById(chatEntity.getCreateBy())
                .ifPresent(user -> chatResponse.setAvatar(user.getAvatar()));
        return chatResponse;
    }

}

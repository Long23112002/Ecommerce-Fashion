package org.example.ecommercefashion.services.impl;

import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.RoomSubscriptionService;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.repositories.ChatRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.ChatRoomService;
import org.example.ecommercefashion.services.ChatService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        ChatResponse chatResponse = convertToDto(savedChat);

        markAllChatsAsSeen(request.getIdRoom());
        webSocketService.responseRealtime("/room/" + request.getIdRoom(), chatResponse);

        return chatResponse;
    }

    @Override
    public List<ChatResponse> findAllChatsByRoomId(String roomId, int page) {
        int limit = 15;
        int offset = page * limit;
        return chatRepository.findAllChatByIdChatRoom(roomId, offset, limit).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public void markAllChatsAsSeen(String roomId, Long userId) {
        updateSeenStatus(roomId, userId);
        var chatRooms = chatRoomService.findAllChatRoom();
        webSocketService.responseRealtime("/admin", chatRooms);
    }

    private void markAllChatsAsSeen(String roomId) {
        var usersInRoom = subscriptionService.getUsersInRoom(roomId);
        updateSeenStatus(roomId, usersInRoom.stream().toList());
        var chatRooms = chatRoomService.findAllChatRoom();
        webSocketService.responseRealtime("/admin", chatRooms);
    }

    private void updateSeenStatus(String roomId, Long creatorId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id_room").is(roomId));
        query.addCriteria(Criteria.where("create_by").ne(creatorId));

        Update update = new Update().set("seen", true);
        mongoTemplate.updateMulti(query, update, Chat.class);
    }

    private void updateSeenStatus(String roomId, List<Long> userIds) {
        for (Long userId : userIds) {
            Query query = new Query();
            query.addCriteria(Criteria.where("id_room").is(roomId));
            query.addCriteria(Criteria.where("create_by").ne(userId));

            Update update = new Update().set("seen", true);
            mongoTemplate.updateMulti(query, update, Chat.class);
        }
    }

    private void setDefaultChatValues(Chat chatEntity) {
        chatEntity.setId(UUID.randomUUID().toString());
        chatEntity.setCreateAt(new Date());
        chatEntity.setDeleted(false);
        chatEntity.setSeen(false);
    }

    private ChatResponse convertToDto(Chat chatEntity) {
        ChatResponse chatResponse = FnCommon.copyProperties(ChatResponse.class, chatEntity);
        userRepository.findById(chatEntity.getCreateBy())
                .ifPresent(user -> chatResponse.setAvatar(user.getAvatar()));
        return chatResponse;
    }
}

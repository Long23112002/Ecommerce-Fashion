package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.RoomSubscriptionService;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.dtos.response.ReplyResponse;
import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ChatRepository;
import org.example.ecommercefashion.services.ChatRoomService;
import org.example.ecommercefashion.services.ChatService;
import org.example.ecommercefashion.services.UserService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;
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
        var responses = chatRepository.findAllChatByIdChatRoom(roomId, offset, limit);
        return toDtos(responses);
    }

    @Override
    public void markAllChatsAsSeen(String roomId, Long userId) {
        updateSeenStatus(roomId, userId);
        var chatRooms = chatRoomService.findAllChatRoom();
        webSocketService.responseRealtime("/admin", chatRooms);
    }

    @Override
    public List<ChatResponse> findChatsUntilTarget(String id) {
        Chat target = chatRepository.findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CHAT_NOT_FOUND));
        var responses = chatRepository.findChatsUntilTarget(target);
        return toDtos(responses);
    }

    @Override
    public List<ChatResponse> findAllLastChatByRoomIds(Collection<String> ids) {
        return findAllLastChatByRoomIds(ids);
    }

    @Async
    public void markAllChatsAsSeenAsync(String roomId) {
        var usersInRoom = subscriptionService.getUsersInRoom(roomId);
        updateSeenStatus(roomId, usersInRoom);
        var chatRooms = chatRoomService.findAllChatRoom();
        webSocketService.responseRealtime("/admin", chatRooms);
    }

    private Update updateSeenStatus(String roomId, Long userId) {
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(
                Criteria.where("id_room").is(roomId),
                Criteria.where("create_by").ne(userId)
        ));
        Update update = new Update().set("seen", true);
        mongoTemplate.updateMulti(query, update, Chat.class);
        return update;
    }

    private void updateSeenStatus(String roomId, Set<Long> userIds) {
        for (Long userId : userIds) {
            Query query = new Query();
            query.addCriteria(new Criteria().andOperator(
                    Criteria.where("id_room").is(roomId),
                    Criteria.where("create_by").ne(userId)
            ));
            Update update = new Update().set("seen", true);
            mongoTemplate.updateMulti(query, update, Chat.class);
        }
    }

    private void setDefaultChatValues(Chat entity) {
        entity.setId(UUID.randomUUID().toString());
        entity.setCreateAt(new Date());
        entity.setDeleted(false);
        entity.setSeen(false);
    }

    private ChatResponse toDto(Chat entity) {
        ChatResponse response = FnCommon.copyProperties(ChatResponse.class, entity);
        User user = userService.findUserOrDefault(entity.getCreateBy());
        response.setAvatar(user.getAvatar());
        response.setNameCreateBy(user.getFullName());

        String idReply = entity.getIdReply();
        if (idReply != null) {
            chatRepository.findById(idReply)
                    .ifPresent(reply -> {
                        String nameCreateBy = userService.findUserOrDefault(reply.getCreateBy()).getFullName();
                        response.setReply(
                                ReplyResponse.builder()
                                        .id(reply.getId())
                                        .content(reply.getContent())
                                        .createAt(reply.getCreateAt())
                                        .createBy(reply.getCreateBy())
                                        .nameCreateBy(nameCreateBy)
                                        .build()
                        );
                    });
        }
        return response;
    }

    private List<ChatResponse> toDtos(Collection<Chat> entities) {
        Set<Long> idUsers = entities.stream()
                .map(entity -> entity.getCreateBy())
                .collect(Collectors.toSet());

        Map<Long, User> mapUsers =
                userService.findAllEntityUserByIds(idUsers).stream()
                        .collect(Collectors.toMap(
                                user -> user.getId(),
                                user -> user
                        ));

        return entities.stream()
                .map(entity -> {
                    ChatResponse response = FnCommon.copyProperties(ChatResponse.class, entity);
                    User user = mapUsers.get(entity.getCreateBy());
                    if (user == null) {
                        user = userService.getDeletedUser();
                    }
                    response.setAvatar(user.getAvatar());
                    response.setNameCreateBy(user.getFullName());

                    String idReply = entity.getIdReply();
                    if (idReply != null) {
                        chatRepository.findById(idReply)
                                .ifPresent(reply -> {
                                    String nameCreateBy = userService.findUserOrDefault(reply.getCreateBy()).getFullName();
                                    response.setReply(
                                            ReplyResponse.builder()
                                                    .id(reply.getId())
                                                    .content(reply.getContent())
                                                    .createAt(reply.getCreateAt())
                                                    .createBy(reply.getCreateBy())
                                                    .nameCreateBy(nameCreateBy)
                                                    .build());
                                });
                    }
                    return response;
                })
                .toList();
    }

}

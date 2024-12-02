package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.chat.RoomSubscriptionService;
import org.example.ecommercefashion.config.socket.WebSocketDestination;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.dtos.response.LoadMoreResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
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
    @Transactional
    public ChatResponse createChat(ChatRequest request) {
        Chat chatEntity = FnCommon.copyProperties(Chat.class, request);
        setDefaultChatValues(chatEntity);
        Chat savedChat = chatRepository.save(chatEntity);
        ChatResponse chatResponse = toDto(savedChat);

        markAllChatsAsSeenAsync(request.getIdRoom());
        webSocketService.responseRealtime(WebSocketDestination.CHAT_ROOM.getDestinationWithSlash() + request.getIdRoom(), chatResponse);

        return chatResponse;
    }

    @Override
    public LoadMoreResponse<ChatResponse> findAllChatsByRoomId(String roomId, int offset, int limit) {
        var entities = chatRepository.findAllChatByIdChatRoom(roomId, offset, limit);
        int count = chatRepository.countByIdRoom(roomId);
        var responses = toDtos(entities);
        return new LoadMoreResponse("/api/v1/chat-room/chats/", roomId, offset, limit, count, responses);
    }

    @Override
    public void markAllChatsAsSeen(String roomId, Long userId) {
        updateSeenStatus(roomId, userId);
        var chatRooms = chatRoomService.findAllChatRoom();
        webSocketService.responseRealtime(WebSocketDestination.CHAT_ADMIN.getDestination(), chatRooms);
    }

    @Override
    public LoadMoreResponse<ChatResponse> findChatsUntilTarget(String id) {
        Chat target = chatRepository.findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CHAT_NOT_FOUND));
        var entities = chatRepository.findChatsUntilTarget(target);
        var responses = toDtos(entities);
        return toLoadMore(responses);
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
        webSocketService.responseRealtime(WebSocketDestination.CHAT_ADMIN.getDestination(), chatRooms);
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
        User user = userService.findUserOrDefault(entity.getCreateBy());
        String idReply = entity.getIdReply() + "";
        Chat reply = chatRepository.findById(idReply)
                .orElse(null);
        return buildChatResponse(entity, user, reply);
    }

    private List<ChatResponse> toDtos(Collection<Chat> entities) {

        Set<String> idReplies = entities.stream()
                .map(entity -> entity.getIdReply())
                .collect(Collectors.toSet());

        Set<Long> idUsers = entities.stream()
                .map(entity -> entity.getCreateBy())
                .collect(Collectors.toSet());

        Map<String, Chat> mapReplies =
                chatRepository.findAllByIds(idReplies).stream()
                        .map(chat -> {
                            idUsers.add(chat.getCreateBy());
                            return chat;
                        })
                        .collect(Collectors.toMap(
                                chat -> chat.getId(),
                                chat -> chat
                        ));

        Map<Long, User> mapUsers =
                userService.findAllEntityUserByIds(idUsers).stream()
                        .collect(Collectors.toMap(
                                user -> user.getId(),
                                user -> user
                        ));

        return entities.stream()
                .map(entity -> {
                    User user = mapUsers.get(entity.getCreateBy());
                    if (user == null) {
                        user = userService.getDeletedUser();
                    }
                    Chat reply = mapReplies.get(entity.getIdReply());
                    return buildChatResponse(entity, user, reply);
                })
                .toList();
    }

    private ChatResponse buildChatResponse(Chat entity, User user, Chat reply) {
        ChatResponse response = FnCommon.copyProperties(ChatResponse.class, entity);
        response.setAvatar(user.getAvatar());
        response.setNameCreateBy(user.getFullName());

        if (reply != null) {
            String nameCreateBy = userService.findUserOrDefault(reply.getCreateBy()).getFullName();
            response.setReply(
                    ReplyResponse.builder()
                            .id(reply.getId())
                            .content(reply.getContent())
                            .image(reply.getImage())
                            .createAt(reply.getCreateAt())
                            .createBy(reply.getCreateBy())
                            .nameCreateBy(nameCreateBy)
                            .build()
            );
        }
        return response;
    }

    private LoadMoreResponse<ChatResponse> toLoadMore(List<ChatResponse> response) {
        String idRoom = response.get(0).getIdRoom();
        int limit = 15;
        int offset = response.size() - limit;
        int count = chatRepository.countByIdRoom(idRoom);
        return new LoadMoreResponse("/api/v1/chat-room/chats/", idRoom, offset, limit, count, response);
    }

}

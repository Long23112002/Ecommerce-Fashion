package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.RoomSubscriptionService;
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
    public LoadMoreResponse<ChatResponse> findAllChatsByRoomId(String roomId, int offset, int limit) {
        var entities = chatRepository.findAllChatByIdChatRoom(roomId, offset, limit);
        int count = chatRepository.countByIdRoom(roomId);
        return toLoadMore(roomId, offset, limit, count, entities);
    }

    @Override
    public void markAllChatsAsSeen(String roomId, Long userId) {
        updateSeenStatus(roomId, userId);
        var chatRooms = chatRoomService.findAllChatRoom();
        webSocketService.responseRealtime("/admin", chatRooms);
    }

    @Override
    public LoadMoreResponse<ChatResponse> findChatsUntilTarget(String id) {
        Chat target = chatRepository.findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CHAT_NOT_FOUND));
        var responses = chatRepository.findChatsUntilTarget(target);
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
        User user = userService.findUserOrDefault(entity.getCreateBy());
        String idReply = entity.getIdReply();
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
                            .createAt(reply.getCreateAt())
                            .createBy(reply.getCreateBy())
                            .nameCreateBy(nameCreateBy)
                            .build()
            );
        }
        return response;
    }

    private LoadMoreResponse<ChatResponse> toLoadMore(List<Chat> chats) {
        String idRoom = chats.get(0).getIdRoom();
        int limit = 15;
        int offset = chats.size() - limit;
        int count = chatRepository.countByIdRoom(idRoom);
        return toLoadMore(idRoom, offset, limit, count, chats);
    }

    private LoadMoreResponse<ChatResponse> toLoadMore(String idRoom, int offset, int limit, int totalChats, List<Chat> chats) {
        String apiBase = "/api/v1/chat_room/chats/";

        String next = generateNextLink(apiBase, idRoom, offset, limit, totalChats);
        String previous = generatePreviousLink(apiBase, idRoom, offset, limit);

        return LoadMoreResponse.<ChatResponse>builder()
                .results(toDtos(chats))
                .next(next)
                .previous(previous)
                .build();
    }

    private String generateNextLink(String apiBase, String idRoom, int offset, int limit, int totalChats) {
        totalChats--;
        if (offset < totalChats) {
            int nextOffset = offset + limit;
            int nextLimit = Math.min(limit, totalChats - offset);
            return String.format("%s%s?offset=%d&limit=%d", apiBase, idRoom, nextOffset, nextLimit);
        }
        return null;
    }

    private String generatePreviousLink(String apiBase, String idRoom, int offset, int limit) {
        if (offset > 0) {
            int previousOffset = Math.max(0, offset - limit);
            int previousLimit = Math.min(limit, offset);
            return String.format("%s%s?offset=%d&limit=%d", apiBase, idRoom, previousOffset, previousLimit);
        }
        return null;
    }




}

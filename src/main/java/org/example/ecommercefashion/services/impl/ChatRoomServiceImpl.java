package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.dtos.request.ChatRoomRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.dtos.response.ChatRoomResponse;
import org.example.ecommercefashion.dtos.response.ReplyResponse;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.entities.ChatRoom;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ChatRepository;
import org.example.ecommercefashion.repositories.ChatRoomRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.ChatRoomService;
import org.example.ecommercefashion.services.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    final ChatRoomRepository chatRoomRepository;
    final ChatRepository chatRepository;
    final UserService userService;
    final WebSocketService webSocketService;
    final MongoTemplate mongoTemplate;

    @Override
    @Cacheable("chatRooms")
    public List<ChatRoomResponse> findAllChatRoom() {
        var reponses = chatRoomRepository.findAllChatRoom();
        return toDtos(reponses);
    }

    @Override
    public ChatRoomResponse findById(String id) {
        return chatRoomRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CHAT_ROOM_NOT_FOUND));
    }

    @Override
    public String findIdChatRoomByUserId(Long id) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findChatRoomByIdUser(id);
        if (chatRoomOptional.isPresent()) {
            return chatRoomOptional.get().getId();
        }
        return null;
    }

    @Override
    public Optional<Chat> findLastChatByIdChatRoom(String id) {
        chatRoomRepository.findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CHAT_ROOM_NOT_FOUND));
        return chatRepository.findLastChatByIdChatRoom(id);
    }

    @Override
    @Transactional
    public ChatRoomResponse create(ChatRoomRequest request) {
        ChatRoom entity = FnCommon.copyProperties(ChatRoom.class, request);
        defaultCreateValue(entity);
        ChatRoom save = chatRoomRepository.save(entity);
        ChatRoomResponse response = toDto(save);
        return response;
    }

    @Override
    public void delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update().set("deleted", true);
        mongoTemplate.updateMulti(query, update, ChatRoom.class);
        webSocketService.responseRealtime("/admin", findAllChatRoom());
    }

    @Override
    public List<ChatRoomResponse> findAllChatRoomByIdUsers(UserParam param) {
        List<UserResponse> users = userService.getAllUsers(param, null).getData();
        Set<Long> idUsers = users.stream()
                .map(user -> user.getId())
                .collect(Collectors.toSet());
        List<ChatRoom> entities = chatRoomRepository.findChatRoomByUserIds(idUsers);
        return toDtos(entities);
    }

    private void defaultCreateValue(ChatRoom entity) {
        entity.setId(UUID.randomUUID().toString());
        entity.setCreateAt(new Date());
        entity.setDeleted(false);
    }

    private ChatRoomResponse toDto(ChatRoom entity) {
        ChatRoomResponse response = FnCommon.copyProperties(ChatRoomResponse.class, entity);

        findLastChatByIdChatRoom(entity.getId()).ifPresent(chat -> {
            response.setLastChatContent(chat.getContent());
            response.setSeen(chat.getSeen());
            response.setLastChatSendBy(chat.getCreateBy());
        });

        User user = userService.findUserOrDefault(entity.getIdClient());

        response.setNameClient(user.getFullName());
        response.setAvatar(user.getAvatar());

        return response;
    }

    private List<ChatRoomResponse> toDtos(Collection<ChatRoom> entities) {

        Set<String> idRooms = entities.stream()
                .map(entity -> entity.getId())
                .collect(Collectors.toSet());

        Set<Long> idUsers = entities.stream()
                .map(entity -> entity.getIdClient())
                .collect(Collectors.toSet());

        Map<String, Chat> mapLastChat =
                chatRepository.findAllLastChatByIdRooms(idRooms).stream()
                        .collect(Collectors.toMap(
                                chat -> chat.getIdRoom(),
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
                    ChatRoomResponse response = FnCommon.copyProperties(ChatRoomResponse.class, entity);

                    Chat chat = mapLastChat.get(entity.getId());
                    if (chat != null) {
                        response.setLastChatContent(chat.getContent());
                        response.setSeen(chat.getSeen());
                        response.setLastChatSendBy(chat.getCreateBy());
                    }

                    User user = mapUsers.get(entity.getIdClient());
                    if (user == null) {
                        user = userService.getDeletedUser();
                    }
                    response.setNameClient(user.getFullName());
                    response.setAvatar(user.getAvatar());

                    return response;
                })
                .toList();
    }

}

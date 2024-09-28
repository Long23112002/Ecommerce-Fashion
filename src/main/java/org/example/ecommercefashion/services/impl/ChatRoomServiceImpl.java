package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.WebSocketService;
import org.example.ecommercefashion.dtos.request.ChatRoomRequest;
import org.example.ecommercefashion.dtos.response.ChatRoomResponse;
import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.entities.ChatRoom;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ChatRepository;
import org.example.ecommercefashion.repositories.ChatRoomRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.ChatRoomService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    final ChatRoomRepository chatRoomRepository;
    final ChatRepository chatRepository;
    final UserRepository userRepository;
    final WebSocketService webSocketService;
    final MongoTemplate mongoTemplate;

    @Override
    public List<ChatRoomResponse> findAllChatRoom() {
        return chatRoomRepository.findAllChatRoom().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ChatRoomResponse findById(String id) {
        return chatRoomRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CHAT_ROOM_NOT_FOUND));
    }

    @Override
    public String findIdChatRoomByUserId(Long id) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findChatRoomByUserId(id);
        if (chatRoomOptional.isPresent()) {
            return chatRoomOptional.get().getId();
        }
        throw new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CHAT_ROOM_NOT_FOUND);
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
        Update update = new Update().set("deleted",true);
        mongoTemplate.updateMulti(query, update, ChatRoom.class);
        webSocketService.responseRealtime("/admin", findAllChatRoom());
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

        User user = userRepository.findById(entity.getIdClient())
                .filter(ent -> !ent.getDeleted())
                .orElseGet(() -> User.builder()
                        .fullName("Không xác định")
                        .build());

        response.setNameClient(user.getFullName());
        response.setAvatar(user.getAvatar());

        return response;
    }

}

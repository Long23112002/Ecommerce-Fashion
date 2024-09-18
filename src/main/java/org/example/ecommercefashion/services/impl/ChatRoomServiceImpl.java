package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
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

    @Override
    public List<ChatRoomResponse> findAllChatRoom() {
        return chatRoomRepository.findAllChatRoom().stream()
                .map(this::toDto)
                .toList();
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
    @Transactional
    public ChatRoomResponse create(ChatRoomRequest request) {
        ChatRoom entity = FnCommon.copyProperties(ChatRoom.class, request);
        defaultCreateValue(entity);
        ChatRoom save = chatRoomRepository.save(entity);
        ChatRoomResponse response = toDto(save);
        return response;
    }

    private void defaultCreateValue(ChatRoom entity) {
        entity.setId(UUID.randomUUID().toString());
        entity.setCreateAt(new Date());
        entity.setDeleted(false);
    }

    private ChatRoomResponse toDto(ChatRoom entity) {
        ChatRoomResponse response = FnCommon.copyProperties(ChatRoomResponse.class, entity);
        User user = userRepository.findById(entity.getIdClient())
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND));
        response.setNameClient(user.getFullName());
        response.setAvatar(user.getAvatar());
        Optional<Chat> optional = chatRepository.findLastChatByIdChatRoom(entity.getId());
        if (optional.isPresent()) {
            Chat chat = optional.get();
            response.setLastChat(chat.getContent());
            response.setSeen(chat.getSeen());
        }
        return response;
    }

}

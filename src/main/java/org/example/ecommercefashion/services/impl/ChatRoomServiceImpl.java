package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ChatRoomRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
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
        ChatRoom chatRoom = chatRoomRepository.findById(id)
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

    private void defaultCreateValue(ChatRoom entity) {
        entity.setId(UUID.randomUUID().toString());
        entity.setCreateAt(new Date());
        entity.setDeleted(false);
    }

    private ChatRoomResponse toDto(ChatRoom entity) {
        ChatRoomResponse response = FnCommon.copyProperties(ChatRoomResponse.class, entity);
        User user = userRepository.findById(entity.getIdClient())
                .filter(ent -> !ent.getDeleted())
                .orElseGet(() -> User.builder()
                        .fullName("Không xác định")
                        .build());
        response.setNameClient(user.getFullName());
        response.setAvatar(user.getAvatar());

        Optional<Chat> optionalChat = findLastChatByIdChatRoom(entity.getId());
        if (optionalChat.isPresent()) {
            Chat chat = optionalChat.get();
            response.setLastChat(chat.getContent());
            response.setSeen(chat.getSeen());
        }
        return response;
    }

}

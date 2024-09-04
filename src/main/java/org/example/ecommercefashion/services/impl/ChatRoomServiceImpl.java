package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ChatRoomRequest;
import org.example.ecommercefashion.dtos.response.ChatRoomResponse;
import org.example.ecommercefashion.entities.ChatRoom;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ChatRoomRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.ChatRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    final ChatRoomRepository chatRoomRepository;
    final UserRepository userRepository;

    @Override
    public String findIdChatRoomByUserId(Long id) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findChatRoomByUserId(id);
        if (chatRoomOptional.isPresent()) {
            return chatRoomOptional.get().getId();
        } else {
            var user = userRepository.findById(id)
                    .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
            var response = create(ChatRoomRequest.builder()
                    .idClient(user.getId())
                    .build());
            return response.getId();
        }
    }

    @Override
    public ChatRoomResponse create(ChatRoomRequest request) {
        ChatRoom entity = FnCommon.copyProperties(ChatRoom.class, request);
        defaultCreateValue(entity);
        ChatRoom save = chatRoomRepository.save(entity);
        ChatRoomResponse response = FnCommon.copyProperties(ChatRoomResponse.class, save);
        return response;
    }

    private void defaultCreateValue(ChatRoom entity) {
        entity.setId(UUID.randomUUID().toString());
        entity.setCreateAt(new Date());
        entity.setDeleted(false);
    }

}

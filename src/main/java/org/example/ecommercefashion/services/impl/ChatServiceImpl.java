package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ChatRequest;
import org.example.ecommercefashion.dtos.response.ChatResponse;
import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ChatRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    final ChatRepository chatRepository;
    final UserRepository userRepository;

    @Override
    public ChatResponse create(ChatRequest request) {
        Chat entity = FnCommon.copyProperties(Chat.class, request);
        defaultCreateValue(entity);
        Chat save = chatRepository.save(entity);
        ChatResponse response = toDto(save);
        return response;
    }

    private void defaultCreateValue(Chat entity) {
        entity.setId(UUID.randomUUID().toString());
        entity.setCreateAt(new Date());
        entity.setDeleted(false);
        entity.setSeen(false);
    }

    private ChatResponse toDto(Chat entity) {
        ChatResponse response = FnCommon.copyProperties(ChatResponse.class, entity);
        User user = userRepository.findById(entity.getCreateBy())
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
        response.setAvatar(user.getAvatar());
        return response;
    }

}

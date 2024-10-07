package org.example.ecommercefashion.config.socket.notification;

import com.longnh.exceptions.ExceptionHandle;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.WebSocketDestination;
import org.example.ecommercefashion.config.socket.chat.RoomSubscriptionService;
import org.example.ecommercefashion.contants.Permissions;
import org.example.ecommercefashion.entities.ChatRoom;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ChatRoomRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements ChannelInterceptor {

    final JwtService jwtService;
    final ChatRoomRepository chatRoomRepository;
    final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor);
        }
        if (StompCommand.SUBSCRIBE.equals(command)) {
            handleSubcribe(accessor);
        }
        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization") + "";
        var user = jwtService.decodeToken(token);
        if (token.length() == 0 || user == null) {
            throw new ExceptionHandle(HttpStatus.FORBIDDEN, ErrorMessage.ACCESS_DENIED);
        }
        Long id = user.getUserId();
        accessor.getSessionAttributes().put("idUser", id);
    }

    private void handleSubcribe(StompHeaderAccessor accessor) {
        String url = accessor.getDestination();
        if (url.startsWith(WebSocketDestination.NOTIFICATION.getDestination())) {

        } else {
            throw new ExceptionHandle(HttpStatus.FORBIDDEN, ErrorMessage.ACCESS_DENIED);
        }
    }

}

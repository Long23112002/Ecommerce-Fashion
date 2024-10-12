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

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final NotificationSubscriptionService subscriptionService;

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
        if (StompCommand.DISCONNECT.equals(command)) {
            handleDisConnect(accessor);
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
            accessor.getSessionAttributes().put("idUserNoti", id);
    }

    private void handleSubcribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination.startsWith(WebSocketDestination.NOTIFICATION.getDestination())) {
            User user = decodeToUser(accessor);
            String id = destination.replace(WebSocketDestination.NOTIFICATION.getDestinationWithSlash(),"");
            if(!id.equals(user.getId().toString())){{
                throw new ExceptionHandle(HttpStatus.FORBIDDEN, ErrorMessage.ACCESS_DENIED);
            }}
            subscriptionService.addUser(user.getId());
            System.out.println(id+"_SUBSCRIBE_TO_NOTIFICATION");
        }
//        else {
//            throw new ExceptionHandle(HttpStatus.FORBIDDEN, ErrorMessage.ACCESS_DENIED);
//        }
    }

    private void handleDisConnect(StompHeaderAccessor accessor) {
        try {
            Optional.ofNullable(accessor.getSessionAttributes().get("idUserNoti"))
                    .ifPresent(object -> {
                        System.out.println(object.toString()+"_DISCONNECT_FROM_CHAT_ROOM");
                        Long idUser = Long.valueOf(object.toString());
                        subscriptionService.removeUser(idUser);
                    });
        } catch (NumberFormatException e) {
            throw new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND);
        }
    }

    private User decodeToUser(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization");
        Long idUser = jwtService.decodeToken(token).getUserId();
        User user =
                userRepository.findById(idUser)
                        .orElseThrow(() ->
                                new ExceptionHandle(HttpStatus.FORBIDDEN, ErrorMessage.USER_NOT_FOUND));
        return user;
    }

}

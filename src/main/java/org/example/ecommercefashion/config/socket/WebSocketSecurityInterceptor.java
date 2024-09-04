package org.example.ecommercefashion.config.socket;


import com.longnh.exceptions.ExceptionHandle;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketSecurityInterceptor implements ChannelInterceptor {

    @Autowired
    JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization") + "";
            if (token.length() == 0 || jwtService.decodeToken(token) == null) {
                throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_DENIED);
            }
        }
//        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
//            String url = accessor.getDestination();
//            if (url.startsWith("/user/")) {
//                verifyUserIdAndToken(accessor);
//            } else if (url.startsWith("/room/")) {
//                isUserInRoom(accessor);
//            } else {
//                throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_DENIED);
//            }
//        }
//        if (StompCommand.SEND.equals(accessor.getCommand())) {
//            isUserInRoom(accessor);
//        }

        return message;
    }

//    private void verifyUserIdAndToken(StompHeaderAccessor accessor) {
//        try {
//            String idUser = accessor.getFirstNativeHeader("idUser");
//            String token = accessor.getFirstNativeHeader("token");
//            User userById = userRepository.findById(Integer.valueOf(idUser)).get();
//            User userByToken = authenticationService.decodeToUser(token);
//            if (!userById.getId().equals(userByToken.getId())) {
//                throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_DENIED);
//            }
//        } catch (Exception e) {
//            throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_DENIED);
//        }
//    }
//
//    private void isUserInRoom(StompHeaderAccessor accessor) {
//        try {
//            String idRoom = accessor.getFirstNativeHeader("idRoom");
//            String token = accessor.getFirstNativeHeader("token");
//            ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(idRoom)).get();
//            User user = authenticationService.decodeToUser(token);
//            if (!chatRoom.getIdUsers().contains(user.getId())) {
//                throw new AppException(ErrorCode.USER_NOT_IN_ROOM);
//            }
//        } catch (Exception e) {
//            throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_DENIED);
//        }
//    }

}
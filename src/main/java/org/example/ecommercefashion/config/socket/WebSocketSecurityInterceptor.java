package org.example.ecommercefashion.config.socket;


import com.longnh.exceptions.ExceptionHandle;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.entities.ChatRoom;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ChatRoomRepository;
import org.example.ecommercefashion.repositories.UserRepository;
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
@RequiredArgsConstructor
public class WebSocketSecurityInterceptor implements ChannelInterceptor {

    final JwtService jwtService;
    final ChatRoomRepository chatRoomRepository;
    final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization") + "";
            if (token.length() == 0 || jwtService.decodeToken(token) == null) {
                throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_DENIED);
            }
        }
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String url = accessor.getDestination();
//            if (url.startsWith("/user/")) {
//                verifyUserIdAndToken(accessor);
//            } else
            if (url.startsWith("/room/")) {
                isUserInRoom(accessor);
            } else {
                throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_DENIED);
            }
        }
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            isUserInRoom(accessor);
        }

        return message;
    }

    //    private void verifyUserIdAndToken(StompHeaderAccessor accessor) {
//        try {
//            String idUser = accessor.getFirstNativeHeader("idUser");
//            String token = accessor.getFirstNativeHeader("Authorization");
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
    private void isUserInRoom(StompHeaderAccessor accessor) {
        try {
            String idRoom = getIdRoomFromDestination(accessor);
            String token = accessor.getFirstNativeHeader("Authorization");
            ChatRoom chatRoom = chatRoomRepository.findById(idRoom)
                    .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CHAT_ROOM_NOT_FOUND));
            Long idUser = jwtService.decodeToken(token).getUserId();
            if (!chatRoom.getIdClient().equals(idUser) && !chatRoom.getIdStaff().equals(idUser)) {
                throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_DENIED);
            }
        } catch (ExceptionHandle e){
            if(e.getMessage().equals("JWT expired")){

            }else{
                throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_DENIED);
            }
        } catch (Exception e) {
            throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.ACCESS_DENIED);
        }
    }

    private String getIdRoomFromDestination(StompHeaderAccessor accessor) {
        String url = accessor.getDestination();
        StringBuilder idRoom = new StringBuilder(url);
        if(url.startsWith("/app/chat.sendMessage/")){
            idRoom.delete(0,"/app/chat.sendMessage/".length());
        }else {
            idRoom.delete(0,"/room/".length());
        }
        return idRoom.toString();
    }

}
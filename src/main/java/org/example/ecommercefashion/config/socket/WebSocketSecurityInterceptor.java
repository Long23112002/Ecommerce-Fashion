package org.example.ecommercefashion.config.socket;

import com.longnh.exceptions.ExceptionHandle;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class WebSocketSecurityInterceptor implements ChannelInterceptor {

  final JwtService jwtService;
  final ChatRoomRepository chatRoomRepository;
  final UserRepository userRepository;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    String urlNotification = accessor.getDestination();
    if (urlNotification != null && urlNotification.startsWith("/notification")) {
      return message;
    }

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = accessor.getFirstNativeHeader("Authorization") + "";
      if (token.length() == 0 || jwtService.decodeToken(token) == null) {
        throw new ExceptionHandle(HttpStatus.FORBIDDEN, ErrorMessage.ACCESS_DENIED);
      }
    }
    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      String url = accessor.getDestination();
      if (url.startsWith("/admin")) {
        isUserHasPermission(accessor);
      } else if (url.startsWith("/room/")) {
        isUserInRoom(accessor);
      } else {
        throw new ExceptionHandle(HttpStatus.FORBIDDEN, ErrorMessage.ACCESS_DENIED);
      }
    }
    if (StompCommand.SEND.equals(accessor.getCommand())) {
      isUserInRoom(accessor);
    }
    return message;
  }

  private void isUserHasPermission(StompHeaderAccessor accessor) {
    User user = decodeToUser(accessor);
    boolean isUserHasPermission =
        userRepository.isUserHasPermission(user.getId(), Permissions.MESSAGE_CONSULT);
    if (!user.getIsAdmin() || !isUserHasPermission) {
      throw new ExceptionHandle(HttpStatus.FORBIDDEN, ErrorMessage.USER_PERMISSION_DENIED);
    }
  }

  private void isUserInRoom(StompHeaderAccessor accessor) {
    User user = decodeToUser(accessor);
    String idRoom = getIdRoomFromDestination(accessor);
    ChatRoom chatRoom =
        chatRoomRepository
            .findById(idRoom)
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CHAT_ROOM_NOT_FOUND));
    if (!chatRoom.getIdClient().equals(user.getId()) && !user.getIsAdmin()) {
      throw new ExceptionHandle(HttpStatus.FORBIDDEN, ErrorMessage.ACCESS_DENIED);
    }
  }

  private User decodeToUser(StompHeaderAccessor accessor) {
    String token = accessor.getFirstNativeHeader("Authorization");
    Long idUser = jwtService.decodeToken(token).getUserId();
    User user =
        userRepository
            .findById(idUser)
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.FORBIDDEN, ErrorMessage.USER_NOT_FOUND));
    return user;
  }

  private String getIdRoomFromDestination(StompHeaderAccessor accessor) {
    String url = accessor.getDestination();
    StringBuilder idRoom = new StringBuilder(url);
    if (url.startsWith("/app/chat.sendMessage/")) {
      idRoom.delete(0, "/app/chat.sendMessage/".length());
    } else {
      idRoom.delete(0, "/room/".length());
    }
    return idRoom.toString();
  }
}

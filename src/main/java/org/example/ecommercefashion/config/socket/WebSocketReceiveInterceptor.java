package org.example.ecommercefashion.config.socket;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.JsonParser;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.entities.Chat;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.ChatRoomService;
import org.example.ecommercefashion.services.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketReceiveInterceptor implements ChannelInterceptor {
//
//    private final UserRepository userRepository;
//    private final ChatService chatService;
//    private final ChatRoomService chatRoomService;
//    private final RoomSubscriptionService subscriptionService;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        if (SimpMessageType.MESSAGE.equals(accessor.getMessageType())
//                && accessor.getDestination() != null
//                && accessor.getDestination().startsWith("/room/")
//        ) {
//            handleReceive(accessor, message);
//        }
//        return message;
//    }
//
//    private void handleReceive(StompHeaderAccessor accessor, Message<?> message) {
////        String idRoom = accessor.getDestination().replaceFirst("/room/", "");
////        Optional<Chat> lastChat = chatRoomService.findLastChatByIdChatRoom(idRoom);
////        if(lastChat.isPresent() && !lastChat.get().getSeen()){
////            Set<Long> users = subscriptionService.getUsersInRoom(idRoom);
////            Chat chat = JsonParser.entity(new String((byte[]) message.getPayload()),Chat.class);
////            for (Long id : users) {
////                User user = userRepository.findById(id)
////                        .orElseThrow(() ->
////                                new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
////                if (user.getIsAdmin() && !chat.getCreateBy().equals(user.getId())) {
////                    chatService.seenAllChatByIdChatRoom(idRoom, user.getId());
////                    break;
////                }
////            }
////        }
//    }
}

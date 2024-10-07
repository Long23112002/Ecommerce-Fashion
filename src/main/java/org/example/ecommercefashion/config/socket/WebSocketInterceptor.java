package org.example.ecommercefashion.config.socket;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.chat.ChatInterceptor;
import org.example.ecommercefashion.config.socket.notification.NotificationInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final ChatInterceptor chatInterceptor;
    private final NotificationInterceptor notificationInterceptor;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String destination = accessor.getDestination();

        if(destination!=null) {
            if (destination.startsWith(WebSocketDestination.CHAT_ROOM.getDestination()) ||
                    destination.startsWith(WebSocketDestination.CHAT_ADMIN.getDestination())) {
                return chatInterceptor.preSend(message, channel);
            }
            else if (destination.startsWith(WebSocketDestination.NOTIFICATION.getDestination())) {
                return notificationInterceptor.preSend(message, channel);
            }
        }

        return message;
    }
}

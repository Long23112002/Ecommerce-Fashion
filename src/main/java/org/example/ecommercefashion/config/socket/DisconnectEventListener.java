package org.example.ecommercefashion.config.socket;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.repositories.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class DisconnectEventListener {

    private final UserRepository userRepository;
    private final RoomSubscriptionManager subscriptionManager;

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");

        if (userId != null) {
            subscriptionManager.removeUserFromAllRooms(userId);
        }
    }
}
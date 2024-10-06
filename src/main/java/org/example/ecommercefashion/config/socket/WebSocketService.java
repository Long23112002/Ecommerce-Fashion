package org.example.ecommercefashion.config.socket;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(@Lazy SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public <T> void responseRealtime(String url, T responses) {
        try {
            messagingTemplate.convertAndSend(url, responses);
        } catch (MessageDeliveryException e) {
        }
    }
}
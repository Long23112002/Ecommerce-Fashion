package org.example.ecommercefashion.config.socket;


import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.config.socket.chat.ChatInterceptor;
import org.example.ecommercefashion.config.socket.notification.NotificationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatInterceptor chatInterceptor;
    private final NotificationInterceptor notificationInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOrigins(
                        "http://localhost:5173",
                        "http://ecommerce-fashion.site"
                )
                .withSockJS();

        registry.addEndpoint("/ws/notification")
                .setAllowedOrigins(
                        "http://localhost:5173",
                        "http://ecommerce-fashion.site"
                )
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker(
                WebSocketDestination.CHAT_ROOM.getDestination(),
                WebSocketDestination.CHAT_ADMIN.getDestination(),
                WebSocketDestination.NOTIFICATION.getDestination()
        );
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatInterceptor, notificationInterceptor);
    }

}
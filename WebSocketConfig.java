package com.sheoran.chatapplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // changed endpoint to avoid conflict with controller mapping
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // simple broker for broadcast
        registry.enableSimpleBroker("/topic", "/queue");
        // prefix for messages sent from client to @MessageMapping handlers
        registry.setApplicationDestinationPrefixes("/app");
        // enable user destination prefix for private messages (convertAndSendToUser)
        registry.setUserDestinationPrefix("/user");
    }
}

package com.manish.chat.chat_app_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer
{
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")          // this url for connection establishment
                .setAllowedOrigins("http://localhost:5173")
                .withSockJS();
        // /chat endpoint par connection apka establish hoga
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        // /topic/messages => show all messages

        config.setApplicationDestinationPrefixes("/app");
        // /app/chat => if client want to send message then use "/app/chat" and then message send to server
        // server-side : @MessaginMapping("/chat")
    }
}

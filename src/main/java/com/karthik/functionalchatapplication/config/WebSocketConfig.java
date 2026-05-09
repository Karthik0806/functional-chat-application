package com.karthik.functionalchatapplication.config;

import com.karthik.functionalchatapplication.websocket.JwtHandShakeInterceptor;
import com.karthik.functionalchatapplication.websocket.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketAuthInterceptor authInterceptor;
    private final JwtHandShakeInterceptor jwtHandShakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(jwtHandShakeInterceptor)
                .setAllowedOrigins(
                        "https://functional-chat-applicati-git-9d8487-karthiks-projects-f9f59917.vercel.app",
                        "https://chat.karthiknarravula.dev",
                        "https://functional-chat-application-frontend-offe-hiy7pg8bk.vercel.app",
                        "http://localhost:3000"

        ).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic","/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
   }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
registration.interceptors(authInterceptor);    }
}

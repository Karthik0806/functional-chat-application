package com.karthik.functionalchatapplication.websocket;

import com.karthik.functionalchatapplication.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketAuthInterceptorTest {

    @Mock JwtService jwtService;
    @Mock MessageChannel channel;
    @InjectMocks WebSocketAuthInterceptor interceptor;

    // -------------------------------------------------------- CONNECT frames
    @Nested
    @DisplayName("CONNECT frame")
    class ConnectFrame {

        @Test
        @DisplayName("allows connection and sets Principal for valid token")
        void validToken() {
            when(jwtService.isValid("good-token")).thenReturn(true);
            when(jwtService.extractUsername("good-token")).thenReturn("karthik");

            Message<?> msg = connectMessage("good-token");
            Message<?> result = interceptor.preSend(msg, channel);

            assertThat(result).isNotNull();
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(result);
            assertThat(accessor.getUser()).isNotNull();
            assertThat(accessor.getUser().getName()).isEqualTo("karthik");
        }

        @Test
        @DisplayName("throws MessageDeliveryException for invalid token")
        void invalidToken() {
            when(jwtService.isValid("bad-token")).thenReturn(false);
            assertThatThrownBy(() -> interceptor.preSend(connectMessage("bad-token"), channel))
                    .isInstanceOf(MessageDeliveryException.class);
        }

        @Test
        @DisplayName("throws MessageDeliveryException when token is missing from session attributes")
        void missingToken() {
            Message<?> msg = connectMessage(null);
            assertThatThrownBy(() -> interceptor.preSend(msg, channel))
                    .isInstanceOf(MessageDeliveryException.class);
        }
    }

    // -------------------------------------------------------- Non-CONNECT frames
    @Nested
    @DisplayName("Non-CONNECT frames (SEND, SUBSCRIBE, etc.)")
    class NonConnectFrames {

        @Test
        @DisplayName("passes SEND frame through without validation")
        void sendFrame() {
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
            accessor.setDestination("/app/private-message");
            Message<?> msg = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

            Message<?> result = interceptor.preSend(msg, channel);

            assertThat(result).isNotNull();
            verifyNoInteractions(jwtService);
        }

        @Test
        @DisplayName("passes SUBSCRIBE frame through without validation")
        void subscribeFrame() {
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
            accessor.setDestination("/user/queue/messages");
            Message<?> msg = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

            assertThat(interceptor.preSend(msg, channel)).isNotNull();
            verifyNoInteractions(jwtService);
        }
    }

    // --------------------------------------------------------------- helpers
    private Message<?> connectMessage(String token) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);

        accessor.setLeaveMutable(true);

        Map<String, Object> sessionAttributes = new HashMap<>();
        if (token != null) {
            sessionAttributes.put("token", token);
        }

        accessor.setSessionAttributes(sessionAttributes);

        return MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );
    }
}

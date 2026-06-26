package com.karthik.functionalchatapplication.websocket;

import com.karthik.functionalchatapplication.service.OnlineUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketEventListenerTest {

    @Mock OnlineUserService onlineUserService;
    @InjectMocks WebSocketEventListener listener;

    @Test
    @DisplayName("handleWebSocketConnectListener adds user to online set")
    void connectAddsUser() {
        listener.handleWebSocketConnectListener(connectedEvent("karthik"));
        verify(onlineUserService).addUser("karthik");
    }

    @Test
    @DisplayName("handleWebSocketConnectListener does nothing when Principal is null")
    void connectNullPrincipal() {
        listener.handleWebSocketConnectListener(connectedEventNoPrincipal());
        verifyNoInteractions(onlineUserService);
    }

    @Test
    @DisplayName("handleWebSocketDisconnectListener removes user from online set")
    void disconnectRemovesUser() {
        listener.handleWebSocketDisconnectListener(disconnectEvent("karthik"));
        verify(onlineUserService).removeUser("karthik");
    }

    @Test
    @DisplayName("handleWebSocketDisconnectListener does nothing when Principal is null")
    void disconnectNullPrincipal() {
        listener.handleWebSocketDisconnectListener(disconnectEventNoPrincipal());
        verifyNoInteractions(onlineUserService);
    }

    // --------------------------------------------------------------- helpers
    private SessionConnectedEvent connectedEvent(String username) {
        var auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECTED);
        accessor.setUser(auth);
        Message<byte[]> msg = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        return new SessionConnectedEvent(this, msg);
    }

    private SessionConnectedEvent connectedEventNoPrincipal() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECTED);
        Message<byte[]> msg = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        return new SessionConnectedEvent(this, msg);
    }

    private SessionDisconnectEvent disconnectEvent(String username) {
        var auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        accessor.setUser(auth);
        Message<byte[]> msg = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        return new SessionDisconnectEvent(this, msg, "session-1", null);
    }

    private SessionDisconnectEvent disconnectEventNoPrincipal() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        Message<byte[]> msg = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        return new SessionDisconnectEvent(this, msg, "session-2", null);
    }
}

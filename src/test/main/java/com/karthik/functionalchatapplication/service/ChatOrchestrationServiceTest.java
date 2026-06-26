package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.Enumeration.AuthProvider;
import com.karthik.functionalchatapplication.Enumeration.MessageStatus;
import com.karthik.functionalchatapplication.dto.ChatMessage;
import com.karthik.functionalchatapplication.entity.Message;
import com.karthik.functionalchatapplication.entity.User;
import com.karthik.functionalchatapplication.exception.ResourceNotFoundException;
import com.karthik.functionalchatapplication.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatOrchestrationServiceTest {

    @Mock ChatService chatService;
    @Mock ConversationService conversationService;
    @Mock OnlineUserService onlineUserService;
    @Mock SimpMessagingTemplate messagingTemplate;
    @Mock UserRepo userRepo;

    @InjectMocks ChatOrchestrationService orchestrationService;

    private Message sentMessage;

    @BeforeEach
    void setUp() {
        sentMessage = Message.builder().id(1L).sender("alice").receiver("bob")
                .content("Hello").timestamp(Instant.now()).status(MessageStatus.SENT).build();
    }

    @Nested
    @DisplayName("processPrivateMessage()")
    class ProcessPrivateMessage {

        @Test
        @DisplayName("sends message and creates/updates conversation when receiver is offline")
        void offlineReceiver() {
            var dto = chatMsg("bob", "Hello");
            when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bobUser()));
            when(chatService.save("alice", dto)).thenReturn(sentMessage);
            when(onlineUserService.isOnline("bob")).thenReturn(false);

            orchestrationService.processPrivateMessage("alice", dto);

            verify(chatService).save("alice", dto);
            verify(conversationService).createOrUpdateConversation("alice", "bob", "Hello");
            verify(chatService, never()).updateStatus(any(), eq(MessageStatus.DELIVERED));
            verify(messagingTemplate).convertAndSendToUser(eq("bob"), eq("/queue/messages"), eq(sentMessage));
        }

        @Test
        @DisplayName("marks message as DELIVERED when receiver is online")
        void onlineReceiver() {
            var dto = chatMsg("bob", "Hello");
            var deliveredMsg = Message.builder().id(1L).status(MessageStatus.DELIVERED).build();
            when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bobUser()));
            when(chatService.save("alice", dto)).thenReturn(sentMessage);
            when(onlineUserService.isOnline("bob")).thenReturn(true);
            when(chatService.updateStatus(sentMessage, MessageStatus.DELIVERED)).thenReturn(deliveredMsg);

            orchestrationService.processPrivateMessage("alice", dto);

            verify(chatService).updateStatus(sentMessage, MessageStatus.DELIVERED);
            verify(messagingTemplate).convertAndSendToUser(eq("bob"), eq("/queue/messages"), eq(deliveredMsg));
        }

        @Test
        @DisplayName("throws IllegalArgumentException when sender messages themselves")
        void selfMessage() {
            var dto = chatMsg("alice", "hi me");
            assertThatThrownBy(() -> orchestrationService.processPrivateMessage("alice", dto))
                    .isInstanceOf(IllegalArgumentException.class);
            verifyNoInteractions(chatService, conversationService, messagingTemplate);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when receiver does not exist")
        void receiverNotFound() {
            var dto = chatMsg("ghost", "hey");
            when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> orchestrationService.processPrivateMessage("alice", dto))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(chatService, never()).save(any(), any());
        }
    }

    private ChatMessage chatMsg(String receiver, String content) {
        return ChatMessage.builder().receiver(receiver).content(content).build();
    }

    private User bobUser() {
        return User.builder().id(2L).username("bob").password("enc").provider(AuthProvider.LOCAL).build();
    }
}

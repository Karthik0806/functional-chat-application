package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.Enumeration.MessageStatus;
import com.karthik.functionalchatapplication.dto.ChatMessage;
import com.karthik.functionalchatapplication.entity.Message;
import com.karthik.functionalchatapplication.repo.MessageRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock MessageRepo messageRepo;
    @InjectMocks ChatService chatService;

    // --------------------------------------------------------------- save()
    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("persists message with SENT status and correct fields")
        void savesWithCorrectFields() {
            var dto = chatMsg("receiver1", "Hello!");
            var saved = savedMessage(1L, "sender1", "receiver1", "Hello!", MessageStatus.SENT);
            when(messageRepo.save(any(Message.class))).thenReturn(saved);

            Message result = chatService.save("sender1", dto);

            ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
            verify(messageRepo).save(captor.capture());
            Message persisted = captor.getValue();

            assertThat(persisted.getSender()).isEqualTo("sender1");
            assertThat(persisted.getReceiver()).isEqualTo("receiver1");
            assertThat(persisted.getContent()).isEqualTo("Hello!");
            assertThat(persisted.getStatus()).isEqualTo(MessageStatus.SENT);
            assertThat(persisted.getTimestamp()).isNotNull();
            assertThat(result).isEqualTo(saved);
        }

        @Test
        @DisplayName("timestamp is close to now")
        void timestampIsRecent() {
            var dto = chatMsg("bob", "Hi");
            var before = Instant.now();
            when(messageRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Message result = chatService.save("alice", dto);

            assertThat(result.getTimestamp()).isBetween(before, Instant.now().plusSeconds(1));
        }
    }

    // ---------------------------------------------------------- updateStatus()
    @Nested
    @DisplayName("updateStatus()")
    class UpdateStatus {

        @Test
        @DisplayName("updates and saves with new status")
        void updatesStatus() {
            var msg = savedMessage(10L, "a", "b", "text", MessageStatus.SENT);
            when(messageRepo.save(msg)).thenReturn(msg);

            Message result = chatService.updateStatus(msg, MessageStatus.DELIVERED);

            assertThat(result.getStatus()).isEqualTo(MessageStatus.DELIVERED);
            verify(messageRepo).save(msg);
        }
    }

    // -------------------------------------------------------- getChatMessages()
    @Nested
    @DisplayName("getChatMessages()")
    class GetChatMessages {

        @Test
        @DisplayName("returns messages from repo ordered by timestamp")
        void returnsList() {
            var messages = List.of(
                    savedMessage(1L, "alice", "bob", "Hi", MessageStatus.READ),
                    savedMessage(2L, "bob", "alice", "Hey", MessageStatus.DELIVERED)
            );
            when(messageRepo.findConversationMessages("alice", "bob")).thenReturn(messages);

            List<Message> result = chatService.getChatMessages("alice", "bob");

            assertThat(result).hasSize(2).isEqualTo(messages);
        }

        @Test
        @DisplayName("returns empty list when no messages exist")
        void emptyConversation() {
            when(messageRepo.findConversationMessages(any(), any())).thenReturn(List.of());
            assertThat(chatService.getChatMessages("x", "y")).isEmpty();
        }
    }

    // -------------------------------------------------------- markMessagesAsRead()
    @Nested
    @DisplayName("markMessagesAsRead()")
    class MarkMessagesAsRead {

        @Test
        @DisplayName("flips all DELIVERED messages to READ and saves them")
        void marksAllDeliveredAsRead() {
            var m1 = savedMessage(1L, "alice", "bob", "msg1", MessageStatus.DELIVERED);
            var m2 = savedMessage(2L, "alice", "bob", "msg2", MessageStatus.DELIVERED);
            when(messageRepo.findBySenderAndReceiverAndStatus("alice", "bob", MessageStatus.DELIVERED))
                    .thenReturn(List.of(m1, m2));

            chatService.markMessagesAsRead("alice", "bob");

            assertThat(m1.getStatus()).isEqualTo(MessageStatus.READ);
            assertThat(m2.getStatus()).isEqualTo(MessageStatus.READ);
            verify(messageRepo).saveAll(List.of(m1, m2));
        }

        @Test
        @DisplayName("does nothing when no DELIVERED messages exist")
        void noDeliveredMessages() {
            when(messageRepo.findBySenderAndReceiverAndStatus(any(), any(), eq(MessageStatus.DELIVERED)))
                    .thenReturn(List.of());
            chatService.markMessagesAsRead("alice", "bob");
            verify(messageRepo).saveAll(List.of());
        }
    }

    // --------------------------------------------------------------- helpers
    private ChatMessage chatMsg(String receiver, String content) {
        return ChatMessage.builder().receiver(receiver).content(content).build();
    }

    private Message savedMessage(Long id, String sender, String receiver, String content, MessageStatus status) {
        return Message.builder().id(id).sender(sender).receiver(receiver)
                .content(content).timestamp(Instant.now()).status(status).build();
    }
}
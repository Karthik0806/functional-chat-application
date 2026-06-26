package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.dto.ConversationResponse;
import com.karthik.functionalchatapplication.entity.Conversation;
import com.karthik.functionalchatapplication.repo.ConversationRepo;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock ConversationRepo conversationRepo;
    @InjectMocks ConversationService conversationService;

    // ----------------------------------------- createOrUpdateConversation()
    @Nested
    @DisplayName("createOrUpdateConversation()")
    class CreateOrUpdate {

        @Test
        @DisplayName("creates new conversation when none exists — user1/user2 sorted lexicographically")
        void createsNewConversation() {
            // "alice" < "bob" lexicographically → user1=alice, user2=bob
            when(conversationRepo.findByUser1AndUser2("alice", "bob")).thenReturn(Optional.empty());
            when(conversationRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            conversationService.createOrUpdateConversation("alice", "bob", "hey");

            ArgumentCaptor<Conversation> cap = ArgumentCaptor.forClass(Conversation.class);
            verify(conversationRepo).save(cap.capture());
            Conversation saved = cap.getValue();

            assertThat(saved.getUser1()).isEqualTo("alice");
            assertThat(saved.getUser2()).isEqualTo("bob");
            assertThat(saved.getLastMessage()).isEqualTo("hey");
            // alice sends, bob receives → unreadCountUser2 = 1
            assertThat(saved.getUnreadCountUser2()).isEqualTo(1);
            assertThat(saved.getUnreadCountUser1()).isEqualTo(0);
        }

        @Test
        @DisplayName("increments unread count for the receiver on existing conversation")
        void incrementsUnreadOnExisting() {
            var existing = conversation("alice", "bob", 0, 2);
            when(conversationRepo.findByUser1AndUser2("alice", "bob")).thenReturn(Optional.of(existing));
            when(conversationRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // alice sends to bob → bob is user2 → unreadCountUser2 should become 3
            conversationService.createOrUpdateConversation("alice", "bob", "another msg");

            ArgumentCaptor<Conversation> cap = ArgumentCaptor.forClass(Conversation.class);
            verify(conversationRepo).save(cap.capture());
            assertThat(cap.getValue().getUnreadCountUser2()).isEqualTo(3);
            assertThat(cap.getValue().getLastMessage()).isEqualTo("another msg");
        }

        @Test
        @DisplayName("updates lastMessage and lastMessageTime on existing")
        void updatesLastMessage() {
            var existing = conversation("alice", "bob", 0, 0);
            when(conversationRepo.findByUser1AndUser2("alice", "bob")).thenReturn(Optional.of(existing));
            when(conversationRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var before = Instant.now();
            conversationService.createOrUpdateConversation("bob", "alice", "updated");
            var after = Instant.now().plusSeconds(1);

            ArgumentCaptor<Conversation> cap = ArgumentCaptor.forClass(Conversation.class);
            verify(conversationRepo).save(cap.capture());
            assertThat(cap.getValue().getLastMessage()).isEqualTo("updated");
            assertThat(cap.getValue().getLastMessageTime()).isBetween(before, after);
        }
    }

    // ----------------------------------------------- markConversationAsRead()
    @Nested
    @DisplayName("markConversationAsRead()")
    class MarkAsRead {

        @Test
        @DisplayName("zeroes unread count for user1 when user1 reads")
        void zeroesUnreadForUser1() {
            // sender=bob reads → receiver=alice who is user1
            var conv = conversation("alice", "bob", 5, 0);
            when(conversationRepo.findByUser1AndUser2("alice", "bob")).thenReturn(Optional.of(conv));

            conversationService.markConversationAsRead("bob", "alice");

            ArgumentCaptor<Conversation> cap = ArgumentCaptor.forClass(Conversation.class);
            verify(conversationRepo).save(cap.capture());
            assertThat(cap.getValue().getUnreadCountUser1()).isEqualTo(0);
        }

        @Test
        @DisplayName("zeroes unread count for user2 when user2 reads")
        void zeroesUnreadForUser2() {
            var conv = conversation("alice", "bob", 0, 7);
            when(conversationRepo.findByUser1AndUser2("alice", "bob")).thenReturn(Optional.of(conv));

            conversationService.markConversationAsRead("alice", "bob");

            ArgumentCaptor<Conversation> cap = ArgumentCaptor.forClass(Conversation.class);
            verify(conversationRepo).save(cap.capture());
            assertThat(cap.getValue().getUnreadCountUser2()).isEqualTo(0);
        }

        @Test
        @DisplayName("does nothing when conversation does not exist")
        void noopWhenMissing() {
            when(conversationRepo.findByUser1AndUser2(any(), any())).thenReturn(Optional.empty());
            conversationService.markConversationAsRead("x", "y");
            verify(conversationRepo, never()).save(any());
        }
    }

    // ----------------------------------------------- getUserConversations()
    @Nested
    @DisplayName("getUserConversations()")
    class GetUserConversations {

        @Test
        @DisplayName("maps to ConversationResponse with correct otherUser and unreadCount for user1")
        void mappingForUser1() {
            var conv = conversation("alice", "bob", 3, 0);
            conv.setLastMessage("sup");
            when(conversationRepo.findByUser1OrUser2OrderByLastMessageTimeDesc("alice", "alice"))
                    .thenReturn(List.of(conv));

            List<ConversationResponse> result = conversationService.getUserConversations("alice");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOtherUser()).isEqualTo("bob");
            assertThat(result.get(0).getUnreadCount()).isEqualTo(3);
            assertThat(result.get(0).getLastMessage()).isEqualTo("sup");
        }

        @Test
        @DisplayName("maps to ConversationResponse with correct otherUser and unreadCount for user2")
        void mappingForUser2() {
            var conv = conversation("alice", "bob", 0, 5);
            conv.setLastMessage("yo");
            when(conversationRepo.findByUser1OrUser2OrderByLastMessageTimeDesc("bob", "bob"))
                    .thenReturn(List.of(conv));

            List<ConversationResponse> result = conversationService.getUserConversations("bob");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOtherUser()).isEqualTo("alice");
            assertThat(result.get(0).getUnreadCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("returns empty list when user has no conversations")
        void emptyList() {
            when(conversationRepo.findByUser1OrUser2OrderByLastMessageTimeDesc(any(), any()))
                    .thenReturn(List.of());
            assertThat(conversationService.getUserConversations("nobody")).isEmpty();
        }
    }

    // --------------------------------------------------------------- helpers
    private Conversation conversation(String user1, String user2, int unread1, int unread2) {
        return Conversation.builder()
                .user1(user1).user2(user2)
                .lastMessage("msg").lastMessageTime(Instant.now())
                .unreadCountUser1(unread1).unreadCountUser2(unread2)
                .build();
    }
}

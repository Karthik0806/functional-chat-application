package com.karthik.functionalchatapplication.controller;

import com.karthik.functionalchatapplication.Enumeration.MessageStatus;
import com.karthik.functionalchatapplication.dto.ConversationResponse;
import com.karthik.functionalchatapplication.entity.Message;
import com.karthik.functionalchatapplication.security.JwtFilter;
import com.karthik.functionalchatapplication.service.ChatService;
import com.karthik.functionalchatapplication.service.ConversationService;
import com.karthik.functionalchatapplication.service.OnlineUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = {MessageController.class, ConversationController.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
class MessageControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean ChatService chatService;
    @MockitoBean OnlineUserService onlineUserService;
    @MockitoBean
    ConversationService conversationService;


    @Test
    @DisplayName("GET /messages/history returns 200 with message list")
    @WithMockUser(username = "karthik")
    void getHistory() throws Exception {
        var msg = Message.builder()
                .id(1L)
                .sender("karthik")
                .receiver("bob")
                .content("hey")
                .timestamp(Instant.now())
                .status(MessageStatus.DELIVERED)
                .build();

        when(chatService.getChatMessages("karthik", "bob")).thenReturn(List.of(msg));

        mockMvc.perform(get("/messages/history").param("receiver", "bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("hey"))
                .andExpect(jsonPath("$[0].sender").value("karthik"))
                .andExpect(jsonPath("$[0].receiver").value("bob"));
    }

    @Test
    @DisplayName("GET /messages/history returns empty list when no messages")
    @WithMockUser(username = "karthik")
    void getHistoryEmpty() throws Exception {
        when(chatService.getChatMessages("karthik", "bob")).thenReturn(List.of());

        mockMvc.perform(get("/messages/history").param("receiver", "bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /messages/history returns 401 when not authenticated")
    void historyRequiresAuth() throws Exception {
        mockMvc.perform(get("/messages/history").param("receiver", "bob"))
                .andExpect(status().isFound());
    }


    @Test
    @DisplayName("GET /messages/online-users returns set of online usernames")
    @WithMockUser
    void onlineUsers() throws Exception {
        when(onlineUserService.getOnlineUsers()).thenReturn(Set.of("alice", "bob"));

        mockMvc.perform(get("/messages/online-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /messages/online-users returns empty set when no one is online")
    @WithMockUser
    void onlineUsersEmpty() throws Exception {
        when(onlineUserService.getOnlineUsers()).thenReturn(Set.of());

        mockMvc.perform(get("/messages/online-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @DisplayName("GET /conversations returns 200 with list of conversations")
    @WithMockUser(username = "karthik")
    @Test
    void getConversations() throws Exception {
        var conv = ConversationResponse.builder()
                .otherUser("bob")
                .lastMessage("sup")
                .lastMessageTime(Instant.now())
                .unreadCount(2)
                .build();

        when(conversationService.getUserConversations("karthik")).thenReturn(List.of(conv));

        mockMvc.perform(get("/conversations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].otherUser").value("bob"))
                .andExpect(jsonPath("$[0].lastMessage").value("sup"))
                .andExpect(jsonPath("$[0].unreadCount").value(2));
    }

    @Test
    @DisplayName("GET /conversations returns empty list when none exist")
    @WithMockUser(username = "karthik")
    void getConversationsEmpty() throws Exception {
        when(conversationService.getUserConversations("karthik")).thenReturn(List.of());

        mockMvc.perform(get("/conversations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /conversations returns 401 when not authenticated")
    void conversationsRequiresAuth() throws Exception {
        mockMvc.perform(get("/conversations"))
                .andExpect(status().isFound());
    }
}
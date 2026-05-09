package com.karthik.functionalchatapplication.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@Builder
public class ConversationResponse {
    private String otherUser;
    private String lastMessage;
    private Instant lastMessageTime;
    private Integer unreadCount;
}

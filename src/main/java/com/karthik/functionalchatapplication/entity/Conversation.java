package com.karthik.functionalchatapplication.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String user1;
    private String user2;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCountUser1;
    private Integer unreadCountUser2;
}

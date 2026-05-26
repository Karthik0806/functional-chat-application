package com.karthik.functionalchatapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Table(indexes = {
                @Index(name = "idx_user1_user2", columnList = "user1,user2"),
                @Index(name = "idx_last_message_time", columnList = "lastMessageTime")
        })
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
    private Instant lastMessageTime;
    private Integer unreadCountUser1;
    private Integer unreadCountUser2;
}

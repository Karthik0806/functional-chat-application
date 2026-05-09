package com.karthik.functionalchatapplication.entity;

import com.karthik.functionalchatapplication.Enumeration.MessageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;



@Entity

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String receiver;
    private String content;
    private Instant timestamp;
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
}

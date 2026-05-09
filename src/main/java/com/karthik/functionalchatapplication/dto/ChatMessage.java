package com.karthik.functionalchatapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    @NotBlank(message = "Receiver required")
    private String receiver;

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 1000,message = "Message too long")
    private String content;
}
package com.karthik.functionalchatapplication.controller;

import com.karthik.functionalchatapplication.dto.ChatMessage;
import com.karthik.functionalchatapplication.dto.ReadReceipt;
import com.karthik.functionalchatapplication.service.ChatOrchestrationService;
import com.karthik.functionalchatapplication.service.ChatService;
import com.karthik.functionalchatapplication.service.ConversationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Tag(name = "WebSocket Chat APIs")

public class ChatController {
    private final ChatOrchestrationService orchestrationService;
    private final ChatService chatService;
    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/private-message")
    public void sendPrivateMessage(@Valid ChatMessage chatMessage, Principal principal) {
        orchestrationService.processPrivateMessage(principal.getName(), chatMessage);
    }

    @MessageMapping("/read-messages")
    public void markMessageAsRead(ReadReceipt readReceipt) {

        chatService.markMessagesAsRead(readReceipt.getSender(), readReceipt.getReceiver());
        conversationService.markConversationAsRead(readReceipt.getSender(), readReceipt.getReceiver());
        messagingTemplate.convertAndSendToUser(readReceipt.getSender(), "/queue/read-receipts", readReceipt);
    }
}
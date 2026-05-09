package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.Enumeration.MessageStatus;
import com.karthik.functionalchatapplication.dto.ChatMessage;
import com.karthik.functionalchatapplication.entity.Message;
import com.karthik.functionalchatapplication.exception.ResourceNotFoundException;
import com.karthik.functionalchatapplication.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatOrchestrationService {
    private final ChatService chatService;
    private final ConversationService conversationService;
    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepo userRepo;

    @Transactional
    public void processPrivateMessage(String sender, ChatMessage chatMessage) {

        log.info("Processing message from {} to {}", sender, chatMessage.getReceiver());
        if (sender.equals(chatMessage.getReceiver())) {
            throw new IllegalArgumentException("You cannot message yourself");
        }

        boolean receiverExists = userRepo.findByUsername(chatMessage.getReceiver()).isPresent();
        if (!receiverExists) {
            throw new ResourceNotFoundException("Receiver does not exist");
        }

        Message savedMessage = chatService.save(sender, chatMessage);
        conversationService.createOrUpdateConversation(sender, chatMessage.getReceiver(), chatMessage.getContent());

        if (onlineUserService.isOnline(chatMessage.getReceiver())) {
            savedMessage = chatService.updateStatus(savedMessage, MessageStatus.DELIVERED);
            log.info("Message delivered to {}", chatMessage.getReceiver());
        }

        messagingTemplate.convertAndSendToUser(chatMessage.getReceiver(), "/queue/messages", savedMessage);

        log.info("Realtime message sent to {}", chatMessage.getReceiver());
    }
}
package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.Enumeration.MessageStatus;
import com.karthik.functionalchatapplication.dto.ChatMessage;
import com.karthik.functionalchatapplication.entity.Message;
import com.karthik.functionalchatapplication.repo.MessageRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepo repo;

    public Message save(String sender, ChatMessage chatMessage) {
        Message message = Message.builder()
                .sender(sender)
                .receiver(chatMessage.getReceiver())
                .content(chatMessage.getContent())
                .timestamp(Instant.now())
                .status(MessageStatus.SENT)
                .build();

        log.info("Message saved from {} to {}", sender, chatMessage.getReceiver());
        return repo.save(message);
    }

    public Message updateStatus(Message message, MessageStatus status) {
        message.setStatus(status);
        return repo.save(message);
    }

    public List<Message> getChatMessages(String sender, String receiver) {
        return repo.findConversationMessages(sender, receiver);
    }

    @Transactional
    public void markMessagesAsRead(String sender, String receiver) {
        List<Message> messages = repo.findBySenderAndReceiverAndStatus(sender, receiver, MessageStatus.DELIVERED);
        messages.forEach(message -> message.setStatus(MessageStatus.READ));
        repo.saveAll(messages);
    }
}
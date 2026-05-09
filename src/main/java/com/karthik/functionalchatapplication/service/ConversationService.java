package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.dto.ConversationResponse;
import com.karthik.functionalchatapplication.entity.Conversation;
import com.karthik.functionalchatapplication.repo.ConversationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepo conversationRepo;

    public Conversation createOrUpdateConversation(String sender, String receiver, String messageContent) {

        String user1 = sender.compareTo(receiver) < 0 ? sender : receiver;
        String user2 = sender.compareTo(receiver) < 0 ? receiver : sender;
        Optional<Conversation> existingConversation = conversationRepo.findByUser1AndUser2(user1, user2);

        Conversation conversation;

        if(existingConversation.isPresent()){
            conversation = existingConversation.get();
            conversation.setLastMessage(messageContent);
            conversation.setLastMessageTime(LocalDateTime.now());

            if(receiver.equals(conversation.getUser1())){
                conversation.setUnreadCountUser1(conversation.getUnreadCountUser1() + 1);

            }else{conversation.setUnreadCountUser2(
                    conversation.getUnreadCountUser2() + 1);
            }

        }else{
            conversation = Conversation.builder()
                            .user1(user1)
                            .user2(user2)
                            .lastMessage(messageContent)
                            .lastMessageTime(LocalDateTime.now())
                            .unreadCountUser1(receiver.equals(user1) ? 1 : 0)
                            .unreadCountUser2(receiver.equals(user2) ? 1 : 0)
                            .build();
        }

        return conversationRepo.save(conversation);
    }

    public void markConversationAsRead(String sender, String receiver) {

        String user1 = sender.compareTo(receiver) < 0 ? sender : receiver;
        String user2 = sender.compareTo(receiver) < 0 ? receiver : sender;
        Optional<Conversation> optionalConversation = conversationRepo.findByUser1AndUser2(user1, user2);

        if(optionalConversation.isPresent()){
            Conversation conversation = optionalConversation.get();

            if(receiver.equals(conversation.getUser1())){
                conversation.setUnreadCountUser1(0);
            }else{
                conversation.setUnreadCountUser2(0);
            }
            conversationRepo.save(conversation);
        }
    }

    public List<ConversationResponse> getUserConversations(String currentUser) {

        List<Conversation> conversations = conversationRepo.findByUser1OrUser2OrderByLastMessageTimeDesc(currentUser, currentUser);
        return conversations.stream().map(conversation -> {
            boolean isUser1 = conversation.getUser1().equals(currentUser);
            String otherUser = isUser1 ? conversation.getUser2() : conversation.getUser1();
            Integer unreadCount = isUser1 ? conversation.getUnreadCountUser1() : conversation.getUnreadCountUser2();

                    return ConversationResponse.builder()
                            .otherUser(otherUser)
                            .lastMessage(conversation.getLastMessage())
                            .lastMessageTime(conversation.getLastMessageTime())
                            .unreadCount(unreadCount)
                            .build();
                }).toList();
    }
}
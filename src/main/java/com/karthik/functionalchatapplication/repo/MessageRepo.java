package com.karthik.functionalchatapplication.repo;

import com.karthik.functionalchatapplication.Enumeration.MessageStatus;
import com.karthik.functionalchatapplication.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo
        extends JpaRepository<Message, Long> {

    @Query("""
            SELECT m FROM Message m
            WHERE
            (m.sender = :sender AND m.receiver = :receiver)
            OR
            (m.sender = :receiver AND m.receiver = :sender)
            ORDER BY m.timestamp ASC
            """)
    List<Message> findConversationMessages(
            @Param("sender") String sender,
            @Param("receiver") String receiver
    );

    List<Message> findBySenderAndReceiverAndStatus(
            String sender,
            String receiver,
            MessageStatus status
    );
}
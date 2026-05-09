package com.karthik.functionalchatapplication.repo;

import com.karthik.functionalchatapplication.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepo
        extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByUser1AndUser2(
            String user1,
            String user2
    );

    List<Conversation>
    findByUser1OrUser2OrderByLastMessageTimeDesc(
            String user1,
            String user2
    );
}
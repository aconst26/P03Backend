package com.example.hoodDeals.repositories;

import com.example.hoodDeals.entities.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<Messages, Long> {

    // All messages in a conversation, ordered oldest → newest
    Page<Messages> findByConversationIdOrderByCreatedAtAsc(Long conversationId, Pageable pageable);
}

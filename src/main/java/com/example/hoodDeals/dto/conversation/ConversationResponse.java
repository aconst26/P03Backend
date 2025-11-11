package com.example.hoodDeals.dto.conversation;
import java.time.LocalDateTime;

public record ConversationResponse(
        Long id,
        Long user1Id,
        Long user2Id,
        Long listingId,
        LocalDateTime lastMessageAt,
        LocalDateTime createdAt
) {
    
}

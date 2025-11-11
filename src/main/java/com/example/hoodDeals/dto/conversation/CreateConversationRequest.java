package com.example.hoodDeals.dto.conversation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateConversationRequest(
           @NotNull @Positive Long user1Id,
        @NotNull @Positive Long user2Id,
        @NotNull @Positive Long listingId) {

}

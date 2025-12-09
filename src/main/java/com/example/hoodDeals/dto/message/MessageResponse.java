package com.example.hoodDeals.dto.message;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long conversationId,
        Long senderId,
        Long receiverId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime readAt,
        String name
) {}
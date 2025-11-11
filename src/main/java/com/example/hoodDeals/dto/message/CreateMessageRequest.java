package com.example.hoodDeals.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMessageRequest(
        @NotNull Long senderId,
        @NotNull Long receiverId,
        @NotBlank @Size(max = 2000) String content
) {}
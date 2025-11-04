package com.example.hoodDeals.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userPicture;
    private String title;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String status;
    private String category;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
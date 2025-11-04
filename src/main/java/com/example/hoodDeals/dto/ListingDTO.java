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
    private Long user_id;
    private String userName;
    private String userPicture;
    private String title;
    private String description;
    private BigDecimal price;
    private String image_url;
    private String status;
    private String category;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
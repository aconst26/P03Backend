package com.example.hoodDeals.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateListingRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private String image_url;
    private String category;
    private String location;
    private Long user_id;
}
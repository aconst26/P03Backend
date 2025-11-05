package com.example.hoodDeals.dto;

import lombok.Data;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class CreateListingRequest {
    private String title;
    private String description;
    private BigDecimal price;
    @JsonProperty("imageUrl") 
    private String image_url;
    private String category;
    private String location;
    private Long user_id;
}
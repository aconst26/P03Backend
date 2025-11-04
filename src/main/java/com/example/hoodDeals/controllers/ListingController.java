package com.example.hoodDeals.controllers;

import com.example.hoodDeals.dto.CreateListingRequest;
import com.example.hoodDeals.dto.ListingDTO;
import com.example.hoodDeals.services.ListingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ListingController {
    
    private final ListingsService listingService;
    
    @PostMapping
    public ResponseEntity<ListingDTO> createListing(
            @RequestBody CreateListingRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        ListingDTO listing = listingService.createListing(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(listing);
    }
    
    @GetMapping
    public ResponseEntity<List<ListingDTO>> getAllActiveListings() {
        List<ListingDTO> listings = listingService.getAllActiveListings();
        return ResponseEntity.ok(listings);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ListingDTO> getListingById(@PathVariable Long id) {
        ListingDTO listing = listingService.getListingById(id);
        return ResponseEntity.ok(listing);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ListingDTO>> getUserListings(@PathVariable Long userId) {
        List<ListingDTO> listings = listingService.getUserListings(userId);
        return ResponseEntity.ok(listings);
    }
    
    @GetMapping("/my-listings")
    public ResponseEntity<List<ListingDTO>> getMyListings(Authentication authentication) {
        String email = authentication.getName();
        List<ListingDTO> listings = listingService.getMyListings(email);
        return ResponseEntity.ok(listings);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ListingDTO>> getListingsByCategory(@PathVariable String category) {
        List<ListingDTO> listings = listingService.getListingsByCategory(category);
        return ResponseEntity.ok(listings);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ListingDTO>> searchListings(@RequestParam String keyword) {
        List<ListingDTO> listings = listingService.searchListings(keyword);
        return ResponseEntity.ok(listings);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ListingDTO> updateListing(
            @PathVariable Long id,
            @RequestBody CreateListingRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        ListingDTO listing = listingService.updateListing(id, request, email);
        return ResponseEntity.ok(listing);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ListingDTO> updateListingStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {
        String email = authentication.getName();
        ListingDTO listing = listingService.updateListingStatus(id, status, email);
        return ResponseEntity.ok(listing);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        listingService.deleteListing(id, email);
        return ResponseEntity.noContent().build();
    }
}
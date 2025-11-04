package com.example.hoodDeals.controllers;

import com.example.hoodDeals.dto.CreateListingRequest;
import com.example.hoodDeals.dto.ListingDTO;
import com.example.hoodDeals.services.ListingsService;
import com.example.hoodDeals.utilities.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ListingController {

    private final ListingsService listingService;
    private final JwtUtil jwtUtil;

    private String extractEmail(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] decoded = java.util.Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decoded);
        String[] values = credentials.split(":", 2);
        if (values.length != 2) throw new RuntimeException("Invalid credentials");
        return values[0]; // email/username
    }

    @PostMapping
    public ResponseEntity<ListingDTO> createListing(
            @RequestBody CreateListingRequest request,
            @RequestHeader("Authorization") String authHeader) {

        ListingDTO listing = listingService.createListing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(listing);
    }

    @GetMapping("/my-listings")
    public ResponseEntity<List<ListingDTO>> getMyListings(
            @RequestHeader("Authorization") String authHeader) {

        String email = extractEmail(authHeader);
        List<ListingDTO> listings = listingService.getMyListings(email);
        return ResponseEntity.ok(listings);
    }

    // Other endpoints that don’t need authentication can stay as they are
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
            @RequestHeader("Authorization") String authHeader) {

        String email = extractEmail(authHeader);
        ListingDTO listing = listingService.updateListing(id, request, email);
        return ResponseEntity.ok(listing);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ListingDTO> updateListingStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("Authorization") String authHeader) {

        String email = extractEmail(authHeader);
        ListingDTO listing = listingService.updateListingStatus(id, status, email);
        return ResponseEntity.ok(listing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String email = extractEmail(authHeader);
        listingService.deleteListing(id, email);
        return ResponseEntity.noContent().build();
    }
}


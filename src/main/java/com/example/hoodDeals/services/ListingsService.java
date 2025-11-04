package com.example.hoodDeals.services;

import com.example.hoodDeals.dto.CreateListingRequest;
import com.example.hoodDeals.dto.ListingDTO;
import com.example.hoodDeals.entities.User;
import com.example.hoodDeals.entities.Listings;
import com.example.hoodDeals.repositories.UserRepository;
import com.example.hoodDeals.repositories.ListingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingsService {
    
    private final ListingsRepository listingRepository;
    private final UserRepository appUserRepository;
    
    @Transactional
    public ListingDTO createListing(CreateListingRequest request) {
        User user = appUserRepository.findById(request.getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Listings listing = new Listings();
        listing.setUser(user);
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setImageUrl(request.getImage_url());
        listing.setCategory(request.getCategory());
        listing.setLocation(request.getLocation());
        listing.setStatus("active");
        
        Listings savedListing = listingRepository.save(listing);
        return convertToDTO(savedListing);
    }
    
    public List<ListingDTO> getAllActiveListings() {
        return listingRepository.findActiveListings().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ListingDTO getListingById(Long id) {
        Listings listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        return convertToDTO(listing);
    }
    
    public List<ListingDTO> getUserListings(Long userId) {
        return listingRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ListingDTO> getMyListings(String email) {
        User user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return getUserListings(user.getId());
    }
    
    public List<ListingDTO> getListingsByCategory(String category) {
        return listingRepository.findActiveByCategoryOrderByCreatedAtDesc(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ListingDTO> searchListings(String keyword) {
        return listingRepository.searchListings(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ListingDTO updateListing(Long id, CreateListingRequest request, String email) {
        Listings listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        
        if (!listing.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to update this listing");
        }
        
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setImageUrl(request.getImage_url());
        listing.setCategory(request.getCategory());
        listing.setLocation(request.getLocation());
        
        Listings updatedListing = listingRepository.save(listing);
        return convertToDTO(updatedListing);
    }
    
    @Transactional
    public ListingDTO updateListingStatus(Long id, String status, String email) {
        Listings listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        
        if (!listing.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to update this listing");
        }
        
        listing.setStatus(status);
        Listings updatedListing = listingRepository.save(listing);
        return convertToDTO(updatedListing);
    }
    
    @Transactional
    public void deleteListing(Long id, String email) {
        Listings listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        
        if (!listing.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to delete this listing");
        }
        
        listingRepository.delete(listing);
    }
    
    private ListingDTO convertToDTO(Listings listing) {
        ListingDTO dto = new ListingDTO();
        dto.setId(listing.getId());
        dto.setUser_id(listing.getUser().getId());
        dto.setUserName(listing.getUser().getName());
        dto.setUserPicture(listing.getUser().getPicture());
        dto.setTitle(listing.getTitle());
        dto.setDescription(listing.getDescription());
        dto.setPrice(listing.getPrice());
        dto.setImage_url(listing.getImageUrl());
        dto.setStatus(listing.getStatus());
        dto.setCategory(listing.getCategory());
        dto.setLocation(listing.getLocation());
        dto.setCreatedAt(listing.getCreatedAt());
        dto.setUpdatedAt(listing.getUpdatedAt());
        return dto;
    }
}
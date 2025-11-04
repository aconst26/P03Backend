package com.example.hoodDeals.repositories;

import com.example.hoodDeals.entities.Listings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingsRepository extends JpaRepository<Listings, Long> {
    
    List<Listings> findByUserId(Long userId);
    
    List<Listings> findByStatus(String status);
    
    List<Listings> findByUserIdAndStatus(Long userId, String status);
    
    List<Listings> findByStatusOrderByCreatedAtDesc(String status);
    
    @Query("SELECT l FROM Listing l WHERE l.status = 'active' ORDER BY l.createdAt DESC")
    List<Listings> findActiveListings();
    
    @Query("SELECT l FROM Listing l WHERE l.category = :category AND l.status = 'active' ORDER BY l.createdAt DESC")
    List<Listings> findActiveByCategoryOrderByCreatedAtDesc(@Param("category") String category);
    
    @Query("SELECT l FROM Listing l WHERE " +
           "LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Listings> searchListings(@Param("keyword") String keyword);
}
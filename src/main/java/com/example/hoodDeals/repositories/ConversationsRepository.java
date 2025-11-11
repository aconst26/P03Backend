package com.example.hoodDeals.repositories;
import com.example.hoodDeals.entities.Conversations;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversationsRepository extends JpaRepository<Conversations, Long> {

    @Query("SELECT c FROM Conversations c WHERE c.user1Id = :userId OR c.user2Id = :userId")
    Page<Conversations> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
           SELECT c FROM Conversations c
           WHERE c.listingId = :listingId
             AND ((c.user1Id = :userA AND c.user2Id = :userB)
               OR  (c.user1Id = :userB AND c.user2Id = :userA))
           """)
    Optional<Conversations> findByUsersAndListingAnyOrder(
            @Param("userA") Long userA,
            @Param("userB") Long userB,
            @Param("listingId") Long listingId
    );
}
package com.example.hoodDeals.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "conversations")
public class Conversations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user1_id")
    private Long user1Id;

    @Column(name = "user2_id")
    private Long user2Id;

    @Column(name = "listing_id")
    private Long listingId;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastMessageAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        lastMessageAt = LocalDateTime.now();
    }
    // Constructors
    public Conversations() {}
    public Conversations(Long user1Id, Long user2Id, Long listingId) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.listingId = listingId;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUser1Id() {
        return user1Id;
    }
    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }
    public Long getUser2Id() {
        return user2Id;
    }
    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }
    public Long getListingId() {
        return listingId;
    }
    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }
    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
public String toString() {
    return "Conversation{" +
           "id=" + id +
           ", user1Id=" + user1Id +
           ", user2Id=" + user2Id +
           ", listingId=" + listingId +
           ", lastMessageAt=" + lastMessageAt +
           ", createdAt=" + createdAt +
           '}';
}

} 
    
package com.example.hoodDeals.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "messages")

public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sender_id")
    private Long senderId;
    @Column(name = "receiver_id")
    private Long receiverId;
    @Column(name = "listing_id")
    private Long listingId;
    @Column(name = "content")
    private String content;
    @Column(name="is_read")
    private Boolean isRead = false;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "conversation_id")
    private Long conversationId;    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Messages() {}
    public Messages(Long senderId, Long receiverId, Long listingId, String content, Long conversationId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.listingId = listingId;
        this.content = content;
        this.conversationId = conversationId;
    }
    // Getters and Setters
    public Long getId() {
        return id;
    }        
    public void setId(Long id) {
        this.id = id;
    }
    public Long getSenderId() {
        return senderId;
    }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public Long getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    public Long getListingId() {
        return listingId;
    }
    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Boolean getIsRead() {
        return isRead;
    }
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
        if (isRead != null) {
            this.isRead = isRead;
        }
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        if (createdAt != null) {
            this.createdAt = createdAt;
        }
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        if (updatedAt != null) {
            this.updatedAt = updatedAt;
        }
    }

}

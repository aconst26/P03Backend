package com.example.hoodDeals.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String googleId; 
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String name;
    
    private String picture;

    private String password;

    private String roles = "USER"; // default role

    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public User() {}
    
    public User(String googleId, String email, String name, String picture) {
        this.googleId = googleId;
        this.email = email;
        this.name = name;
        this.picture = picture;
    }

    // Getters and Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getGoogleId() { 
        return googleId; 
    }
    
    public void setGoogleId(String googleId) { 
        this.googleId = googleId; 
    }

    public String getPassword() { 
        return password; 
    }

    public void setRoles(String role) { 
        this.roles = role; 
    }

    public String getRoles() { 
        return roles; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }

    public Boolean getIsActive() { 
        return isActive; 
    }
    
    public void setIsActive(Boolean active) { 
        this.isActive = active; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public String getPicture() { 
        return picture; 
    }
    
    public void setPicture(String picture) { 
        this.picture = picture; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
}
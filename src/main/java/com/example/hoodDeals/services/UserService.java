package com.example.hoodDeals.services;

import com.example.hoodDeals.entities.User;
import com.example.hoodDeals.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Add/Update this method in UserService.java
    public User createOrUpdateGitHubUser(String email, String name, String picture, String githubId) {
        // Try to find by GitHub ID first
        Optional<User> existingByGithubId = userRepository.findByGithubId(githubId);

        if (existingByGithubId.isPresent()) {
            User user = existingByGithubId.get();

            // Update name if provided
            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }

            // Only update picture if new one is not null
            // This preserves existing picture from Google login
            if (picture != null && !picture.isEmpty()) {
                user.setPicture(picture);
            }

            return userRepository.save(user);
        }

        // Try to find by email (user might have signed up with Google first)
        Optional<User> existingByEmail = findByEmail(email);

        if (existingByEmail.isPresent()) {
            // Link GitHub to existing account
            User user = existingByEmail.get();
            user.setGithubId(githubId);

            // Update name if provided
            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }

            // Only update picture if:
            // 1. New picture exists AND
            // 2. Either existing picture is null OR we want to use the latest
            if (picture != null && !picture.isEmpty()) {
                user.setPicture(picture);
            }
            // If picture is null from GitHub, keep existing picture (from Google)

            return userRepository.save(user);
        }

        // Create new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPicture(picture); // Can be null for new users
        newUser.setGithubId(githubId);
        newUser.setPassword(null); // OAuth users don't need password
        return saveUser(newUser);
    }

    // Create or update a user, works for both Google OAuth and standard users
    // Add/Update this method in UserService.java
    public User createOrUpdateUser(String email, String name, String picture, String googleId) {
        // Try to find by Google ID first
        Optional<User> existingByGoogleId = userRepository.findByGoogleId(googleId);

        if (existingByGoogleId.isPresent()) {
            User user = existingByGoogleId.get();

            // Update name if provided
            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }

            // Only update picture if new one is not null
            if (picture != null && !picture.isEmpty()) {
                user.setPicture(picture);
            }

            return userRepository.save(user);
        }

        // Try to find by email
        Optional<User> existingByEmail = findByEmail(email);

        if (existingByEmail.isPresent()) {
            // Link Google to existing account
            User user = existingByEmail.get();
            user.setGoogleId(googleId);

            // Update name if provided
            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }

            // Only update picture if new one exists
            if (picture != null && !picture.isEmpty()) {
                user.setPicture(picture);
            }

            return userRepository.save(user);
        }

        // Create new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPicture(picture);
        newUser.setGoogleId(googleId);
        newUser.setPassword(null);
        return saveUser(newUser);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}

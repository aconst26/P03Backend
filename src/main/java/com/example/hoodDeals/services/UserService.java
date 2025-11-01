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

    // Create or update a user, works for both Google OAuth and standard users
    public User createOrUpdateUser(String email, String name, String picture, String googleId) {
        // First, check if user exists by Google ID
        Optional<User> existingUserByGoogleId = userRepository.findByGoogleId(googleId);

        if (existingUserByGoogleId.isPresent()) {
            User user = existingUserByGoogleId.get();
            boolean updated = false;

            if (name != null && !name.equals(user.getName())) {
                user.setName(name);
                updated = true;
            }
            if (picture != null && !picture.equals(user.getPicture())) {
                user.setPicture(picture);
                updated = true;
            }
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
                updated = true;
            }

            if (updated) {
                return userRepository.save(user);
            }
            return user;
        }

        // Check if user exists by email (standard signup)
        Optional<User> existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail.isPresent()) {
            User user = existingUserByEmail.get();

            // Link Google ID if not already linked
            if (user.getGoogleId() == null && googleId != null) {
                user.setGoogleId(googleId);
                if (name != null) user.setName(name);
                if (picture != null) user.setPicture(picture);
                return userRepository.save(user);
            }
            return user;
        }

        // User doesn't exist, create new
        User newUser = new User();
        newUser.setGoogleId(googleId);
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPicture(picture);
        newUser.setIsActive(true);
        newUser.setRoles("USER");

        return userRepository.save(newUser);
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
}

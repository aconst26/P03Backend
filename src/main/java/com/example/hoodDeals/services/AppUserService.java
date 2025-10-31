package com.example.hoodDeals.services;

import com.example.hoodDeals.entities.AppUser;
import com.example.hoodDeals.repositories.AppUserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AppUserService {
    private final AppUserRepository userRepository;

    public AppUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser saveUser(AppUser user) {
        return userRepository.save(user);
    }

    public Optional<AppUser> getUserByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }
}

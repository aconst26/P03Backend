package com.example.hoodDeals.services;

import com.example.hoodDeals.entities.AppUser;
import com.example.hoodDeals.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository userRepository;

    public AppUser findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId).orElse(null);
    }

    public AppUser saveUser(AppUser user) {
        return userRepository.save(user);
    }
}

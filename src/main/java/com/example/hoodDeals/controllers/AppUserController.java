package com.example.hoodDeals.controllers;

import com.example.hoodDeals.entities.AppUser;
import com.example.hoodDeals.services.AppUserService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class AppUserController {
    private final AppUserService userService;

    public AppUserController(AppUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public AppUser createUser(@RequestBody AppUser user) {
        return userService.saveUser(user);
    }

    @GetMapping("/{googleId}")
    public Optional<AppUser> getUser(@PathVariable String googleId) {
        return userService.getUserByGoogleId(googleId);
    }
}

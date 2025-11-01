package com.example.hoodDeals.controllers;

import com.example.hoodDeals.entities.AppUser;
import com.example.hoodDeals.services.AppUserService;
import com.example.hoodDeals.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        AppUser user = appUserService.findByGoogleId(request.getGoogleId());
        if (user == null) {
            user = new AppUser();
            user.setGoogleId(request.getGoogleId());
            user.setEmail(request.getEmail());
            user.setName(request.getName());
            user.setPicture(request.getPicture());
            user = appUserService.saveUser(user);
        }

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new GoogleLoginResponse(token, user));
    }
}

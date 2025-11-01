package com.example.hoodDeals.controllers;

import com.example.hoodDeals.entities.User;
import com.example.hoodDeals.utilities.JwtUtil;
import com.example.hoodDeals.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }
            
            String email = jwtUtil.getEmailFromToken(token);
            
            return userService.findByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
}
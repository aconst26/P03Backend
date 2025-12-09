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
public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
    try {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(401).body("Missing Authorization header");
        }

        // If header starts with Bearer → JWT path
        if (authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            String email = jwtUtil.getEmailFromToken(token);
            return userService.findByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        // Otherwise, assume Basic Auth path
        if (authHeader.startsWith("Basic ")) {
            // This just returns the list of users so you can verify Basic works
            // Later, you can change this to look up a real user (if you want)
            return ResponseEntity.ok(userService.findAll());
        }

        return ResponseEntity.status(400).body("Unsupported Authorization type");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(401).body("Unauthorized: " + e.getMessage());
    }
}


    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
    @GetMapping("/by-email")
public ResponseEntity<?> getByEmail(@RequestParam("email") String email) {
    return userService.findByEmail(email)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(404).body(new User()));


}
    @GetMapping("/by-userid")
public ResponseEntity<?> getByUserId(@RequestParam("userid") Long userId) {
    return userService.findById(userId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(404).body(new User()));


}
}
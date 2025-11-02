package com.example.hoodDeals.controllers;

import com.example.hoodDeals.entities.User;
import com.example.hoodDeals.utilities.JwtUtil;
import com.example.hoodDeals.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");
            String name = body.get("name");

            if (email == null || password == null || name == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }

            // Check if user already exists
            if (userService.findByEmail(email).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Email already in use"));
            }

            // Encrypt password
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(new BCryptPasswordEncoder().encode(password));
            newUser.setGoogleId(null);
            userService.saveUser(newUser);

            String token = jwtUtil.generateToken(newUser.getEmail(), newUser.getId());

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", newUser.getId());
            userMap.put("email", newUser.getEmail());
            userMap.put("name", newUser.getName());
            userMap.put("googleId", newUser.getGoogleId());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userMap);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Signup failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");

            if (email == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing fields"));
            }

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            // Verify password
            if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid credentials"));
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getId());

            // Use HashMap to safely allow nulls
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("name", user.getName());
            userMap.put("googleId", user.getGoogleId()); 

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userMap);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateGoogle(@RequestBody GoogleAuthRequest request) {
        try {
            System.out.println("=== Google Authentication Request ===");
            System.out.println("Google ID: " + request.getId());
            System.out.println("Email: " + request.getEmail());
            System.out.println("Name: " + request.getName());

            // Validate request
            if (request.getId() == null || request.getEmail() == null) {
                System.err.println("ERROR: Missing required fields");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing required fields (id or email)");
                return ResponseEntity.badRequest().body(error);
            }

            // Check if user already exists and create/update
            User user = userService.createOrUpdateUser(
                    request.getEmail(),
                    request.getName(),
                    request.getPicture(),
                    request.getId());

            System.out.println("User processed - ID: " + user.getId() + ", Email: " + user.getEmail());

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());
            System.out.println("JWT token generated successfully");

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "picture", user.getPicture(),
                    "googleId", user.getGoogleId()));

            System.out.println("=== Authentication Successful ===\n");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("=== Authentication Failed ===");
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Inner class for request body
    public static class GoogleAuthRequest {
        private String id;
        private String email;
        private String name;
        private String picture;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
    }
}

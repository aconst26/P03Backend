package com.example.hoodDeals.controllers;

import com.example.hoodDeals.entities.User;
import com.example.hoodDeals.utilities.JwtUtil;
import com.example.hoodDeals.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
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

    @Value("${github.client-id}")
    private String githubClientId;

    @Value("${github.client-secret}")
    private String githubClientSecret;

    @Value("${github.web.client-id:}")
    private String githubWebClientId;

    @Value("${github.web.client-secret:}")
    private String githubWebClientSecret;

    @Value("${github.token-url:https://github.com/login/oauth/access_token}")
    private String githubTokenUrl;

    @Value("${github.user-url:https://api.github.com/user}")
    private String githubUserUrl;

    @Value("${github.email-url:https://api.github.com/user/emails}")
    private String githubEmailUrl;

    
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

    @PostMapping("/github")
    public ResponseEntity<?> authenticateWithGitHub(@RequestBody GitHubAuthRequest request) {
        try {
            System.out.println("=== GitHub Authentication Request ===");
            System.out.println("Authorization Code: " + (request.getCode() != null ? "Received" : "Missing"));
            System.out.println("GitHub Client ID: " + githubClientId);
            System.out.println("GitHub Client Secret: " + (githubClientSecret != null && !githubClientSecret.isEmpty() ? "Set (length: " + githubClientSecret.length() + ")" : "MISSING!"));
            System.out.println("GitHub Token URL: " + githubTokenUrl);

            // Step 1: Exchange code for access token
            String accessToken = exchangeCodeForToken(request.getCode());
            
            if (accessToken == null) {
                System.err.println("ERROR: Failed to get access token from GitHub");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Failed to get access token from GitHub"));
            }

            System.out.println("Access token received from GitHub");

            // Step 2: Get user info from GitHub
            GitHubUserInfo githubUser = getGitHubUserInfo(accessToken);
            
            if (githubUser == null) {
                System.err.println("ERROR: Failed to get user info from GitHub");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Failed to get user info from GitHub"));
            }

            System.out.println("GitHub User ID: " + githubUser.getId());
            System.out.println("GitHub Login: " + githubUser.getLogin());
            System.out.println("GitHub Email: " + githubUser.getEmail());

            // Step 3: If email is null, fetch it separately
            if (githubUser.getEmail() == null || githubUser.getEmail().isEmpty()) {
                System.out.println("Email not in profile, fetching from emails endpoint...");
                String email = getGitHubUserEmail(accessToken);
                githubUser.setEmail(email);
            }

            if (githubUser.getEmail() == null) {
                System.err.println("ERROR: Unable to retrieve email from GitHub");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Unable to retrieve email from GitHub. Please make your email public or grant email permission."));
            }

            // Step 4: Find or create user in database
            User user = userService.createOrUpdateGitHubUser(
                    githubUser.getEmail(),
                    githubUser.getName() != null ? githubUser.getName() : githubUser.getLogin(),
                    githubUser.getAvatarUrl(),
                    String.valueOf(githubUser.getId()));

            System.out.println("User processed - ID: " + user.getId() + ", Email: " + user.getEmail());

            // Step 5: Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());
            System.out.println("JWT token generated successfully");

            // Step 6: Return response
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("name", user.getName());
            userMap.put("picture", user.getPicture());
            userMap.put("githubId", user.getGithubId());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userMap);

            System.out.println("=== GitHub Authentication Successful ===\n");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("=== GitHub Authentication Failed ===");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

    private String exchangeCodeForToken(String code) {
        try {
            System.out.println("=== Exchanging Code for Token ===");
            System.out.println("Code: " + code);
            
            // Try mobile credentials first, then web
            String clientId = githubClientId;
            String clientSecret = githubClientSecret;
            
            System.out.println("Client ID: " + clientId);
            System.out.println("Client Secret exists: " + (clientSecret != null && !clientSecret.isEmpty()));
            
            RestTemplate restTemplate = new RestTemplate();
            
            // Prepare request body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("client_id", githubClientId);
            requestBody.put("client_secret", githubClientSecret);
            requestBody.put("code", code);

            System.out.println("Request body: " + requestBody);

            // Set headers to request JSON response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // Make request to GitHub
            ResponseEntity<String> response = restTemplate.exchange(
                githubTokenUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            System.out.println("GitHub Token Response Status: " + response.getStatusCode());
            System.out.println("Raw Response Body: " + response.getBody());

            // Manual extraction (more reliable than Jackson for this case)
            try {
                String body = response.getBody();
                if (body != null && body.contains("access_token")) {
                    int start = body.indexOf("\"access_token\":\"") + 16;
                    int end = body.indexOf("\"", start);
                    String token = body.substring(start, end);
                    System.out.println("Extracted access token: " + token.substring(0, 10) + "...");
                    return token;
                }
            } catch (Exception manualEx) {
                System.err.println("Manual extraction failed: " + manualEx.getMessage());
            }

            // Fallback: Try Jackson parsing
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                
                GitHubTokenResponse tokenResponse = mapper.readValue(response.getBody(), GitHubTokenResponse.class);
                
                System.out.println("Parsed token response - access_token: " + tokenResponse.getAccessToken());
                
                if (tokenResponse.getAccessToken() != null && !tokenResponse.getAccessToken().isEmpty()) {
                    System.out.println("Access token received successfully");
                    return tokenResponse.getAccessToken();
                }
            } catch (Exception parseEx) {
                System.err.println("Failed to parse token response: " + parseEx.getMessage());
            }

            System.err.println("No access token in GitHub response");
            return null;
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("HTTP Error from GitHub:");
            System.err.println("Status: " + e.getStatusCode());
            System.err.println("Response Body: " + e.getResponseBodyAsString());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Error exchanging code for token:");
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private GitHubUserInfo getGitHubUserInfo(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<GitHubUserInfo> response = restTemplate.exchange(
                githubUserUrl,
                HttpMethod.GET,
                entity,
                GitHubUserInfo.class
            );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error getting GitHub user info:");
            e.printStackTrace();
            return null;
        }
    }

    private String getGitHubUserEmail(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<GitHubEmail[]> response = restTemplate.exchange(
                githubEmailUrl,
                HttpMethod.GET,
                entity,
                GitHubEmail[].class
            );

            if (response.getBody() != null && response.getBody().length > 0) {
                // Find primary verified email
                for (GitHubEmail email : response.getBody()) {
                    if (email.isPrimary() && email.isVerified()) {
                        return email.getEmail();
                    }
                }
                // If no primary verified, return first verified
                for (GitHubEmail email : response.getBody()) {
                    if (email.isVerified()) {
                        return email.getEmail();
                    }
                }
                // Return first email as fallback
                return response.getBody()[0].getEmail();
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error getting GitHub user email:");
            e.printStackTrace();
            return null;
        }
    }

    // ===== Inner Classes for Request/Response DTOs =====

    public static class GoogleAuthRequest {
        private String id;
        private String email;
        private String name;
        private String picture;

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

    public static class GitHubAuthRequest {
        private String code;
        private String platform; // "web" or "mobile"

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }
    }

    public static class GitHubUserInfo {
        private Long id;
        private String login;
        private String name;
        private String avatar_url;
        private String email;
        private String bio;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatarUrl() {
            return avatar_url;
        }

        public void setAvatarUrl(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }
    }

    public static class GitHubTokenResponse {
        private String access_token;  // Changed from camelCase
        private String token_type;    // Changed from camelCase
        private String scope;

        public String getAccessToken() {
            return access_token;
        }

        public void setAccessToken(String access_token) {
            this.access_token = access_token;
        }

        public String getTokenType() {
            return token_type;
        }

        public void setTokenType(String token_type) {
            this.token_type = token_type;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
    }

    public static class GitHubEmail {
        private String email;
        private boolean primary;
        private boolean verified;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }
    }
}
package com.gaelcraves.project3.GaelCravings_Backend.Controllers;

import com.gaelcraves.project3.GaelCravings_Backend.Auth.Service.JwTService;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.AuthRequest;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.User;
import com.gaelcraves.project3.GaelCravings_Backend.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    //The HTTP Request GET POST PUT methods for the users

    private final UserService service;
    private final JwTService jwtService;
    private AuthenticationManager authenticationManager;

    public UserController(UserService service, JwTService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        // Never return passwords or security answers
        List<Map<String, Object>> sanitized = service.getAllUsers().stream()
                .map(user -> Map.of(
                        "userId", (Object) user.getUserId(),
                        "email", user.getEmail()
                ))
                .toList();
        return ResponseEntity.ok(sanitized);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return service.getUserById(id)
                .map(user -> ResponseEntity.ok(Map.of(
                        "userId", user.getUserId(),
                        "email", user.getEmail()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return service.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(Map.of(
                        "userId", user.getUserId(),
                        "email", user.getEmail()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            // Sanitize and validate email
            user.setEmail(user.getEmail().toLowerCase().trim());

            User created = service.createUser(user);

            // Generate JWT token
            String token = jwtService.generateToken(created.getEmail(), created.getUserId());

            // Return safe response
            Map<String, Object> response = Map.of(
                    "userId", created.getUserId(),
                    "email", created.getEmail(),
                    "firstName", created.getFirstName(),
                    "lastName", created.getLastName(),
                    "roles", created.getRoleNames(),
                    "token", token,
                    "message", "User registered successfully"
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        System.out.println("🔐 Login attempt for: " + email);

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email and password are required"));
        }

        Optional<User> userOpt = service.getUserByEmailAndPassword(email.toLowerCase().trim(), password);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        User user = userOpt.get();

        String token = jwtService.generateToken(user.getEmail(), user.getUserId());

        // Get role names as a Set<String>
        Set<String> roleNames = user.getRoleNames();

        // Return safe user data with roles as array
        Map<String, Object> response = Map.of(
                "userId", user.getUserId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "roles", new ArrayList<>(roleNames), // Convert Set to List
                "token", token
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String securityAnswer = body.get("securityAnswer");
            String newPassword = body.get("newPassword");

            if (email == null || securityAnswer == null || newPassword == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email, security answer, and new password are required"));
            }

            // Verify security answer
            if (!service.verifySecurityAnswer(email, securityAnswer)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Incorrect security answer"));
            }

            service.resetPassword(email, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            service.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to delete user"));
        }
    }

    @GetMapping("/security-question")
    public ResponseEntity<?> getSecurityQuestion(@RequestParam String email) {
        Optional<User> user = service.getUserByEmail(email);

        return user.<ResponseEntity<?>>map(value -> ResponseEntity.ok(Map.of(
                "securityQuestion", value.getSecurityQuestion()
        ))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found")));

    }
}
package com.gaelcraves.project3.GaelCravings_Backend.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<?> root() {
        return ResponseEntity.ok(Map.of(
            "status", "online",
            "message", "GaelCraves Backend API",
            "version", "1.0.0",
            "endpoints", Map.of(
                "health", "/health",
                "menus", "/api/menus",
                "foodItems", "/api/food-items",
                "login", "/api/users/login",
                "register", "/api/users"
            )
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "timestamp", System.currentTimeMillis()
        ));
    }
}

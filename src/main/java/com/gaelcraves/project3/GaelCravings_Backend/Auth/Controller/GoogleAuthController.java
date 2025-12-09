package com.gaelcraves.project3.GaelCravings_Backend.Auth.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/auth")
public class GoogleAuthController {

    @Value("${google.client.id:}")
    private String clientId;

    @Value("${google.client.secret:}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping(path = "/google", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exchangeCode(@RequestBody Map<String, String> body) {
        // If Google OAuth not configured, return error
        if (clientId == null || clientId.isEmpty()) {
            return ResponseEntity.status(501)
                .body(Map.of("error", "Google OAuth not configured on server"));
        }

        String code = body.get("code");
        String redirectUri = body.get("redirectUri");
        String idToken = body.get("idToken");

        // If id_token is provided, accept it (client-side OAuth)
        if (idToken != null && !idToken.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "message", "Google authentication successful",
                "email", body.getOrDefault("email", ""),
                "firstName", body.getOrDefault("firstName", ""),
                "lastName", body.getOrDefault("lastName", "")
            ));
        }

        // If code is provided, exchange it
        if (code == null || code.isEmpty() || redirectUri == null || redirectUri.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Missing required fields: code and redirectUri"));
        }

        String tokenUrl = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<String> resp = restTemplate.postForEntity(tokenUrl, request, String.class);
            return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to exchange authorization code: " + e.getMessage()));
        }
    }
}

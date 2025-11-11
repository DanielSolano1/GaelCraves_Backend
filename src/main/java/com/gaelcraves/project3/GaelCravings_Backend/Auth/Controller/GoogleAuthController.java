package com.gaelcraves.project3.GaelCravings_Backend.Auth.Controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class GoogleAuthController {

    private final RestTemplate restTemplate = new RestTemplate();

    // Accepts a JSON body with { code, redirectUri } and performs the
    // server-side exchange with Google's token endpoint using the client
    // secret stored in environment variables (Client_ID and Client_Secret).
    @PostMapping(path = "/api/v1/auth/google", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exchangeCode(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String redirectUri = body.get("redirectUri");

        if (code == null || redirectUri == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "code and redirectUri are required"));
        }

        String clientId = System.getenv("Client_ID");
        String clientSecret = System.getenv("Client_Secret");
        if (clientId == null || clientSecret == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Missing Google client credentials on the server"));
        }

        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<String> resp = restTemplate.postForEntity(tokenUrl, request, String.class);
            // Return Google's token response directly to the frontend for now.
            // In production, I will create a session for the user here and
            // return a secure session token/cookie instead of raw provider tokens.
            return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Failed to exchange code with Google", "detail", e.getMessage()));
        }
    }
}

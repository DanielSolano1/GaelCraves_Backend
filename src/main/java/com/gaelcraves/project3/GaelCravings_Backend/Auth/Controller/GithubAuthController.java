package com.gaelcraves.project3.GaelCravings_Backend.Auth.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/auth/github")
public class GithubAuthController {

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;

    @GetMapping("/me")
    public ResponseEntity<?> getGithubUser(@AuthenticationPrincipal OAuth2User principal) {
        try {
            if (principal == null) {
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("error", "Not authenticated with GitHub");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
            }

            Map<String, Object> attrs = principal.getAttributes();

            Object idObj = attrs.get("id");
            String id = idObj != null ? idObj.toString() : "";

            Object loginObj = attrs.get("login");
            String login = loginObj != null ? loginObj.toString() : "";

            Object nameObj = attrs.get("name");
            String name = nameObj != null ? nameObj.toString() : login;

            Object emailObj = attrs.get("email");
            String email = emailObj != null ? emailObj.toString() : "";

            String firstName = name != null ? name : "";
            String lastName = "";
            if (name != null && name.contains(" ")) {
                String[] parts = name.split(" ", 2);
                firstName = parts[0];
                lastName = parts[1];
            }

            if ((email == null || email.isBlank()) && login != null && !login.isBlank()) {
                email = login + "@github.local";
            }

            Map<String, Object> body = new HashMap<>();
            body.put("id", id);
            body.put("email", email != null ? email : "");
            body.put("firstName", firstName != null ? firstName : "");
            body.put("lastName", lastName != null ? lastName : "");
            List<String> roles = new ArrayList<>();
            roles.add("GITHUB_USER");
            body.put("roles", roles);

            return ResponseEntity.ok(body);
        } catch (Exception e) {
            // Avoid propagating exceptions to the client as a generic 500 with no details
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Failed to read GitHub user: " + e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }

    @PostMapping("/mobile")
    public ResponseEntity<?> githubMobileLogin(@RequestBody Map<String, String> payload) {
        try {
            String code = payload.get("code");
            String redirectUri = payload.get("redirectUri");

            if (code == null || code.isBlank()) {
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("error", "Missing authorization code");
                return ResponseEntity.badRequest().body(errorBody);
            }

            RestTemplate restTemplate = new RestTemplate();

            // Exchange authorization code for access token
            String tokenUrl = "https://github.com/login/oauth/access_token";

            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            tokenHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("client_id", githubClientId);
            form.add("client_secret", githubClientSecret);
            form.add("code", code);
            if (redirectUri != null && !redirectUri.isBlank()) {
                form.add("redirect_uri", redirectUri);
            }

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(form, tokenHeaders);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest, Map.class);

            if (!tokenResponse.getStatusCode().is2xxSuccessful() || tokenResponse.getBody() == null) {
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("error", "Failed to exchange code for access token");
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorBody);
            }

            Object accessTokenObj = tokenResponse.getBody().get("access_token");
            if (accessTokenObj == null) {
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("error", "No access token returned by GitHub");
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorBody);
            }

            String accessToken = accessTokenObj.toString();

            // Fetch GitHub user profile
            HttpHeaders apiHeaders = new HttpHeaders();
            apiHeaders.setBearerAuth(accessToken);
            apiHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> apiRequest = new HttpEntity<>(apiHeaders);
            ResponseEntity<Map> userResponse = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    apiRequest,
                    Map.class);

            if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("error", "Failed to fetch GitHub user profile");
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorBody);
            }

            Map<String, Object> attrs = userResponse.getBody();

            Object idObj = attrs.get("id");
            String id = idObj != null ? idObj.toString() : "";

            Object loginObj = attrs.get("login");
            String login = loginObj != null ? loginObj.toString() : "";

            Object nameObj = attrs.get("name");
            String name = nameObj != null ? nameObj.toString() : login;

            Object emailObj = attrs.get("email");
            String email = emailObj != null ? emailObj.toString() : "";

            String firstName = name != null ? name : "";
            String lastName = "";
            if (name != null && name.contains(" ")) {
                String[] parts = name.split(" ", 2);
                firstName = parts[0];
                lastName = parts[1];
            }

            if ((email == null || email.isBlank()) && login != null && !login.isBlank()) {
                email = login + "@github.local";
            }

            Map<String, Object> body = new HashMap<>();
            body.put("id", id);
            body.put("email", email != null ? email : "");
            body.put("firstName", firstName != null ? firstName : "");
            body.put("lastName", lastName != null ? lastName : "");
            List<String> roles = new ArrayList<>();
            roles.add("GITHUB_USER");
            body.put("roles", roles);

            return ResponseEntity.ok(body);
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "GitHub mobile login failed: " + e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }
}

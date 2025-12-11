package com.gaelcraves.project3.GaelCravings_Backend.Auth.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/github")
public class GithubAuthController {

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
}

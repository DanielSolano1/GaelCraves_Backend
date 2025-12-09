package com.gaelcraves.project3.GaelCravings_Backend.Auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions, use JWT
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication required)
                        .requestMatchers("/", "/error", "/health", "/actuator/health").permitAll()
                        .requestMatchers("/api/users/login", "/api/users", "/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/users/security-question", "/api/users/reset-password").permitAll()
                        .requestMatchers("/api/menus", "/api/menus/**").permitAll()
                        .requestMatchers("/api/food-items", "/api/food-items/**").permitAll()
                        .requestMatchers("/api/orders/create-payment-intent", "/api/orders/payment").permitAll()
                        
                        // Admin-only endpoints (allow both ADMIN and GAEL_HIMSELF roles)
                        .requestMatchers("/api/orders/admin/**").hasAnyRole("ADMIN", "GAEL_HIMSELF")
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "GAEL_HIMSELF")

                        // Protected endpoints (require authentication)
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/orders/**").authenticated()
                        .requestMatchers("/api/addresses/**").authenticated()

                        // Allow all other requests
                        .anyRequest().permitAll()
                )
                // Add JWT filter before the default authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private CorsConfigurationSource corsSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        
        // Parse multiple origins from environment
        String[] origins = allowedOrigin.split(",");
        List<String> allowedOriginsList = new java.util.ArrayList<>();
        
        for (String origin : origins) {
            String trimmed = origin.trim();
            if (!trimmed.isEmpty()) {
                allowedOriginsList.add(trimmed);
            }
        }
        
        // Always allow production Heroku frontend
        if (!allowedOriginsList.contains("https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com")) {
            allowedOriginsList.add("https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com");
        }
        
        // Always allow localhost for development
        allowedOriginsList.add("http://localhost:8081");
        allowedOriginsList.add("http://localhost:3000");
        allowedOriginsList.add("http://localhost:19006");
        allowedOriginsList.add("http://127.0.0.1:8081");
        
        cfg.setAllowedOrigins(allowedOriginsList);
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization","Content-Type"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                            "https://gaelcraves-frontend-7a6e5c03f69a.herokuapp.com",
                            "http://localhost:8081",
                            "http://localhost:3000",
                            "http://localhost:19006"
                        )
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization", "Content-Type")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
package com.gaelcraves.project3.GaelCravings_Backend.Service;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.User;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return repository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    // FIX: Arguments were reversed
    public boolean verifySecurityAnswer(String email, String securityAnswer) {
        User user = getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));

        String hashedAnswer = user.getSecurityAnswer();
        // First argument is plain text, second is hashed
        return passwordEncoder.matches(securityAnswer, hashedAnswer);
    }

    public Optional<User> getUserByEmailAndPassword(String email, String password) {
        Optional<User> user = getUserByEmail(email);
        if(user.isEmpty()) {
            return Optional.empty();
        }
        boolean passwordMatches = passwordEncoder.matches(password, user.get().getPassword());
        if (passwordMatches) {
            return user;
        }
        return Optional.empty();
    }

    public User createUser(@Valid User user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Validate password strength
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setSecurityAnswer(passwordEncoder.encode(user.getSecurityAnswer()));

        return repository.save(user);
    }

    public void resetPassword(String email, @Valid String newPassword) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }

    public void deleteUser(Integer id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    // NEW: Search users by partial name match
    public List<User> getUsersByName(String firstName) {
        return repository.findByFirstNameContainingIgnoreCase(firstName);  // ✅
    }
    // NEW: Simple boolean check for frontend signup forms
    public boolean userExistsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    // NEW: Combined lookup (email OR ID string)
    public Optional<User> getUserByEmailOrId(String identifier) {
        // Try email
        Optional<User> byEmail = repository.findByEmail(identifier);
        if (byEmail.isPresent()) return byEmail;

        // Try ID if numeric
        try {
            Integer id = Integer.parseInt(identifier);
            return repository.findById(id);
        } catch (NumberFormatException ignore) {}

        return Optional.empty();
    }

    // NEW: Lightweight summary for frontend (no password, no security)
    public Optional<User> getUserSummary(Integer id) {
        return repository.findById(id).map(user -> {
            User summary = new User();
            summary.setUserId(user.getUserId());
            summary.setFirstName(user.getFirstName());
            summary.setEmail(user.getEmail());
            return summary;
        });
    }

    // NEW: Update basic user info (safe for profile editing)
    public Optional<User> updateUserBasicInfo(Integer id, String name, String email) {
        Optional<User> userOpt = repository.findById(id);
        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();

        if (name != null && !name.isBlank()) user.setFirstName(name);
        if (email != null && !email.isBlank()) user.setEmail(email);

        return Optional.of(repository.save(user));
    }

}
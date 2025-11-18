package com.gaelcraves.project3.GaelCravings_Backend.Service;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.Roles;
import com.gaelcraves.project3.GaelCravings_Backend.Entity.User;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.RoleRepository;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.UserRepository;
import jakarta.transaction.Transactional;
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
    private final RoleRepository roleRepository;


    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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

    @Transactional
    public User createUser(@Valid User user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Validate password strength
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        // Hash password and security answer
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setSecurityAnswer(passwordEncoder.encode(user.getSecurityAnswer()));

        // Assign default "USER" role
        Roles userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new IllegalStateException("Default USER role not found in database"));

        user.addRole(userRole);

        User savedUser = repository.save(user);
        log.info("User created successfully with ID: {} and role: USER", savedUser.getUserId());

        return savedUser;
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
}
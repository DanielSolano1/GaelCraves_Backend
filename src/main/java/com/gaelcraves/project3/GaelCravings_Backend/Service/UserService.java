package com.gaelcraves.project3.GaelCravings_Backend.Service;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.User;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
    }
    // all of these are queries basically we need for functions
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return repository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }
    public Optional<User> getUserByPassword(String password) {
        return repository.findByPassword(password);
    }
    public Optional<User> getUserByEmailAndPassword(String email, String password) {
        return repository.findByEmailAndPassword(email, password);
    }

    public User createUser(User user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User created = repository.save(user);
        created.setPassword(passwordEncoder.encode(user.getPassword()));
        return created;
    }

    public void resetPassword(String email, String newPassword) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        user.setPassword(newPassword);
        repository.save(user);
    }


    public void deleteUser(Integer id) {
        repository.deleteById(id);
    }
}

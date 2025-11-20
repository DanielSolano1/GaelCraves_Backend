package com.gaelcraves.project3.GaelCravings_Backend.Repository;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);

    // FIXED: Changed from findByName to findByFirstName
    List<User> findByFirstNameContainingIgnoreCase(String firstName);

    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    boolean existsByEmail(String email);
}
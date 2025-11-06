package com.gaelcraves.project3.GaelCravings_Backend;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.*;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPassword(String password);
    Optional<User> findByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email);


}

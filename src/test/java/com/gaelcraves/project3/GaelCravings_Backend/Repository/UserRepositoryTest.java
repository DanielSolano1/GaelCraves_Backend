package com.gaelcraves.project3.GaelCravings_Backend.Repository;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User createUser(
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        User u = new User();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setPassword(password);
        u.setSecurityQuestion("Favorite food?");
        u.setSecurityAnswer("Pizza");
        return u;
    }

    @Test
    @DisplayName("Saving a valid user assigns an ID")
    void saveUser_assignsId() {
        User user = createUser("Alice", "Smith", "alice@example.com", "Secret123!");

        User saved = userRepository.save(user);

        assertNotNull(saved.getUserId());
    }

    @Test
    @DisplayName("findById returns the saved user")
    void findById_returnsUser() {
        User user = createUser("Bob", "Jones", "bob@example.com", "Secret123!");
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(saved.getUserId());

        assertTrue(found.isPresent());
        assertEquals("bob@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("findByEmail returns the correct user")
    void findByEmail_returnsUser() {
        User user = createUser("Carol", "Lee", "carol@example.com", "Secret123!");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("carol@example.com");

        assertTrue(found.isPresent());
        assertEquals("Carol", found.get().getFirstName());
    }

    @Test
    @DisplayName("findByEmail returns empty when user does not exist")
    void findByEmail_missingUser() {
        Optional<User> found = userRepository.findByEmail("missing@example.com");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("findByEmailAndPassword returns user when credentials match")
    void findByEmailAndPassword_success() {
        User user = createUser("Dave", "Young", "dave@example.com", "Password123!");
        userRepository.save(user);

        Optional<User> found =
                userRepository.findByEmailAndPassword("dave@example.com", "Password123!");

        assertTrue(found.isPresent());
        assertEquals("Dave", found.get().getFirstName());
    }

    @Test
    @DisplayName("findByEmailAndPassword returns empty when password is wrong")
    void findByEmailAndPassword_wrongPassword() {
        User user = createUser("Eve", "Kim", "eve@example.com", "Password123!");
        userRepository.save(user);

        Optional<User> found =
                userRepository.findByEmailAndPassword("eve@example.com", "WrongPassword");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("findByFirstNameContainingIgnoreCase finds users by partial first name")
    void findByFirstNameContainingIgnoreCase_works() {
        userRepository.save(createUser("Frank", "Hill", "frank@example.com", "Secret1!"));
        userRepository.save(createUser("Francis", "Long", "francis@example.com", "Secret2!"));
        userRepository.save(createUser("George", "Miller", "george@example.com", "Secret3!"));

        List<User> frUsers =
                userRepository.findByFirstNameContainingIgnoreCase("fr");

        assertEquals(2, frUsers.size());
        assertTrue(frUsers.stream().allMatch(u ->
                u.getFirstName().toLowerCase().startsWith("fr")));
    }

    @Test
    @DisplayName("findByFirstNameAndLastName returns exact match")
    void findByFirstNameAndLastName_exactMatch() {
        userRepository.save(createUser("Hanna", "Park", "hanna@example.com", "Secret1!"));
        userRepository.save(createUser("Hanna", "Lee", "hanna.lee@example.com", "Secret2!"));

        List<User> result =
                userRepository.findByFirstNameAndLastName("Hanna", "Park");

        assertEquals(1, result.size());
        assertEquals("hanna@example.com", result.get(0).getEmail());
    }

    @Test
    @DisplayName("existsByEmail is true when user exists")
    void existsByEmail_true() {
        userRepository.save(createUser("Ivy", "Ng", "ivy@example.com", "Secret123!"));

        assertTrue(userRepository.existsByEmail("ivy@example.com"));
    }

    @Test
    @DisplayName("existsByEmail is false when user does not exist")
    void existsByEmail_false() {
        assertFalse(userRepository.existsByEmail("noone@example.com"));
    }

    @Test
    @DisplayName("Unique email constraint prevents duplicate users")
    void uniqueEmailConstraint_preventsDuplicates() {
        User first = createUser("Jack", "Doe", "jack@example.com", "Secret123!");
        User second = createUser("Jake", "Roe", "jack@example.com", "Secret999!");

        userRepository.saveAndFlush(first);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(second);
        });
    }

    @Test
    @DisplayName("Invalid email triggers validation error")
    void invalidEmail_throwsConstraintViolation() {
        User user = createUser("Liam", "King", "not-an-email", "Secret123!");

        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }
}

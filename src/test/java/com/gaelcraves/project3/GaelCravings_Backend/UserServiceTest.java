package com.gaelcraves.project3.GaelCravings_Backend;

import com.gaelcraves.project3.GaelCravings_Backend.Entity.User;
import com.gaelcraves.project3.GaelCravings_Backend.Repository.UserRepository;
import com.gaelcraves.project3.GaelCravings_Backend.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUserId(1);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setSecurityQuestion("What is your pet name?");
        testUser.setSecurityAnswer("Fluffy");
    }

    // ============= GET ALL USERS TESTS =============

    @Test
    @DisplayName("Should return all users")
    void testGetAllUsers() {
        // Arrange
        User user2 = new User();
        user2.setUserId(2);
        user2.setEmail("user2@example.com");

        List<User> users = Arrays.asList(testUser, user2);
        when(repository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsersEmpty() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    // ============= GET USER BY ID TESTS =============

    @Test
    @DisplayName("Should return user when found by ID")
    void testGetUserById() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(repository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void testGetUserByIdNotFound() {
        // Arrange
        when(repository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserById(999);

        // Assert
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findById(999);
    }

    // ============= GET USER BY EMAIL TESTS =============

    @Test
    @DisplayName("Should return user when found by email")
    void testGetUserByEmail() {
        // Arrange
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.getUserByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(repository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void testGetUserByEmailNotFound() {
        // Arrange
        when(repository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserByEmail("nonexistent@example.com");

        // Assert
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByEmail("nonexistent@example.com");
    }

    // ============= CREATE USER TESTS =============

    @Test
    @DisplayName("Should create user with hashed password and security answer")
    void testCreateUser() {
        // Arrange
        when(repository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(repository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser);

        // Assert
        assertNotNull(result);
        verify(passwordEncoder, times(2)).encode(anyString()); // Once for password, once for security answer
        verify(repository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreateUserEmailExists() {
        // Arrange
        when(repository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(testUser)
        );

        assertEquals("Email already registered", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when password is too short")
    void testCreateUserPasswordTooShort() {
        // Arrange
        testUser.setPassword("short");
        when(repository.existsByEmail(testUser.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(testUser)
        );

        assertEquals("Password must be at least 8 characters long", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    void testCreateUserPasswordNull() {
        // Arrange
        testUser.setPassword(null);
        when(repository.existsByEmail(testUser.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(testUser)
        );

        assertEquals("Password must be at least 8 characters long", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    // ============= LOGIN (GET USER BY EMAIL AND PASSWORD) TESTS =============

    @Test
    @DisplayName("Should return user when email and password match")
    void testGetUserByEmailAndPasswordSuccess() {
        // Arrange
        String hashedPassword = "$2a$10$hashedPassword";
        testUser.setPassword(hashedPassword);

        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", hashedPassword)).thenReturn(true);

        // Act
        Optional<User> result = userService.getUserByEmailAndPassword("test@example.com", "password123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(passwordEncoder, times(1)).matches("password123", hashedPassword);
    }

    @Test
    @DisplayName("Should return empty when password doesn't match")
    void testGetUserByEmailAndPasswordWrongPassword() {
        // Arrange
        String hashedPassword = "$2a$10$hashedPassword";
        testUser.setPassword(hashedPassword);

        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", hashedPassword)).thenReturn(false);

        // Act
        Optional<User> result = userService.getUserByEmailAndPassword("test@example.com", "wrongPassword");

        // Assert
        assertTrue(result.isEmpty());
        verify(passwordEncoder, times(1)).matches("wrongPassword", hashedPassword);
    }

    @Test
    @DisplayName("Should return empty when user not found")
    void testGetUserByEmailAndPasswordUserNotFound() {
        // Arrange
        when(repository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserByEmailAndPassword("nonexistent@example.com", "password123");

        // Assert
        assertTrue(result.isEmpty());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // ============= VERIFY SECURITY ANSWER TESTS =============

    @Test
    @DisplayName("Should return true when security answer matches")
    void testVerifySecurityAnswerSuccess() {
        // Arrange
        String hashedAnswer = "$2a$10$hashedAnswer";
        testUser.setSecurityAnswer(hashedAnswer);

        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Fluffy", hashedAnswer)).thenReturn(true);

        // Act
        boolean result = userService.verifySecurityAnswer("test@example.com", "Fluffy");

        // Assert
        assertTrue(result);
        verify(passwordEncoder, times(1)).matches("Fluffy", hashedAnswer);
    }

    @Test
    @DisplayName("Should return false when security answer doesn't match")
    void testVerifySecurityAnswerFailed() {
        // Arrange
        String hashedAnswer = "$2a$10$hashedAnswer";
        testUser.setSecurityAnswer(hashedAnswer);

        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongAnswer", hashedAnswer)).thenReturn(false);

        // Act
        boolean result = userService.verifySecurityAnswer("test@example.com", "WrongAnswer");

        // Assert
        assertFalse(result);
        verify(passwordEncoder, times(1)).matches("WrongAnswer", hashedAnswer);
    }

    @Test
    @DisplayName("Should throw exception when user not found for security answer verification")
    void testVerifySecurityAnswerUserNotFound() {
        // Arrange
        when(repository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.verifySecurityAnswer("nonexistent@example.com", "Fluffy")
        );

        assertEquals("User Not Found", exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // ============= RESET PASSWORD TESTS =============

    @Test
    @DisplayName("Should reset password with valid input")
    void testResetPassword() {
        // Arrange
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashedNewPassword");
        when(repository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.resetPassword("test@example.com", "newPassword123");

        // Assert
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(repository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when resetting password for non-existent user")
    void testResetPasswordUserNotFound() {
        // Arrange
        when(repository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.resetPassword("nonexistent@example.com", "newPassword123")
        );

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when new password is too short")
    void testResetPasswordTooShort() {
        // Arrange
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.resetPassword("test@example.com", "short")
        );

        assertEquals("Password must be at least 8 characters long", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when new password is null")
    void testResetPasswordNull() {
        // Arrange
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.resetPassword("test@example.com", null)
        );

        assertEquals("Password must be at least 8 characters long", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    // ============= DELETE USER TESTS =============

    @Test
    @DisplayName("Should delete user when user exists")
    void testDeleteUser() {
        // Arrange
        when(repository.existsById(1)).thenReturn(true);
        doNothing().when(repository).deleteById(1);

        // Act
        userService.deleteUser(1);

        // Assert
        verify(repository, times(1)).existsById(1);
        verify(repository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteUserNotFound() {
        // Arrange
        when(repository.existsById(999)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteUser(999)
        );

        assertEquals("User not found with ID: 999", exception.getMessage());
        verify(repository, never()).deleteById(anyInt());
    }
}
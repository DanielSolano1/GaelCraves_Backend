package com.gaelcraves.project3.GaelCravings_Backend.Entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotBlank(message = "First name is required")
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(nullable = false, length = 100)
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
             message = "Invalid email pattern")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank(message = "Security question is required")
    @Column(nullable = false)
    private String securityQuestion;

    @NotBlank(message = "Security answer is required")
    @Column(nullable = false, length = 255)
    private String securityAnswer;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> userRoles = new HashSet<>();

    // ========== Helper Methods ==========

    /**
     * Add a role to this user
     */
    public void addRole(Roles role) {
        UserRole userRole = new UserRole();
        userRole.setUser(this);
        userRole.setRole(role);
        this.userRoles.add(userRole);
    }

    /**
     * Get all role names for this user
     */
    public Set<String> getRoleNames() {
        Set<String> roleNames = userRoles.stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toSet());
        
        System.out.println("📋 Roles for " + email + ": " + roleNames);
        return roleNames;
    }

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(String roleName) {
        return userRoles.stream()
                .anyMatch(ur -> ur.getRole().getRoleName().equals(roleName));
    }

    /**
     * Check if user is an admin
     */
    public boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("GAEL_HIMSELF");
    }

    /**
     * Remove all roles (useful for role updates)
     */
    public void clearRoles() {
        userRoles.clear();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + getRoleNames() +
                '}';
    }
}
package com.gaelcraves.project3.GaelCravings_Backend.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Column(name = "user_id")
    private Integer userId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank(message = "First name is required")
    @Column(length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(length = 100)
    private String lastName;

    @NotBlank(message = "Security question is required")
    @Column(nullable = false)
    private String securityQuestion;

    @NotBlank(message = "Security answer is required")
    @Column(nullable = false, length = 255)
    private String securityAnswer;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> userRoles = new HashSet<>();

    public void addRole(Roles role) {
        UserRole userRole = new UserRole();
        userRole.setUser(this);
        userRole.setRole(role);
        this.userRoles.add(userRole);
    }

    public Set<String> getRoleNames() {
        if (userRoles == null || userRoles.isEmpty()) {
            System.out.println("⚠️ No roles found for user: " + email);
            return Set.of();
        }

        Set<String> roleNames = userRoles.stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toSet());

        System.out.println("📋 Roles for " + email + ": " + roleNames);
        return roleNames;
    }
}
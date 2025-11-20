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
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment PK
    private Integer userId;

    @NotBlank
    @Setter
    @Getter
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Setter
    @Getter
    @Column(nullable = false, length = 100)
    private String lastName;

    @Email @NotBlank
    @Setter
    @Getter
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false, length = 255)
    private String password;

    @Getter
    @Setter
    @NotBlank
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

    @Getter
    @Setter
    @NotBlank
    @Column(nullable = false, length = 255)
    private String securityAnswer;

        Set<String> roleNames = userRoles.stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toSet());

        System.out.println("📋 Roles for " + email + ": " + roleNames);
        return roleNames;
    }
}
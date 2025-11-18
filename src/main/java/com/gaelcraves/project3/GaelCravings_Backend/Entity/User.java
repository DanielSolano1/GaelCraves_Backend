package com.gaelcraves.project3.GaelCravings_Backend.Entity;

import com.gaelcraves.project3.GaelCravings_Backend.Tools.StrongPassword;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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

    @Email
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @StrongPassword
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

    public void addRole(Roles role) {
        UserRole userRole = new UserRole();
        userRole.setUser(this);
        userRole.setRole(role);
        this.userRoles.add(userRole);
    }

    public Set<String> getRoleNames() {
        Set<String> roleNames = new HashSet<>();
        for (UserRole userRole : userRoles) {
            roleNames.add(userRole.getRole().getRoleName());
        }
        return roleNames;
    }
}
package com.gaelcraves.project3.GaelCravings_Backend.Entity;


import com.gaelcraves.project3.GaelCravings_Backend.Service.StrongPassword;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//Layer	        What it does	        When to add methods
//================================================================================
//Repository	Talks directly to DB	Only when you need a new query
//Service	    Business logic(calls repo)	When you need a new logical operation
//Controller	HTTP endpoint	        When you want to expose something via API

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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment PK
    private Integer userId;

    @Email @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank
    @StrongPassword
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String securityQuestion;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String securityAnswer;
}
package com.gaelcraves.project3.GaelCravings_Backend.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



//Layer	        What it does	        When to add methods
//================================================================================
//Repository	Talks directly to DB	Only when you need a new query
//Service	    Business logic(calls repo)	When you need a new logical operation
//Controller	HTTP endpoint	        When you want to expose something via API

@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment PK
    private Integer userId;

    @Email @NotBlank
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String securityQuestion;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String securityAnswer;

    public User() {}

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
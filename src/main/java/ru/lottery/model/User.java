package ru.lottery.model;

import ru.lottery.model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private UUID id;
    private String login;
    private String password;
    private UserRole role;
    private LocalDateTime createdAt;

    public User() {
        this.role = UserRole.USER;
        this.createdAt = LocalDateTime.now();
    }

    public User(String login, String password, UserRole role) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
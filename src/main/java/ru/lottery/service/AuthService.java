package ru.lottery.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lottery.dto.request.LoginRequest;
import ru.lottery.dto.request.RegisterRequest;
import ru.lottery.dto.response.AuthResponse;
import ru.lottery.model.User;
import ru.lottery.model.enums.UserRole;
import ru.lottery.repository.UserRepository;
import ru.lottery.security.PasswordEncoder;
import ru.lottery.security.SessionManager;

import java.sql.SQLException;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public AuthResponse register(RegisterRequest request) {
        try {
            // Validate input
            if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
                return AuthResponse.error("Login cannot be empty");
            }

            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return AuthResponse.error("Password must be at least 6 characters");
            }

            // Check if user already exists
            if (userRepository.existsByLogin(request.getLogin())) {
                return AuthResponse.error("User with this login already exists");
            }

            // Create new user
            User user = new User();
            user.setLogin(request.getLogin());
            user.setPassword(PasswordEncoder.encode(request.getPassword()));
            user.setRole(UserRole.USER); // Default role

            user = userRepository.save(user);

            // Create session
            String token = SessionManager.createSession(user.getId(), user.getRole().getValue());

            logger.info("User registered successfully: {}", user.getLogin());
            return AuthResponse.success(token, user.getId().toString(), user.getLogin(), user.getRole().getValue());

        } catch (SQLException e) {
            logger.error("Registration failed", e);
            return AuthResponse.error("Database error: " + e.getMessage());
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Find user by login
            User user = userRepository.findByLogin(request.getLogin()).orElse(null);

            if (user == null) {
                logger.warn("Login failed: user not found - {}", request.getLogin());
                return AuthResponse.error("Invalid login or password");
            }

            // Check password
            if (!PasswordEncoder.matches(request.getPassword(), user.getPassword())) {
                logger.warn("Login failed: invalid password for user - {}", request.getLogin());
                return AuthResponse.error("Invalid login or password");
            }

            // Create session
            String token = SessionManager.createSession(user.getId(), user.getRole().getValue());

            logger.info("User logged in successfully: {}", user.getLogin());
            return AuthResponse.success(token, user.getId().toString(), user.getLogin(), user.getRole().getValue());

        } catch (SQLException e) {
            logger.error("Login failed", e);
            return AuthResponse.error("Database error: " + e.getMessage());
        }
    }

    public boolean logout(String token) {
        if (token != null) {
            SessionManager.invalidateToken(token);
            logger.info("User logged out");
            return true;
        }
        return false;
    }
}
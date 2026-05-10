package ru.lottery.controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.lottery.dto.request.LoginRequest;
import ru.lottery.dto.request.RegisterRequest;
import ru.lottery.dto.response.AuthResponse;
import ru.lottery.service.AuthService;
import ru.lottery.util.ValidationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class AuthController implements HttpHandler {
    private final AuthService authService;
    private final Gson gson;

    public AuthController() {
        this.authService = new AuthService();
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("POST".equals(method)) {
                if (path.endsWith("/register")) {
                    handleRegister(exchange);
                } else if (path.endsWith("/login")) {
                    handleLogin(exchange);
                } else if (path.endsWith("/logout")) {
                    handleLogout(exchange);
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        RegisterRequest request = gson.fromJson(body, RegisterRequest.class);

        // Validate request
        if (!ValidationUtils.isValidLogin(request.getLogin())) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid login format\"}");
            return;
        }

        if (!ValidationUtils.isValidPassword(request.getPassword())) {
            sendResponse(exchange, 400, "{\"error\":\"Password must be at least 6 characters\"}");
            return;
        }

        AuthResponse response = authService.register(request);
        int statusCode = response.isSuccess() ? 201 : 400;
        sendResponse(exchange, statusCode, gson.toJson(response));
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        LoginRequest request = gson.fromJson(body, LoginRequest.class);

        if (request.getLogin() == null || request.getPassword() == null) {
            sendResponse(exchange, 400, "{\"error\":\"Login and password are required\"}");
            return;
        }

        AuthResponse response = authService.login(request);
        int statusCode = response.isSuccess() ? 200 : 401;
        sendResponse(exchange, statusCode, gson.toJson(response));
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null) {
            authService.logout(token);
            sendResponse(exchange, 200, "{\"message\":\"Logged out successfully\"}");
        } else {
            sendResponse(exchange, 400, "{\"error\":\"No token provided\"}");
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
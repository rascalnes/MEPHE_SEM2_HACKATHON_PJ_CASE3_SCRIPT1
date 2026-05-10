package ru.lottery.security;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import ru.lottery.model.enums.UserRole;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class AuthenticationFilter extends Filter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/register",
            "/auth/login",
            "/health"
    );

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // Allow public paths
        if (isPublicPath(path)) {
            chain.doFilter(exchange);
            return;
        }

        // Check authentication
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        String token = extractToken(authHeader);

        SessionManager.Session session = SessionManager.validateToken(token);
        if (session == null) {
            sendUnauthorized(exchange, "Missing or invalid authentication token");
            return;
        }

        // Store user info in exchange attributes
        exchange.setAttribute("userId", session.getUserId());
        exchange.setAttribute("userRole", session.getRole());

        // Check admin roles if needed
        if (requiresAdmin(path) && !UserRole.ADMIN.getValue().equals(session.getRole())) {
            sendForbidden(exchange, "Admin access required");
            return;
        }

        chain.doFilter(exchange);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean requiresAdmin(String path) {
        return path.startsWith("/draws") &&
                (path.contains("/start") || path.contains("/finish") ||
                        path.contains("/generate-result") || path.equals("/draws"));
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private void sendUnauthorized(HttpExchange exchange, String message) throws IOException {
        String response = "{\"error\":\"" + message + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(401, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }

    private void sendForbidden(HttpExchange exchange, String message) throws IOException {
        String response = "{\"error\":\"" + message + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(403, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }

    @Override
    public String description() {
        return "Authentication and Authorization Filter";
    }
}
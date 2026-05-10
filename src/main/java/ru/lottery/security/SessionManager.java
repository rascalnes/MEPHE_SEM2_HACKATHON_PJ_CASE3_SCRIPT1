package ru.lottery.security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final long SESSION_TIMEOUT_MS = 3600000; // 1 hour

    public static String createSession(UUID userId, String role) {
        String token = generateToken();
        Session session = new Session(userId, role, System.currentTimeMillis());
        sessions.put(token, session);
        cleanExpiredSessions();
        return token;
    }

    public static Session validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        Session session = sessions.get(token);
        if (session == null) {
            return null;
        }

        if (System.currentTimeMillis() - session.getCreatedAt() > SESSION_TIMEOUT_MS) {
            sessions.remove(token);
            return null;
        }

        return session;
    }

    public static void invalidateToken(String token) {
        sessions.remove(token);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }

    private static void cleanExpiredSessions() {
        long now = System.currentTimeMillis();
        sessions.entrySet().removeIf(entry ->
                now - entry.getValue().getCreatedAt() > SESSION_TIMEOUT_MS
        );
    }

    public static class Session {
        private final UUID userId;
        private final String role;
        private final long createdAt;

        public Session(UUID userId, String role, long createdAt) {
            this.userId = userId;
            this.role = role;
            this.createdAt = createdAt;
        }

        public UUID getUserId() { return userId; }
        public String getRole() { return role; }
        public long getCreatedAt() { return createdAt; }
    }
}
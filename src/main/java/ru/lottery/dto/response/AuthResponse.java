package ru.lottery.dto.response;

public class AuthResponse {
    private boolean success;
    private String token;
    private String message;
    private UserInfo user;

    public AuthResponse(boolean success, String token, String message, UserInfo user) {
        this.success = success;
        this.token = token;
        this.message = message;
        this.user = user;
    }

    public static AuthResponse success(String token, String userId, String login, String role) {
        return new AuthResponse(true, token, "Authentication successful",
                new UserInfo(userId, login, role));
    }

    public static AuthResponse error(String message) {
        return new AuthResponse(false, null, message, null);
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    public static class UserInfo {
        private String id;
        private String login;
        private String role;

        public UserInfo(String id, String login, String role) {
            this.id = id;
            this.login = login;
            this.role = role;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
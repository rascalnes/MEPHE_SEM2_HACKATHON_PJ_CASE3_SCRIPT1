package ru.lottery.util;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");

    public static boolean isValidLogin(String login) {
        return login != null && LOGIN_PATTERN.matcher(login).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidDrawName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }

    public static boolean isValidTicketNumbers(String numbers) {
        if (numbers == null || numbers.isEmpty()) return false;
        String[] parts = numbers.split(",");
        if (parts.length != 6) return false;

        try {
            for (String part : parts) {
                int num = Integer.parseInt(part.trim());
                if (num < 1 || num > 49) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
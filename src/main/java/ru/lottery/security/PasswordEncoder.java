package ru.lottery.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncoder {
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    // Simple but secure password hashing (can be replaced with BCrypt library if needed)
    public static String encode(String password) {
        try {
            byte[] salt = generateSalt();
            byte[] hash = hashPassword(password.toCharArray(), salt);

            // Store salt + hash together
            byte[] saltAndHash = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hash, 0, saltAndHash, salt.length, hash.length);

            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode password", e);
        }
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            byte[] saltAndHash = Base64.getDecoder().decode(encodedPassword);
            byte[] salt = new byte[SALT_LENGTH];
            byte[] storedHash = new byte[saltAndHash.length - SALT_LENGTH];

            System.arraycopy(saltAndHash, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(saltAndHash, SALT_LENGTH, storedHash, 0, storedHash.length);

            byte[] computedHash = hashPassword(rawPassword.toCharArray(), salt);

            return MessageDigest.isEqual(computedHash, storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] hashPassword(char[] password, byte[] salt) throws Exception {
        javax.crypto.SecretKeyFactory skf = javax.crypto.SecretKeyFactory.getInstance(ALGORITHM);
        javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        javax.crypto.SecretKey key = skf.generateSecret(spec);
        return key.getEncoded();
    }
}
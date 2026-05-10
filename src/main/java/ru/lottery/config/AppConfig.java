package ru.lottery.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static Dotenv dotenv;

    static {
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            logger.info("Configuration loaded successfully");
        } catch (Exception e) {
            logger.warn("No .env file found, using system environment variables");
            dotenv = null;
        }
    }

    private static String getEnv(String key, String defaultValue) {
        String value = dotenv != null ? dotenv.get(key) : null;
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return value != null && !value.isEmpty() ? value : defaultValue;
    }

    // Database configuration
    public static String getDbHost() { return getEnv("DB_HOST", "localhost"); }
    public static int getDbPort() { return Integer.parseInt(getEnv("DB_PORT", "5432")); }
    public static String getDbName() { return getEnv("DB_NAME", "lottery_db"); }
    public static String getDbUser() { return getEnv("DB_USER", "lottery_user"); }
    public static String getDbPassword() { return getEnv("DB_PASSWORD", "lottery_pass"); }

    public static String getJdbcUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s",
                getDbHost(), getDbPort(), getDbName());
    }

    // Connection pool configuration
    public static int getPoolMaxSize() {
        return Integer.parseInt(getEnv("POOL_MAX_SIZE", "10"));
    }

    public static int getPoolMinIdle() {
        return Integer.parseInt(getEnv("POOL_MIN_IDLE", "2"));
    }

    public static long getPoolIdleTimeout() {
        return Long.parseLong(getEnv("POOL_IDLE_TIMEOUT", "300000"));
    }

    public static long getPoolConnectionTimeout() {
        return Long.parseLong(getEnv("POOL_CONNECTION_TIMEOUT", "30000"));
    }

    // Application configuration
    public static int getAppPort() {
        return Integer.parseInt(getEnv("APP_PORT", "8080"));
    }

    public static String getAppEnv() {
        return getEnv("APP_ENV", "development");
    }

    public static void initializeDatabasePool() {
        DatabaseConnection.initialize(
                getJdbcUrl(),
                getDbUser(),
                getDbPassword(),
                getPoolMaxSize(),
                getPoolMinIdle(),
                getPoolIdleTimeout(),
                getPoolConnectionTimeout()
        );
    }

    public static void shutdown() {
        DatabaseConnection.closePool();
    }
}
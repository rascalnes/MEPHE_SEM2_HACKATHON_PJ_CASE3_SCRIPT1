package ru.lottery;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lottery.config.AppConfig;
import ru.lottery.controller.AuthController;
import ru.lottery.security.AuthenticationFilter;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static HttpServer server;

    public static void main(String[] args) {
        try {
            // Initialize configuration and database
            AppConfig.initializeDatabasePool();
            logger.info("Application started in {} mode", AppConfig.getAppEnv());

            // Test database connection
            testDatabaseConnection();

            // Create HTTP server
            int port = AppConfig.getAppPort();
            server = HttpServer.create(new InetSocketAddress(port), 0);

            // Add authentication filter
            server.createContext("/", (HttpHandler) new AuthenticationFilter());

            // Register controllers
            server.createContext("/auth", new AuthController());

            // Health check endpoint (public)
            server.createContext("/health", exchange -> {
                String response = "{\"status\":\"ok\",\"env\":\"" + AppConfig.getAppEnv() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
            });

            server.setExecutor(null);
            server.start();

            logger.info("=== Lottery Backend System Started ===");
            logger.info("HTTP server listening on port: {}", port);
            logger.info("Health check: http://localhost:{}/health", port);
            logger.info("Auth endpoints:");
            logger.info("  POST http://localhost:{}/auth/register - Register new user", port);
            logger.info("  POST http://localhost:{}/auth/login    - Login user", port);
            logger.info("  POST http://localhost:{}/auth/logout   - Logout user", port);

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down application...");
                if (server != null) {
                    server.stop(0);
                }
                AppConfig.shutdown();
                logger.info("Application stopped");
            }));

        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }

    private static void testDatabaseConnection() {
        try {
            boolean connected = ru.lottery.config.DatabaseConnection.isInitialized();
            if (connected) {
                logger.info("Database connection test: SUCCESS");
            } else {
                logger.warn("Database connection test: FAILED");
            }
        } catch (Exception e) {
            logger.error("Database connection test: ERROR - {}", e.getMessage());
        }
    }
}
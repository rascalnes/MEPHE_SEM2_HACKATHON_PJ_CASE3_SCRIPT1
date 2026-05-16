package ru.lottery;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lottery.config.AppConfig;
import ru.lottery.controller.AuthController;
import ru.lottery.controller.DrawController;
import ru.lottery.controller.TicketController;
import ru.lottery.security.AuthenticationFilter;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static HttpServer server;

    public static void main(String[] args) {
        try {
            AppConfig.initializeDatabasePool();
            logger.info("Application started in {} mode", AppConfig.getAppEnv());
            testDatabaseConnection();

            int port = AppConfig.getAppPort();
            server = HttpServer.create(new InetSocketAddress(port), 0);

            AuthenticationFilter authFilter = new AuthenticationFilter();

            registerContext("/auth", new AuthController(), authFilter);
            registerContext("/tickets", new TicketController(), authFilter);
            registerContext("/draws", new DrawController(), authFilter);

            server.createContext("/health", exchange -> {
                String response = "{\"status\":\"ok\",\"env\":\"" + AppConfig.getAppEnv() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
            });

            server.setExecutor(null);
            server.start();

            server.setExecutor(null);
            server.start();

            logger.info("=== Lottery Backend System Started ===");
            logger.info("HTTP server listening on port: {}", port);
            logger.info("Health check: http://localhost:{}/health", port);
            logger.info("\n=== Available Endpoints ===");
            logger.info("\n--- Authentication (Public) ---");
            logger.info("  POST   /auth/register");
            logger.info("  POST   /auth/login");
            logger.info("  POST   /auth/logout");
            logger.info("\n--- Draws (Authenticated) ---");
            logger.info("  GET    /draws");
            logger.info("  GET    /draws/active");
            logger.info("  GET    /draws/{id}");
            logger.info("  POST   /draws (ADMIN)");
            logger.info("  POST   /draws/{id}/start (ADMIN)");
            logger.info("  POST   /draws/{id}/finish (ADMIN)");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down...");
                if (server != null) server.stop(0);
                AppConfig.shutdown();
            }));

        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }

    private static void registerContext(String path, HttpHandler handler, AuthenticationFilter filter) {
        var context = server.createContext(path, handler);
        context.getFilters().add(filter);
        logger.debug("Registered context: {}", path);
    }

    private static void testDatabaseConnection() {
        try {
            boolean connected = ru.lottery.config.DatabaseConnection.isInitialized();
            if (connected) logger.info("Database connection test: SUCCESS");
            else logger.warn("Database connection test: FAILED");
        } catch (Exception e) {
            logger.error("Database connection test: ERROR - {}", e.getMessage());
        }
    }
}

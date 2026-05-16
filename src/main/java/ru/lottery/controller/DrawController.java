package ru.lottery.controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.lottery.dto.request.CreateDrawRequest;
import ru.lottery.dto.response.DrawResponse;
import ru.lottery.service.DrawService;
import ru.lottery.util.GsonUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DrawController implements HttpHandler {
    private final DrawService drawService;
    private final Gson gson;

    public DrawController() {
        this.drawService = new DrawService();
        this.gson = GsonUtil.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Get authenticated user info from filter
        UUID userId = (UUID) exchange.getAttribute("userId");
        String userRole = (String) exchange.getAttribute("userRole");

        try {
            if ("GET".equals(method)) {
                if (path.equals("/draws")) {
                    handleGetAllDraws(exchange);
                } else if (path.equals("/draws/active")) {
                    handleGetActiveDraws(exchange);
                } else if (path.matches("/draws/\\d+")) {
                    Long drawId = extractDrawId(path);
                    if (drawId != null) {
                        handleGetDrawById(exchange, drawId);
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"Invalid draw ID\"}");
                    }
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
                }
            } else if ("POST".equals(method)) {
                if (path.equals("/draws")) {
                    // Only ADMIN can create draws
                    if (!"ADMIN".equals(userRole)) {
                        sendResponse(exchange, 403, "{\"error\":\"Admin access required\"}");
                        return;
                    }
                    handleCreateDraw(exchange, userId);
                } else if (path.matches("/draws/\\d+/start")) {
                    if (!"ADMIN".equals(userRole)) {
                        sendResponse(exchange, 403, "{\"error\":\"Admin access required\"}");
                        return;
                    }
                    Long drawId = extractDrawId(path);
                    if (drawId != null) {
                        handleStartDraw(exchange, drawId, userId);
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"Invalid draw ID\"}");
                    }
                } else if (path.matches("/draws/\\d+/finish")) {
                    if (!"ADMIN".equals(userRole)) {
                        sendResponse(exchange, 403, "{\"error\":\"Admin access required\"}");
                        return;
                    }
                    Long drawId = extractDrawId(path);
                    if (drawId != null) {
                        handleFinishDraw(exchange, drawId, userId);
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"Invalid draw ID\"}");
                    }
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

    private void handleCreateDraw(HttpExchange exchange, UUID adminId) throws IOException {
        String body = readRequestBody(exchange);
        CreateDrawRequest request = gson.fromJson(body, CreateDrawRequest.class);

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"Draw name is required\"}");
            return;
        }

        DrawResponse response = drawService.createDraw(request.getName(), adminId);
        int statusCode = response.isSuccess() ? 201 : 400;
        sendResponse(exchange, statusCode, gson.toJson(response));
    }

    private void handleStartDraw(HttpExchange exchange, Long drawId, UUID adminId) throws IOException {
        DrawResponse response = drawService.startDraw(drawId, adminId);
        int statusCode = response.isSuccess() ? 200 : 400;
        sendResponse(exchange, statusCode, gson.toJson(response));
    }

    private void handleFinishDraw(HttpExchange exchange, Long drawId, UUID adminId) throws IOException {
        DrawResponse response = drawService.finishDraw(drawId, adminId);
        int statusCode = response.isSuccess() ? 200 : 400;
        sendResponse(exchange, statusCode, gson.toJson(response));
    }

    private void handleGetActiveDraws(HttpExchange exchange) throws IOException {
        List<DrawResponse> draws = drawService.getActiveDraws();
        String response = gson.toJson(draws);
        sendResponse(exchange, 200, response);
    }

    private void handleGetAllDraws(HttpExchange exchange) throws IOException {
        List<DrawResponse> draws = drawService.getAllDraws();
        String response = gson.toJson(draws);
        sendResponse(exchange, 200, response);
    }

    private void handleGetDrawById(HttpExchange exchange, Long drawId) throws IOException {
        DrawResponse response = drawService.getDrawById(drawId);
        int statusCode = response.isSuccess() ? 200 : 404;
        sendResponse(exchange, statusCode, gson.toJson(response));
    }

    private Long extractDrawId(String path) {
        String[] parts = path.split("/");
        for (String part : parts) {
            if (part.matches("\\d+")) {
                return Long.parseLong(part);
            }
        }
        return null;
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
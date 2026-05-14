package ru.lottery.controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.lottery.dto.request.BuyTicketRequest;
import ru.lottery.dto.response.TicketResponse;
import ru.lottery.model.DrawResult;
import ru.lottery.service.ResultService;
import ru.lottery.service.TicketService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class DrawController implements HttpHandler {
    private final TicketService ticketService = new TicketService();
    private final ResultService resultService = new ResultService();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        UUID userId = (UUID) exchange.getAttribute("userId");
        if (userId == null) {
            sendError(exchange, 401, "Не авторизован");
            return;
        }

        try {
            if ("POST".equals(method) && path.matches("/draws/\\d+/tickets")) {
                handlePurchase(exchange, path, userId);
            } else if ("POST".equals(method) && path.matches("/draws/\\d+/finish")) {
                handleFinishDraw(exchange, path);
            } else if ("GET".equals(method) && path.matches("/draws/\\d+/results")) {
                handleGetDrawResult(exchange, path);
            } else {
                sendError(exchange, 404, "Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
        }
    }

    private void handlePurchase(HttpExchange exchange, String path, UUID userId) throws IOException {
        String[] parts = path.split("/");
        Long drawId = Long.parseLong(parts[2]);
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        BuyTicketRequest request = gson.fromJson(body, BuyTicketRequest.class);
        if (request == null) request = new BuyTicketRequest();
        try {
            TicketResponse response = ticketService.purchaseTicket(drawId, userId, request);
            sendJson(exchange, 201, gson.toJson(response));
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleFinishDraw(HttpExchange exchange, String path) throws IOException {
        Long drawId = Long.parseLong(path.split("/")[2]);
        try {
            DrawResult result = resultService.finishDraw(drawId);
            sendJson(exchange, 200, gson.toJson(result));
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleGetDrawResult(HttpExchange exchange, String path) throws IOException {
        Long drawId = Long.parseLong(path.split("/")[2]);
        try {
            Optional<DrawResult> opt = resultService.getDrawResult(drawId);
            if (opt.isPresent()) {
                sendJson(exchange, 200, gson.toJson(opt.get()));
            } else {
                sendError(exchange, 404, "Результаты не найдены");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error");
        }
    }

    private void sendJson(HttpExchange exchange, int code, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        String json = "{\"error\":\"" + message + "\"}";
        sendJson(exchange, code, json);
    }
}

package ru.lottery.controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.lottery.dto.request.BuyTicketRequest;
import ru.lottery.dto.response.TicketResponse;
import ru.lottery.service.TicketService;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class TicketController implements HttpHandler {
    private final TicketService ticketService = new TicketService();
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            URI uri = exchange.getRequestURI();
            String path = uri.getPath();

            UUID userId = (UUID) exchange.getAttribute("userId");
            if (userId == null) {
                sendError(exchange, 401, "Не авторизован");
                return;
            }

            if ("POST".equals(method) && path.matches("/draws/\\d+/tickets")) {
                handlePurchase(exchange, path, userId);
            } else if ("GET".equals(method) && "/tickets".equals(path)) {
                handleGetTickets(exchange, userId);
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
        if (request == null) {
            request = new BuyTicketRequest();
        }
        try {
            TicketResponse response = ticketService.purchaseTicket(drawId, userId, request);
            sendJson(exchange, 201, gson.toJson(response));
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleGetTickets(HttpExchange exchange, UUID userId) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Long drawId = null;
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "drawId".equals(pair[0])) {
                    drawId = Long.parseLong(pair[1]);
                }
            }
        }
        try {
            List<TicketResponse> tickets = ticketService.getUserTickets(userId, drawId);
            sendJson(exchange, 200, gson.toJson(tickets));
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(exchange, 500, "Database error: " + e.getMessage());
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

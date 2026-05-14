package ru.lottery.controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.lottery.dto.response.TicketResponse;
import ru.lottery.model.Ticket;
import ru.lottery.service.ResultService;
import ru.lottery.service.TicketService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TicketController implements HttpHandler {
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
            if ("GET".equals(method) && path.matches("/tickets/\\d+/status")) {
                handleGetTicketStatus(exchange, path);
            } else if ("GET".equals(method) && path.equals("/tickets")) {
                handleGetTickets(exchange, userId);
            } else {
                sendError(exchange, 404, "Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
        }
    }

    private void handleGetTicketStatus(HttpExchange exchange, String path) throws IOException {
        Long ticketId = Long.parseLong(path.split("/")[2]);
        try {
            Optional<Ticket> opt = resultService.getTicketById(ticketId);
            if (opt.isPresent()) {
                Ticket t = opt.get();
                TicketResponse resp = new TicketResponse();
                resp.setId(t.getId());
                resp.setDrawId(t.getDrawId());
                resp.setNumbers(t.getNumbers());
                resp.setStatus(t.getStatus().name());
                resp.setPurchasedAt(t.getPurchasedAt());
                sendJson(exchange, 200, gson.toJson(resp));
            } else {
                sendError(exchange, 404, "Билет не найден");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error");
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
        } catch (Exception e) {
            sendError(exchange, 500, "Ошибка получения билетов");
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

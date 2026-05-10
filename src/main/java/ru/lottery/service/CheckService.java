package ru.lottery.service;

import ru.lottery.model.Ticket;
import ru.lottery.model.DrawResult;

import java.util.*;

public class CheckService {

    private final Map<Long, Ticket> tickets = new HashMap<>();
    private final Map<Long, DrawResult> results = new HashMap<>();

    public void addTicket(Ticket ticket) {
        tickets.put(ticket.getId(), ticket);
    }

    public void addDrawResult(DrawResult result) {
        results.put(result.getDrawId(), result);
    }

    public String checkSingleTicket(Long ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null || !"PENDING".equals(ticket.getStatus())) {
            return "Билет не найден или уже проверен";
        }

        DrawResult result = results.get(ticket.getDrawId());
        if (result == null) {
            return "Результат тиража ещё не сгенерирован";
        }

        boolean isWin = checkWin(ticket.getNumbers(), result.getWinningNumbers());
        String newStatus = isWin ? "WIN" : "LOSE";
        ticket.setStatus(newStatus);

        return newStatus;
    }

    public void checkAllTicketsForDraw(Long drawId) {
        List<Ticket> ticketsForDraw = new ArrayList<>();
        for (Ticket ticket : tickets.values()) {
            if (ticket.getDrawId().equals(drawId)) {
                ticketsForDraw.add(ticket);
            }
        }

        DrawResult result = results.get(drawId);
        if (result == null) return;

        for (Ticket ticket : ticketsForDraw) {
            if (!"PENDING".equals(ticket.getStatus())) continue;
            boolean isWin = checkWin(ticket.getNumbers(), result.getWinningNumbers());
            ticket.setStatus(isWin ? "WIN" : "LOSE");
        }
    }

    public String getTicketStatus(Long ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) return "Билет не найден";
        return ticket.getStatus();
    }

    public String getDrawResults(Long drawId) {
        DrawResult result = results.get(drawId);
        if (result == null) return "Результаты тиража ещё не опубликованы";
        return "Выигрышная комбинация: " + result.getWinningNumbers();
    }

    private boolean checkWin(String ticketNumbers, String winningNumbers) {
        if (ticketNumbers == null || winningNumbers == null) return false;
        Set<String> ticketSet = new HashSet<>(Arrays.asList(ticketNumbers.split(",")));
        Set<String> winSet = new HashSet<>(Arrays.asList(winningNumbers.split(",")));
        long matchCount = ticketSet.stream().filter(winSet::contains).count();
        return matchCount >= 3;
    }
}
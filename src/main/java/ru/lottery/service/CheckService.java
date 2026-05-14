package ru.lottery.service;

import ru.lottery.model.Ticket;
import ru.lottery.model.DrawResult;
import ru.lottery.repository.TicketRepository;
import ru.lottery.repository.DrawResultRepository;
import java.util.*;

public class CheckService {

    private final TicketRepository ticketRepository;
    private final DrawResultRepository drawResultRepository;

    public CheckService(TicketRepository ticketRepository, DrawResultRepository drawResultRepository) {
        this.ticketRepository = ticketRepository;
        this.drawResultRepository = drawResultRepository;
    }

    public String checkSingleTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null || !"PENDING".equals(ticket.getStatus())) {
            return "Билет не найден или уже проверен";
        }
        DrawResult result = drawResultRepository.findByDrawId(ticket.getDrawId());
        if (result == null) return "Результат тиража ещё не сгенерирован";
        boolean isWin = checkWin(ticket.getNumbers(), result.getWinningNumbers());
        String newStatus = isWin ? "WIN" : "LOSE";
        ticket.setStatus(newStatus);
        ticketRepository.update(ticket);
        return newStatus;
    }

    public void checkAllTicketsForDraw(Long drawId) {
        List<Ticket> tickets = ticketRepository.findByDrawId(drawId);
        DrawResult result = drawResultRepository.findByDrawId(drawId);
        if (result == null) return;
        for (Ticket ticket : tickets) {
            if (!"PENDING".equals(ticket.getStatus())) continue;
            boolean isWin = checkWin(ticket.getNumbers(), result.getWinningNumbers());
            ticket.setStatus(isWin ? "WIN" : "LOSE");
            ticketRepository.update(ticket);
        }
    }

    public String getTicketStatus(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId);
        return ticket == null ? "Билет не найден" : ticket.getStatus();
    }

    private boolean checkWin(String ticketNumbers, String winningNumbers) {
        if (ticketNumbers == null || winningNumbers == null) return false;
        Set<String> ticketSet = new HashSet<>(Arrays.asList(ticketNumbers.split(",")));
        Set<String> winSet = new HashSet<>(Arrays.asList(winningNumbers.split(",")));
        long matchCount = ticketSet.stream().filter(winSet::contains).count();
        return matchCount >= 3;
    }
}
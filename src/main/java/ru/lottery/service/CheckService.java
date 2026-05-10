package ru.lottery.service;

import ru.lottery.model.Ticket;
import ru.lottery.model.DrawResult;
import ru.lottery.repository.TicketRepository;
import ru.lottery.repository.DrawResultRepository;

import java.util.*;

public class CheckService {

    private final TicketRepository ticketRepository;
    private final DrawResultRepository drawResultRepository;

    // Конструктор (внедрение зависимостей)
    public CheckService(TicketRepository ticketRepository, DrawResultRepository drawResultRepository) {
        this.ticketRepository = ticketRepository;
        this.drawResultRepository = drawResultRepository;
    }

    // Проверка одного билета
    public String checkSingleTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null || !"PENDING".equals(ticket.getStatus())) {
            return "Билет не найден или уже проверен";
        }

        DrawResult result = drawResultRepository.findByDrawId(ticket.getDrawId());
        if (result == null) {
            return "Результат тиража ещё не сгенерирован";
        }

        boolean isWin = checkWin(ticket.getNumbers(), result.getWinningNumbers());
        String newStatus = isWin ? "WIN" : "LOSE";
        ticket.setStatus(newStatus);
        ticketRepository.update(ticket);

        return newStatus;
    }

    // Массовая проверка всех билетов тиража
    public void checkAllTicketsForDraw(Long drawId) {
        List<Ticket> tickets = ticketRepository.findByDrawId(drawId);
        DrawResult result = drawResultRepository.findByDrawId(drawId);

        if (result == null) {
            System.out.println("Результат тиража #" + drawId + " не найден");
            return;
        }

        int winCount = 0;
        for (Ticket ticket : tickets) {
            if (!"PENDING".equals(ticket.getStatus())) continue;

            boolean isWin = checkWin(ticket.getNumbers(), result.getWinningNumbers());
            ticket.setStatus(isWin ? "WIN" : "LOSE");
            ticketRepository.update(ticket);
            if (isWin) winCount++;
        }

        System.out.println("Проверка тиража #" + drawId + " завершена. Выигрышных билетов: " + winCount);
    }

    // Получить статус билета
    public String getTicketStatus(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) return "Билет не найден";
        return ticket.getStatus();
    }

    // Получить результаты тиража
    public String getDrawResults(Long drawId) {
        DrawResult result = drawResultRepository.findByDrawId(drawId);
        if (result == null) return "Результаты тиража ещё не опубликованы";
        return "Выигрышная комбинация: " + result.getWinningNumbers();
    }

    // Логика проверки выигрыша
    private boolean checkWin(String ticketNumbers, String winningNumbers) {
        if (ticketNumbers == null || winningNumbers == null) return false;

        Set<String> ticketSet = new HashSet<>(Arrays.asList(ticketNumbers.split(",")));
        Set<String> winSet = new HashSet<>(Arrays.asList(winningNumbers.split(",")));

        // Считаем количество совпадений
        long matchCount = 0;
        for (String num : ticketSet) {
            if (winSet.contains(num)) {
                matchCount++;
            }
        }

        return matchCount >= 3;  // Выигрыш при 3+ совпадениях
    }
}
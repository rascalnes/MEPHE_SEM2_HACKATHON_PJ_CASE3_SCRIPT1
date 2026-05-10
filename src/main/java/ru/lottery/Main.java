package ru.lottery;

import ru.lottery.model.*;
import ru.lottery.repository.*;
import ru.lottery.service.CheckService;

public class Main {
    public static void main(String[] args) {
        // Создаём репозитории и сервис
        TicketRepository ticketRepo = new TicketRepository();
        DrawResultRepository resultRepo = new DrawResultRepository();
        CheckService checkService = new CheckService(ticketRepo, resultRepo);

        // Создаём тестовый билет
        Ticket ticket = new Ticket();
        ticket.setDrawId(1L);
        ticket.setUserId(1L);
        ticket.setNumbers("5,12,23,34,45");
        ticket.setStatus("PENDING");
        ticketRepo.save(ticket);

        System.out.println("Билет создан. ID: " + ticket.getId());
        System.out.println("Статус билета: " + checkService.getTicketStatus(ticket.getId()));

        // Создаём выигрышную комбинацию
        DrawResult result = new DrawResult();
        result.setDrawId(1L);
        result.setWinningNumbers("5,12,23,33,44");
        resultRepo.save(result);

        System.out.println("Результат тиража сохранён");

        // Проверяем билет
        System.out.println("Результат проверки: " + checkService.checkSingleTicket(ticket.getId()));
        System.out.println("Новый статус: " + checkService.getTicketStatus(ticket.getId()));
    }
}
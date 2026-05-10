package ru.lottery;

import ru.lottery.model.Ticket;
import ru.lottery.model.DrawResult;
import ru.lottery.repository.TicketRepository;
import ru.lottery.repository.DrawResultRepository;
import ru.lottery.service.CheckService;
import ru.lottery.config.AppConfig;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("Запуск теста...");

        // Инициализация БД
        AppConfig.initializeDatabasePool();

        // Создаём репозитории и сервис
        TicketRepository ticketRepo = new TicketRepository();
        DrawResultRepository resultRepo = new DrawResultRepository();
        CheckService checkService = new CheckService(ticketRepo, resultRepo);

        // 1. Создаём билет
        Ticket ticket = new Ticket();
        ticket.setDrawId(1L);
        ticket.setUserId(1L);
        ticket.setNumbers("5,12,23,34,45");
        ticket.setStatus("PENDING");
        ticketRepo.save(ticket);
        System.out.println("✅ Билет создан. ID: " + ticket.getId());

        // 2. Проверяем статус
        System.out.println("📌 Статус билета: " + checkService.getTicketStatus(ticket.getId()));

        // 3. Создаём результат тиража
        DrawResult result = new DrawResult();
        result.setDrawId(1L);
        result.setWinningNumbers("5,12,23,33,44");
        resultRepo.save(result);
        System.out.println("✅ Результат тиража сохранён");

        // 4. Проверяем билет
        String resultCheck = checkService.checkSingleTicket(ticket.getId());
        System.out.println("🔍 Результат проверки: " + resultCheck);
        System.out.println("📌 Новый статус: " + checkService.getTicketStatus(ticket.getId()));

        // 5. Закрываем соединение
        AppConfig.shutdown();
    }
}
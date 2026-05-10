package ru.lottery.controller;

import ru.lottery.service.CheckService;

public class CheckController {
    private final CheckService checkService;

    public CheckController(CheckService checkService) {
        this.checkService = checkService;
    }

    // Проверить статус билета
    public String getTicketStatus(Long ticketId) {
        return checkService.getTicketStatus(ticketId);
    }

    // Проверить один билет (изменить статус)
    public String checkTicket(Long ticketId) {
        return checkService.checkSingleTicket(ticketId);
    }

    // Запустить массовую проверку тиража
    public String checkAllTickets(Long drawId) {
        checkService.checkAllTicketsForDraw(drawId);
        return "Массовая проверка тиража #" + drawId + " запущена";
    }

    // Получить результаты тиража
    public String getDrawResults(Long drawId) {
        return checkService.getDrawResults(drawId);
    }
}
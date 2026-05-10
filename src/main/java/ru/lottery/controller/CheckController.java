package ru.lottery.controller;

import ru.lottery.service.CheckService;

public class CheckController {
    private final CheckService checkService;

    public CheckController(CheckService checkService) {
        this.checkService = checkService;
    }

    public String checkTicket(Long ticketId) {
        return checkService.checkSingleTicket(ticketId);
    }

    public String checkAllTickets(Long drawId) {
        checkService.checkAllTicketsForDraw(drawId);
        return "Массовая проверка тиража #" + drawId + " выполнена";
    }

    public String getTicketStatus(Long ticketId) {
        return checkService.getTicketStatus(ticketId);
    }
}
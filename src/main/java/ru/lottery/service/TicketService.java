package ru.lottery.service;

import ru.lottery.model.Draw;
import ru.lottery.model.Ticket;
import ru.lottery.model.enums.DrawStatus;
import ru.lottery.model.enums.TicketStatus;
import ru.lottery.repository.DrawRepository;
import ru.lottery.repository.TicketRepository;
import ru.lottery.util.CombinationGenerator;
import ru.lottery.util.ValidationUtils;
import ru.lottery.dto.request.BuyTicketRequest;
import ru.lottery.dto.response.TicketResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketService {
    private final TicketRepository ticketRepo = new TicketRepository();
    private final DrawRepository drawRepo = new DrawRepository();

    public TicketResponse purchaseTicket(Long drawId, UUID userId, BuyTicketRequest request)
            throws SQLException, IllegalArgumentException {
        // Проверить тираж
        Draw draw = drawRepo.findById(drawId)
                   .orElseThrow(() -> new IllegalArgumentException("Тираж не найден"));
        if (draw.getStatus() != DrawStatus.ACTIVE) {
            throw new IllegalArgumentException("Билеты можно покупать только в активном тираже");
        }

        // Комбинация
        String numbers;
        if (request.getNumbers() != null && !request.getNumbers().isBlank()) {
            numbers = request.getNumbers().replace(" ", "");
            if (!ValidationUtils.isValidTicketNumbers(numbers)) {
                throw new IllegalArgumentException("Некорректная комбинация чисел");
            }
        } else {
            numbers = CombinationGenerator.generate();
        }

        // Создать и сохранить билет
        Ticket ticket = new Ticket(drawId, userId, numbers, TicketStatus.PENDING);
        ticket = ticketRepo.save(ticket);

        // Ответ
        return mapToResponse(ticket);
    }

    public List<TicketResponse> getUserTickets(UUID userId, Long drawId) throws SQLException {
        List<Ticket> tickets;
        if (drawId != null) {
            tickets = ticketRepo.findByUserIdAndDrawId(userId, drawId);
        } else {
            tickets = ticketRepo.findByUserId(userId);
        }
        return tickets.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse r = new TicketResponse();
        r.setId(ticket.getId());
        r.setDrawId(ticket.getDrawId());
        r.setNumbers(ticket.getNumbers());
        r.setStatus(ticket.getStatus().name());
        r.setPurchasedAt(ticket.getPurchasedAt());
        return r;
    }
}

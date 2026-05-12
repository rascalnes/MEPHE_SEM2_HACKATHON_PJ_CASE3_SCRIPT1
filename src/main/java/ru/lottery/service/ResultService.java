package ru.lottery.service;

import ru.lottery.model.Draw;
import ru.lottery.model.DrawResult;
import ru.lottery.model.Ticket;
import ru.lottery.model.enums.DrawStatus;
import ru.lottery.model.enums.TicketStatus;
import ru.lottery.repository.DrawRepository;
import ru.lottery.repository.DrawResultRepository;
import ru.lottery.repository.TicketRepository;
import ru.lottery.util.CombinationGenerator;
import ru.lottery.util.TicketChecker;

import java.sql.SQLException;
import java.util.Optional;

public class ResultService {
    private final DrawRepository drawRepo = new DrawRepository();
    private final DrawResultRepository drawResultRepo = new DrawResultRepository();
    private final TicketRepository ticketRepo = new TicketRepository();

    public DrawResult finishDraw(Long drawId) throws SQLException, IllegalArgumentException {
        Draw draw = drawRepo.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("Тираж не найден"));
        if (draw.getStatus() != DrawStatus.ACTIVE) {
            throw new IllegalArgumentException("Завершить можно только активный тираж");
        }

        String winningCombo = CombinationGenerator.generate();
        DrawResult result = drawResultRepo.save(new DrawResult(drawId, winningCombo));

        var tickets = ticketRepo.findByDrawId(drawId);
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.PENDING) {
                boolean win = TicketChecker.isWinning(ticket.getNumbers(), winningCombo);
                ticketRepo.updateStatus(ticket.getId(), win ? TicketStatus.WIN : TicketStatus.LOSE);
            }
        }

        drawRepo.updateStatus(drawId, DrawStatus.FINISHED);
        return result;
    }

    public Optional<DrawResult> getDrawResult(Long drawId) throws SQLException {
        return drawResultRepo.findByDrawId(drawId);
    }

    public Optional<Ticket> getTicketById(Long ticketId) throws SQLException {
        return ticketRepo.findById(ticketId);
    }
}

package ru.lottery.model;

import ru.lottery.model.enums.TicketStatus;
import java.sql.Timestamp;
import java.util.UUID;

public class Ticket {
    private Long id;
    private Long drawId;
    private UUID userId;
    private String numbers;
    private TicketStatus status;
    private Timestamp checkedAt;
    private Timestamp purchasedAt;

    public Ticket() {}

    public Ticket(Long drawId, UUID userId, String numbers, TicketStatus status) {
        this.drawId = drawId;
        this.userId = userId;
        this.numbers = numbers;
        this.status = status;
        this.purchasedAt = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDrawId() { return drawId; }
    public void setDrawId(Long drawId) { this.drawId = drawId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getNumbers() { return numbers; }
    public void setNumbers(String numbers) { this.numbers = numbers; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public Timestamp getCheckedAt() { return checkedAt; }
    public void setCheckedAt(Timestamp checkedAt) { this.checkedAt = checkedAt; }
    public Timestamp getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(Timestamp purchasedAt) { this.purchasedAt = purchasedAt; }
}

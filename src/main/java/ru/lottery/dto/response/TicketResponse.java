package ru.lottery.dto.response;

import java.sql.Timestamp;

public class TicketResponse {
    private Long id;
    private Long drawId;
    private String numbers;
    private String status;
    private Timestamp purchasedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDrawId() { return drawId; }
    public void setDrawId(Long drawId) { this.drawId = drawId; }
    public String getNumbers() { return numbers; }
    public void setNumbers(String numbers) { this.numbers = numbers; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(Timestamp purchasedAt) { this.purchasedAt = purchasedAt; }
}

package ru.lottery.model;

public class Ticket {
    private Long id;
    private Long drawId;
    private Long userId;
    private String numbers;
    private String status;

    public Ticket() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDrawId() { return drawId; }
    public void setDrawId(Long drawId) { this.drawId = drawId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNumbers() { return numbers; }
    public void setNumbers(String numbers) { this.numbers = numbers; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
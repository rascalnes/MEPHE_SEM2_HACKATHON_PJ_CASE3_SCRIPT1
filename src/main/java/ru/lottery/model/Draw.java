package ru.lottery.model;

import java.time.LocalDateTime;

public class Draw {
    private Long id;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;

    public Draw() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getFinishTime() { return finishTime; }
    public void setFinishTime(LocalDateTime finishTime) { this.finishTime = finishTime; }
}
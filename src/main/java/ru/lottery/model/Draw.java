package ru.lottery.model;

import ru.lottery.model.enums.DrawStatus;
import java.sql.Timestamp;
import java.util.UUID;

public class Draw {
    private Long id;
    private String name;
    private DrawStatus status;
    private UUID createdBy;
    private Timestamp createdAt;
    private Timestamp startedAt;
    private Timestamp finishedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public DrawStatus getStatus() { return status; }
    public void setStatus(DrawStatus status) { this.status = status; }
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getStartedAt() { return startedAt; }
    public void setStartedAt(Timestamp startedAt) { this.startedAt = startedAt; }
    public Timestamp getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Timestamp finishedAt) { this.finishedAt = finishedAt; }
}

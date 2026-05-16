package ru.lottery.model;

import ru.lottery.model.enums.DrawStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Draw {
    private Long id;
    private String name;
    private DrawStatus status;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public Draw() {
        this.status = DrawStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
    }

    public Draw(String name, UUID createdBy) {
        this.name = name;
        this.createdBy = createdBy;
        this.status = DrawStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DrawStatus getStatus() { return status; }
    public void setStatus(DrawStatus status) { this.status = status; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }

    public boolean isActive() {
        return status == DrawStatus.ACTIVE;
    }

    public boolean isFinished() {
        return status == DrawStatus.FINISHED;
    }

    @Override
    public String toString() {
        return "Draw{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}
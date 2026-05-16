package ru.lottery.model;

import java.time.LocalDateTime;

public class DrawResult {
    private Long id;
    private Long drawId;
    private String winningCombo;
    private LocalDateTime generatedAt;

    public DrawResult() {
        this.generatedAt = LocalDateTime.now();
    }

    public DrawResult(Long drawId, String winningCombo) {
        this.drawId = drawId;
        this.winningCombo = winningCombo;
        this.generatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDrawId() { return drawId; }
    public void setDrawId(Long drawId) { this.drawId = drawId; }

    public String getWinningCombo() { return winningCombo; }
    public void setWinningCombo(String winningCombo) { this.winningCombo = winningCombo; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    @Override
    public String toString() {
        return "DrawResult{" +
                "id=" + id +
                ", drawId=" + drawId +
                ", winningCombo='" + winningCombo + '\'' +
                ", generatedAt=" + generatedAt +
                '}';
    }
}
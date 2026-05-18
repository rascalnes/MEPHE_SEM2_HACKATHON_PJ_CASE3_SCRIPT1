package ru.lottery.model;

import java.sql.Timestamp;

public class DrawResult {
    private Long id;
    private Long drawId;
    private String winningCombo;
    private Timestamp generatedAt;

    public DrawResult() {}
    public DrawResult(Long drawId, String winningCombo) {
        this.drawId = drawId;
        this.winningCombo = winningCombo;
        this.generatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDrawId() { return drawId; }
    public void setDrawId(Long drawId) { this.drawId = drawId; }
    public String getWinningCombo() { return winningCombo; }
    public void setWinningCombo(String winningCombo) { this.winningCombo = winningCombo; }
    public Timestamp getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Timestamp generatedAt) { this.generatedAt = generatedAt; }
}

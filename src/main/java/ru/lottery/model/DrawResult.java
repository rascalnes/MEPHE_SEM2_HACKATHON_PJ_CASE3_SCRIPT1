package ru.lottery.model;

public class DrawResult {
    private Long drawId;
    private String winningNumbers;

    public DrawResult() {}

    public Long getDrawId() { return drawId; }
    public void setDrawId(Long drawId) { this.drawId = drawId; }

    public String getWinningNumbers() { return winningNumbers; }
    public void setWinningNumbers(String winningNumbers) { this.winningNumbers = winningNumbers; }
}
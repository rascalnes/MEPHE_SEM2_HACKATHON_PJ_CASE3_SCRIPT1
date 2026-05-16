package ru.lottery.dto.response;

import ru.lottery.model.Draw;
import ru.lottery.model.DrawResult;

import java.time.LocalDateTime;

public class DrawResponse {
    private boolean success;
    private String message;
    private DrawData draw;
    private String winningCombo;

    public DrawResponse(boolean success, String message, DrawData draw, String winningCombo) {
        this.success = success;
        this.message = message;
        this.draw = draw;
        this.winningCombo = winningCombo;
    }

    public static DrawResponse success(Draw draw, String message) {
        return new DrawResponse(true, message, DrawData.fromDraw(draw), null);
    }

    public static DrawResponse successWithResult(Draw draw, DrawResult result, String message) {
        DrawResponse response = new DrawResponse(true, message, DrawData.fromDraw(draw), result.getWinningCombo());
        response.setWinningCombo(result.getWinningCombo());
        return response;
    }

    public static DrawResponse error(String message) {
        return new DrawResponse(false, message, null, null);
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public DrawData getDraw() { return draw; }
    public void setDraw(DrawData draw) { this.draw = draw; }

    public String getWinningCombo() { return winningCombo; }
    public void setWinningCombo(String winningCombo) { this.winningCombo = winningCombo; }

    public static class DrawData {
        private Long id;
        private String name;
        private String status;
        private String createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime startedAt;
        private LocalDateTime finishedAt;

        public static DrawData fromDraw(Draw draw) {
            DrawData data = new DrawData();
            data.setId(draw.getId());
            data.setName(draw.getName());
            data.setStatus(draw.getStatus().getValue());
            data.setCreatedBy(draw.getCreatedBy() != null ? draw.getCreatedBy().toString() : null);
            data.setCreatedAt(draw.getCreatedAt());
            data.setStartedAt(draw.getStartedAt());
            data.setFinishedAt(draw.getFinishedAt());
            return data;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

        public LocalDateTime getFinishedAt() { return finishedAt; }
        public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
    }
}
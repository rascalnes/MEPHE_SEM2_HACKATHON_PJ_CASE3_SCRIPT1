package ru.lottery.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lottery.dto.response.DrawResponse;
import ru.lottery.model.Draw;
import ru.lottery.model.DrawResult;
import ru.lottery.model.enums.DrawStatus;
import ru.lottery.repository.DrawRepository;
import ru.lottery.repository.DrawResultRepository;
import ru.lottery.util.CombinationGenerator;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DrawService {
    private static final Logger logger = LoggerFactory.getLogger(DrawService.class);
    private final DrawRepository drawRepository;
    private final DrawResultRepository drawResultRepository;

    public DrawService() {
        this.drawRepository = new DrawRepository();
        this.drawResultRepository = new DrawResultRepository();
    }

    /**
     * Create new draw (DRAFT status)
     */
    public DrawResponse createDraw(String name, UUID adminId) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return DrawResponse.error("Draw name cannot be empty");
            }

            if (name.length() > 100) {
                return DrawResponse.error("Draw name too long (max 100 characters)");
            }

            Draw draw = new Draw(name.trim(), adminId);
            draw = drawRepository.save(draw);

            logger.info("Draw created: {} (ID: {}) by admin: {}", name, draw.getId(), adminId);
            return DrawResponse.success(draw, "Draw created successfully");

        } catch (SQLException e) {
            logger.error("Failed to create draw", e);
            return DrawResponse.error("Database error: " + e.getMessage());
        }
    }

    /**
     * Start draw (DRAFT -> ACTIVE)
     */
    public DrawResponse startDraw(Long drawId, UUID adminId) {
        try {
            Draw draw = drawRepository.findById(drawId).orElse(null);

            if (draw == null) {
                return DrawResponse.error("Draw not found");
            }

            if (draw.getStatus() != DrawStatus.DRAFT) {
                return DrawResponse.error("Only DRAFT draws can be started");
            }

            if (!draw.getStatus().canTransitionTo(DrawStatus.ACTIVE)) {
                return DrawResponse.error("Cannot start draw from current status: " + draw.getStatus().getValue());
            }

            draw.setStatus(DrawStatus.ACTIVE);
            draw.setStartedAt(LocalDateTime.now());
            drawRepository.update(draw);

            logger.info("Draw started: {} (ID: {}) by admin: {}", draw.getName(), drawId, adminId);
            return DrawResponse.success(draw, "Draw started successfully");

        } catch (SQLException e) {
            logger.error("Failed to start draw", e);
            return DrawResponse.error("Database error: " + e.getMessage());
        }
    }

    /**
     * Finish draw (ACTIVE -> FINISHED) and generate results
     */
    public DrawResponse finishDraw(Long drawId, UUID adminId) {
        try {
            Draw draw = drawRepository.findById(drawId).orElse(null);

            if (draw == null) {
                return DrawResponse.error("Draw not found");
            }

            if (draw.getStatus() != DrawStatus.ACTIVE) {
                return DrawResponse.error("Only ACTIVE draws can be finished");
            }

            if (!draw.getStatus().canTransitionTo(DrawStatus.FINISHED)) {
                return DrawResponse.error("Cannot finish draw from current status: " + draw.getStatus().getValue());
            }

            // Generate winning combination
            String winningCombo = CombinationGenerator.generateRandomCombination();

            // Save draw result
            DrawResult result = new DrawResult(drawId, winningCombo);
            drawResultRepository.save(result);

            // Update draw status
            draw.setStatus(DrawStatus.FINISHED);
            draw.setFinishedAt(LocalDateTime.now());
            drawRepository.update(draw);

            logger.info("Draw finished: {} (ID: {}) by admin: {}. Winning combo: {}",
                    draw.getName(), drawId, adminId, winningCombo);

            return DrawResponse.successWithResult(draw, result, "Draw finished successfully");

        } catch (SQLException e) {
            logger.error("Failed to finish draw", e);
            return DrawResponse.error("Database error: " + e.getMessage());
        }
    }

    /**
     * Get all active draws
     */
    public List<DrawResponse> getActiveDraws() {
        try {
            List<Draw> draws = drawRepository.findActiveDraws();
            return draws.stream()
                    .map(draw -> DrawResponse.success(draw, null))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            logger.error("Failed to get active draws", e);
            return List.of();
        }
    }

    /**
     * Get draw by ID
     */
    public DrawResponse getDrawById(Long drawId) {
        try {
            Draw draw = drawRepository.findById(drawId).orElse(null);

            if (draw == null) {
                return DrawResponse.error("Draw not found");
            }

            DrawResult result = drawResultRepository.findByDrawId(drawId).orElse(null);

            if (result != null) {
                return DrawResponse.successWithResult(draw, result, null);
            } else {
                return DrawResponse.success(draw, null);
            }

        } catch (SQLException e) {
            logger.error("Failed to get draw", e);
            return DrawResponse.error("Database error: " + e.getMessage());
        }
    }

    /**
     * Get all draws
     */
    public List<DrawResponse> getAllDraws() {
        try {
            List<Draw> draws = drawRepository.findAll();
            return draws.stream()
                    .map(draw -> DrawResponse.success(draw, null))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            logger.error("Failed to get all draws", e);
            return List.of();
        }
    }

    /**
     * Check if draw exists and is active
     */
    public boolean isDrawActive(Long drawId) {
        try {
            Draw draw = drawRepository.findById(drawId).orElse(null);
            return draw != null && draw.isActive();
        } catch (SQLException e) {
            logger.error("Failed to check draw status", e);
            return false;
        }
    }

    /**
     * Get winning combination for finished draw
     */
    public String getWinningCombination(Long drawId) {
        try {
            DrawResult result = drawResultRepository.findByDrawId(drawId).orElse(null);
            return result != null ? result.getWinningCombo() : null;
        } catch (SQLException e) {
            logger.error("Failed to get winning combination", e);
            return null;
        }
    }
}
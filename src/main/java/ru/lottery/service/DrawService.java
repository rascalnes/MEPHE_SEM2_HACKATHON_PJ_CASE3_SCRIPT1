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

    public DrawResponse createDraw(String name, UUID adminId) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return DrawResponse.error("Название тиража не может быть пустым");
            }
            if (name.length() > 100) {
                return DrawResponse.error("Название тиража слишком длинное (максимум 100 символов)");
            }
            Draw draw = new Draw(name.trim(), adminId);
            draw = drawRepository.save(draw);
            logger.info("Тираж создан: {} (ID: {}) администратором: {}", name, draw.getId(), adminId);
            return DrawResponse.success(draw, "Тираж успешно создан");
        } catch (SQLException e) {
            logger.error("Ошибка создания тиража", e);
            return DrawResponse.error("Ошибка базы данных: " + e.getMessage());
        }
    }

    public DrawResponse startDraw(Long drawId, UUID adminId) {
        try {
            Draw draw = drawRepository.findById(drawId).orElse(null);
            if (draw == null) {
                return DrawResponse.error("Тираж не найден");
            }
            if (draw.getStatus() != DrawStatus.DRAFT) {
                return DrawResponse.error("Запустить можно только тираж в статусе DRAFT");
            }
            if (!draw.getStatus().canTransitionTo(DrawStatus.ACTIVE)) {
                return DrawResponse.error("Невозможно запустить тираж из текущего статуса: " + draw.getStatus().getValue());
            }
            draw.setStatus(DrawStatus.ACTIVE);
            draw.setStartedAt(LocalDateTime.now());
            drawRepository.update(draw);
            logger.info("Тираж запущен: {} (ID: {}) администратором: {}", draw.getName(), drawId, adminId);
            return DrawResponse.success(draw, "Тираж успешно запущен");
        } catch (SQLException e) {
            logger.error("Ошибка запуска тиража", e);
            return DrawResponse.error("Ошибка базы данных: " + e.getMessage());
        }
    }

    public DrawResponse finishDraw(Long drawId, UUID adminId) {
        try {
            Draw draw = drawRepository.findById(drawId).orElse(null);
            if (draw == null) {
                return DrawResponse.error("Тираж не найден");
            }
            if (draw.getStatus() != DrawStatus.ACTIVE) {
                return DrawResponse.error("Завершить можно только активный тираж");
            }
            if (!draw.getStatus().canTransitionTo(DrawStatus.FINISHED)) {
                return DrawResponse.error("Невозможно завершить тираж из текущего статуса: " + draw.getStatus().getValue());
            }
            String winningCombo = CombinationGenerator.generateRandomCombination();
            DrawResult result = new DrawResult(drawId, winningCombo);
            drawResultRepository.save(result);
            draw.setStatus(DrawStatus.FINISHED);
            draw.setFinishedAt(LocalDateTime.now());
            drawRepository.update(draw);
            logger.info("Тираж завершён: {} (ID: {}), выигрышная комбинация: {}", draw.getName(), drawId, winningCombo);
            return DrawResponse.successWithResult(draw, result, "Тираж успешно завершён");
        } catch (SQLException e) {
            logger.error("Ошибка завершения тиража", e);
            return DrawResponse.error("Ошибка базы данных: " + e.getMessage());
        }
    }

    public List<DrawResponse> getActiveDraws() {
        try {
            List<Draw> draws = drawRepository.findActiveDraws();
            return draws.stream()
                    .map(draw -> DrawResponse.success(draw, null))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            logger.error("Ошибка получения активных тиражей", e);
            return List.of();
        }
    }

    public DrawResponse getDrawById(Long drawId) {
        try {
            Draw draw = drawRepository.findById(drawId).orElse(null);
            if (draw == null) {
                return DrawResponse.error("Тираж не найден");
            }
            DrawResult result = drawResultRepository.findByDrawId(drawId).orElse(null);
            if (result != null) {
                return DrawResponse.successWithResult(draw, result, null);
            } else {
                return DrawResponse.success(draw, null);
            }
        } catch (SQLException e) {
            logger.error("Ошибка получения тиража", e);
            return DrawResponse.error("Ошибка базы данных: " + e.getMessage());
        }
    }

    public List<DrawResponse> getAllDraws() {
        try {
            List<Draw> draws = drawRepository.findAll();
            return draws.stream()
                    .map(draw -> DrawResponse.success(draw, null))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            logger.error("Ошибка получения всех тиражей", e);
            return List.of();
        }
    }

    public boolean isDrawActive(Long drawId) {
        try {
            Draw draw = drawRepository.findById(drawId).orElse(null);
            return draw != null && draw.isActive();
        } catch (SQLException e) {
            logger.error("Ошибка проверки статуса тиража", e);
            return false;
        }
    }

    public String getWinningCombination(Long drawId) {
        try {
            DrawResult result = drawResultRepository.findByDrawId(drawId).orElse(null);
            return result != null ? result.getWinningCombo() : null;
        } catch (SQLException e) {
            logger.error("Ошибка получения выигрышной комбинации", e);
            return null;
        }
    }
}

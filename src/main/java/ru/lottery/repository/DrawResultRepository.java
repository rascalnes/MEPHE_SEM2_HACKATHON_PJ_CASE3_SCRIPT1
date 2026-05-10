package ru.lottery.repository;

import ru.lottery.model.DrawResult;
import java.util.*;

public class DrawResultRepository {
    private final Map<Long, DrawResult> storage = new HashMap<>();

    // Сохранить результат тиража
    public void save(DrawResult result) {
        storage.put(result.getDrawId(), result);
    }

    // Найти по ID тиража
    public DrawResult findByDrawId(Long drawId) {
        return storage.get(drawId);
    }

    // Обновить результат
    public void update(DrawResult result) {
        storage.put(result.getDrawId(), result);
    }

    // Найти все результаты
    public List<DrawResult> findAll() {
        return new ArrayList<>(storage.values());
    }
}
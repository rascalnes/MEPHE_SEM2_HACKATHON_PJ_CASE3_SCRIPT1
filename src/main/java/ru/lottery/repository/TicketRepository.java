package ru.lottery.repository;

import ru.lottery.model.Ticket;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class TicketRepository {
    // Временное хранилище (потом заменим на реальную БД)
    private final Map<Long, Ticket> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // Сохранить билет
    public Ticket save(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(idGenerator.getAndIncrement());
        }
        storage.put(ticket.getId(), ticket);
        return ticket;
    }

    // Найти по ID
    public Ticket findById(Long id) {
        return storage.get(id);
    }

    // Найти все билеты тиража
    public List<Ticket> findByDrawId(Long drawId) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : storage.values()) {
            if (ticket.getDrawId().equals(drawId)) {
                result.add(ticket);
            }
        }
        return result;
    }

    // Обновить билет
    public void update(Ticket ticket) {
        storage.put(ticket.getId(), ticket);
    }

    // Найти все билеты пользователя
    public List<Ticket> findByUserId(Long userId) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : storage.values()) {
            if (ticket.getUserId().equals(userId)) {
                result.add(ticket);
            }
        }
        return result;
    }

    // Найти все билеты
    public List<Ticket> findAll() {
        return new ArrayList<>(storage.values());
    }
}
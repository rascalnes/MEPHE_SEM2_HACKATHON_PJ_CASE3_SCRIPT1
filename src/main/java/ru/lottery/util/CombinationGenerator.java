package ru.lottery.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.List;

public class CombinationGenerator {
    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 49;
    private static final int NUMBERS_COUNT = 6;
    private static final Random random = new Random();
    private static final int COUNT = 6;

    public static String generate() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= MAX_NUMBER; i++) numbers.add(i);
        Collections.shuffle(numbers, new Random());
        return numbers.subList(0, COUNT)
                .stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * Generate random lottery combination
     * @return String with comma-separated numbers (sorted)
     */
    public static String generateRandomCombination() {
        Set<Integer> numbers = new TreeSet<>();

        while (numbers.size() < NUMBERS_COUNT) {
            numbers.add(random.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER);
        }

        return numbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * Parse combination string to list of integers
     */
    public static List<Integer> parseCombination(String combination) {
        if (combination == null || combination.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(combination.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Check if ticket combination matches winning combination
     */
    public static boolean isWinningCombination(String ticketNumbers, String winningNumbers) {
        List<Integer> ticket = parseCombination(ticketNumbers);
        List<Integer> winning = parseCombination(winningNumbers);

        return ticket.equals(winning);
    }

    /**
     * Validate combination format
     */
    public static boolean isValidCombination(String combination) {
        if (combination == null || combination.isEmpty()) {
            return false;
        }

        String[] parts = combination.split(",");
        if (parts.length != NUMBERS_COUNT) {
            return false;
        }

        try {
            Set<Integer> numbers = new HashSet<>();
            for (String part : parts) {
                int num = Integer.parseInt(part.trim());
                if (num < MIN_NUMBER || num > MAX_NUMBER) {
                    return false;
                }
                if (!numbers.add(num)) {
                    return false; // Duplicate numbers
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Generate combination description
     */
    public static String formatCombination(String combination) {
        List<Integer> numbers = parseCombination(combination);
        return numbers.stream()
                .map(n -> String.format("%02d", n))
                .collect(Collectors.joining(" - "));
    }
}
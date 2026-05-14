package ru.lottery.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class TicketChecker {
    public static boolean isWinning(String ticketNumbers, String winningNumbers) {
        if (ticketNumbers == null || winningNumbers == null) return false;
        Set<Integer> ticketSet = parseNumbers(ticketNumbers);
        Set<Integer> winSet = parseNumbers(winningNumbers);
        return ticketSet.equals(winSet) && ticketSet.size() == 6;
    }

    private static Set<Integer> parseNumbers(String numbers) {
        return Arrays.stream(numbers.split(","))
                     .map(String::trim)
                     .map(Integer::parseInt)
                     .collect(Collectors.toSet());
    }
}

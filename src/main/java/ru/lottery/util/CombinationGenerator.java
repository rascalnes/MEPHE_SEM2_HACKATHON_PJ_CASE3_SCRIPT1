package ru.lottery.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

public class CombinationGenerator {
    private static final int COUNT = 6;
    private static final int MAX_NUMBER = 49;

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
}

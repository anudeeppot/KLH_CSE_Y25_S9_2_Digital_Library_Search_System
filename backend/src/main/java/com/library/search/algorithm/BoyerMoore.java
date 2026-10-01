package com.library.search.algorithm;

import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Boyer-Moore Pattern Matching Algorithm with Bad Character Heuristic.
 *
 * Matches pattern characters from right to left.
 * When a mismatch occurs, shifts the pattern by using the bad character table.
 *
 * Complexity:
 * - Best Case: O(n / m) (Sub-linear)
 * - Average Case: O(n)
 * - Worst Case: O(n * m)
 * - Auxiliary Space: O(sigma) where sigma is alphabet size
 */
@Component
public class BoyerMoore {

    private static final int ALPHABET_SIZE = 256;

    /**
     * Builds the Bad Character table (last occurrence of each character in pattern).
     */
    public int[] buildBadCharTable(String pattern) {
        int[] badChar = new int[ALPHABET_SIZE];
        Arrays.fill(badChar, -1);

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c < ALPHABET_SIZE) {
                badChar[c] = i;
            }
        }
        return badChar;
    }

    public List<Integer> search(String text, String pattern) {
        return searchWithMetrics(text, pattern).getPositions();
    }

    public BoyerMooreResult searchWithMetrics(String text, String pattern) {
        long startTime = System.nanoTime();
        List<Integer> positions = new ArrayList<>();
        List<String> steps = new ArrayList<>();
        Map<Character, Integer> badCharMap = new LinkedHashMap<>();
        long comparisons = 0;
        long totalShifts = 0;

        if (text == null || pattern == null || text.isEmpty() || pattern.isEmpty()) {
            long executionTime = System.nanoTime() - startTime;
            return new BoyerMooreResult(positions, comparisons, totalShifts, executionTime, badCharMap, steps);
        }

        String searchText = text.toLowerCase();
        String searchPattern = pattern.toLowerCase();
        int n = searchText.length();
        int m = searchPattern.length();

        if (m > n) {
            long executionTime = System.nanoTime() - startTime;
            return new BoyerMooreResult(positions, comparisons, totalShifts, executionTime, badCharMap, steps);
        }

        int[] badChar = buildBadCharTable(searchPattern);

        for (int i = 0; i < m; i++) {
            badCharMap.put(searchPattern.charAt(i), i);
        }

        steps.add("Constructed Bad Character Table: " + badCharMap);

        int shift = 0;
        while (shift <= (n - m)) {
            int j = m - 1;

            // Right-to-left scan
            while (j >= 0) {
                comparisons++;
                char textChar = searchText.charAt(shift + j);
                char patChar = searchPattern.charAt(j);

                if (textChar == patChar) {
                    j--;
                } else {
                    break;
                }
            }

            if (j < 0) {
                // Found a match
                positions.add(shift);
                steps.add(">> Match found at index " + shift + "! Pattern aligned completely <<");
                // Shift pattern so that the next character aligns with the last occurrence in pattern
                int nextShift = (shift + m < n) ? m - (searchText.charAt(shift + m) < ALPHABET_SIZE ? badChar[searchText.charAt(shift + m)] : -1) : 1;
                if (nextShift <= 0) nextShift = 1;
                shift += nextShift;
                totalShifts++;
            } else {
                char mismatchedChar = searchText.charAt(shift + j);
                int lastOccur = (mismatchedChar < ALPHABET_SIZE) ? badChar[mismatchedChar] : -1;
                int jump = Math.max(1, j - lastOccur);
                if (steps.size() < 40) {
                    steps.add("Mismatch at pattern pos " + j + " with text char '" + mismatchedChar + "'. Jump = max(1, " + j + " - " + lastOccur + ") = " + jump);
                }
                shift += jump;
                totalShifts++;
            }
        }

        long executionTime = System.nanoTime() - startTime;
        return new BoyerMooreResult(positions, comparisons, totalShifts, executionTime, badCharMap, steps);
    }

    public static class BoyerMooreResult {
        private final List<Integer> positions;
        private final long comparisons;
        private final long totalShifts;
        private final long executionTime;
        private final Map<Character, Integer> badCharacterTable;
        private final List<String> steps;

        public BoyerMooreResult(List<Integer> positions, long comparisons, long totalShifts,
                                long executionTime, Map<Character, Integer> badCharacterTable, List<String> steps) {
            this.positions = positions;
            this.comparisons = comparisons;
            this.totalShifts = totalShifts;
            this.executionTime = executionTime;
            this.badCharacterTable = badCharacterTable;
            this.steps = steps;
        }

        public List<Integer> getPositions() { return positions; }
        public long getComparisons() { return comparisons; }
        public long getTotalShifts() { return totalShifts; }
        public long getExecutionTime() { return executionTime; }
        public Map<Character, Integer> getBadCharacterTable() { return badCharacterTable; }
        public List<String> getSteps() { return steps; }
    }
}

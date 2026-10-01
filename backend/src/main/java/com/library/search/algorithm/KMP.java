package com.library.search.algorithm;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) Pattern Matching Algorithm.
 * Integrates the user's original KMP.java implementation and extends it
 * with comparison tracking, execution timing, and LPS step trace for DSA-3 demonstration.
 *
 * Complexity:
 * - Preprocessing (LPS Construction): O(m)
 * - Searching: O(n)
 * - Total Time: O(n + m)
 * - Auxiliary Space: O(m)
 */
@Component
public class KMP {

    /**
     * CO2: Build the LPS (Longest Proper Prefix which is also Suffix) array.
     * Preserves exact logic from user's original KMP.java.
     */
    public int[] buildLPS(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return new int[0];
        }
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else if (length != 0) {
                length = lps[length - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }

    /**
     * CO2: Standard KMP search matching user's original signature.
     */
    public List<Integer> search(String text, String pattern) {
        List<Integer> positions = new ArrayList<>();

        if (text == null || pattern == null || text.isEmpty() || pattern.isEmpty()) {
            return positions;
        }

        String searchText = text.toLowerCase();
        String searchPattern = pattern.toLowerCase();

        if (searchPattern.length() > searchText.length()) {
            return positions;
        }

        int[] lps = buildLPS(searchPattern);

        int i = 0;
        int j = 0;

        while (i < searchText.length()) {
            if (searchText.charAt(i) == searchPattern.charAt(j)) {
                i++;
                j++;

                if (j == searchPattern.length()) {
                    positions.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j != 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }

        return positions;
    }

    public boolean found(String text, String pattern) {
        return !search(text, pattern).isEmpty();
    }

    /**
     * Extended KMP execution with detailed metrics and step trace for UI visualization.
     */
    public KMPResult searchWithMetrics(String text, String pattern) {
        long startTime = System.nanoTime();
        List<Integer> positions = new ArrayList<>();
        List<String> steps = new ArrayList<>();
        long comparisons = 0;

        if (text == null || pattern == null || text.isEmpty() || pattern.isEmpty()) {
            long executionTime = System.nanoTime() - startTime;
            return new KMPResult(positions, comparisons, executionTime, new int[0], steps);
        }

        String searchText = text.toLowerCase();
        String searchPattern = pattern.toLowerCase();

        int[] lps = buildLPS(searchPattern);
        steps.add("Constructed LPS Array for pattern '" + pattern + "': " + formatArray(lps));

        if (searchPattern.length() <= searchText.length()) {
            int i = 0;
            int j = 0;

            while (i < searchText.length()) {
                comparisons++;
                char textChar = searchText.charAt(i);
                char patChar = searchPattern.charAt(j);

                if (textChar == patChar) {
                    if (steps.size() < 40) {
                        steps.add("Match at text[" + i + "]='" + textChar + "' and pat[" + j + "]='" + patChar + "'");
                    }
                    i++;
                    j++;

                    if (j == searchPattern.length()) {
                        int matchPos = i - j;
                        positions.add(matchPos);
                        steps.add(">> Full pattern match found at index: " + matchPos + " <<");
                        j = lps[j - 1];
                    }
                } else {
                    if (steps.size() < 40) {
                        steps.add("Mismatch: text[" + i + "]='" + textChar + "' != pat[" + j + "]='" + patChar + "'");
                    }
                    if (j != 0) {
                        int prevJ = j;
                        j = lps[j - 1];
                        if (steps.size() < 40) {
                            steps.add("Shifted pattern index using LPS: " + prevJ + " -> " + j);
                        }
                    } else {
                        i++;
                    }
                }
            }
        }

        long executionTime = System.nanoTime() - startTime;
        return new KMPResult(positions, comparisons, executionTime, lps, steps);
    }

    private String formatArray(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static class KMPResult {
        private final List<Integer> positions;
        private final long comparisons;
        private final long executionTime;
        private final int[] lps;
        private final List<String> steps;

        public KMPResult(List<Integer> positions, long comparisons, long executionTime, int[] lps, List<String> steps) {
            this.positions = positions;
            this.comparisons = comparisons;
            this.executionTime = executionTime;
            this.lps = lps;
            this.steps = steps;
        }

        public List<Integer> getPositions() { return positions; }
        public long getComparisons() { return comparisons; }
        public long getExecutionTime() { return executionTime; }
        public int[] getLps() { return lps; }
        public List<String> getSteps() { return steps; }
    }
}

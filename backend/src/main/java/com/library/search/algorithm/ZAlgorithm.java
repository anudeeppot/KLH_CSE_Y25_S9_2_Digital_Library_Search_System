package com.library.search.algorithm;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Course Outcome 2 (CO-2):
 * "Apply linear-time string algorithms (KMP, Z-function, Rabin-Karp with rolling hash)
 * and suffix-based structures (suffix array, LCP array, suffix automaton at intuition level)
 * to solve large-scale pattern-matching problems."
 *
 * Z-Algorithm (Z-Function):
 * For a string S of length n, Z[i] is the length of the longest common prefix between S and the suffix of S starting at i.
 * Computes the entire Z-array in strictly linear O(n) time using the Z-box [L, R] invariant.
 * Applied to pattern matching on S = P + '$' + T in O(|P| + |T|) time.
 */
@Component
public class ZAlgorithm {

    public static class ZStep {
        private final int index;
        private final int zValue;
        private final int l;
        private final int r;
        private final String action;

        public ZStep(int index, int zValue, int l, int r, String action) {
            this.index = index;
            this.zValue = zValue;
            this.l = l;
            this.r = r;
            this.action = action;
        }

        public int getIndex() { return index; }
        public int getzValue() { return zValue; }
        public int getL() { return l; }
        public int getR() { return r; }
        public String getAction() { return action; }
    }

    public static class ZResult {
        private final String combinedString;
        private final int[] zArray;
        private final List<Integer> matchPositions;
        private final List<ZStep> steps;
        private final long executionTimeNanos;
        private final int comparisons;

        public ZResult(String combinedString, int[] zArray, List<Integer> matchPositions,
                       List<ZStep> steps, long executionTimeNanos, int comparisons) {
            this.combinedString = combinedString;
            this.zArray = zArray;
            this.matchPositions = matchPositions;
            this.steps = steps;
            this.executionTimeNanos = executionTimeNanos;
            this.comparisons = comparisons;
        }

        public String getCombinedString() { return combinedString; }
        public int[] getzArray() { return zArray; }
        public List<Integer> getMatchPositions() { return matchPositions; }
        public List<ZStep> getSteps() { return steps; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public int getComparisons() { return comparisons; }
    }

    public ZResult searchWithMetrics(String text, String pattern) {
        long startTime = System.nanoTime();

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return new ZResult("", new int[0], new ArrayList<>(), new ArrayList<>(), 0, 0);
        }

        char delimiter = '$';
        String combined = pattern + delimiter + text;
        int n = combined.length();
        int m = pattern.length();

        int[] z = new int[n];
        List<ZStep> steps = new ArrayList<>();
        List<Integer> matchPositions = new ArrayList<>();

        int l = 0, r = 0;
        int comparisons = 0;

        for (int i = 1; i < n; i++) {
            if (i > r) {
                // i is outside the current Z-box [L, R]
                l = r = i;
                while (r < n && combined.charAt(r) == combined.charAt(r - l)) {
                    comparisons++;
                    r++;
                }
                if (r < n) comparisons++; // mismatch comparison
                z[i] = r - l;
                r--;
                steps.add(new ZStep(i, z[i], l, r, "i > R: Expanded new Z-box from index " + i + " to " + r));
            } else {
                // i is inside current Z-box [L, R]
                int k = i - l; // corresponding index in prefix
                if (z[k] < r - i + 1) {
                    // Value is completely inside the Z-box; directly copied!
                    z[i] = z[k];
                    steps.add(new ZStep(i, z[i], l, r, "Inside Z-box: Copied Z[" + k + "] = " + z[k] + " (strictly inside boundary)"));
                } else {
                    // May extend beyond R
                    l = i;
                    while (r < n && combined.charAt(r) == combined.charAt(r - l)) {
                        comparisons++;
                        r++;
                    }
                    if (r < n) comparisons++;
                    z[i] = r - l;
                    r--;
                    steps.add(new ZStep(i, z[i], l, r, "Inside Z-box: Extended beyond boundary R to new R = " + r));
                }
            }

            // If Z[i] == m, we found an occurrence of pattern in text
            if (z[i] == m && i > m) {
                int textPos = i - m - 1; // convert from combined index to original text index
                matchPositions.add(textPos);
            }
        }

        long executionTime = System.nanoTime() - startTime;
        return new ZResult(combined, z, matchPositions, steps, executionTime, comparisons);
    }

    public List<Integer> search(String text, String pattern) {
        return searchWithMetrics(text, pattern).getMatchPositions();
    }
}

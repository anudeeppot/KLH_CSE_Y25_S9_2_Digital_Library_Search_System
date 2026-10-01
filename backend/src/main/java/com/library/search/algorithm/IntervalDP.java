package com.library.search.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Course Outcome 3 (CO-3):
 * "Apply advanced dynamic-programming patterns — interval DP, bitmask DP, DP on trees, DP on subsets —
 * to design polynomial-time algorithms for combinatorial optimisation."
 *
 * Pattern 1: Interval Dynamic Programming
 * Interval DP solves optimization problems on contiguous segments [i, j].
 *
 * Digital Library Application: Optimal Search Query Filter Chain Evaluation & Query Execution Plan.
 * In a digital library multi-facet query (e.g. "[Keyword Search] AND [Date Range] AND [Author Facet] AND [FullText Scan]"),
 * the order in which adjacent boolean filters are merged dramatically impacts database intermediate cardinality.
 *
 * Recurrence Relation:
 * DP[i][j] = min_{i <= k < j} (DP[i][k] + DP[k+1][j] + cost(i, k, j))
 * Base Cases: DP[i][i] = 0
 * Complexity: O(N^3) time, O(N^2) space.
 */
@Component
public class IntervalDP {

    public static class IntervalResult {
        private final List<String> queryTokens;
        private final int[] dimensionWeights;
        private final long[][] dpTable;
        private final int[][] splitTable;
        private final long optimalCost;
        private final String optimalParenthesization;
        private final List<String> computationLog;
        private final long executionTimeNanos;
        private final String recurrenceFormula;

        public IntervalResult(List<String> queryTokens, int[] dimensionWeights, long[][] dpTable,
                              int[][] splitTable, long optimalCost, String optimalParenthesization,
                              List<String> computationLog, long executionTimeNanos, String recurrenceFormula) {
            this.queryTokens = queryTokens;
            this.dimensionWeights = dimensionWeights;
            this.dpTable = dpTable;
            this.splitTable = splitTable;
            this.optimalCost = optimalCost;
            this.optimalParenthesization = optimalParenthesization;
            this.computationLog = computationLog;
            this.executionTimeNanos = executionTimeNanos;
            this.recurrenceFormula = recurrenceFormula;
        }

        public List<String> getQueryTokens() { return queryTokens; }
        public int[] getDimensionWeights() { return dimensionWeights; }
        public long[][] getDpTable() { return dpTable; }
        public int[][] getSplitTable() { return splitTable; }
        public long getOptimalCost() { return optimalCost; }
        public String getOptimalParenthesization() { return optimalParenthesization; }
        public List<String> getComputationLog() { return computationLog; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public String getRecurrenceFormula() { return recurrenceFormula; }
    }

    public IntervalResult computeOptimalQueryPlan(List<String> tokens, int[] weights) {
        long startTime = System.nanoTime();

        List<String> filterTokens = (tokens != null && tokens.size() >= 2) ? tokens :
                Arrays.asList("Title_KMP", "Author_Index", "Year_Filter", "Category_Hash", "FullText_Scan");

        int n = filterTokens.size();
        // Weights array has length n + 1 representing dimensions for matrix chain / pipeline cost
        int[] p = (weights != null && weights.length == n + 1) ? weights : new int[]{10, 35, 15, 5, 20, 25};

        long[][] m = new long[n][n];
        int[][] s = new int[n][n];
        List<String> log = new ArrayList<>();

        // Length of chain len from 2 to n
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                m[i][j] = Long.MAX_VALUE;

                for (int k = i; k < j; k++) {
                    long cost = m[i][k] + m[k + 1][j] + (long) p[i] * p[k + 1] * p[j + 1];
                    if (cost < m[i][j]) {
                        m[i][j] = cost;
                        s[i][j] = k;
                    }
                }
                log.add("Interval [" + i + ".." + j + "]: min cost = " + m[i][j] + " via split at k=" + s[i][j]);
            }
        }

        String parenthesization = buildTreeString(s, 0, n - 1, filterTokens);
        long executionTime = System.nanoTime() - startTime;
        String formula = "DP[i][j] = min_{i <= k < j} { DP[i][k] + DP[k+1][j] + p[i]*p[k+1]*p[j+1] }";

        return new IntervalResult(filterTokens, p, m, s, m[0][n - 1], parenthesization, log, executionTime, formula);
    }

    private String buildTreeString(int[][] s, int i, int j, List<String> tokens) {
        if (i == j) {
            return tokens.get(i);
        }
        int k = s[i][j];
        return "(" + buildTreeString(s, i, k, tokens) + " \u2297 " + buildTreeString(s, k + 1, j, tokens) + ")";
    }
}

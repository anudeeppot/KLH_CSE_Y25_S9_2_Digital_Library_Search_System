package com.library.search.algorithm;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Levenshtein Edit Distance using Dynamic Programming.
 *
 * Quantifies string similarity and powers Fuzzy Search & Typo Detection in the Digital Library.
 *
 * Complexity:
 * - Time Complexity: O(n * m)
 * - Space Complexity: O(n * m)
 */
@Component
public class EditDistance {

    public EditDistanceResult compute(String s1, String s2) {
        long startTime = System.nanoTime();

        String str1 = (s1 != null) ? s1 : "";
        String str2 = (s2 != null) ? s2 : "";

        int m = str1.length();
        int n = str2.length();

        int[][] dp = new int[m + 1][n + 1];

        // Base cases
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // Fill DP Matrix
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (Character.toLowerCase(str1.charAt(i - 1)) == Character.toLowerCase(str2.charAt(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int deleteOp = dp[i - 1][j];
                    int insertOp = dp[i][j - 1];
                    int replaceOp = dp[i - 1][j - 1];
                    dp[i][j] = 1 + Math.min(deleteOp, Math.min(insertOp, replaceOp));
                }
            }
        }

        int distance = dp[m][n];
        int maxLen = Math.max(m, n);
        double similarity = (maxLen == 0) ? 100.0 : Math.round((1.0 - (double) distance / maxLen) * 10000.0) / 100.0;

        // Backtrack optimal alignment operations
        List<EditOperation> operations = backtrackOperations(str1, str2, dp);

        long executionTime = System.nanoTime() - startTime;
        return new EditDistanceResult(str1, str2, distance, similarity, dp, operations, executionTime);
    }

    private List<EditOperation> backtrackOperations(String s1, String s2, int[][] dp) {
        List<EditOperation> ops = new ArrayList<>();
        int i = s1.length();
        int j = s2.length();

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && Character.toLowerCase(s1.charAt(i - 1)) == Character.toLowerCase(s2.charAt(j - 1))) {
                ops.add(0, new EditOperation("MATCH", s1.charAt(i - 1), s2.charAt(j - 1), 0));
                i--;
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                ops.add(0, new EditOperation("SUBSTITUTE", s1.charAt(i - 1), s2.charAt(j - 1), 1));
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add(0, new EditOperation("DELETE", s1.charAt(i - 1), '-', 1));
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                ops.add(0, new EditOperation("INSERT", '-', s2.charAt(j - 1), 1));
                j--;
            } else {
                break;
            }
        }
        return ops;
    }

    public static class EditOperation {
        private final String type; // MATCH, INSERT, DELETE, SUBSTITUTE
        private final char sourceChar;
        private final char targetChar;
        private final int cost;

        public EditOperation(String type, char sourceChar, char targetChar, int cost) {
            this.type = type;
            this.sourceChar = sourceChar;
            this.targetChar = targetChar;
            this.cost = cost;
        }

        public String getType() { return type; }
        public char getSourceChar() { return sourceChar; }
        public char getTargetChar() { return targetChar; }
        public int getCost() { return cost; }
    }

    public static class EditDistanceResult {
        private final String source;
        private final String target;
        private final int distance;
        private final double similarity;
        private final int[][] dpMatrix;
        private final List<EditOperation> operations;
        private final long executionTime;

        public EditDistanceResult(String source, String target, int distance, double similarity,
                                  int[][] dpMatrix, List<EditOperation> operations, long executionTime) {
            this.source = source;
            this.target = target;
            this.distance = distance;
            this.similarity = similarity;
            this.dpMatrix = dpMatrix;
            this.operations = operations;
            this.executionTime = executionTime;
        }

        public String getSource() { return source; }
        public String getTarget() { return target; }
        public int getDistance() { return distance; }
        public double getSimilarity() { return similarity; }
        public int[][] getDpMatrix() { return dpMatrix; }
        public List<EditOperation> getOperations() { return operations; }
        public long getExecutionTime() { return executionTime; }
    }
}

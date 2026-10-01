package com.library.search.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Course Outcome 1 & 3 (CO-1 / CO-3):
 * "Sequence Alignment & Dynamic Programming on Subsets"
 *
 * Needleman-Wunsch Global Sequence Alignment & Manuscript Revision Diff.
 * In a digital library, comparing successive draft editions of academic papers, manuscripts, or citations
 * requires optimal global sequence alignment.
 *
 * Recurrence:
 * DP[i][j] = max of:
 *   1. DP[i-1][j-1] + score(seqA[i], seqB[j])  (Match / Mismatch)
 *   2. DP[i-1][j] + gapPenalty                 (Deletion / Gap in B)
 *   3. DP[i][j-1] + gapPenalty                 (Insertion / Gap in A)
 *
 * Complexity: O(N * M) time and O(N * M) space.
 */
@Component
public class SequenceAlignmentDP {

    public static class AlignmentResult {
        private final String sequenceA;
        private final String sequenceB;
        private final String alignedA;
        private final String alignmentSymbols;
        private final String alignedB;
        private final int alignmentScore;
        private final int matches;
        private final int mismatches;
        private final int gaps;
        private final double similarityPercent;
        private final int[][] scoreMatrix;
        private final long executionTimeNanos;
        private final String theoreticalInsight;

        public AlignmentResult(String sequenceA, String sequenceB, String alignedA,
                               String alignmentSymbols, String alignedB, int alignmentScore,
                               int matches, int mismatches, int gaps, double similarityPercent,
                               int[][] scoreMatrix, long executionTimeNanos, String theoreticalInsight) {
            this.sequenceA = sequenceA;
            this.sequenceB = sequenceB;
            this.alignedA = alignedA;
            this.alignmentSymbols = alignmentSymbols;
            this.alignedB = alignedB;
            this.alignmentScore = alignmentScore;
            this.matches = matches;
            this.mismatches = mismatches;
            this.gaps = gaps;
            this.similarityPercent = similarityPercent;
            this.scoreMatrix = scoreMatrix;
            this.executionTimeNanos = executionTimeNanos;
            this.theoreticalInsight = theoreticalInsight;
        }

        public String getSequenceA() { return sequenceA; }
        public String getSequenceB() { return sequenceB; }
        public String getAlignedA() { return alignedA; }
        public String getAlignmentSymbols() { return alignmentSymbols; }
        public String getAlignedB() { return alignedB; }
        public int getAlignmentScore() { return alignmentScore; }
        public int getMatches() { return matches; }
        public int getMismatches() { return mismatches; }
        public int getGaps() { return gaps; }
        public double getSimilarityPercent() { return similarityPercent; }
        public int[][] getScoreMatrix() { return scoreMatrix; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public String getTheoreticalInsight() { return theoreticalInsight; }
    }

    public AlignmentResult align(String seqA, String seqB) {
        long startTime = System.nanoTime();

        String a = (seqA != null && !seqA.isEmpty()) ? seqA.toUpperCase() : "ALGORITHM";
        String b = (seqB != null && !seqB.isEmpty()) ? seqB.toUpperCase() : "LOGARITHM";

        int n = a.length();
        int m = b.length();

        int matchScore = 2;
        int mismatchPenalty = -1;
        int gapPenalty = -2;

        int[][] dp = new int[n + 1][m + 1];

        // Base cases
        for (int i = 0; i <= n; i++) dp[i][0] = i * gapPenalty;
        for (int j = 0; j <= m; j++) dp[0][j] = j * gapPenalty;

        // DP Table Filling
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int match = dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? matchScore : mismatchPenalty);
                int delete = dp[i - 1][j] + gapPenalty;
                int insert = dp[i][j - 1] + gapPenalty;
                dp[i][j] = Math.max(match, Math.max(delete, insert));
            }
        }

        // Backtrack
        StringBuilder alignA = new StringBuilder();
        StringBuilder symbols = new StringBuilder();
        StringBuilder alignB = new StringBuilder();

        int i = n, j = m;
        int matches = 0, mismatches = 0, gaps = 0;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? matchScore : mismatchPenalty)) {
                alignA.append(a.charAt(i - 1));
                alignB.append(b.charAt(j - 1));
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    symbols.append("|");
                    matches++;
                } else {
                    symbols.append(".");
                    mismatches++;
                }
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + gapPenalty) {
                alignA.append(a.charAt(i - 1));
                alignB.append("-");
                symbols.append(" ");
                gaps++;
                i--;
            } else {
                alignA.append("-");
                alignB.append(b.charAt(j - 1));
                symbols.append(" ");
                gaps++;
                j--;
            }
        }

        alignA.reverse();
        symbols.reverse();
        alignB.reverse();

        int totalLen = alignA.length();
        double similarityPercent = totalLen > 0 ? Math.round(((double) matches / totalLen) * 10000.0) / 100.0 : 0.0;
        long executionTime = System.nanoTime() - startTime;
        String insight = "Needleman-Wunsch DP alignment yielded optimal score " + dp[n][m] + " with " + matches +
                " exact matches, " + mismatches + " substitutions, and " + gaps + " indels across length " + totalLen + ".";

        return new AlignmentResult(a, b, alignA.toString(), symbols.toString(), alignB.toString(),
                dp[n][m], matches, mismatches, gaps, similarityPercent, dp, executionTime, insight);
    }
}

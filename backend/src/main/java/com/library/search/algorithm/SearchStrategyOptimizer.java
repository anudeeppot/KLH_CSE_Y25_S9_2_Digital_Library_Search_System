package com.library.search.algorithm;

import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

/**
 * Module 5: Practical Optimization Algorithm for Digital Library Systems.
 *
 * "Best Search Strategy Selection"
 * Dynamically selects the most optimal string searching or fuzzy retrieval algorithm
 * based on input pattern characteristics, text size, alphabet entropy, and typo probability.
 */
@Component
public class SearchStrategyOptimizer {

    public static class StrategyDecision {
        private final String recommendedAlgorithm;
        private final String rationale;
        private final double estimatedEfficiencyScore;
        private final String complexityProfile;

        public StrategyDecision(String recommendedAlgorithm, String rationale, double estimatedEfficiencyScore, String complexityProfile) {
            this.recommendedAlgorithm = recommendedAlgorithm;
            this.rationale = rationale;
            this.estimatedEfficiencyScore = estimatedEfficiencyScore;
            this.complexityProfile = complexityProfile;
        }

        public String getRecommendedAlgorithm() { return recommendedAlgorithm; }
        public String getRationale() { return rationale; }
        public double getEstimatedEfficiencyScore() { return estimatedEfficiencyScore; }
        public String getComplexityProfile() { return complexityProfile; }
    }

    public StrategyDecision selectOptimalAlgorithm(String query, int totalCorpusCharacters, boolean typoSuspected) {
        if (query == null || query.trim().isEmpty()) {
            return new StrategyDecision("KMP", "Query is empty; defaulting to linear-time deterministic scanner.", 50.0, "O(n + m)");
        }

        String trimmed = query.trim();
        int m = trimmed.length();

        // 1. If typo is suspected or user is searching conversational text with potential spelling mistake
        if (typoSuspected || trimmed.contains(" ")) {
            // Check if it looks like a typo (e.g. repeated consonant or missing vowel)
            return new StrategyDecision(
                    "FUZZY",
                    "Query suggests possible typographical error or multi-term query. Edit Distance dynamic programming selected to match closest library titles/keywords.",
                    85.0,
                    "Time: O(n * m), Space: O(n * m)"
            );
        }

        // Measure alphabet diversity in pattern
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : trimmed.toCharArray()) {
            uniqueChars.add(c);
        }
        double diversityRatio = (double) uniqueChars.size() / m;

        // 2. Short pattern (1-3 chars)
        if (m <= 3) {
            return new StrategyDecision(
                    "KMP",
                    "Short pattern with low shift potential. KMP provides deterministic linear-time scanning with O(m) preprocessing and 0 backtracking overhead.",
                    90.0,
                    "Pre: O(m), Search: O(n), Total: O(n + m)"
            );
        }

        // 3. Medium-to-long pattern with high alphabet diversity in large text corpus
        if (m >= 5 && diversityRatio >= 0.7 && totalCorpusCharacters > 5000) {
            return new StrategyDecision(
                    "BOYER_MOORE",
                    "Longer pattern (" + m + " chars) with high character diversity (" + uniqueChars.size() + " unique chars). Boyer-Moore bad-character heuristic yields maximum shift jumps, achieving sub-linear average performance O(n / m).",
                    98.0,
                    "Best: O(n / m), Average: O(n), Space: O(sigma)"
            );
        }

        // 4. Repeated sub-patterns or small alphabet
        if (diversityRatio < 0.5) {
            return new StrategyDecision(
                    "KMP",
                    "Pattern exhibits high repetition (repetitive prefixes/suffixes). KMP LPS array guarantees linear time without degrading to worst-case mismatch cascades.",
                    92.0,
                    "Pre: O(m), Search: O(n), Total: O(n + m)"
            );
        }

        // 5. Default high-efficiency hash or Boyer-Moore
        return new StrategyDecision(
                "BOYER_MOORE",
                "General natural language search across digital library text. Boyer-Moore provides fastest empirical scan speeds for English text corpora.",
                95.0,
                "Average: O(n), Best: O(n / m)"
        );
    }
}

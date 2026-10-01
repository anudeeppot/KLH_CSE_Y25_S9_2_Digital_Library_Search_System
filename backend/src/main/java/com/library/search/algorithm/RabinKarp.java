package com.library.search.algorithm;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Rabin-Karp String Matching Algorithm using Rolling Hash.
 *
 * Complexity:
 * - Preprocessing: O(m)
 * - Average Time: O(n + m)
 * - Worst-Case Time: O(n * m) (occurs when multiple hash collisions happen)
 * - Auxiliary Space: O(1)
 */
@Component
public class RabinKarp {

    private static final int BASE = 256;
    private static final long MODULUS = 1000000007L;

    public List<Integer> search(String text, String pattern) {
        return searchWithMetrics(text, pattern).getPositions();
    }

    public RabinKarpResult searchWithMetrics(String text, String pattern) {
        long startTime = System.nanoTime();
        List<Integer> positions = new ArrayList<>();
        List<String> steps = new ArrayList<>();
        long hashComparisons = 0;
        long characterComparisons = 0;
        long collisions = 0;

        if (text == null || pattern == null || text.isEmpty() || pattern.isEmpty()) {
            long executionTime = System.nanoTime() - startTime;
            return new RabinKarpResult(positions, hashComparisons, characterComparisons, collisions, executionTime, 0, steps);
        }

        String searchText = text.toLowerCase();
        String searchPattern = pattern.toLowerCase();
        int n = searchText.length();
        int m = searchPattern.length();

        if (m > n) {
            long executionTime = System.nanoTime() - startTime;
            return new RabinKarpResult(positions, hashComparisons, characterComparisons, collisions, executionTime, 0, steps);
        }

        // Calculate h = (BASE^(m-1)) % MODULUS
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % MODULUS;
        }

        long patternHash = 0;
        long textHash = 0;

        // Calculate initial hash value of pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (BASE * patternHash + searchPattern.charAt(i)) % MODULUS;
            textHash = (BASE * textHash + searchText.charAt(i)) % MODULUS;
        }

        steps.add("Base Prime: " + BASE + ", Modulus: " + MODULUS);
        steps.add("Pattern Hash computed: " + patternHash);

        // Slide the pattern over text
        for (int i = 0; i <= n - m; i++) {
            hashComparisons++;

            if (patternHash == textHash) {
                // Potential match, verify character-by-character
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    characterComparisons++;
                    if (searchText.charAt(i + j) != searchPattern.charAt(j)) {
                        match = false;
                        collisions++;
                        if (steps.size() < 40) {
                            steps.add("Window " + i + ": Hash matched (" + textHash + ") but character mismatch at offset " + j + " (Collision!)");
                        }
                        break;
                    }
                }

                if (match) {
                    positions.add(i);
                    if (steps.size() < 40) {
                        steps.add("Window " + i + ": Hash matched (" + textHash + ") -> VERIFIED MATCH at index " + i);
                    }
                }
            } else {
                if (steps.size() < 25) {
                    steps.add("Window " + i + ": Text Hash (" + textHash + ") != Pattern Hash (" + patternHash + ")");
                }
            }

            // Calculate rolling hash for next window:
            // hash(i+1) = (BASE * (hash(i) - text[i]*h) + text[i+m]) % MODULUS
            if (i < n - m) {
                textHash = (BASE * (textHash - searchText.charAt(i) * h) + searchText.charAt(i + m)) % MODULUS;
                if (textHash < 0) {
                    textHash = (textHash + MODULUS);
                }
            }
        }

        long executionTime = System.nanoTime() - startTime;
        return new RabinKarpResult(positions, hashComparisons, characterComparisons, collisions, executionTime, patternHash, steps);
    }

    public static class RabinKarpResult {
        private final List<Integer> positions;
        private final long hashComparisons;
        private final long characterComparisons;
        private final long collisions;
        private final long executionTime;
        private final long patternHash;
        private final List<String> steps;

        public RabinKarpResult(List<Integer> positions, long hashComparisons, long characterComparisons,
                                long collisions, long executionTime, long patternHash, List<String> steps) {
            this.positions = positions;
            this.hashComparisons = hashComparisons;
            this.characterComparisons = characterComparisons;
            this.collisions = collisions;
            this.executionTime = executionTime;
            this.patternHash = patternHash;
            this.steps = steps;
        }

        public List<Integer> getPositions() { return positions; }
        public long getHashComparisons() { return hashComparisons; }
        public long getCharacterComparisons() { return characterComparisons; }
        public long getCollisions() { return collisions; }
        public long getExecutionTime() { return executionTime; }
        public long getPatternHash() { return patternHash; }
        public List<String> getSteps() { return steps; }
    }
}

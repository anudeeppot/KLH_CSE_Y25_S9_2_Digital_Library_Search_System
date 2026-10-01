package com.library.search.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Course Outcome 1 (CO-1):
 * "Evaluate problem-class signatures (substring search, sequence alignment, flow on a network, NP-hard scheduling)
 * and select an appropriate advanced-algorithm strategy."
 *
 * This evaluator inspects input parameters, data structures, and mathematical constraints of a digital library workload
 * to identify its theoretical Problem-Class Signature and dynamically dispatch the optimal advanced algorithm.
 */
@Component
public class ProblemClassSignatureEvaluator {

    public enum ProblemClass {
        SUBSTRING_SEARCH("Exact Substring Matching & Text Indexing", "CO-2", "O(N + M) or O(M log N)"),
        SEQUENCE_ALIGNMENT("Approximate String Matching & Sequence Alignment", "CO-3", "O(N * M) DP"),
        NETWORK_FLOW("Capacity-Constrained Flow & Bipartite Assignment", "CO-4", "O(V * E^2) or O(V^2 * E)"),
        COMBINATORIAL_OPTIMIZATION("Exhaustive/Exponential State Optimization (DP Patterns)", "CO-3", "O(2^N * N) or O(N^3)"),
        NP_HARD_SCHEDULING("NP-Hard Combinatorial Scheduling (Restricted / Heuristic Signature)", "CO-1 Signature", "Exponential / Approximation");

        private final String title;
        private final String mappedCO;
        private final String typicalComplexity;

        ProblemClass(String title, String mappedCO, String typicalComplexity) {
            this.title = title;
            this.mappedCO = mappedCO;
            this.typicalComplexity = typicalComplexity;
        }

        public String getTitle() { return title; }
        public String getMappedCO() { return mappedCO; }
        public String getTypicalComplexity() { return typicalComplexity; }
    }

    public static class SignatureProfile {
        private String queryPattern;
        private int textLength;
        private int patternLength;
        private double alphabetEntropy;
        private boolean multiPatternOrRecurring;
        private boolean toleranceForTypos;
        private boolean hasFlowConstraints;
        private int subsetCardinality;
        private boolean isTreeStructured;

        public SignatureProfile() {}

        public String getQueryPattern() { return queryPattern; }
        public void setQueryPattern(String queryPattern) { this.queryPattern = queryPattern; }
        public int getTextLength() { return textLength; }
        public void setTextLength(int textLength) { this.textLength = textLength; }
        public int getPatternLength() { return patternLength; }
        public void setPatternLength(int patternLength) { this.patternLength = patternLength; }
        public double getAlphabetEntropy() { return alphabetEntropy; }
        public void setAlphabetEntropy(double alphabetEntropy) { this.alphabetEntropy = alphabetEntropy; }
        public boolean isMultiPatternOrRecurring() { return multiPatternOrRecurring; }
        public void setMultiPatternOrRecurring(boolean multiPatternOrRecurring) { this.multiPatternOrRecurring = multiPatternOrRecurring; }
        public boolean isToleranceForTypos() { return toleranceForTypos; }
        public void setToleranceForTypos(boolean toleranceForTypos) { this.toleranceForTypos = toleranceForTypos; }
        public boolean isHasFlowConstraints() { return hasFlowConstraints; }
        public void setHasFlowConstraints(boolean hasFlowConstraints) { this.hasFlowConstraints = hasFlowConstraints; }
        public int getSubsetCardinality() { return subsetCardinality; }
        public void setSubsetCardinality(int subsetCardinality) { this.subsetCardinality = subsetCardinality; }
        public boolean isTreeStructured() { return isTreeStructured; }
        public void setTreeStructured(boolean treeStructured) { isTreeStructured = treeStructured; }
    }

    public static class EvaluationResult {
        private ProblemClass identifiedClass;
        private String selectedStrategy;
        private String justification;
        private String theoreticalTimeComplexity;
        private String theoreticalSpaceComplexity;
        private Map<String, Object> signatureMetrics;
        private List<String> alternativeCandidates;

        public EvaluationResult() {}

        public ProblemClass getIdentifiedClass() { return identifiedClass; }
        public void setIdentifiedClass(ProblemClass identifiedClass) { this.identifiedClass = identifiedClass; }
        public String getSelectedStrategy() { return selectedStrategy; }
        public void setSelectedStrategy(String selectedStrategy) { this.selectedStrategy = selectedStrategy; }
        public String getJustification() { return justification; }
        public void setJustification(String justification) { this.justification = justification; }
        public String getTheoreticalTimeComplexity() { return theoreticalTimeComplexity; }
        public void setTheoreticalTimeComplexity(String theoreticalTimeComplexity) { this.theoreticalTimeComplexity = theoreticalTimeComplexity; }
        public String getTheoreticalSpaceComplexity() { return theoreticalSpaceComplexity; }
        public void setTheoreticalSpaceComplexity(String theoreticalSpaceComplexity) { this.theoreticalSpaceComplexity = theoreticalSpaceComplexity; }
        public Map<String, Object> getSignatureMetrics() { return signatureMetrics; }
        public void setSignatureMetrics(Map<String, Object> signatureMetrics) { this.signatureMetrics = signatureMetrics; }
        public List<String> getAlternativeCandidates() { return alternativeCandidates; }
        public void setAlternativeCandidates(List<String> alternativeCandidates) { this.alternativeCandidates = alternativeCandidates; }
    }

    public EvaluationResult evaluateSignature(SignatureProfile profile) {
        EvaluationResult result = new EvaluationResult();
        Map<String, Object> metrics = new LinkedHashMap<>();

        // Calculate Shannon Entropy of query string if provided
        double entropy = profile.getAlphabetEntropy();
        if (profile.getQueryPattern() != null && !profile.getQueryPattern().isEmpty()) {
            entropy = calculateShannonEntropy(profile.getQueryPattern());
            profile.setPatternLength(profile.getQueryPattern().length());
        }
        metrics.put("patternLength", profile.getPatternLength());
        metrics.put("textLength", profile.getTextLength());
        metrics.put("shannonEntropy", Math.round(entropy * 1000.0) / 1000.0);
        metrics.put("toleranceForTypos", profile.isToleranceForTypos());
        metrics.put("hasFlowConstraints", profile.isHasFlowConstraints());
        metrics.put("subsetCardinality", profile.getSubsetCardinality());
        metrics.put("isTreeStructured", profile.isTreeStructured());

        // Decision Tree based on Problem-Class Signatures:

        // 1. Flow on a Network Signature
        if (profile.isHasFlowConstraints()) {
            result.setIdentifiedClass(ProblemClass.NETWORK_FLOW);
            result.setSelectedStrategy("Dinic's Algorithm / Edmonds-Karp with Max-Flow / Min-Cut Duality");
            result.setJustification("Problem exhibits source-sink conservation, capacity constraints, and bipartite matching signatures. Solvable via augmenting paths (Edmonds-Karp O(V E^2)) or blocking flows on level graphs (Dinic O(V^2 E)).");
            result.setTheoreticalTimeComplexity("Dinic: O(V^2 * E), Edmonds-Karp: O(V * E^2), Unit networks: O(E * sqrt(V))");
            result.setTheoreticalSpaceComplexity("O(V + E) for residual graph and level arrays");
            result.setAlternativeCandidates(Arrays.asList("Ford-Fulkerson (DFS augmenting)", "Push-Relabel (Pre-flow)"));
            result.setSignatureMetrics(metrics);
            return result;
        }

        // 2. Tree / Subset / Interval Combinatorial Optimization Signatures (CO-3)
        if (profile.isTreeStructured()) {
            result.setIdentifiedClass(ProblemClass.COMBINATORIAL_OPTIMIZATION);
            result.setSelectedStrategy("DP on Trees (Post-order Subtree Traversal)");
            result.setJustification("Knowledge taxonomy exhibits optimal substructure over a directed tree hierarchy. DP on Trees yields optimal sub-tree resource/category selection in linear O(N) time.");
            result.setTheoreticalTimeComplexity("O(N) where N is the number of taxonomy nodes");
            result.setTheoreticalSpaceComplexity("O(N) auxiliary call-stack / memoization table");
            result.setAlternativeCandidates(Arrays.asList("Tree Knapsack DP", "Heavy-Light Decomposition"));
            result.setSignatureMetrics(metrics);
            return result;
        }

        if (profile.getSubsetCardinality() > 0 && profile.getSubsetCardinality() <= 20) {
            result.setIdentifiedClass(ProblemClass.COMBINATORIAL_OPTIMIZATION);
            result.setSelectedStrategy("Bitmask Dynamic Programming (O(2^N * N))");
            result.setJustification("State space is represented by binary subsets of cardinality N <= 20 (e.g. syllabus topic coverage). Bitmask DP avoids O(N!) brute force by memoizing 2^N state transitions.");
            result.setTheoreticalTimeComplexity("O(2^N * N) or O(3^N) for submask iteration");
            result.setTheoreticalSpaceComplexity("O(2^N) state array");
            result.setAlternativeCandidates(Arrays.asList("Branch and Bound", "Integer Linear Programming (ILP)"));
            result.setSignatureMetrics(metrics);
            return result;
        }

        // 3. Sequence Alignment / Edit Distance Signature
        if (profile.isToleranceForTypos()) {
            result.setIdentifiedClass(ProblemClass.SEQUENCE_ALIGNMENT);
            result.setSelectedStrategy("Dynamic Programming Sequence Alignment (Needleman-Wunsch / Levenshtein)");
            result.setJustification("Workload requires edit operations (insertions, deletions, substitutions) with optimal alignment prefix subproblems. 2D DP matrix guarantees optimal alignment score.");
            result.setTheoreticalTimeComplexity("O(N * M) time via dynamic programming table filling");
            result.setTheoreticalSpaceComplexity("O(N * M) standard, reducible to O(min(N, M)) with Hirschberg space optimization");
            result.setAlternativeCandidates(Arrays.asList("Smith-Waterman (Local Alignment)", "Ukkonen's Banded DP"));
            result.setSignatureMetrics(metrics);
            return result;
        }

        // 4. Substring Search & Suffix-Based Indexing Signature (CO-2)
        result.setIdentifiedClass(ProblemClass.SUBSTRING_SEARCH);

        if (profile.isMultiPatternOrRecurring()) {
            result.setSelectedStrategy("Suffix Array + LCP Array (Kasai) OR Suffix Automaton (DAWG)");
            result.setJustification("Corpus text is static while queries are recurring. Amortized preprocessing with Suffix Array / Suffix Automaton allows binary search query answering in O(M log N) or O(M) time.");
            result.setTheoreticalTimeComplexity("Preprocessing: O(N log N) / O(N), Query: O(M log N) with Suffix Array or O(M) with Suffix Automaton");
            result.setTheoreticalSpaceComplexity("O(N) auxiliary space for sorted suffix index and LCP array");
            result.setAlternativeCandidates(Arrays.asList("Rabin-Karp Rolling Hash for multi-term fingerprinting", "Aho-Corasick Automaton"));
        } else if (entropy < 2.0 && profile.getPatternLength() > 4) {
            result.setSelectedStrategy("Knuth-Morris-Pratt (KMP) with Prefix Function / LPS");
            result.setJustification("Pattern exhibits low Shannon entropy (" + Math.round(entropy * 100.0) / 100.0 + ") indicating repeated prefixes/suffixes. KMP avoids rollback via the Failure Function (pi array).");
            result.setTheoreticalTimeComplexity("Preprocessing: O(M), Scanning: O(N), Total: O(N + M)");
            result.setTheoreticalSpaceComplexity("O(M) for LPS / pi array");
            result.setAlternativeCandidates(Arrays.asList("Z-Algorithm", "Finite State Automaton"));
        } else if (profile.getPatternLength() > 6 && entropy >= 2.8) {
            result.setSelectedStrategy("Boyer-Moore Bad Character Shift");
            result.setJustification("High Shannon entropy and longer pattern length allow sub-linear character skips (O(N / M) best case) by scanning right-to-left.");
            result.setTheoreticalTimeComplexity("Best Case: O(N / M), Average: O(N), Worst Case: O(N * M)");
            result.setTheoreticalSpaceComplexity("O(sigma) for alphabet shift table");
            result.setAlternativeCandidates(Arrays.asList("Turbo Boyer-Moore", "KMP"));
        } else {
            result.setSelectedStrategy("Z-Algorithm (Z-Function Linear Pattern Matching)");
            result.setJustification("Balanced string search signature. Constructing string S = P + '$' + T and computing the Z-box in linear O(N + M) time directly yields all occurrences where Z[i] == |P|.");
            result.setTheoreticalTimeComplexity("O(N + M) strictly linear time");
            result.setTheoreticalSpaceComplexity("O(N + M) for Z-array");
            result.setAlternativeCandidates(Arrays.asList("KMP Algorithm", "Rabin-Karp Rolling Hash"));
        }

        result.setSignatureMetrics(metrics);
        return result;
    }

    private double calculateShannonEntropy(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        Map<Character, Integer> counts = new HashMap<>();
        for (char c : s.toCharArray()) {
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }
        double entropy = 0.0;
        int len = s.length();
        for (int count : counts.values()) {
            double p = (double) count / len;
            entropy -= p * (Math.log(p) / Math.log(2));
        }
        return entropy;
    }
}

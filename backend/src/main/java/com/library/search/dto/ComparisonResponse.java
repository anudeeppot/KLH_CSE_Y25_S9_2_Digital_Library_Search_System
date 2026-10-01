package com.library.search.dto;

import java.util.List;

public class ComparisonResponse {
    private String text;
    private String pattern;
    private List<AlgorithmMetric> metrics;
    private String fastestAlgorithm;
    private String explanation;

    public ComparisonResponse() {}

    public ComparisonResponse(String text, String pattern, List<AlgorithmMetric> metrics,
                              String fastestAlgorithm, String explanation) {
        this.text = text;
        this.pattern = pattern;
        this.metrics = metrics;
        this.fastestAlgorithm = fastestAlgorithm;
        this.explanation = explanation;
    }

    public static class AlgorithmMetric {
        private String name;
        private long executionTimeNanos;
        private double executionTimeMs;
        private long comparisons;
        private int matchesFound;
        private String bestCase;
        private String averageCase;
        private String worstCase;
        private String spaceComplexity;

        public AlgorithmMetric() {}

        public AlgorithmMetric(String name, long executionTimeNanos, double executionTimeMs,
                               long comparisons, int matchesFound, String bestCase,
                               String averageCase, String worstCase, String spaceComplexity) {
            this.name = name;
            this.executionTimeNanos = executionTimeNanos;
            this.executionTimeMs = executionTimeMs;
            this.comparisons = comparisons;
            this.matchesFound = matchesFound;
            this.bestCase = bestCase;
            this.averageCase = averageCase;
            this.worstCase = worstCase;
            this.spaceComplexity = spaceComplexity;
        }

        public String getName() { return name; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public double getExecutionTimeMs() { return executionTimeMs; }
        public long getComparisons() { return comparisons; }
        public int getMatchesFound() { return matchesFound; }
        public String getBestCase() { return bestCase; }
        public String getAverageCase() { return averageCase; }
        public String getWorstCase() { return worstCase; }
        public String getSpaceComplexity() { return spaceComplexity; }
    }

    public String getText() { return text; }
    public String getPattern() { return pattern; }
    public List<AlgorithmMetric> getMetrics() { return metrics; }
    public String getFastestAlgorithm() { return fastestAlgorithm; }
    public String getExplanation() { return explanation; }
}

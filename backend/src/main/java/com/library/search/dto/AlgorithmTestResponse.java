package com.library.search.dto;

import java.util.List;
import java.util.Map;

public class AlgorithmTestResponse {
    private String algorithm;
    private String text;
    private String pattern;
    private boolean found;
    private List<Integer> positions;
    private long comparisons;
    private long executionTime; // Nanoseconds
    private double executionTimeMs;
    private int[] lps;
    private long collisions;
    private long patternHash;
    private Map<Character, Integer> badCharacterTable;
    private List<String> steps;
    private String timeComplexity;
    private String spaceComplexity;

    public AlgorithmTestResponse() {}

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public boolean isFound() { return found; }
    public void setFound(boolean found) { this.found = found; }

    public List<Integer> getPositions() { return positions; }
    public void setPositions(List<Integer> positions) { this.positions = positions; }

    public long getComparisons() { return comparisons; }
    public void setComparisons(long comparisons) { this.comparisons = comparisons; }

    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }

    public double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(double executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public int[] getLps() { return lps; }
    public void setLps(int[] lps) { this.lps = lps; }

    public long getCollisions() { return collisions; }
    public void setCollisions(long collisions) { this.collisions = collisions; }

    public long getPatternHash() { return patternHash; }
    public void setPatternHash(long patternHash) { this.patternHash = patternHash; }

    public Map<Character, Integer> getBadCharacterTable() { return badCharacterTable; }
    public void setBadCharacterTable(Map<Character, Integer> badCharacterTable) { this.badCharacterTable = badCharacterTable; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public String getTimeComplexity() { return timeComplexity; }
    public void setTimeComplexity(String timeComplexity) { this.timeComplexity = timeComplexity; }

    public String getSpaceComplexity() { return spaceComplexity; }
    public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }
}

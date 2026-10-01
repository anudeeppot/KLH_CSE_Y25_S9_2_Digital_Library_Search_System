package com.library.search.dto;

import java.util.List;

public class SearchResponse {
    private String query;
    private String algorithmUsed;
    private String selectionRationale;
    private long executionTimeNanos;
    private double executionTimeMs;
    private int totalResults;
    private String didYouMean;
    private List<SearchResultItem> results;

    public SearchResponse() {}

    public SearchResponse(String query, String algorithmUsed, String selectionRationale,
                          long executionTimeNanos, double executionTimeMs, int totalResults,
                          String didYouMean, List<SearchResultItem> results) {
        this.query = query;
        this.algorithmUsed = algorithmUsed;
        this.selectionRationale = selectionRationale;
        this.executionTimeNanos = executionTimeNanos;
        this.executionTimeMs = executionTimeMs;
        this.totalResults = totalResults;
        this.didYouMean = didYouMean;
        this.results = results;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getAlgorithmUsed() { return algorithmUsed; }
    public void setAlgorithmUsed(String algorithmUsed) { this.algorithmUsed = algorithmUsed; }

    public String getSelectionRationale() { return selectionRationale; }
    public void setSelectionRationale(String selectionRationale) { this.selectionRationale = selectionRationale; }

    public long getExecutionTimeNanos() { return executionTimeNanos; }
    public void setExecutionTimeNanos(long executionTimeNanos) { this.executionTimeNanos = executionTimeNanos; }

    public double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(double executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }

    public String getDidYouMean() { return didYouMean; }
    public void setDidYouMean(String didYouMean) { this.didYouMean = didYouMean; }

    public List<SearchResultItem> getResults() { return results; }
    public void setResults(List<SearchResultItem> results) { this.results = results; }
}

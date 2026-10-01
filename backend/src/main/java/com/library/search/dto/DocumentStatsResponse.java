package com.library.search.dto;

import java.util.Map;

public class DocumentStatsResponse {
    private long totalDocuments;
    private long totalBooks;
    private long totalResearchPapers;
    private long totalJournals;
    private long totalMagazines;
    private long totalCitations;
    private int supportedAlgorithmsCount;
    private Map<String, Long> categoryDistribution;

    public DocumentStatsResponse() {}

    public DocumentStatsResponse(long totalDocuments, long totalBooks, long totalResearchPapers,
                                 long totalJournals, long totalMagazines, long totalCitations,
                                 int supportedAlgorithmsCount, Map<String, Long> categoryDistribution) {
        this.totalDocuments = totalDocuments;
        this.totalBooks = totalBooks;
        this.totalResearchPapers = totalResearchPapers;
        this.totalJournals = totalJournals;
        this.totalMagazines = totalMagazines;
        this.totalCitations = totalCitations;
        this.supportedAlgorithmsCount = supportedAlgorithmsCount;
        this.categoryDistribution = categoryDistribution;
    }

    public long getTotalDocuments() { return totalDocuments; }
    public long getTotalBooks() { return totalBooks; }
    public long getTotalResearchPapers() { return totalResearchPapers; }
    public long getTotalJournals() { return totalJournals; }
    public long getTotalMagazines() { return totalMagazines; }
    public long getTotalCitations() { return totalCitations; }
    public int getSupportedAlgorithmsCount() { return supportedAlgorithmsCount; }
    public Map<String, Long> getCategoryDistribution() { return categoryDistribution; }
}

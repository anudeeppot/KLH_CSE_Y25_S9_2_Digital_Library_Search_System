package com.library.search.dto;

import com.library.search.model.LibraryDocument;
import java.util.List;

public class SearchResultItem {
    private LibraryDocument document;
    private String matchedField;
    private List<Integer> positions;
    private int matchCount;
    private double relevanceScore;
    private String highlightedSnippet;

    public SearchResultItem() {}

    public SearchResultItem(LibraryDocument document, String matchedField, List<Integer> positions,
                            int matchCount, double relevanceScore, String highlightedSnippet) {
        this.document = document;
        this.matchedField = matchedField;
        this.positions = positions;
        this.matchCount = matchCount;
        this.relevanceScore = relevanceScore;
        this.highlightedSnippet = highlightedSnippet;
    }

    public LibraryDocument getDocument() { return document; }
    public void setDocument(LibraryDocument document) { this.document = document; }

    public String getMatchedField() { return matchedField; }
    public void setMatchedField(String matchedField) { this.matchedField = matchedField; }

    public List<Integer> getPositions() { return positions; }
    public void setPositions(List<Integer> positions) { this.positions = positions; }

    public int getMatchCount() { return matchCount; }
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }

    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }

    public String getHighlightedSnippet() { return highlightedSnippet; }
    public void setHighlightedSnippet(String highlightedSnippet) { this.highlightedSnippet = highlightedSnippet; }
}

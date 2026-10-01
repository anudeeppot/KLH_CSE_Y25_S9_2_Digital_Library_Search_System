package com.library.search.dto;

import java.util.List;

public class SimilarDocumentItem {
    private Long id;
    private String title;
    private String author;
    private String category;
    private String documentType;
    private double similarityScore;
    private double similarityPercentage;
    private List<String> sharedKeywords;

    public SimilarDocumentItem() {}

    public SimilarDocumentItem(Long id, String title, String author, String category,
                               String documentType, double similarityScore, double similarityPercentage,
                               List<String> sharedKeywords) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.documentType = documentType;
        this.similarityScore = similarityScore;
        this.similarityPercentage = similarityPercentage;
        this.sharedKeywords = sharedKeywords;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getDocumentType() { return documentType; }
    public double getSimilarityScore() { return similarityScore; }
    public double getSimilarityPercentage() { return similarityPercentage; }
    public List<String> getSharedKeywords() { return sharedKeywords; }
}

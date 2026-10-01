package com.library.search.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a Digital Library Document.
 * Integrates and extends the original LibraryDocument class with JPA persistence,
 * ISBN, document types, abstract, keywords, and citations for DSA-3 graph analysis.
 */
@Entity
@Table(name = "documents")
public class LibraryDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(nullable = false, length = 500)
    private String title = "";

    @Column(nullable = false, length = 500)
    private String author = "";

    private String category = "";

    @Column(name = "publication_year")
    private String year = "";

    private String isbn = "";

    @Column(name = "document_type")
    private String documentType = "Research Paper"; // Book, Journal, Research Paper, Magazine

    @Column(length = 1000)
    private String keywords = "";

    @Lob
    @Column(name = "abstract_text", columnDefinition = "TEXT")
    private String abstractText = "";

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content = "";

    @Column(name = "citation_count")
    private int citationCount = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "document_citation_keys", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "citation_target")
    private List<String> citations = new ArrayList<>();

    // Default constructor for JPA
    public LibraryDocument() {}

    // Preserving the original constructor from the user's codebase
    public LibraryDocument(String fileName) {
        this.fileName = fileName;
    }

    // Getters and Setters preserved and extended
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title != null ? title : ""; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author != null ? author : ""; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category != null ? category : ""; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year != null ? year : ""; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn != null ? isbn : ""; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType != null ? documentType : "Research Paper"; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords != null ? keywords : ""; }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText != null ? abstractText : ""; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content != null ? content : ""; }

    public int getCitationCount() { return citationCount; }
    public void setCitationCount(int citationCount) { this.citationCount = citationCount; }

    public List<String> getCitations() { return citations; }
    public void setCitations(List<String> citations) { this.citations = citations != null ? citations : new ArrayList<>(); }
}

package com.library.search.dto;

public class SearchRequest {
    private String query;
    private String algorithm = "AUTO"; // AUTO, KMP, RABIN_KARP, BOYER_MOORE, FUZZY, SUFFIX_ARRAY
    private String category;
    private String documentType;

    public SearchRequest() {}

    public SearchRequest(String query, String algorithm) {
        this.query = query;
        this.algorithm = algorithm;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
}

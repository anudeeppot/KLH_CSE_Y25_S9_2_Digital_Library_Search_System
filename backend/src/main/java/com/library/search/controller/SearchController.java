package com.library.search.controller;

import com.library.search.dto.SearchRequest;
import com.library.search.dto.SearchResponse;
import com.library.search.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<SearchResponse> searchGet(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(name = "algorithm", defaultValue = "AUTO") String algorithm,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "documentType", required = false) String documentType) {

        SearchRequest request = new SearchRequest(query, algorithm);
        request.setCategory(category);
        request.setDocumentType(documentType);

        SearchResponse response = searchService.search(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SearchResponse> searchPost(@RequestBody SearchRequest request) {
        SearchResponse response = searchService.search(request);
        return ResponseEntity.ok(response);
    }
}

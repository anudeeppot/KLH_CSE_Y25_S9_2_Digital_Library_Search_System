package com.library.search.controller;

import com.library.search.dto.DocumentStatsResponse;
import com.library.search.dto.SearchRequest;
import com.library.search.dto.SearchResponse;
import com.library.search.dto.SimilarDocumentItem;
import com.library.search.model.LibraryDocument;
import com.library.search.service.DocumentService;
import com.library.search.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final SearchService searchService;

    public DocumentController(DocumentService documentService, SearchService searchService) {
        this.documentService = documentService;
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<List<LibraryDocument>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibraryDocument> getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LibraryDocument> createDocument(@RequestBody LibraryDocument document) {
        LibraryDocument saved = documentService.saveDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LibraryDocument> uploadDocument(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            LibraryDocument saved = documentService.uploadDocument(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibraryDocument> updateDocument(@PathVariable Long id, @RequestBody LibraryDocument document) {
        return documentService.updateDocument(id, document)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        boolean deleted = documentService.deleteDocument(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> searchDocuments(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(name = "algorithm", defaultValue = "AUTO") String algorithm,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "documentType", required = false) String documentType) {
        SearchRequest req = new SearchRequest(query, algorithm);
        req.setCategory(category);
        req.setDocumentType(documentType);
        return ResponseEntity.ok(searchService.search(req));
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<SimilarDocumentItem>> getSimilarDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getSimilarDocuments(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<DocumentStatsResponse> getStats() {
        return ResponseEntity.ok(documentService.getStats());
    }
}

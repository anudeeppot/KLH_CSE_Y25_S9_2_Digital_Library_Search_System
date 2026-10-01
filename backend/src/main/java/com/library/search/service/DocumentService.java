package com.library.search.service;

import com.library.search.algorithm.DocumentSimilarity;
import com.library.search.dto.DocumentStatsResponse;
import com.library.search.dto.SimilarDocumentItem;
import com.library.search.model.LibraryDocument;
import com.library.search.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentSimilarity documentSimilarity;
    private final CorpusReader corpusReader;

    public DocumentService(DocumentRepository documentRepository,
                           DocumentSimilarity documentSimilarity,
                           CorpusReader corpusReader) {
        this.documentRepository = documentRepository;
        this.documentSimilarity = documentSimilarity;
        this.corpusReader = corpusReader;
    }

    public LibraryDocument uploadDocument(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.txt";
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        LibraryDocument doc = corpusReader.parseDocumentFromString(filename, content);
        if (doc == null) {
            throw new IllegalArgumentException("Could not parse uploaded document.");
        }
        return documentRepository.save(doc);
    }

    public List<LibraryDocument> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<LibraryDocument> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public LibraryDocument saveDocument(LibraryDocument document) {
        return documentRepository.save(document);
    }

    public Optional<LibraryDocument> updateDocument(Long id, LibraryDocument updated) {
        return documentRepository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setAuthor(updated.getAuthor());
            existing.setCategory(updated.getCategory());
            existing.setYear(updated.getYear());
            existing.setIsbn(updated.getIsbn());
            existing.setDocumentType(updated.getDocumentType());
            existing.setKeywords(updated.getKeywords());
            existing.setAbstractText(updated.getAbstractText());
            existing.setContent(updated.getContent());
            return documentRepository.save(existing);
        });
    }

    public boolean deleteDocument(Long id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public DocumentStatsResponse getStats() {
        List<LibraryDocument> all = documentRepository.findAll();
        long totalDocs = all.size();
        long books = 0;
        long papers = 0;
        long journals = 0;
        long magazines = 0;
        long totalCitations = 0;
        Map<String, Long> categoryMap = new HashMap<>();

        for (LibraryDocument doc : all) {
            String type = (doc.getDocumentType() != null) ? doc.getDocumentType().trim().toLowerCase() : "";
            if (type.contains("book")) books++;
            else if (type.contains("paper")) papers++;
            else if (type.contains("journal")) journals++;
            else if (type.contains("magazine")) magazines++;
            else papers++;

            totalCitations += doc.getCitations() != null ? doc.getCitations().size() : 0;

            String cat = (doc.getCategory() != null && !doc.getCategory().isEmpty()) ? doc.getCategory() : "General";
            categoryMap.put(cat, categoryMap.getOrDefault(cat, 0L) + 1);
        }

        return new DocumentStatsResponse(totalDocs, books, papers, journals, magazines, totalCitations, 6, categoryMap);
    }

    public List<SimilarDocumentItem> getSimilarDocuments(Long id) {
        Optional<LibraryDocument> targetOpt = documentRepository.findById(id);
        if (targetOpt.isEmpty()) {
            return Collections.emptyList();
        }

        LibraryDocument target = targetOpt.get();
        String targetText = target.getTitle() + " " + target.getKeywords() + " " + target.getAbstractText() + " " + target.getContent();

        List<LibraryDocument> all = documentRepository.findAll();
        List<SimilarDocumentItem> similarList = new ArrayList<>();

        for (LibraryDocument other : all) {
            if (other.getId().equals(target.getId())) {
                continue;
            }

            String otherText = other.getTitle() + " " + other.getKeywords() + " " + other.getAbstractText() + " " + other.getContent();
            DocumentSimilarity.SimilarityResult sim = documentSimilarity.calculateCosineSimilarity(targetText, otherText);

            if (sim.getPercentage() > 5.0) {
                similarList.add(new SimilarDocumentItem(
                        other.getId(),
                        other.getTitle(),
                        other.getAuthor(),
                        other.getCategory(),
                        other.getDocumentType(),
                        sim.getScore(),
                        sim.getPercentage(),
                        sim.getSharedKeywords()
                ));
            }
        }

        // Sort descending by similarity percentage
        similarList.sort((a, b) -> Double.compare(b.getSimilarityPercentage(), a.getSimilarityPercentage()));

        if (similarList.size() > 6) {
            return similarList.subList(0, 6);
        }
        return similarList;
    }
}

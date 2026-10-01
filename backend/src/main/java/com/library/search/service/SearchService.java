package com.library.search.service;

import com.library.search.algorithm.*;
import com.library.search.dto.SearchRequest;
import com.library.search.dto.SearchResponse;
import com.library.search.dto.SearchResultItem;
import com.library.search.model.LibraryDocument;
import com.library.search.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Search Service for the Digital Library.
 * Coordinates exact pattern searching (KMP, Rabin-Karp, Boyer-Moore),
 * indexing retrieval (Suffix Array), typo-tolerant matching (Edit Distance Fuzzy Search),
 * and intelligent algorithmic Auto-selection.
 */
@Service
public class SearchService {

    private final DocumentRepository documentRepository;
    private final KMP kmp;
    private final RabinKarp rabinKarp;
    private final BoyerMoore boyerMoore;
    private final EditDistance editDistance;
    private final SuffixArray suffixArray;
    private final SearchStrategyOptimizer strategyOptimizer;

    public SearchService(DocumentRepository documentRepository,
                         KMP kmp,
                         RabinKarp rabinKarp,
                         BoyerMoore boyerMoore,
                         EditDistance editDistance,
                         SuffixArray suffixArray,
                         SearchStrategyOptimizer strategyOptimizer) {
        this.documentRepository = documentRepository;
        this.kmp = kmp;
        this.rabinKarp = rabinKarp;
        this.boyerMoore = boyerMoore;
        this.editDistance = editDistance;
        this.suffixArray = suffixArray;
        this.strategyOptimizer = strategyOptimizer;
    }

    public SearchResponse search(SearchRequest request) {
        long startTime = System.nanoTime();

        String rawQuery = (request.getQuery() != null) ? request.getQuery().trim() : "";
        String selectedAlgo = (request.getAlgorithm() != null) ? request.getAlgorithm().toUpperCase().trim() : "AUTO";

        if (rawQuery.isEmpty()) {
            return new SearchResponse(rawQuery, selectedAlgo, "Empty search query provided.", 0, 0.0, 0, null, Collections.emptyList());
        }

        List<LibraryDocument> documents = documentRepository.findAll();
        // Optional filtering by category or docType
        if (request.getCategory() != null && !request.getCategory().equalsIgnoreCase("All") && !request.getCategory().isEmpty()) {
            documents = documents.stream().filter(d -> d.getCategory() != null && d.getCategory().equalsIgnoreCase(request.getCategory())).toList();
        }
        if (request.getDocumentType() != null && !request.getDocumentType().equalsIgnoreCase("All") && !request.getDocumentType().isEmpty()) {
            documents = documents.stream().filter(d -> d.getDocumentType() != null && d.getDocumentType().equalsIgnoreCase(request.getDocumentType())).toList();
        }

        int totalCorpusChars = documents.stream().mapToInt(d -> (d.getTitle() + d.getContent()).length()).sum();

        String effectiveAlgo = selectedAlgo;
        String rationale = "User explicitly specified " + selectedAlgo + " algorithm.";
        String didYouMean = null;

        // Auto Selection Logic
        if ("AUTO".equals(selectedAlgo)) {
            // First check if exact search would yield matches using KMP across any field
            boolean hasExactMatch = false;
            for (LibraryDocument doc : documents) {
                if (kmp.found(doc.getTitle(), rawQuery) ||
                    kmp.found(doc.getKeywords(), rawQuery) ||
                    kmp.found(doc.getCategory(), rawQuery) ||
                    kmp.found(doc.getAuthor(), rawQuery) ||
                    kmp.found(doc.getContent(), rawQuery)) {
                    hasExactMatch = true;
                    break;
                }
            }

            SearchStrategyOptimizer.StrategyDecision decision = strategyOptimizer.selectOptimalAlgorithm(rawQuery, totalCorpusChars, !hasExactMatch);
            effectiveAlgo = decision.getRecommendedAlgorithm();
            rationale = "Auto-Selected [" + effectiveAlgo + "]: " + decision.getRationale();
        }

        List<SearchResultItem> results = new ArrayList<>();

        if ("KMP".equals(effectiveAlgo)) {
            results = executeKMPSearch(documents, rawQuery);
        } else if ("RABIN_KARP".equals(effectiveAlgo) || "RABIN-KARP".equals(effectiveAlgo)) {
            results = executeRabinKarpSearch(documents, rawQuery);
        } else if ("BOYER_MOORE".equals(effectiveAlgo) || "BOYER-MOORE".equals(effectiveAlgo)) {
            results = executeBoyerMooreSearch(documents, rawQuery);
        } else if ("SUFFIX_ARRAY".equals(effectiveAlgo) || "SUFFIX-ARRAY".equals(effectiveAlgo)) {
            results = executeSuffixArraySearch(documents, rawQuery);
        } else if ("FUZZY".equals(effectiveAlgo) || "EDIT_DISTANCE".equals(effectiveAlgo)) {
            FuzzyResult fuzzyRes = executeFuzzySearch(documents, rawQuery);
            results = fuzzyRes.items;
            didYouMean = fuzzyRes.suggestedQuery;
        } else {
            // Fallback to KMP
            results = executeKMPSearch(documents, rawQuery);
            rationale = "Unrecognized algorithm '" + effectiveAlgo + "'; defaulted to KMP.";
        }

        // If exact search yielded 0 results and wasn't fuzzy, check if a fuzzy alternative exists
        if (results.isEmpty() && !"FUZZY".equals(effectiveAlgo) && !"EDIT_DISTANCE".equals(effectiveAlgo)) {
            FuzzyResult fuzzyCheck = executeFuzzySearch(documents, rawQuery);
            if (!fuzzyCheck.items.isEmpty()) {
                didYouMean = fuzzyCheck.suggestedQuery;
                if (didYouMean != null && !didYouMean.equalsIgnoreCase(rawQuery)) {
                    results = fuzzyCheck.items;
                    rationale += " [No exact match. Activated Fuzzy Fallback with suggested query: '" + didYouMean + "']";
                }
            }
        }

        // Sort results by relevance descending
        results.sort((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()));

        long executionTimeNanos = System.nanoTime() - startTime;
        double executionTimeMs = Math.round((executionTimeNanos / 1_000_000.0) * 1000.0) / 1000.0;

        return new SearchResponse(rawQuery, effectiveAlgo, rationale, executionTimeNanos, executionTimeMs, results.size(), didYouMean, results);
    }

    private List<SearchResultItem> executeKMPSearch(List<LibraryDocument> documents, String query) {
        List<SearchResultItem> results = new ArrayList<>();
        for (LibraryDocument doc : documents) {
            List<Integer> titlePos = kmp.search(doc.getTitle(), query);
            List<Integer> authorPos = kmp.search(doc.getAuthor(), query);
            List<Integer> categoryPos = kmp.search(doc.getCategory(), query);
            List<Integer> isbnPos = kmp.search(doc.getIsbn(), query);
            List<Integer> keywordPos = kmp.search(doc.getKeywords(), query);
            List<Integer> abstractPos = kmp.search(doc.getAbstractText(), query);
            List<Integer> contentPos = kmp.search(doc.getContent(), query);

            int totalMatches = titlePos.size() + authorPos.size() + categoryPos.size() +
                               isbnPos.size() + keywordPos.size() + abstractPos.size() + contentPos.size();
            if (totalMatches > 0) {
                String matchedField = !titlePos.isEmpty() ? "Title" :
                        (!keywordPos.isEmpty() ? "Keywords" :
                        (!categoryPos.isEmpty() ? "Category" :
                        (!authorPos.isEmpty() ? "Author" :
                        (!isbnPos.isEmpty() ? "ISBN" :
                        (!abstractPos.isEmpty() ? "Abstract" : "Content")))));

                List<Integer> bestPositions = !titlePos.isEmpty() ? titlePos :
                        (!keywordPos.isEmpty() ? keywordPos :
                        (!categoryPos.isEmpty() ? categoryPos : contentPos));

                double score = titlePos.size() * 10.0 + keywordPos.size() * 6.0 + categoryPos.size() * 5.0 +
                               authorPos.size() * 4.0 + isbnPos.size() * 8.0 + abstractPos.size() * 2.0 + contentPos.size();
                String snippet = extractSnippet(doc.getContent(), contentPos, query);

                results.add(new SearchResultItem(doc, matchedField, bestPositions, totalMatches, score, snippet));
            }
        }
        return results;
    }

    private List<SearchResultItem> executeRabinKarpSearch(List<LibraryDocument> documents, String query) {
        List<SearchResultItem> results = new ArrayList<>();
        for (LibraryDocument doc : documents) {
            List<Integer> titlePos = rabinKarp.search(doc.getTitle(), query);
            List<Integer> authorPos = rabinKarp.search(doc.getAuthor(), query);
            List<Integer> categoryPos = rabinKarp.search(doc.getCategory(), query);
            List<Integer> isbnPos = rabinKarp.search(doc.getIsbn(), query);
            List<Integer> keywordPos = rabinKarp.search(doc.getKeywords(), query);
            List<Integer> abstractPos = rabinKarp.search(doc.getAbstractText(), query);
            List<Integer> contentPos = rabinKarp.search(doc.getContent(), query);

            int totalMatches = titlePos.size() + authorPos.size() + categoryPos.size() +
                               isbnPos.size() + keywordPos.size() + abstractPos.size() + contentPos.size();
            if (totalMatches > 0) {
                String matchedField = !titlePos.isEmpty() ? "Title" :
                        (!keywordPos.isEmpty() ? "Keywords" :
                        (!categoryPos.isEmpty() ? "Category" :
                        (!authorPos.isEmpty() ? "Author" :
                        (!isbnPos.isEmpty() ? "ISBN" :
                        (!abstractPos.isEmpty() ? "Abstract" : "Content")))));

                List<Integer> bestPositions = !titlePos.isEmpty() ? titlePos :
                        (!keywordPos.isEmpty() ? keywordPos : contentPos);

                double score = titlePos.size() * 10.0 + keywordPos.size() * 6.0 + categoryPos.size() * 5.0 +
                               authorPos.size() * 4.0 + isbnPos.size() * 8.0 + abstractPos.size() * 2.0 + contentPos.size();
                String snippet = extractSnippet(doc.getContent(), contentPos, query);

                results.add(new SearchResultItem(doc, matchedField, bestPositions, totalMatches, score, snippet));
            }
        }
        return results;
    }

    private List<SearchResultItem> executeBoyerMooreSearch(List<LibraryDocument> documents, String query) {
        List<SearchResultItem> results = new ArrayList<>();
        for (LibraryDocument doc : documents) {
            List<Integer> titlePos = boyerMoore.search(doc.getTitle(), query);
            List<Integer> authorPos = boyerMoore.search(doc.getAuthor(), query);
            List<Integer> categoryPos = boyerMoore.search(doc.getCategory(), query);
            List<Integer> isbnPos = boyerMoore.search(doc.getIsbn(), query);
            List<Integer> keywordPos = boyerMoore.search(doc.getKeywords(), query);
            List<Integer> abstractPos = boyerMoore.search(doc.getAbstractText(), query);
            List<Integer> contentPos = boyerMoore.search(doc.getContent(), query);

            int totalMatches = titlePos.size() + authorPos.size() + categoryPos.size() +
                               isbnPos.size() + keywordPos.size() + abstractPos.size() + contentPos.size();
            if (totalMatches > 0) {
                String matchedField = !titlePos.isEmpty() ? "Title" :
                        (!keywordPos.isEmpty() ? "Keywords" :
                        (!categoryPos.isEmpty() ? "Category" :
                        (!authorPos.isEmpty() ? "Author" :
                        (!isbnPos.isEmpty() ? "ISBN" :
                        (!abstractPos.isEmpty() ? "Abstract" : "Content")))));

                List<Integer> bestPositions = !titlePos.isEmpty() ? titlePos :
                        (!keywordPos.isEmpty() ? keywordPos : contentPos);

                double score = titlePos.size() * 10.0 + keywordPos.size() * 6.0 + categoryPos.size() * 5.0 +
                               authorPos.size() * 4.0 + isbnPos.size() * 8.0 + abstractPos.size() * 2.0 + contentPos.size();
                String snippet = extractSnippet(doc.getContent(), contentPos, query);

                results.add(new SearchResultItem(doc, matchedField, bestPositions, totalMatches, score, snippet));
            }
        }
        return results;
    }

    private List<SearchResultItem> executeSuffixArraySearch(List<LibraryDocument> documents, String query) {
        List<SearchResultItem> results = new ArrayList<>();
        for (LibraryDocument doc : documents) {
            SuffixArray.SuffixArrayResult saResult = suffixArray.buildAndSearch(doc.getContent(), query);
            List<Integer> contentPos = saResult.getMatchPositions();
            List<Integer> titlePos = kmp.search(doc.getTitle(), query);
            List<Integer> keywordPos = kmp.search(doc.getKeywords(), query);
            List<Integer> categoryPos = kmp.search(doc.getCategory(), query);

            int totalMatches = contentPos.size() + titlePos.size() + keywordPos.size() + categoryPos.size();
            if (totalMatches > 0) {
                String matchedField = !titlePos.isEmpty() ? "Title" :
                        (!keywordPos.isEmpty() ? "Keywords" :
                        (!categoryPos.isEmpty() ? "Category" : "Indexed Content (Suffix Array)"));

                double score = titlePos.size() * 10.0 + keywordPos.size() * 6.0 + categoryPos.size() * 5.0 + contentPos.size();
                String snippet = extractSnippet(doc.getContent(), contentPos, query);

                results.add(new SearchResultItem(doc, matchedField, contentPos, totalMatches, score, snippet));
            }
        }
        return results;
    }

    private static class FuzzyResult {
        List<SearchResultItem> items = new ArrayList<>();
        String suggestedQuery;
    }

    private FuzzyResult executeFuzzySearch(List<LibraryDocument> documents, String query) {
        FuzzyResult res = new FuzzyResult();
        String lowerQuery = query.toLowerCase().trim();
        String[] queryTerms = lowerQuery.split("\\s+");

        // Build comprehensive dictionary of vocabulary words from library documents
        Set<String> dictionary = new HashSet<>();
        for (LibraryDocument doc : documents) {
            String combined = (doc.getTitle() + " " + doc.getCategory() + " " + doc.getKeywords() + " " + doc.getAuthor()).toLowerCase();
            String[] tokens = combined.split("[^a-zA-Z0-9]+");
            for (String t : tokens) {
                if (t.length() >= 3) {
                    dictionary.add(t);
                }
            }
        }

        // For each term in user query, find the best matching term in library vocabulary using Edit Distance
        List<String> correctedTerms = new ArrayList<>();
        boolean anyCorrectionMade = false;

        for (String term : queryTerms) {
            if (dictionary.contains(term)) {
                correctedTerms.add(term);
                continue;
            }

            String bestMatch = term;
            int bestDist = Integer.MAX_VALUE;
            double bestSim = 0.0;

            int maxDist = term.length() <= 4 ? 1 : 2;

            for (String dictWord : dictionary) {
                // Quick length filter to speed up comparison
                if (Math.abs(dictWord.length() - term.length()) > maxDist) continue;

                EditDistance.EditDistanceResult ed = editDistance.compute(term, dictWord);
                if (ed.getDistance() <= maxDist && ed.getSimilarity() > bestSim) {
                    bestSim = ed.getSimilarity();
                    bestDist = ed.getDistance();
                    bestMatch = dictWord;
                }
            }

            if (bestDist <= maxDist && !bestMatch.equals(term)) {
                correctedTerms.add(bestMatch);
                anyCorrectionMade = true;
            } else {
                correctedTerms.add(term);
            }
        }

        String suggestedPhrase = String.join(" ", correctedTerms);
        if (anyCorrectionMade) {
            res.suggestedQuery = suggestedPhrase;
        }

        // Search documents matching either the raw query terms or corrected terms
        Set<String> targetTerms = new HashSet<>(Arrays.asList(queryTerms));
        targetTerms.addAll(correctedTerms);

        for (LibraryDocument doc : documents) {
            String searchable = (doc.getTitle() + " " + doc.getKeywords() + " " + doc.getCategory() + " " + doc.getAuthor()).toLowerCase();
            String[] docWords = searchable.split("[^a-zA-Z0-9]+");

            int termMatches = 0;
            double docScore = 0.0;

            for (String targetTerm : targetTerms) {
                boolean termFound = false;
                for (String dw : docWords) {
                    if (dw.equals(targetTerm)) {
                        termMatches++;
                        docScore += 100.0;
                        termFound = true;
                        break;
                    }
                    if (Math.abs(dw.length() - targetTerm.length()) <= 2) {
                        EditDistance.EditDistanceResult ed = editDistance.compute(targetTerm, dw);
                        if (ed.getDistance() <= 2 && ed.getSimilarity() >= 70.0) {
                            termMatches++;
                            docScore += ed.getSimilarity();
                            termFound = true;
                            break;
                        }
                    }
                }
            }

            // Also check whole-phrase similarity with Title
            EditDistance.EditDistanceResult titleEd = editDistance.compute(lowerQuery, doc.getTitle().toLowerCase());
            if (titleEd.getSimilarity() >= 60.0) {
                docScore += titleEd.getSimilarity();
                termMatches++;
            }

            if (termMatches > 0) {
                String snippet = (doc.getAbstractText() != null && !doc.getAbstractText().isEmpty())
                        ? doc.getAbstractText()
                        : (doc.getContent().length() > 200 ? doc.getContent().substring(0, 200) + "..." : doc.getContent());

                res.items.add(new SearchResultItem(doc, "Fuzzy Match (Edit Distance)", Collections.emptyList(), termMatches, docScore, snippet));
            }
        }

        return res;
    }

    private String extractSnippet(String content, List<Integer> positions, String query) {
        if (content == null || content.isEmpty()) return "";
        if (positions == null || positions.isEmpty()) {
            return content.length() > 180 ? content.substring(0, 180) + "..." : content;
        }

        int firstPos = positions.get(0);
        int start = Math.max(0, firstPos - 60);
        int end = Math.min(content.length(), firstPos + query.length() + 80);

        String snippet = content.substring(start, end).trim();
        if (start > 0) snippet = "..." + snippet;
        if (end < content.length()) snippet = snippet + "...";
        return snippet;
    }
}

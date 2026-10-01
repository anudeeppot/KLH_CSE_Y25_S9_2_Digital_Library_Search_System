package com.library.search.controller;

import com.library.search.algorithm.*;
import com.library.search.dto.*;
import com.library.search.model.LibraryDocument;
import com.library.search.repository.DocumentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmController {

    private final KMP kmp;
    private final RabinKarp rabinKarp;
    private final BoyerMoore boyerMoore;
    private final EditDistance editDistance;
    private final SuffixArray suffixArray;
    private final DocumentSimilarity documentSimilarity;
    private final ParallelSearchEngine parallelSearchEngine;
    private final RandomizedSampler randomizedSampler;
    private final DocumentRepository documentRepository;

    public AlgorithmController(KMP kmp,
                               RabinKarp rabinKarp,
                               BoyerMoore boyerMoore,
                               EditDistance editDistance,
                               SuffixArray suffixArray,
                               DocumentSimilarity documentSimilarity,
                               ParallelSearchEngine parallelSearchEngine,
                               RandomizedSampler randomizedSampler,
                               DocumentRepository documentRepository) {
        this.kmp = kmp;
        this.rabinKarp = rabinKarp;
        this.boyerMoore = boyerMoore;
        this.editDistance = editDistance;
        this.suffixArray = suffixArray;
        this.documentSimilarity = documentSimilarity;
        this.parallelSearchEngine = parallelSearchEngine;
        this.randomizedSampler = randomizedSampler;
        this.documentRepository = documentRepository;
    }

    @PostMapping("/kmp")
    public ResponseEntity<AlgorithmTestResponse> testKMP(@RequestBody AlgorithmTestRequest request) {
        KMP.KMPResult result = kmp.searchWithMetrics(request.getText(), request.getPattern());

        AlgorithmTestResponse res = new AlgorithmTestResponse();
        res.setAlgorithm("KMP");
        res.setText(request.getText());
        res.setPattern(request.getPattern());
        res.setFound(!result.getPositions().isEmpty());
        res.setPositions(result.getPositions());
        res.setComparisons(result.getComparisons());
        res.setExecutionTime(result.getExecutionTime());
        res.setExecutionTimeMs(Math.round((result.getExecutionTime() / 1_000_000.0) * 1000.0) / 1000.0);
        res.setLps(result.getLps());
        res.setSteps(result.getSteps());
        res.setTimeComplexity("Preprocessing: O(m), Searching: O(n), Total: O(n + m)");
        res.setSpaceComplexity("Auxiliary Space: O(m) for LPS array");

        return ResponseEntity.ok(res);
    }

    @PostMapping("/rabin-karp")
    public ResponseEntity<AlgorithmTestResponse> testRabinKarp(@RequestBody AlgorithmTestRequest request) {
        RabinKarp.RabinKarpResult result = rabinKarp.searchWithMetrics(request.getText(), request.getPattern());

        AlgorithmTestResponse res = new AlgorithmTestResponse();
        res.setAlgorithm("Rabin-Karp");
        res.setText(request.getText());
        res.setPattern(request.getPattern());
        res.setFound(!result.getPositions().isEmpty());
        res.setPositions(result.getPositions());
        res.setComparisons(result.getHashComparisons() + result.getCharacterComparisons());
        res.setCollisions(result.getCollisions());
        res.setPatternHash(result.getPatternHash());
        res.setExecutionTime(result.getExecutionTime());
        res.setExecutionTimeMs(Math.round((result.getExecutionTime() / 1_000_000.0) * 1000.0) / 1000.0);
        res.setSteps(result.getSteps());
        res.setTimeComplexity("Average: O(n + m), Worst-Case: O(n * m) (under high collision probability)");
        res.setSpaceComplexity("Auxiliary Space: O(1)");

        return ResponseEntity.ok(res);
    }

    @PostMapping("/boyer-moore")
    public ResponseEntity<AlgorithmTestResponse> testBoyerMoore(@RequestBody AlgorithmTestRequest request) {
        BoyerMoore.BoyerMooreResult result = boyerMoore.searchWithMetrics(request.getText(), request.getPattern());

        AlgorithmTestResponse res = new AlgorithmTestResponse();
        res.setAlgorithm("Boyer-Moore");
        res.setText(request.getText());
        res.setPattern(request.getPattern());
        res.setFound(!result.getPositions().isEmpty());
        res.setPositions(result.getPositions());
        res.setComparisons(result.getComparisons());
        res.setBadCharacterTable(result.getBadCharacterTable());
        res.setExecutionTime(result.getExecutionTime());
        res.setExecutionTimeMs(Math.round((result.getExecutionTime() / 1_000_000.0) * 1000.0) / 1000.0);
        res.setSteps(result.getSteps());
        res.setTimeComplexity("Best Case: O(n / m) (Sub-linear), Average: O(n), Worst Case: O(n * m)");
        res.setSpaceComplexity("Auxiliary Space: O(sigma) for Bad-Character Table");

        return ResponseEntity.ok(res);
    }

    @PostMapping("/edit-distance")
    public ResponseEntity<EditDistance.EditDistanceResult> testEditDistance(@RequestBody AlgorithmTestRequest request) {
        String source = request.getText() != null ? request.getText() : request.getPattern();
        String target = request.getTarget() != null ? request.getTarget() : request.getPattern();

        EditDistance.EditDistanceResult result = editDistance.compute(source, target);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/suffix-array")
    public ResponseEntity<SuffixArray.SuffixArrayResult> testSuffixArray(@RequestBody AlgorithmTestRequest request) {
        SuffixArray.SuffixArrayResult result = suffixArray.buildAndSearch(request.getText(), request.getPattern());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/similarity")
    public ResponseEntity<DocumentSimilarity.SimilarityResult> testSimilarity(@RequestBody AlgorithmTestRequest request) {
        String docA = request.getText() != null ? request.getText() : "";
        String docB = request.getTarget() != null ? request.getTarget() : (request.getPattern() != null ? request.getPattern() : "");

        DocumentSimilarity.SimilarityResult result = documentSimilarity.calculateCosineSimilarity(docA, docB);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/compare")
    public ResponseEntity<ComparisonResponse> compareAlgorithms(@RequestBody AlgorithmTestRequest request) {
        String text = (request.getText() != null && !request.getText().isEmpty())
                ? request.getText()
                : "Dynamic programming and string matching algorithms like Knuth Morris Pratt, Rabin Karp, and Boyer Moore enable high performance digital library search systems.";
        String pattern = (request.getPattern() != null && !request.getPattern().isEmpty())
                ? request.getPattern()
                : "algorithms";

        // Run KMP
        KMP.KMPResult kmpRes = kmp.searchWithMetrics(text, pattern);
        ComparisonResponse.AlgorithmMetric kmpMetric = new ComparisonResponse.AlgorithmMetric(
                "KMP",
                kmpRes.getExecutionTime(),
                kmpRes.getExecutionTime() / 1_000_000.0,
                kmpRes.getComparisons(),
                kmpRes.getPositions().size(),
                "O(n)", "O(n + m)", "O(n + m)", "O(m)"
        );

        // Run Rabin-Karp
        RabinKarp.RabinKarpResult rkRes = rabinKarp.searchWithMetrics(text, pattern);
        ComparisonResponse.AlgorithmMetric rkMetric = new ComparisonResponse.AlgorithmMetric(
                "Rabin-Karp",
                rkRes.getExecutionTime(),
                rkRes.getExecutionTime() / 1_000_000.0,
                rkRes.getHashComparisons() + rkRes.getCharacterComparisons(),
                rkRes.getPositions().size(),
                "O(n + m)", "O(n + m)", "O(n * m)", "O(1)"
        );

        // Run Boyer-Moore
        BoyerMoore.BoyerMooreResult bmRes = boyerMoore.searchWithMetrics(text, pattern);
        ComparisonResponse.AlgorithmMetric bmMetric = new ComparisonResponse.AlgorithmMetric(
                "Boyer-Moore",
                bmRes.getExecutionTime(),
                bmRes.getExecutionTime() / 1_000_000.0,
                bmRes.getComparisons(),
                bmRes.getPositions().size(),
                "O(n / m)", "O(n)", "O(n * m)", "O(sigma)"
        );

        // Run Suffix Array
        SuffixArray.SuffixArrayResult saRes = suffixArray.buildAndSearch(text, pattern);
        ComparisonResponse.AlgorithmMetric saMetric = new ComparisonResponse.AlgorithmMetric(
                "Suffix Array",
                saRes.getExecutionTime(),
                saRes.getExecutionTime() / 1_000_000.0,
                saRes.getBinarySearchSteps().size(),
                saRes.getMatchPositions().size(),
                "O(m + log n)", "O(m * log n)", "O(m * log n)", "O(n)"
        );

        List<ComparisonResponse.AlgorithmMetric> metrics = Arrays.asList(kmpMetric, rkMetric, bmMetric, saMetric);

        // Determine fastest empirical run
        ComparisonResponse.AlgorithmMetric fastest = metrics.stream()
                .min(Comparator.comparingLong(ComparisonResponse.AlgorithmMetric::getExecutionTimeNanos))
                .orElse(kmpMetric);

        String explanation = "Empirical benchmark completed. Performance is inherently context-dependent: Boyer-Moore excels on natural language with large alphabets and longer patterns due to sublinear shifts; KMP guarantees linear worst-case performance without any collision risk; Rabin-Karp allows efficient multi-pattern hashing; and Suffix Array amortizes preprocessing across recurring queries.";

        return ResponseEntity.ok(new ComparisonResponse(text, pattern, metrics, fastest.getName(), explanation));
    }

    @PostMapping("/parallel")
    public ResponseEntity<ParallelSearchEngine.ParallelBenchmarkResult> runParallelBenchmark(
            @RequestParam(name = "query", defaultValue = "algorithm") String query,
            @RequestParam(name = "multiplier", defaultValue = "20") int multiplier) {

        List<LibraryDocument> documents = documentRepository.findAll();
        ParallelSearchEngine.ParallelBenchmarkResult result = parallelSearchEngine.runBenchmark(documents, query, multiplier);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/randomized")
    public ResponseEntity<RandomizedSampler.SamplingResult> sampleCorpus(
            @RequestParam(name = "k", defaultValue = "5") int k) {

        List<LibraryDocument> documents = documentRepository.findAll();
        RandomizedSampler.SamplingResult result = randomizedSampler.sampleDocuments(documents, k);
        return ResponseEntity.ok(result);
    }
}

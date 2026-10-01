package com.library.search.controller;

import com.library.search.algorithm.*;
import com.library.search.dto.*;
import com.library.search.service.MaxFlowMinCutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller for Course Outcomes CO-1 through CO-4 Algorithm Demonstrations.
 */
@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmController {

    private final ProblemClassSignatureEvaluator signatureEvaluator;
    private final KMP kmp;
    private final ZAlgorithm zAlgorithm;
    private final RabinKarp rabinKarp;
    private final BoyerMoore boyerMoore;
    private final SuffixArray suffixArray;
    private final SuffixAutomaton suffixAutomaton;
    private final IntervalDP intervalDP;
    private final BitmaskDP bitmaskDP;
    private final TreeDP treeDP;
    private final SequenceAlignmentDP sequenceAlignmentDP;
    private final EditDistance editDistance;
    private final DocumentSimilarity documentSimilarity;
    private final MaxFlowMinCutService flowService;

    public AlgorithmController(ProblemClassSignatureEvaluator signatureEvaluator,
                               KMP kmp,
                               ZAlgorithm zAlgorithm,
                               RabinKarp rabinKarp,
                               BoyerMoore boyerMoore,
                               SuffixArray suffixArray,
                               SuffixAutomaton suffixAutomaton,
                               IntervalDP intervalDP,
                               BitmaskDP bitmaskDP,
                               TreeDP treeDP,
                               SequenceAlignmentDP sequenceAlignmentDP,
                               EditDistance editDistance,
                               DocumentSimilarity documentSimilarity,
                               MaxFlowMinCutService flowService) {
        this.signatureEvaluator = signatureEvaluator;
        this.kmp = kmp;
        this.zAlgorithm = zAlgorithm;
        this.rabinKarp = rabinKarp;
        this.boyerMoore = boyerMoore;
        this.suffixArray = suffixArray;
        this.suffixAutomaton = suffixAutomaton;
        this.intervalDP = intervalDP;
        this.bitmaskDP = bitmaskDP;
        this.treeDP = treeDP;
        this.sequenceAlignmentDP = sequenceAlignmentDP;
        this.editDistance = editDistance;
        this.documentSimilarity = documentSimilarity;
        this.flowService = flowService;
    }

    // -------------------------------------------------------------
    // CO-1: Problem-Class Signature Evaluator & Strategy Selector
    // -------------------------------------------------------------
    @PostMapping("/evaluate-signature")
    public ResponseEntity<ProblemClassSignatureEvaluator.EvaluationResult> evaluateSignature(
            @RequestBody ProblemClassSignatureEvaluator.SignatureProfile profile) {
        return ResponseEntity.ok(signatureEvaluator.evaluateSignature(profile));
    }

    // -------------------------------------------------------------
    // CO-2: Linear-Time String Algorithms & Suffix Structures
    // -------------------------------------------------------------
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
        res.setSpaceComplexity("Auxiliary Space: O(m) for LPS / \u03c0 array");

        return ResponseEntity.ok(res);
    }

    @PostMapping("/z-algorithm")
    public ResponseEntity<ZAlgorithm.ZResult> testZAlgorithm(@RequestBody AlgorithmTestRequest request) {
        ZAlgorithm.ZResult result = zAlgorithm.searchWithMetrics(request.getText(), request.getPattern());
        return ResponseEntity.ok(result);
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
        res.setTimeComplexity("Average: O(n + m), Worst-Case: O(n * m) (under hash collision cascading)");
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
        res.setSpaceComplexity("Auxiliary Space: O(\u03c3) for Bad-Character Table");

        return ResponseEntity.ok(res);
    }

    @PostMapping("/suffix-array")
    public ResponseEntity<SuffixArray.SuffixArrayResult> testSuffixArray(@RequestBody AlgorithmTestRequest request) {
        SuffixArray.SuffixArrayResult result = suffixArray.buildAndSearch(request.getText(), request.getPattern());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/suffix-automaton")
    public ResponseEntity<SuffixAutomaton.AutomatonResult> testSuffixAutomaton(@RequestBody AlgorithmTestRequest request) {
        SuffixAutomaton.AutomatonResult result = suffixAutomaton.buildAndQuery(request.getText(), request.getPattern());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/compare")
    public ResponseEntity<ComparisonResponse> compareAlgorithms(@RequestBody AlgorithmTestRequest request) {
        String text = (request.getText() != null && !request.getText().isEmpty())
                ? request.getText()
                : "Dynamic programming and string matching algorithms like Knuth Morris Pratt, Rabin Karp, Z-algorithm, and Suffix Arrays enable high performance digital library search systems.";
        String pattern = (request.getPattern() != null && !request.getPattern().isEmpty())
                ? request.getPattern()
                : "algorithms";

        // 1. KMP
        KMP.KMPResult kmpRes = kmp.searchWithMetrics(text, pattern);
        ComparisonResponse.AlgorithmMetric kmpMetric = new ComparisonResponse.AlgorithmMetric(
                "KMP",
                kmpRes.getExecutionTime(),
                kmpRes.getExecutionTime() / 1_000_000.0,
                kmpRes.getComparisons(),
                kmpRes.getPositions().size(),
                "O(n)", "O(n + m)", "O(n + m)", "O(m)"
        );

        // 2. Z-Algorithm
        ZAlgorithm.ZResult zRes = zAlgorithm.searchWithMetrics(text, pattern);
        ComparisonResponse.AlgorithmMetric zMetric = new ComparisonResponse.AlgorithmMetric(
                "Z-Algorithm",
                zRes.getExecutionTimeNanos(),
                zRes.getExecutionTimeNanos() / 1_000_000.0,
                zRes.getComparisons(),
                zRes.getMatchPositions().size(),
                "O(n + m)", "O(n + m)", "O(n + m)", "O(n + m)"
        );

        // 3. Rabin-Karp
        RabinKarp.RabinKarpResult rkRes = rabinKarp.searchWithMetrics(text, pattern);
        ComparisonResponse.AlgorithmMetric rkMetric = new ComparisonResponse.AlgorithmMetric(
                "Rabin-Karp",
                rkRes.getExecutionTime(),
                rkRes.getExecutionTime() / 1_000_000.0,
                rkRes.getHashComparisons() + rkRes.getCharacterComparisons(),
                rkRes.getPositions().size(),
                "O(n + m)", "O(n + m)", "O(n * m)", "O(1)"
        );

        // 4. Boyer-Moore
        BoyerMoore.BoyerMooreResult bmRes = boyerMoore.searchWithMetrics(text, pattern);
        ComparisonResponse.AlgorithmMetric bmMetric = new ComparisonResponse.AlgorithmMetric(
                "Boyer-Moore",
                bmRes.getExecutionTime(),
                bmRes.getExecutionTime() / 1_000_000.0,
                bmRes.getComparisons(),
                bmRes.getPositions().size(),
                "O(n / m)", "O(n)", "O(n * m)", "O(\u03c3)"
        );

        // 5. Suffix Array
        SuffixArray.SuffixArrayResult saRes = suffixArray.buildAndSearch(text, pattern);
        ComparisonResponse.AlgorithmMetric saMetric = new ComparisonResponse.AlgorithmMetric(
                "Suffix Array",
                saRes.getExecutionTime(),
                saRes.getExecutionTime() / 1_000_000.0,
                saRes.getBinarySearchSteps().size(),
                saRes.getMatchPositions().size(),
                "O(m + log n)", "O(m * log n)", "O(m * log n)", "O(n)"
        );

        List<ComparisonResponse.AlgorithmMetric> metrics = Arrays.asList(kmpMetric, zMetric, rkMetric, bmMetric, saMetric);

        ComparisonResponse.AlgorithmMetric fastest = metrics.stream()
                .min(Comparator.comparingLong(ComparisonResponse.AlgorithmMetric::getExecutionTimeNanos))
                .orElse(kmpMetric);

        String explanation = "Empirical comparison completed across CO-2 linear and suffix structures. Boyer-Moore achieves sublinear skips on natural language text with large alphabets; KMP and Z-Algorithm guarantee strictly linear deterministic O(N + M) performance with 0 rollback; Rabin-Karp enables rolling hash fingerprinting; and Suffix Array amortizes preprocessing for recurring binary-search queries.";

        return ResponseEntity.ok(new ComparisonResponse(text, pattern, metrics, fastest.getName(), explanation));
    }

    // -------------------------------------------------------------
    // CO-3: Advanced Dynamic Programming Suite
    // -------------------------------------------------------------
    @PostMapping("/interval-dp")
    public ResponseEntity<IntervalDP.IntervalResult> testIntervalDP(
            @RequestBody(required = false) Map<String, Object> body) {
        List<String> tokens = null;
        int[] weights = null;
        if (body != null && body.containsKey("tokens")) {
            tokens = (List<String>) body.get("tokens");
        }
        return ResponseEntity.ok(intervalDP.computeOptimalQueryPlan(tokens, weights));
    }

    @PostMapping("/bitmask-dp")
    public ResponseEntity<BitmaskDP.BitmaskResult> testBitmaskDP(
            @RequestBody(required = false) Map<String, Object> body) {
        List<String> topics = null;
        if (body != null && body.containsKey("topics")) {
            topics = (List<String>) body.get("topics");
        }
        return ResponseEntity.ok(bitmaskDP.computeOptimalTopicCoverage(topics, null));
    }

    @GetMapping("/tree-dp")
    public ResponseEntity<TreeDP.TreeDPResult> testTreeDP() {
        return ResponseEntity.ok(treeDP.optimizeTaxonomyTree());
    }

    @PostMapping("/sequence-alignment")
    public ResponseEntity<SequenceAlignmentDP.AlignmentResult> testSequenceAlignment(
            @RequestBody AlgorithmTestRequest request) {
        String seqA = (request.getText() != null) ? request.getText() : request.getPattern();
        String seqB = (request.getTarget() != null) ? request.getTarget() : "ALGORITHM";
        return ResponseEntity.ok(sequenceAlignmentDP.align(seqA, seqB));
    }

    @PostMapping("/edit-distance")
    public ResponseEntity<EditDistance.EditDistanceResult> testEditDistance(@RequestBody AlgorithmTestRequest request) {
        String source = request.getText() != null ? request.getText() : request.getPattern();
        String target = request.getTarget() != null ? request.getTarget() : request.getPattern();
        return ResponseEntity.ok(editDistance.compute(source, target));
    }

    @PostMapping("/similarity")
    public ResponseEntity<DocumentSimilarity.SimilarityResult> testSimilarity(@RequestBody AlgorithmTestRequest request) {
        String docA = request.getText() != null ? request.getText() : "";
        String docB = request.getTarget() != null ? request.getTarget() : (request.getPattern() != null ? request.getPattern() : "");
        return ResponseEntity.ok(documentSimilarity.calculateCosineSimilarity(docA, docB));
    }

    // -------------------------------------------------------------
    // CO-4: Network Flow & Max-Flow / Min-Cut Duality
    // -------------------------------------------------------------
    @GetMapping("/flow/reservation")
    public ResponseEntity<MaxFlowMinCutService.NetworkFlowResponse> solveReservationMatching(
            @RequestParam(name = "algorithm", defaultValue = "EDMONDS_KARP") String algorithm) {
        return ResponseEntity.ok(flowService.solveReservationMatching(algorithm));
    }

    @GetMapping("/flow/cdn")
    public ResponseEntity<MaxFlowMinCutService.NetworkFlowResponse> solveCdnDistribution(
            @RequestParam(name = "algorithm", defaultValue = "DINIC") String algorithm) {
        return ResponseEntity.ok(flowService.solveCdnDistribution(algorithm));
    }
}

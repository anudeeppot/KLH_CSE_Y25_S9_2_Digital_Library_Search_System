package com.library.search;

import com.library.search.algorithm.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmTest {

    private final ProblemClassSignatureEvaluator signatureEvaluator = new ProblemClassSignatureEvaluator();
    private final KMP kmp = new KMP();
    private final ZAlgorithm zAlgorithm = new ZAlgorithm();
    private final RabinKarp rabinKarp = new RabinKarp();
    private final BoyerMoore boyerMoore = new BoyerMoore();
    private final EditDistance editDistance = new EditDistance();
    private final SuffixArray suffixArray = new SuffixArray();
    private final SuffixAutomaton suffixAutomaton = new SuffixAutomaton();
    private final IntervalDP intervalDP = new IntervalDP();
    private final BitmaskDP bitmaskDP = new BitmaskDP();
    private final TreeDP treeDP = new TreeDP();
    private final SequenceAlignmentDP sequenceAlignmentDP = new SequenceAlignmentDP();
    private final EdmondsKarp edmondsKarp = new EdmondsKarp();
    private final DinicAlgorithm dinicAlgorithm = new DinicAlgorithm();
    private final DocumentSimilarity documentSimilarity = new DocumentSimilarity();

    @Test
    void testCO1ProblemClassSignatureEvaluator() {
        ProblemClassSignatureEvaluator.SignatureProfile profile = new ProblemClassSignatureEvaluator.SignatureProfile();
        profile.setQueryPattern("algorithm");
        profile.setTextLength(50000);
        profile.setToleranceForTypos(false);
        profile.setHasFlowConstraints(false);

        ProblemClassSignatureEvaluator.EvaluationResult res = signatureEvaluator.evaluateSignature(profile);
        assertNotNull(res);
        assertEquals(ProblemClassSignatureEvaluator.ProblemClass.SUBSTRING_SEARCH, res.getIdentifiedClass());
        assertNotNull(res.getSelectedStrategy());
    }

    @Test
    void testCO2ZAlgorithm() {
        String text = "banana";
        String pattern = "an";
        ZAlgorithm.ZResult result = zAlgorithm.searchWithMetrics(text, pattern);
        assertNotNull(result);
        assertFalse(result.getMatchPositions().isEmpty());
        assertEquals(Arrays.asList(1, 3), result.getMatchPositions());
    }

    @Test
    void testCO2SuffixAutomaton() {
        String text = "algorithmics";
        String query = "algo";
        SuffixAutomaton.AutomatonResult result = suffixAutomaton.buildAndQuery(text, query);
        assertNotNull(result);
        assertTrue(result.isSubstring());
        assertTrue(result.getTotalStates() > 0);
        assertTrue(result.getTotalTransitions() > 0);
    }

    @Test
    void testCO3IntervalDP() {
        IntervalDP.IntervalResult result = intervalDP.computeOptimalQueryPlan(null, null);
        assertNotNull(result);
        assertTrue(result.getOptimalCost() > 0);
        assertNotNull(result.getOptimalParenthesization());
    }

    @Test
    void testCO3BitmaskDP() {
        BitmaskDP.BitmaskResult result = bitmaskDP.computeOptimalTopicCoverage(null, null);
        assertNotNull(result);
        assertTrue(result.getOptimalCost() > 0);
        assertFalse(result.getSelectedBooks().isEmpty());
    }

    @Test
    void testCO3TreeDP() {
        TreeDP.TreeDPResult result = treeDP.optimizeTaxonomyTree();
        assertNotNull(result);
        assertTrue(result.getMaxImpactScore() > 0);
        assertFalse(result.getSelectedCategories().isEmpty());
    }

    @Test
    void testCO3SequenceAlignment() {
        SequenceAlignmentDP.AlignmentResult result = sequenceAlignmentDP.align("ALGORITHM", "LOGARITHM");
        assertNotNull(result);
        assertTrue(result.getAlignmentScore() > 0);
        assertEquals(result.getAlignedA().length(), result.getAlignedB().length());
    }

    @Test
    void testCO4EdmondsKarpAndDinicFlow() {
        FlowNetwork network = new FlowNetwork(4);
        network.addEdge(0, 1, 10);
        network.addEdge(0, 2, 5);
        network.addEdge(1, 2, 15);
        network.addEdge(1, 3, 10);
        network.addEdge(2, 3, 10);

        EdmondsKarp.EdmondsKarpResult ekRes = edmondsKarp.computeMaxFlow(network, 0, 3);
        assertEquals(15, ekRes.getMaxFlow());

        // Test Dinic on fresh network
        FlowNetwork dinicNet = new FlowNetwork(4);
        dinicNet.addEdge(0, 1, 10);
        dinicNet.addEdge(0, 2, 5);
        dinicNet.addEdge(1, 2, 15);
        dinicNet.addEdge(1, 3, 10);
        dinicNet.addEdge(2, 3, 10);

        DinicAlgorithm.DinicResult dinicRes = dinicAlgorithm.computeMaxFlow(dinicNet, 0, 3);
        assertEquals(15, dinicRes.getMaxFlow());
    }

    @Test
    void testKMPPatternMatching() {
        String text = "data structures and algorithms in digital library search system";
        String pattern = "algorithms";

        List<Integer> positions = kmp.search(text, pattern);
        assertFalse(positions.isEmpty());
        assertEquals(20, positions.get(0));

        int[] lps = kmp.buildLPS("ababcaba");
        assertNotNull(lps);
        assertEquals(8, lps.length);
    }

    @Test
    void testRabinKarpMatching() {
        String text = "the rabin karp algorithm utilizes a rolling hash technique";
        String pattern = "rolling hash";

        List<Integer> positions = rabinKarp.search(text, pattern);
        assertFalse(positions.isEmpty());
        assertTrue(text.toLowerCase().startsWith(pattern.toLowerCase(), positions.get(0)));
    }

    @Test
    void testBoyerMooreMatching() {
        String text = "boyer moore utilizes bad character heuristic for sublinear search";
        String pattern = "bad character";

        List<Integer> positions = boyerMoore.search(text, pattern);
        assertFalse(positions.isEmpty());
        assertTrue(text.toLowerCase().startsWith(pattern.toLowerCase(), positions.get(0)));
    }

    @Test
    void testEditDistanceCalculation() {
        String query = "machne";
        String target = "machine";

        EditDistance.EditDistanceResult result = editDistance.compute(query, target);
        assertEquals(1, result.getDistance());
        assertTrue(result.getSimilarity() > 80.0);
        assertNotNull(result.getDpMatrix());
    }

    @Test
    void testSuffixArraySearch() {
        String text = "banana";
        String pattern = "ana";

        SuffixArray.SuffixArrayResult result = suffixArray.buildAndSearch(text, pattern);
        assertFalse(result.getMatchPositions().isEmpty());
        assertEquals(2, result.getMatchPositions().size());
    }

    @Test
    void testDocumentSimilarity() {
        String doc1 = "Machine learning algorithms and deep neural networks in artificial intelligence.";
        String doc2 = "Deep learning and neural networks for artificial intelligence systems.";

        DocumentSimilarity.SimilarityResult result = documentSimilarity.calculateCosineSimilarity(doc1, doc2);
        assertTrue(result.getPercentage() > 40.0);
        assertFalse(result.getSharedKeywords().isEmpty());
    }
}

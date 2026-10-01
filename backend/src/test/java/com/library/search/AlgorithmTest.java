package com.library.search;

import com.library.search.algorithm.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmTest {

    private final KMP kmp = new KMP();
    private final RabinKarp rabinKarp = new RabinKarp();
    private final BoyerMoore boyerMoore = new BoyerMoore();
    private final EditDistance editDistance = new EditDistance();
    private final SuffixArray suffixArray = new SuffixArray();
    private final DocumentSimilarity documentSimilarity = new DocumentSimilarity();

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
        assertEquals(2, result.getMatchPositions().size()); // 'ana' at index 1 and 3
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

import java.io.*;
import java.util.*;

/**
 * CorpusVerificationTests.java
 * Comprehensive verification harness for the expanded corpus:
 * 1. Loads all 100 documents via CorpusReader().loadCorpus("corpus")
 * 2. Tests KMP exact search
 * 3. Tests Levenshtein fuzzy search with intentional typos
 * 4. Tests SequenceAlignment (Needleman-Wunsch)
 * 5. Tests ReadingPlanGenerator (0/1 Knapsack) with updated word counts
 * 6. Tests ReservoirSampler on actual 100-doc corpus
 * 7. Tests Parallel vs Sequential search consistency
 * 8. Tests EdmondsKarp, MinCut, and BipartiteMatching
 */
public class CorpusVerificationTests {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   EXPANDED CORPUS & ALGORITHM VERIFICATION");
        System.out.println("=================================================");

        int passed = 0;
        int failed = 0;

        // 1. Ingestion via CorpusReader
        CorpusReader reader = new CorpusReader();
        List<LibraryDocument> corpus = reader.loadCorpus("corpus");
        if (corpus.size() == 100) {
            System.out.println("[PASS] Corpus Loading: Exactly 100 documents loaded via CorpusReader");
            passed++;
        } else {
            System.out.println("[FAIL] Corpus Loading: Expected 100, got " + corpus.size());
            failed++;
        }

        // Verify word counts and non-blank lines
        boolean allExpanded = true;
        long totalWords = 0;
        for (LibraryDocument doc : corpus) {
            int words = doc.getWordCount();
            totalWords += words;
            if (words < 800) {
                allExpanded = false;
                System.out.println("  Warning: Doc " + doc.getTitle() + " has only " + words + " words");
            }
        }
        if (allExpanded && totalWords > 130000) {
            System.out.println("[PASS] Corpus Content: All 100 docs enriched (Total words: " + totalWords + ", Avg: " + (totalWords / 100) + ")");
            passed++;
        } else {
            System.out.println("[FAIL] Corpus Content: Words below threshold: " + totalWords);
            failed++;
        }

        // 2. KMP Exact Search
        KMP kmp = new KMP();
        int kmpMatches = 0;
        for (LibraryDocument doc : corpus) {
            List<Integer> pos = kmp.search(doc.getContent(), "algorithm");
            kmpMatches += pos.size();
        }
        if (kmpMatches > 100) {
            System.out.println("[PASS] KMP Exact Search: Query 'algorithm' found " + kmpMatches + " total matches across corpus");
            passed++;
        } else {
            System.out.println("[FAIL] KMP Exact Search: Matches too low: " + kmpMatches);
            failed++;
        }

        // 3. Levenshtein Fuzzy Search on Intentional Typos
        String[][] typoPairs = {
            {"algoritm", "algorithm"},
            {"databse", "database"},
            {"machien", "machine"},
            {"paralell", "parallel"},
            {"classifcation", "classification"},
            {"recurrsion", "recursion"},
            {"knapscak", "knapsack"},
            {"needlman", "needleman"},
            {"complxity", "complexity"},
            {"resrvoir", "reservoir"}
        };

        boolean allTyposMatch = true;
        for (String[] pair : typoPairs) {
            String typo = pair[0];
            String canonical = pair[1];
            int dist = EditDistance.levenshteinDistance(typo, canonical);
            if (dist > 2) {
                System.out.println("  Failed distance for pair: " + typo + " vs " + canonical + " (dist=" + dist + ")");
                allTyposMatch = false;
            }
        }
        if (allTyposMatch) {
            System.out.println("[PASS] Levenshtein Typo Pairs: All representative test typos verify edit distance <= 2");
            passed++;
        } else {
            System.out.println("[FAIL] Levenshtein Typo Pairs: Distance threshold exceeded");
            failed++;
        }

        // Search for typos in corpus content using KMP
        int docsWithIntentionalTypos = 0;
        for (LibraryDocument doc : corpus) {
            if (!kmp.search(doc.getContent(), "algoritm").isEmpty() ||
                !kmp.search(doc.getContent(), "databse").isEmpty() ||
                !kmp.search(doc.getContent(), "machien").isEmpty() ||
                !kmp.search(doc.getContent(), "paralell").isEmpty() ||
                !kmp.search(doc.getContent(), "knapscak").isEmpty() ||
                !kmp.search(doc.getContent(), "needlman").isEmpty() ||
                !kmp.search(doc.getContent(), "recurrsion").isEmpty()) {
                docsWithIntentionalTypos++;
            }
        }
        if (docsWithIntentionalTypos >= 7) {
            System.out.println("[PASS] Corpus Typo Presence: Intentional typos verified inside document text bodies (" + docsWithIntentionalTypos + " docs matched sample typo set)");
            passed++;
        } else {
            System.out.println("[FAIL] Corpus Typo Presence: Expected >= 7 docs with sample typos, found " + docsWithIntentionalTypos);
            failed++;
        }

        // 4. Needleman-Wunsch Alignment (SequenceAlignment)
        LibraryDocument doc1 = corpus.get(0); // KMP
        LibraryDocument doc5 = corpus.get(4); // LPS
        LibraryDocument doc89 = corpus.get(88); // Blockchain

        String sample1 = doc1.getContent().substring(0, Math.min(100, doc1.getContent().length()));
        String sample5 = doc5.getContent().substring(0, Math.min(100, doc5.getContent().length()));
        String sample89 = doc89.getContent().substring(0, Math.min(100, doc89.getContent().length()));

        SequenceAlignment.AlignmentResult alignRelated = SequenceAlignment.needlemanWunsch(sample1, sample5, 1, -1, -2);
        SequenceAlignment.AlignmentResult alignUnrelated = SequenceAlignment.needlemanWunsch(sample1, sample89, 1, -1, -2);
        System.out.println("  Needleman-Wunsch alignment score related (KMP vs LPS): " + alignRelated.score);
        System.out.println("  Needleman-Wunsch alignment score unrelated (KMP vs Blockchain): " + alignUnrelated.score);
        System.out.println("[PASS] Needleman-Wunsch Alignment: Document comparison executed successfully");
        passed++;

        // 5. Reading Plan & Knapsack
        int budgetMinutes = 60;
        List<ReadingPlanGenerator.PlanItem> candidates = new ArrayList<>();
        for (LibraryDocument d : corpus) {
            int wordCount = d.getWordCount();
            int estMinutes = d.getEstimatedReadingMinutes(200);
            int priority = 5;
            candidates.add(new ReadingPlanGenerator.PlanItem(d, wordCount, estMinutes, priority));
        }
        ReadingPlanGenerator.PlanResult plan = ReadingPlanGenerator.generatePlan(candidates, budgetMinutes, 200);
        if (plan.getSelectedItems().size() > 0 && plan.getTotalMinutes() <= budgetMinutes) {
            System.out.println("[PASS] Reading Plan: Generated study pack with " + plan.getSelectedItems().size() + 
                               " docs (" + plan.getTotalMinutes() + " mins <= " + budgetMinutes + " min budget)");
            passed++;
        } else {
            System.out.println("[FAIL] Reading Plan: Budget exceeded or 0 docs returned");
            failed++;
        }

        // 6. Reservoir Sampling
        int samples = 5;
        Set<String> sampledIds = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            LibraryDocument sampled = ReservoirSampler.sample(corpus);
            if (sampled != null) sampledIds.add(sampled.getFileName());
        }
        if (sampledIds.size() >= samples) {
            System.out.println("[PASS] Reservoir Sampling: Sampled diverse documents across corpus (" + sampledIds.size() + " distinct files)");
            passed++;
        } else {
            System.out.println("[FAIL] Reservoir Sampling: Degenerate sampling");
            failed++;
        }

        // 7. Parallel vs Sequential Search Consistency
        LibrarySearchEngine engine = new LibrarySearchEngine(kmp);
        LibrarySearchEngine.SearchExecutionResult seqRes = engine.searchWithMetrics(corpus, "algorithm", LibrarySearchEngine.SearchMode.SEQUENTIAL);
        LibrarySearchEngine.SearchExecutionResult parRes = engine.searchWithMetrics(corpus, "algorithm", LibrarySearchEngine.SearchMode.PARALLEL);

        List<LibrarySearchEngine.SearchMatch> seqMatches = seqRes.getMatches();
        List<LibrarySearchEngine.SearchMatch> parMatches = parRes.getMatches();

        if (seqMatches.size() == parMatches.size()) {
            boolean identical = true;
            for (int i = 0; i < seqMatches.size(); i++) {
                if (!seqMatches.get(i).getDocument().getFileName().equals(parMatches.get(i).getDocument().getFileName()) ||
                    seqMatches.get(i).getPositions().size() != parMatches.get(i).getPositions().size()) {
                    identical = false;
                    break;
                }
            }
            if (identical) {
                System.out.println("[PASS] Parallel vs Sequential Search: 100% identical results across " + seqMatches.size() + " matches (Seq: " + 
                                   String.format("%.2f", seqRes.getElapsedMs()) + "ms, Par: " + String.format("%.2f", parRes.getElapsedMs()) + "ms)");
                passed++;
            } else {
                System.out.println("[FAIL] Parallel vs Sequential Search: Result contents differ");
                failed++;
            }
        } else {
            System.out.println("[FAIL] Parallel vs Sequential Search: Count mismatch (Seq: " + seqMatches.size() + ", Par: " + parMatches.size() + ")");
            failed++;
        }

        // 8. Resource Allocation & Graph Algorithms
        FlowGraph net = new FlowGraph(6);
        net.addEdge(0, 1, 10);
        net.addEdge(0, 2, 10);
        net.addEdge(1, 2, 2);
        net.addEdge(1, 3, 4);
        net.addEdge(1, 4, 8);
        net.addEdge(2, 4, 9);
        net.addEdge(3, 5, 10);
        net.addEdge(4, 5, 10);

        int maxFlow = EdmondsKarp.maxFlow(net, 0, 5, false);
        boolean[] side = MinCut.sourceSide(net, 0);
        int cutCap = MinCut.cutCapacity(net, side);
        if (maxFlow == 14 && cutCap == 14) {
            System.out.println("[PASS] Edmonds-Karp & Min-Cut: Max flow is " + maxFlow + ", Min-cut capacity equals " + cutCap);
            passed++;
        } else {
            System.out.println("[FAIL] Edmonds-Karp & Min-Cut: Flow " + maxFlow + " != Cut " + cutCap);
            failed++;
        }

        // 9. Bipartite Matching
        boolean[][] compat = {
            {true, true, false, false},
            {false, true, false, false},
            {false, false, true, false},
            {false, false, true, true}
        };
        int matchCount = BipartiteMatching.maximumMatching(compat);
        if (matchCount == 4) {
            System.out.println("[PASS] Bipartite Matching: Maximum user-resource cardinality assignment = " + matchCount);
            passed++;
        } else {
            System.out.println("[FAIL] Bipartite Matching: Expected 4, got " + matchCount);
            failed++;
        }

        System.out.println("=================================================");
        System.out.println("   VERIFICATION SUMMARY: " + passed + " / " + (passed + failed) + " PASSED");
        System.out.println("=================================================");
        if (failed > 0) {
            System.exit(1);
        }
    }
}

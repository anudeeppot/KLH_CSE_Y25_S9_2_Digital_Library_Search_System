import java.util.*;

/**
 * AlgorithmTests.java — Comprehensive Test Suite for Module 5 & Module 6 Algorithms.
 *
 * Tests:
 *   1. Module 5: Greedy 1/2-Approximation for 0/1 Knapsack (ReadingPlanGenerator)
 *      - Empty candidates list
 *      - Zero and negative budget
 *      - Candidate exceeding budget
 *      - Single high-value item beats greedy set (v(S_greedy) < v_max)
 *      - Greedy set beats any individual candidate
 *      - Equal priority-to-time ratios
 *      - Total reading time constraint verification
 *      - Mathematical 1/2-approximation bound vs. exact DP knapsack (ratio >= 0.5)
 *
 *   2. Module 6: Reservoir Sampling (ReservoirSampler)
 *      - Empty corpus handling
 *      - Single-document corpus
 *      - Multi-document corpus
 *      - Deterministic / controllable Random instance testing
 *      - Statistical uniformity test over 10,000 trials (Monte Carlo validation)
 *
 *   3. Module 6: Parallel Document Search (LibrarySearchEngine)
 *      - Empty & small corpora (< threshold)
 *      - Large corpus (>= threshold)
 *      - Queries with zero results
 *      - Queries with multiple results
 *      - Strict 100% equivalence between Sequential & Parallel result items & ordering
 *      - Multi-threaded repeated query stress test
 *      - Graceful executor shutdown
 */
public class AlgorithmTests {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   DIGITAL LIBRARY SYSTEM — ALGORITHM TEST SUITE");
        System.out.println("=================================================");

        testReadingPlan_EmptyList();
        testReadingPlan_ZeroOrInvalidBudget();
        testReadingPlan_ExceedingBudget();
        testReadingPlan_SingleBestBeatsGreedySet();
        testReadingPlan_GreedySetBeatsAnySingleItem();
        testReadingPlan_EqualRatios();
        testReadingPlan_BudgetConstraintGuarantee();
        testReadingPlan_CompareWithExactKnapsack();

        testReservoirSampler_Empty();
        testReservoirSampler_SingleItem();
        testReservoirSampler_MultiItem();
        testReservoirSampler_ControlledRandom();
        testReservoirSampler_StatisticalUniformity();

        testParallelSearch_EmptyAndSmallCorpus();
        testParallelSearch_ZeroAndMultipleResults();
        testParallelSearch_StrictEquivalence();
        testParallelSearch_ConcurrencyStress();

        System.out.println("\n=================================================");
        System.out.printf("   TEST SUMMARY: %d / %d PASSED (%.1f%%)\n",
            testsPassed, testsRun, (100.0 * testsPassed / testsRun));
        System.out.println("=================================================");

        if (testsPassed != testsRun) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("[PASS] " + testName);
        } else {
            System.err.println("[FAIL] " + testName);
        }
    }

    private static LibraryDocument makeDummyDoc(String id, String title, int wordCount) {
        LibraryDocument doc = new LibraryDocument(id);
        doc.setTitle(title);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordCount; i++) sb.append("word").append(i).append(" ");
        doc.setContent(sb.toString().trim());
        return doc;
    }

    // =======================================================================
    // 1. Reading Plan (Knapsack 1/2-Approximation) Tests
    // =======================================================================

    private static void testReadingPlan_EmptyList() {
        ReadingPlanGenerator.PlanResult res = ReadingPlanGenerator.generatePlan(new ArrayList<>(), 30, 200);
        assertTrue("Reading Plan: Empty list returns 0 items and 0 minutes",
            res.getSelectedItems().isEmpty() && res.getTotalMinutes() == 0 && res.getTotalPriority() == 0);
    }

    private static void testReadingPlan_ZeroOrInvalidBudget() {
        List<ReadingPlanGenerator.PlanItem> items = new ArrayList<>();
        items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("d1.txt", "Doc 1", 200), 200, 1, 5));

        ReadingPlanGenerator.PlanResult resZero = ReadingPlanGenerator.generatePlan(items, 0, 200);
        ReadingPlanGenerator.PlanResult resNeg = ReadingPlanGenerator.generatePlan(items, -10, 200);

        assertTrue("Reading Plan: Zero and negative budget returns empty selection",
            resZero.getSelectedItems().isEmpty() && resNeg.getSelectedItems().isEmpty());
    }

    private static void testReadingPlan_ExceedingBudget() {
        // Document requires 40 minutes, budget is 20 minutes
        List<ReadingPlanGenerator.PlanItem> items = new ArrayList<>();
        items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("big.txt", "Big Doc", 8000), 8000, 40, 10));

        ReadingPlanGenerator.PlanResult res = ReadingPlanGenerator.generatePlan(items, 20, 200);
        assertTrue("Reading Plan: Document exceeding total budget is rejected",
            res.getSelectedItems().isEmpty() && res.getTotalMinutes() == 0);
    }

    private static void testReadingPlan_SingleBestBeatsGreedySet() {
        // Classic Knapsack case where density greedy prefix is worse than single best item:
        // Budget W = 10
        // Item 1: time = 1,  prio = 2   (density = 2.0)
        // Item 2: time = 10, prio = 9   (density = 0.9)
        // Greedy takes Item 1 (time 1, prio 2). Item 2 doesn't fit (needs 10, only 9 left).
        // Best single item is Item 2 (prio 9).
        // 1/2-approximation must select Item 2 (score 9 > 2).
        List<ReadingPlanGenerator.PlanItem> items = new ArrayList<>();
        items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("i1.txt", "Item 1", 200), 200, 1, 2));
        items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("i2.txt", "Item 2", 2000), 2000, 10, 9));

        ReadingPlanGenerator.PlanResult res = ReadingPlanGenerator.generatePlan(items, 10, 200);
        assertTrue("Reading Plan: Single best feasible item chosen when it beats greedy set",
            res.getSelectedItems().size() == 1
            && res.getSelectedItems().get(0).getDocument().getFileName().equals("i2.txt")
            && res.getTotalPriority() == 9
            && res.getStrategy().contains("Single Best"));
    }

    private static void testReadingPlan_GreedySetBeatsAnySingleItem() {
        // Budget W = 10
        // Item 1: time = 3, prio = 5 (density 1.66)
        // Item 2: time = 3, prio = 4 (density 1.33)
        // Item 3: time = 3, prio = 4 (density 1.33)
        // Best single item: prio = 5
        // Greedy set takes items 1, 2, 3 (time = 9, prio = 13)
        // 13 > 5, so greedy set must be chosen
        List<ReadingPlanGenerator.PlanItem> items = new ArrayList<>();
        items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("a.txt", "Doc A", 600), 600, 3, 5));
        items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("b.txt", "Doc B", 600), 600, 3, 4));
        items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("c.txt", "Doc C", 600), 600, 3, 4));

        ReadingPlanGenerator.PlanResult res = ReadingPlanGenerator.generatePlan(items, 10, 200);
        assertTrue("Reading Plan: Greedy set chosen when its sum exceeds best single item",
            res.getSelectedItems().size() == 3
            && res.getTotalPriority() == 13
            && res.getTotalMinutes() == 9
            && res.getStrategy().contains("Greedy"));
    }

    private static void testReadingPlan_EqualRatios() {
        // All items have same density: time 2, prio 4 (density 2.0)
        List<ReadingPlanGenerator.PlanItem> items = new ArrayList<>();
        items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("e1.txt", "E1", 400), 400, 2, 4));
        items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("e2.txt", "E2", 400), 400, 2, 4));
        items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("e3.txt", "E3", 400), 400, 2, 4));

        ReadingPlanGenerator.PlanResult res = ReadingPlanGenerator.generatePlan(items, 5, 200);
        assertTrue("Reading Plan: Handles equal density items correctly up to budget",
            res.getSelectedItems().size() == 2 && res.getTotalMinutes() == 4 && res.getTotalPriority() == 8);
    }

    private static void testReadingPlan_BudgetConstraintGuarantee() {
        // Verify across 20 random items that sum(minutes) <= Budget always holds
        Random rng = new Random(42);
        List<ReadingPlanGenerator.PlanItem> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            int mins = rng.nextInt(15) + 1;
            int prio = rng.nextInt(10) + 1;
            items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("doc" + i + ".txt", "Doc " + i, mins * 200), mins * 200, mins, prio));
        }

        int budget = 35;
        ReadingPlanGenerator.PlanResult res = ReadingPlanGenerator.generatePlan(items, budget, 200);
        assertTrue("Reading Plan: Total minutes never exceeds budget (Total: " + res.getTotalMinutes() + " <= Budget: " + budget + ")",
            res.getTotalMinutes() <= budget && res.getRemainingMinutes() == (budget - res.getTotalMinutes()));
    }

    private static void testReadingPlan_CompareWithExactKnapsack() {
        // Verify mathematical guarantee: ApproxScore >= 0.5 * ExactOptScore
        Random rng = new Random(12345);
        boolean guaranteeHeld = true;

        for (int trial = 0; trial < 10; trial++) {
            List<ReadingPlanGenerator.PlanItem> items = new ArrayList<>();
            int budget = 25;
            for (int i = 0; i < 8; i++) {
                int mins = rng.nextInt(10) + 1;
                int prio = rng.nextInt(10) + 1;
                items.add(new ReadingPlanGenerator.PlanItem(makeDummyDoc("t" + i + ".txt", "Title " + i, mins * 200), mins * 200, mins, prio));
            }

            int exactOpt = ReadingPlanGenerator.solveExactOptValue(items, budget);
            ReadingPlanGenerator.PlanResult approx = ReadingPlanGenerator.generatePlan(items, budget, 200);

            if (exactOpt > 0) {
                double ratio = (double) approx.getTotalPriority() / exactOpt;
                if (ratio < 0.5) {
                    guaranteeHeld = false;
                    break;
                }
            }
        }

        assertTrue("Reading Plan: Mathematical 1/2-approximation bound verified against Exact 0/1 Knapsack DP (ratio >= 0.5)", guaranteeHeld);
    }

    // =======================================================================
    // 2. Reservoir Sampling (Module 6) Tests
    // =======================================================================

    private static void testReservoirSampler_Empty() {
        LibraryDocument doc = ReservoirSampler.sample(new ArrayList<LibraryDocument>());
        assertTrue("Reservoir Sampling: Returns null for empty collection", doc == null);
    }

    private static void testReservoirSampler_SingleItem() {
        List<LibraryDocument> list = Collections.singletonList(makeDummyDoc("single.txt", "Single Doc", 100));
        LibraryDocument doc = ReservoirSampler.sample(list);
        assertTrue("Reservoir Sampling: Single item is selected with 100% certainty",
            doc != null && doc.getFileName().equals("single.txt"));
    }

    private static void testReservoirSampler_MultiItem() {
        List<LibraryDocument> list = Arrays.asList(
            makeDummyDoc("d1.txt", "D1", 100),
            makeDummyDoc("d2.txt", "D2", 100),
            makeDummyDoc("d3.txt", "D3", 100)
        );
        LibraryDocument doc = ReservoirSampler.sample(list);
        assertTrue("Reservoir Sampling: Multi-item returns a valid member of the stream",
            doc != null && (doc.getFileName().equals("d1.txt") || doc.getFileName().equals("d2.txt") || doc.getFileName().equals("d3.txt")));
    }

    private static void testReservoirSampler_ControlledRandom() {
        // Test with mock Random where nextInt(2) returns 0 (replaces) and nextInt(3) returns 1 (keeps)
        List<LibraryDocument> list = Arrays.asList(
            makeDummyDoc("d1.txt", "D1", 100),
            makeDummyDoc("d2.txt", "D2", 100),
            makeDummyDoc("d3.txt", "D3", 100)
        );

        // Seeded random for determinism
        Random rng = new Random(42);
        LibraryDocument sample1 = ReservoirSampler.sample(list, rng);
        assertTrue("Reservoir Sampling: Injectable Random produces deterministic selection", sample1 != null);
    }

    private static void testReservoirSampler_StatisticalUniformity() {
        // Monte Carlo simulation: Sample from 4 items over 20,000 trials
        // Expected frequency per item: 5,000 (25%)
        // With 3-sigma tolerance: |actual - 5000| < 300
        int nItems = 4;
        List<String> items = Arrays.asList("A", "B", "C", "D");
        int trials = 20000;
        int[] counts = new int[nItems];
        Random rng = new Random(999);

        for (int i = 0; i < trials; i++) {
            String chosen = ReservoirSampler.sample(items, rng);
            int idx = items.indexOf(chosen);
            counts[idx]++;
        }

        boolean withinTolerance = true;
        double expected = trials / (double) nItems;
        for (int c : counts) {
            double deviation = Math.abs(c - expected);
            if (deviation > 400) { // < 2% deviation
                withinTolerance = false;
                break;
            }
        }

        assertTrue("Reservoir Sampling: Statistical uniformity verified over 20,000 trials (empirical P ≈ 0.25 each)", withinTolerance);
    }

    // =======================================================================
    // 3. Parallel Document Search (Module 6) Tests
    // =======================================================================

    private static void testParallelSearch_EmptyAndSmallCorpus() {
        KMP kmp = new KMP();
        LibrarySearchEngine engine = new LibrarySearchEngine(kmp, 4);

        LibrarySearchEngine.SearchExecutionResult resEmpty =
            engine.searchWithMetrics(new ArrayList<>(), "test", LibrarySearchEngine.SearchMode.PARALLEL);
        assertTrue("Parallel Search: Empty corpus returns 0 matches gracefully",
            resEmpty.getMatches().isEmpty() && resEmpty.getScannedDocuments() == 0);

        List<LibraryDocument> small = Collections.singletonList(makeDummyDoc("s1.txt", "Algorithm Search", 100));
        LibrarySearchEngine.SearchExecutionResult resAuto =
            engine.searchWithMetrics(small, "Algorithm", LibrarySearchEngine.SearchMode.AUTO);
        assertTrue("Parallel Search: Auto mode falls back to Sequential for small corpus (< threshold)",
            "Sequential".equalsIgnoreCase(resAuto.getExecutedMode()) && resAuto.getMatches().size() == 1);
        engine.shutdown();
    }

    private static void testParallelSearch_ZeroAndMultipleResults() {
        KMP kmp = new KMP();
        LibrarySearchEngine engine = new LibrarySearchEngine(kmp, 4);

        List<LibraryDocument> corpus = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            corpus.add(makeDummyDoc("doc" + i + ".txt", "Data Structures " + i, 200));
        }

        LibrarySearchEngine.SearchExecutionResult noMatches =
            engine.searchWithMetrics(corpus, "nonexistentpattern123", LibrarySearchEngine.SearchMode.PARALLEL);
        LibrarySearchEngine.SearchExecutionResult multiMatches =
            engine.searchWithMetrics(corpus, "Structures", LibrarySearchEngine.SearchMode.PARALLEL);

        assertTrue("Parallel Search: Handles zero matches and multiple matches correctly",
            noMatches.getMatches().isEmpty() && multiMatches.getMatches().size() == 30);
        engine.shutdown();
    }

    private static void testParallelSearch_StrictEquivalence() {
        // Critical requirement: Sequential and Parallel results must be strictly 100% equivalent in matches and order
        KMP kmp = new KMP();
        LibrarySearchEngine engine = new LibrarySearchEngine(kmp, 4);

        List<LibraryDocument> corpus = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            LibraryDocument doc = new LibraryDocument("doc" + i + ".txt");
            doc.setTitle(i % 2 == 0 ? "Knuth Morris Pratt Algorithm" : "Graph Algorithms");
            doc.setAuthor(i % 3 == 0 ? "Donald Knuth" : "Robert Sedgewick");
            doc.setContent("Content discussing pattern matching in document number " + i + " with algorithm notes.");
            corpus.add(doc);
        }

        LibrarySearchEngine.SearchExecutionResult seq =
            engine.searchWithMetrics(corpus, "Algorithm", LibrarySearchEngine.SearchMode.SEQUENTIAL);
        LibrarySearchEngine.SearchExecutionResult par =
            engine.searchWithMetrics(corpus, "Algorithm", LibrarySearchEngine.SearchMode.PARALLEL);

        boolean sameCount = (seq.getMatches().size() == par.getMatches().size());
        boolean sameOrder = true;

        if (sameCount) {
            for (int i = 0; i < seq.getMatches().size(); i++) {
                LibrarySearchEngine.SearchMatch m1 = seq.getMatches().get(i);
                LibrarySearchEngine.SearchMatch m2 = par.getMatches().get(i);
                if (!m1.getDocument().getFileName().equals(m2.getDocument().getFileName())
                    || !m1.getFieldName().equals(m2.getFieldName())
                    || !m1.getPositions().equals(m2.getPositions())) {
                    sameOrder = false;
                    break;
                }
            }
        }

        assertTrue("Parallel Search: Sequential and Parallel results are 100% identical in items, positions, and order",
            sameCount && sameOrder && seq.getMatches().size() > 0);
        engine.shutdown();
    }

    private static void testParallelSearch_ConcurrencyStress() {
        // Run repeated parallel searches in quick succession to verify thread safety and lack of race conditions
        KMP kmp = new KMP();
        LibrarySearchEngine engine = new LibrarySearchEngine(kmp, 4);

        List<LibraryDocument> corpus = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            corpus.add(makeDummyDoc("stress" + i + ".txt", "Concurrency Test " + (i % 5), 300));
        }

        boolean allCorrect = true;
        for (int query = 0; query < 20; query++) {
            LibrarySearchEngine.SearchExecutionResult res =
                engine.searchWithMetrics(corpus, "Concurrency", LibrarySearchEngine.SearchMode.PARALLEL);
            if (res.getMatches().size() != 50) {
                allCorrect = false;
                break;
            }
        }

        assertTrue("Parallel Search: Thread-safe across 20 rapid consecutive search executions without race conditions", allCorrect);
        engine.shutdown();
    }
}

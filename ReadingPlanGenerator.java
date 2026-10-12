import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ReadingPlanGenerator.java — Module 5: NP-Completeness & Approximation Algorithms.
 *
 * Implements a Greedy 1/2-Approximation Algorithm for the 0/1 Knapsack Problem
 * applied to Digital Library document selection:
 *
 * Problem Formulation:
 *   - Knapsack Capacity: Available reading time budget W (in minutes).
 *   - Item Weights w_i: Estimated reading time of document i in minutes,
 *                       derived from actual text word count and reading speed:
 *                       w_i = max(1, ceil(wordCount / wpm)).
 *   - Item Values v_i: User-assigned document priority score (1 to 10 scale).
 *   - Goal: Select subset S ⊆ Candidates such that sum(w_i) <= W maximizing sum(v_i).
 *
 * Theoretical Foundation (NP-Completeness):
 *   - 0/1 Knapsack is NP-hard (decision version is NP-complete via reduction from SUBSET-SUM).
 *   - For large inputs or strict response bounds, a polynomial-time approximation is ideal.
 *   - The greedy density heuristic selects items in descending order of efficiency (v_i / w_i).
 *   - Comparing the greedy prefix set with the single most valuable feasible item guarantees:
 *         max(V(Greedy), V(SingleBest)) >= 0.5 * OPT
 *     yielding a guaranteed approximation ratio of 1/2 (factor of 2).
 */
public class ReadingPlanGenerator {

    public static final int DEFAULT_READING_SPEED_WPM = 200; // Average adult reading speed

    /** Candidate document with computed reading weight and priority. */
    public static class PlanItem {
        private final LibraryDocument document;
        private final int wordCount;
        private final int estimatedMinutes;
        private final int priority; // 1 to 10
        private final double density; // priority / estimatedMinutes

        public PlanItem(LibraryDocument document, int wordCount, int estimatedMinutes, int priority) {
            this.document = document;
            this.wordCount = wordCount;
            this.estimatedMinutes = estimatedMinutes;
            this.priority = priority;
            this.density = (estimatedMinutes > 0) ? ((double) priority / estimatedMinutes) : 0.0;
        }

        public LibraryDocument getDocument() { return document; }
        public int getWordCount() { return wordCount; }
        public int getEstimatedMinutes() { return estimatedMinutes; }
        public int getPriority() { return priority; }
        public double getDensity() { return density; }
    }

    /** Encapsulates the generated reading plan and complexity metadata. */
    public static class PlanResult {
        private final List<PlanItem> selectedItems;
        private final int totalMinutes;
        private final int totalPriority;
        private final int budgetMinutes;
        private final int remainingMinutes;
        private final int readingSpeedWpm;
        private final String strategy;
        private final String algorithm;
        private final String complexity;
        private final String approximationGuarantee;
        private final String explanation;

        public PlanResult(List<PlanItem> selectedItems, int totalMinutes, int totalPriority,
                          int budgetMinutes, int readingSpeedWpm, String strategy) {
            this.selectedItems = selectedItems;
            this.totalMinutes = totalMinutes;
            this.totalPriority = totalPriority;
            this.budgetMinutes = budgetMinutes;
            this.remainingMinutes = Math.max(0, budgetMinutes - totalMinutes);
            this.readingSpeedWpm = readingSpeedWpm;
            this.strategy = strategy;
            this.algorithm = "Greedy 1/2-Approximation for 0/1 Knapsack (Density Ordering)";
            this.complexity = "Time O(n log n) [sorting], Space O(n)";
            this.approximationGuarantee = "Total Score >= 0.5 * OPT (Mathematical bound for positive-value 0/1 Knapsack)";
            this.explanation = "The 0/1 Knapsack problem is NP-hard. This approximation sorts candidates by priority density "
                + "(priority / reading minutes), takes the greedy prefix that fits the budget, and compares it against "
                + "the single highest-value feasible document. Taking the maximum guarantees at least 50% of the theoretical optimum.";
        }

        public List<PlanItem> getSelectedItems() { return selectedItems; }
        public int getTotalMinutes() { return totalMinutes; }
        public int getTotalPriority() { return totalPriority; }
        public int getBudgetMinutes() { return budgetMinutes; }
        public int getRemainingMinutes() { return remainingMinutes; }
        public int getReadingSpeedWpm() { return readingSpeedWpm; }
        public String getStrategy() { return strategy; }
        public String getAlgorithm() { return algorithm; }
        public String getComplexity() { return complexity; }
        public String getApproximationGuarantee() { return approximationGuarantee; }
        public String getExplanation() { return explanation; }
    }

    /**
     * Generate an approximate reading plan for the candidate documents within the given budget.
     *
     * @param candidates       list of candidate PlanItems
     * @param budgetMinutes    maximum reading time budget in minutes
     * @param readingSpeedWpm  reading speed in words per minute
     * @return PlanResult containing selected items and metrics
     */
    public static PlanResult generatePlan(List<PlanItem> candidates, int budgetMinutes, int readingSpeedWpm) {
        if (candidates == null || candidates.isEmpty() || budgetMinutes <= 0) {
            return new PlanResult(new ArrayList<>(), 0, 0, Math.max(0, budgetMinutes), readingSpeedWpm, "Empty / Zero Budget");
        }

        // Step 1: Discard invalid candidates and documents exceeding the entire budget
        List<PlanItem> feasible = new ArrayList<>();
        PlanItem bestSingle = null;
        int maxSingleValue = -1;

        for (PlanItem item : candidates) {
            if (item == null || item.getDocument() == null) continue;
            if (item.getEstimatedMinutes() <= 0 || item.getPriority() <= 0) continue;

            if (item.getEstimatedMinutes() <= budgetMinutes) {
                feasible.add(item);
                if (item.getPriority() > maxSingleValue) {
                    maxSingleValue = item.getPriority();
                    bestSingle = item;
                }
            }
        }

        if (feasible.isEmpty()) {
            return new PlanResult(new ArrayList<>(), 0, 0, budgetMinutes, readingSpeedWpm, "No Feasible Document (All exceed budget)");
        }

        // Step 2: Sort candidates by priority density (priority / reading time) descending
        // Stable tie-breaking: higher priority first, then filename
        Collections.sort(feasible, (a, b) -> {
            int cmp = Double.compare(b.getDensity(), a.getDensity());
            if (cmp != 0) return cmp;
            int prioCmp = Integer.compare(b.getPriority(), a.getPriority());
            if (prioCmp != 0) return prioCmp;
            return a.getDocument().getFileName().compareTo(b.getDocument().getFileName());
        });

        // Step 3: Greedily add documents while the next document fits. Stop at first candidate that cannot be added.
        List<PlanItem> greedySet = new ArrayList<>();
        int greedyMinutes = 0;
        int greedyValue = 0;

        for (PlanItem item : feasible) {
            if (greedyMinutes + item.getEstimatedMinutes() <= budgetMinutes) {
                greedySet.add(item);
                greedyMinutes += item.getEstimatedMinutes();
                greedyValue += item.getPriority();
            } else {
                // Stop at the first candidate that cannot be added (canonical greedy prefix)
                break;
            }
        }

        // Step 4 & 5: Compare greedy set with best single feasible document
        if (bestSingle != null && bestSingle.getPriority() > greedyValue) {
            List<PlanItem> singleSet = new ArrayList<>();
            singleSet.add(bestSingle);
            return new PlanResult(singleSet, bestSingle.getEstimatedMinutes(), bestSingle.getPriority(),
                                  budgetMinutes, readingSpeedWpm, "Single Best Feasible Document (Exceeded Greedy Prefix Value)");
        } else {
            return new PlanResult(greedySet, greedyMinutes, greedyValue,
                                  budgetMinutes, readingSpeedWpm, "Greedy Density Prefix Set");
        }
    }

    /**
     * Exact 0/1 Knapsack Dynamic Programming solver (used for verification & test comparisons).
     * Time: O(n * W), Space: O(n * W).
     */
    public static int solveExactOptValue(List<PlanItem> items, int budgetMinutes) {
        if (items == null || items.isEmpty() || budgetMinutes <= 0) return 0;

        List<PlanItem> valid = new ArrayList<>();
        for (PlanItem it : items) {
            if (it != null && it.getEstimatedMinutes() > 0 && it.getPriority() > 0 && it.getEstimatedMinutes() <= budgetMinutes) {
                valid.add(it);
            }
        }
        int n = valid.size();
        if (n == 0) return 0;

        int[][] dp = new int[n + 1][budgetMinutes + 1];
        for (int i = 1; i <= n; i++) {
            PlanItem it = valid.get(i - 1);
            int wt = it.getEstimatedMinutes();
            int val = it.getPriority();
            for (int w = 0; w <= budgetMinutes; w++) {
                dp[i][w] = dp[i - 1][w];
                if (w >= wt) {
                    dp[i][w] = Math.max(dp[i][w], dp[i - 1][w - wt] + val);
                }
            }
        }
        return dp[n][budgetMinutes];
    }
}

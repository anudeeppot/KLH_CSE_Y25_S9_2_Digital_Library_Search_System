package com.library.search.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Course Outcome 3 (CO-3):
 * "Apply advanced dynamic-programming patterns — interval DP, bitmask DP, DP on trees, DP on subsets —
 * to design polynomial-time algorithms for combinatorial optimisation."
 *
 * Pattern 2: Bitmask Dynamic Programming
 * State representation uses an integer bitmask (0 <= mask < 2^N) to represent a subset of competencies/topics.
 *
 * Digital Library Application: Curator's Minimum Cost Syllabus Topic Coverage (Optimal Book Bundle Selection).
 * Given N required academic topics (e.g. 8 topics: DSA, DB, OS, Networks, AI, Crypto, Compilers, Distributed Systems),
 * and a repository of library books, where each book covers a specific bitmask of topics and has a cost/page-count,
 * find the minimum cost subset of books that achieves full topic coverage (11111111_2).
 *
 * Recurrence:
 * DP[mask | book.mask] = min(DP[mask | book.mask], DP[mask] + book.cost)
 * Base Case: DP[0] = 0, all other DP[mask] = infinity
 * Complexity: O(2^N * M) where N is number of topics and M is number of books.
 */
@Component
public class BitmaskDP {

    public static class LibraryBookItem {
        private final int id;
        private final String title;
        private final int cost; // e.g. page count, weight, or cost
        private final int topicMask;
        private final List<String> coveredTopics;

        public LibraryBookItem(int id, String title, int cost, int topicMask, List<String> coveredTopics) {
            this.id = id;
            this.title = title;
            this.cost = cost;
            this.topicMask = topicMask;
            this.coveredTopics = coveredTopics;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
        public int getCost() { return cost; }
        public int getTopicMask() { return topicMask; }
        public List<String> getCoveredTopics() { return coveredTopics; }
    }

    public static class BitmaskStep {
        private final String fromMaskBinary;
        private final String toMaskBinary;
        private final String bookAdded;
        private final int newCost;

        public BitmaskStep(String fromMaskBinary, String toMaskBinary, String bookAdded, int newCost) {
            this.fromMaskBinary = fromMaskBinary;
            this.toMaskBinary = toMaskBinary;
            this.bookAdded = bookAdded;
            this.newCost = newCost;
        }

        public String getFromMaskBinary() { return fromMaskBinary; }
        public String getToMaskBinary() { return toMaskBinary; }
        public String getBookAdded() { return bookAdded; }
        public int getNewCost() { return newCost; }
    }

    public static class BitmaskResult {
        private final List<String> requiredTopics;
        private final List<LibraryBookItem> availableBooks;
        private final int optimalCost;
        private final List<LibraryBookItem> selectedBooks;
        private final List<BitmaskStep> transitionHistory;
        private final int totalStatesEvaluated;
        private final long executionTimeNanos;
        private final String theoreticalInsight;

        public BitmaskResult(List<String> requiredTopics, List<LibraryBookItem> availableBooks,
                             int optimalCost, List<LibraryBookItem> selectedBooks,
                             List<BitmaskStep> transitionHistory, int totalStatesEvaluated,
                             long executionTimeNanos, String theoreticalInsight) {
            this.requiredTopics = requiredTopics;
            this.availableBooks = availableBooks;
            this.optimalCost = optimalCost;
            this.selectedBooks = selectedBooks;
            this.transitionHistory = transitionHistory;
            this.totalStatesEvaluated = totalStatesEvaluated;
            this.executionTimeNanos = executionTimeNanos;
            this.theoreticalInsight = theoreticalInsight;
        }

        public List<String> getRequiredTopics() { return requiredTopics; }
        public List<LibraryBookItem> getAvailableBooks() { return availableBooks; }
        public int getOptimalCost() { return optimalCost; }
        public List<LibraryBookItem> getSelectedBooks() { return selectedBooks; }
        public List<BitmaskStep> getTransitionHistory() { return transitionHistory; }
        public int getTotalStatesEvaluated() { return totalStatesEvaluated; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public String getTheoreticalInsight() { return theoreticalInsight; }
    }

    public BitmaskResult computeOptimalTopicCoverage(List<String> topics, List<LibraryBookItem> customBooks) {
        long startTime = System.nanoTime();

        List<String> targetTopics = (topics != null && !topics.isEmpty()) ? topics :
                Arrays.asList("Algorithms", "Database Systems", "Operating Systems", "Computer Networks", "Artificial Intelligence", "Cryptography");

        int n = targetTopics.size();
        int targetMask = (1 << n) - 1;

        List<LibraryBookItem> books = (customBooks != null && !customBooks.isEmpty()) ? customBooks : getDefaultBooks();

        int[] dp = new int[1 << n];
        int[] parentMask = new int[1 << n];
        int[] parentBook = new int[1 << n];

        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        Arrays.fill(parentMask, -1);
        Arrays.fill(parentBook, -1);
        dp[0] = 0;

        List<BitmaskStep> steps = new ArrayList<>();

        for (int mask = 0; mask < (1 << n); mask++) {
            if (dp[mask] >= Integer.MAX_VALUE / 2) continue;

            for (LibraryBookItem book : books) {
                int nextMask = mask | book.getTopicMask();
                int nextCost = dp[mask] + book.getCost();

                if (nextCost < dp[nextMask]) {
                    dp[nextMask] = nextCost;
                    parentMask[nextMask] = mask;
                    parentBook[nextMask] = book.getId();

                    if (steps.size() < 30) {
                        steps.add(new BitmaskStep(
                                toBinaryString(mask, n),
                                toBinaryString(nextMask, n),
                                book.getTitle(),
                                nextCost
                        ));
                    }
                }
            }
        }

        // Backtrack selected books
        List<LibraryBookItem> selectedBooks = new ArrayList<>();
        int curr = targetMask;
        if (dp[targetMask] < Integer.MAX_VALUE / 2) {
            while (curr > 0) {
                int bookId = parentBook[curr];
                int prev = parentMask[curr];
                for (LibraryBookItem b : books) {
                    if (b.getId() == bookId) {
                        selectedBooks.add(b);
                        break;
                    }
                }
                curr = prev;
            }
            Collections.reverse(selectedBooks);
        }

        long executionTime = System.nanoTime() - startTime;
        String insight = "Bitmask DP explored " + (1 << n) + " state configurations across " + books.size() +
                " candidate volumes. By avoiding 2^" + books.size() + " exhaustive subset evaluation, optimal cost is " +
                (dp[targetMask] < Integer.MAX_VALUE / 2 ? dp[targetMask] + " pages/units" : "Infeasible") +
                " using " + selectedBooks.size() + " selected volumes.";

        return new BitmaskResult(targetTopics, books, dp[targetMask], selectedBooks, steps, (1 << n), executionTime, insight);
    }

    private String toBinaryString(int mask, int len) {
        StringBuilder sb = new StringBuilder(Integer.toBinaryString(mask));
        while (sb.length() < len) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    private List<LibraryBookItem> getDefaultBooks() {
        List<LibraryBookItem> list = new ArrayList<>();
        // 6 Topics: 0: Algorithms, 1: Databases, 2: OS, 3: Networks, 4: AI, 5: Cryptography
        list.add(new LibraryBookItem(1, "Introduction to Algorithms (CLRS)", 1200, (1 << 0), Collections.singletonList("Algorithms")));
        list.add(new LibraryBookItem(2, "Designing Data-Intensive Applications", 600, (1 << 0) | (1 << 1) | (1 << 3), Arrays.asList("Algorithms", "Databases", "Computer Networks")));
        list.add(new LibraryBookItem(3, "Modern Operating Systems (Tanenbaum)", 900, (1 << 2) | (1 << 3), Arrays.asList("Operating Systems", "Computer Networks")));
        list.add(new LibraryBookItem(4, "Database System Concepts (Silberschatz)", 800, (1 << 1), Collections.singletonList("Databases")));
        list.add(new LibraryBookItem(5, "Artificial Intelligence: A Modern Approach", 1100, (1 << 0) | (1 << 4), Arrays.asList("Algorithms", "Artificial Intelligence")));
        list.add(new LibraryBookItem(6, "Computer Networking: A Top-Down Approach", 850, (1 << 3) | (1 << 5), Arrays.asList("Computer Networks", "Cryptography")));
        list.add(new LibraryBookItem(7, "Cryptography and Network Security (Stallings)", 750, (1 << 5), Collections.singletonList("Cryptography")));
        list.add(new LibraryBookItem(8, "Foundations of Deep Learning", 800, (1 << 4), Collections.singletonList("Artificial Intelligence")));
        return list;
    }
}

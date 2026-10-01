package com.library.search.algorithm;

import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Suffix Array Algorithm for Full-Text Indexing and Binary Search Substring Retrieval.
 *
 * Constructs a sorted index of all suffixes of a text.
 * Pattern searching is performed using Binary Search.
 *
 * Complexity Analysis:
 * - Construction: O(n^2 log n) using standard sorting (or O(n log n) with prefix doubling / O(n) with SA-IS).
 * - Pattern Search: Binary search requires O(log n) comparisons.
 *   Because each string comparison inspects up to m characters, standard binary search on suffix array
 *   takes O(m * log n) time.
 * - With LCP (Longest Common Prefix) acceleration, it can be reduced towards O(m + log n).
 */
@Component
public class SuffixArray {

    public static class SuffixItem {
        private final int index;
        private final String suffix;

        public SuffixItem(int index, String suffix) {
            this.index = index;
            this.suffix = suffix;
        }

        public int getIndex() { return index; }
        public String getSuffix() { return suffix; }
    }

    public static class SuffixArrayResult {
        private final String text;
        private final String pattern;
        private final int[] suffixArray;
        private final List<SuffixItem> sampleSuffixes;
        private final int[] lcpArray;
        private final List<Integer> matchPositions;
        private final List<String> binarySearchSteps;
        private final long executionTime;
        private final String complexityExplanation;

        public SuffixArrayResult(String text, String pattern, int[] suffixArray, List<SuffixItem> sampleSuffixes,
                                 int[] lcpArray, List<Integer> matchPositions, List<String> binarySearchSteps,
                                 long executionTime, String complexityExplanation) {
            this.text = text;
            this.pattern = pattern;
            this.suffixArray = suffixArray;
            this.sampleSuffixes = sampleSuffixes;
            this.lcpArray = lcpArray;
            this.matchPositions = matchPositions;
            this.binarySearchSteps = binarySearchSteps;
            this.executionTime = executionTime;
            this.complexityExplanation = complexityExplanation;
        }

        public String getText() { return text; }
        public String getPattern() { return pattern; }
        public int[] getSuffixArray() { return suffixArray; }
        public List<SuffixItem> getSampleSuffixes() { return sampleSuffixes; }
        public int[] getLcpArray() { return lcpArray; }
        public List<Integer> getMatchPositions() { return matchPositions; }
        public List<String> getBinarySearchSteps() { return binarySearchSteps; }
        public long getExecutionTime() { return executionTime; }
        public String getComplexityExplanation() { return complexityExplanation; }
    }

    public SuffixArrayResult buildAndSearch(String text, String pattern) {
        long startTime = System.nanoTime();
        String targetText = (text != null) ? text : "";
        String targetPattern = (pattern != null) ? pattern : "";
        int n = targetText.length();

        if (n == 0 || targetPattern.isEmpty()) {
            return new SuffixArrayResult(
                    targetText, targetPattern, new int[0], new ArrayList<>(), new int[0],
                    new ArrayList<>(), Collections.singletonList("Input text or pattern was empty."),
                    System.nanoTime() - startTime, "Empty input provided."
            );
        }

        // 1. Build Suffix Array
        Integer[] sa = new Integer[n];
        for (int i = 0; i < n; i++) {
            sa[i] = i;
        }

        // Sort suffixes lexicographically (case-insensitive for library search)
        String lowerText = targetText.toLowerCase();
        Arrays.sort(sa, (a, b) -> lowerText.substring(a).compareTo(lowerText.substring(b)));

        int[] suffixArray = new int[n];
        for (int i = 0; i < n; i++) {
            suffixArray[i] = sa[i];
        }

        // 2. Build LCP (Kasai's algorithm)
        int[] lcp = computeLCP(lowerText, suffixArray);

        // 3. Prepare sample suffixes for visualizer (limit to first 30 for UI responsiveness)
        List<SuffixItem> sampleSuffixes = new ArrayList<>();
        int sampleLimit = Math.min(n, 30);
        for (int i = 0; i < sampleLimit; i++) {
            int idx = suffixArray[i];
            String suf = targetText.substring(idx);
            if (suf.length() > 50) {
                suf = suf.substring(0, 47) + "...";
            }
            sampleSuffixes.add(new SuffixItem(idx, suf));
        }

        // 4. Binary Search for pattern range [lowBound, highBound]
        List<String> searchSteps = new ArrayList<>();
        String lowerPat = targetPattern.toLowerCase();
        int m = lowerPat.length();

        int low = 0;
        int high = n - 1;
        int firstOccur = -1;

        searchSteps.add("Binary searching for prefix range of pattern '" + targetPattern + "' over " + n + " sorted suffixes.");

        // Find lower bound
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int sufIndex = suffixArray[mid];
            String midSuffix = lowerText.substring(sufIndex, Math.min(sufIndex + m, n));

            int cmp = midSuffix.compareTo(lowerPat);
            searchSteps.add("Step: low=" + low + ", mid=" + mid + ", high=" + high + " | Suffix[" + sufIndex + "] starts with '" + midSuffix + "' vs Pattern '" + lowerPat + "' -> cmp=" + cmp);

            if (cmp >= 0) {
                if (cmp == 0) firstOccur = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        List<Integer> matchPositions = new ArrayList<>();
        if (firstOccur != -1) {
            // Expand range to collect all matches sharing this prefix
            int curr = firstOccur;
            while (curr < n) {
                int sufIndex = suffixArray[curr];
                if (lowerText.startsWith(lowerPat, sufIndex)) {
                    matchPositions.add(sufIndex);
                    curr++;
                } else {
                    break;
                }
            }
            Collections.sort(matchPositions);
            searchSteps.add(">> Found " + matchPositions.size() + " occurrences in Suffix Array prefix range <<");
        } else {
            searchSteps.add("Pattern not present in any suffix prefix.");
        }

        long executionTime = System.nanoTime() - startTime;
        String explanation = "Binary Search over Suffix Array requires O(m * log n) time because each binary step involves up to m character comparisons. With LCP acceleration, this approaches O(m + log n). Preprocessing sorts all n suffixes once, allowing repeated queries without scanning full text.";

        return new SuffixArrayResult(targetText, targetPattern, suffixArray, sampleSuffixes, lcp, matchPositions, searchSteps, executionTime, explanation);
    }

    private int[] computeLCP(String text, int[] sa) {
        int n = text.length();
        int[] lcp = new int[n];
        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) h--;
            }
        }
        return lcp;
    }
}

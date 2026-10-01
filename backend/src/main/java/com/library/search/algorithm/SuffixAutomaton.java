package com.library.search.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Course Outcome 2 (CO-2):
 * "Apply linear-time string algorithms (KMP, Z-function, Rabin-Karp with rolling hash)
 * and suffix-based structures (suffix array, LCP array, suffix automaton at intuition level)
 * to solve large-scale pattern-matching problems."
 *
 * Suffix Automaton (Intuition Level):
 * A Suffix Automaton (Directed Acyclic Word Graph - DAWG) is the minimal deterministic finite automaton
 * that accepts all substrings of a given string S of length n.
 *
 * Key Theoretical Properties:
 * 1. Number of states <= 2n - 1 (for n >= 2).
 * 2. Number of transitions <= 3n - 4 (for n >= 3).
 * 3. Linear-time construction in O(n) using suffix links (link[u]).
 * 4. Exact substring query answering in O(m) time where m = |pattern|, completely independent of text length n!
 */
@Component
public class SuffixAutomaton {

    public static class State {
        private final int id;
        private int len;
        private int link;
        private final Map<Character, Integer> next = new HashMap<>();
        private boolean isClone;
        private int occurrences;

        public State(int id, int len, int link) {
            this.id = id;
            this.len = len;
            this.link = link;
            this.isClone = false;
            this.occurrences = 0;
        }

        public int getId() { return id; }
        public int getLen() { return len; }
        public int getLink() { return link; }
        public Map<Character, Integer> getNext() { return next; }
        public boolean isClone() { return isClone; }
        public int getOccurrences() { return occurrences; }
    }

    public static class StateView {
        private final int id;
        private final int len;
        private final int suffixLink;
        private final boolean isClone;
        private final Map<String, Integer> transitions;

        public StateView(int id, int len, int suffixLink, boolean isClone, Map<String, Integer> transitions) {
            this.id = id;
            this.len = len;
            this.suffixLink = suffixLink;
            this.isClone = isClone;
            this.transitions = transitions;
        }

        public int getId() { return id; }
        public int getLen() { return len; }
        public int getSuffixLink() { return suffixLink; }
        public boolean isClone() { return isClone; }
        public Map<String, Integer> getTransitions() { return transitions; }
    }

    public static class AutomatonResult {
        private final String sourceText;
        private final String queryPattern;
        private final boolean isSubstring;
        private final int totalStates;
        private final int totalTransitions;
        private final List<Integer> traversalPath;
        private final List<StateView> sampleStates;
        private final long executionTimeNanos;
        private final String theoreticalInsight;

        public AutomatonResult(String sourceText, String queryPattern, boolean isSubstring,
                               int totalStates, int totalTransitions, List<Integer> traversalPath,
                               List<StateView> sampleStates, long executionTimeNanos, String theoreticalInsight) {
            this.sourceText = sourceText;
            this.queryPattern = queryPattern;
            this.isSubstring = isSubstring;
            this.totalStates = totalStates;
            this.totalTransitions = totalTransitions;
            this.traversalPath = traversalPath;
            this.sampleStates = sampleStates;
            this.executionTimeNanos = executionTimeNanos;
            this.theoreticalInsight = theoreticalInsight;
        }

        public String getSourceText() { return sourceText; }
        public String getQueryPattern() { return queryPattern; }
        public boolean isSubstring() { return isSubstring; }
        public int getTotalStates() { return totalStates; }
        public int getTotalTransitions() { return totalTransitions; }
        public List<Integer> getTraversalPath() { return traversalPath; }
        public List<StateView> getSampleStates() { return sampleStates; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public String getTheoreticalInsight() { return theoreticalInsight; }
    }

    public AutomatonResult buildAndQuery(String text, String pattern) {
        long startTime = System.nanoTime();
        String src = (text != null && !text.isEmpty()) ? text : "digital library search";
        String pat = (pattern != null) ? pattern : "library";

        List<State> states = new ArrayList<>();
        states.add(new State(0, 0, -1)); // initial state (root)
        int last = 0;

        // Build Suffix Automaton in linear O(n)
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            int cur = states.size();
            states.add(new State(cur, states.get(last).len + 1, 0));

            int p = last;
            while (p >= 0 && !states.get(p).next.containsKey(c)) {
                states.get(p).next.put(c, cur);
                p = states.get(p).link;
            }

            if (p == -1) {
                states.get(cur).link = 0;
            } else {
                int q = states.get(p).next.get(c);
                if (states.get(p).len + 1 == states.get(q).len) {
                    states.get(cur).link = q;
                } else {
                    int clone = states.size();
                    State cloneState = new State(clone, states.get(p).len + 1, states.get(q).link);
                    cloneState.next.putAll(states.get(q).next);
                    cloneState.isClone = true;
                    states.add(cloneState);

                    while (p >= 0 && states.get(p).next.get(c) != null && states.get(p).next.get(c) == q) {
                        states.get(p).next.put(c, clone);
                        p = states.get(p).link;
                    }
                    states.get(q).link = clone;
                    states.get(cur).link = clone;
                }
            }
            last = cur;
        }

        // Count total transitions
        int totalTransitions = 0;
        for (State s : states) {
            totalTransitions += s.next.size();
        }

        // Query the pattern in O(m) time!
        List<Integer> path = new ArrayList<>();
        path.add(0);
        int currState = 0;
        boolean found = true;

        for (int i = 0; i < pat.length(); i++) {
            char c = pat.charAt(i);
            if (!states.get(currState).next.containsKey(c)) {
                found = false;
                break;
            }
            currState = states.get(currState).next.get(c);
            path.add(currState);
        }

        // Export state views (limit to first 25 for UI clarity)
        List<StateView> stateViews = new ArrayList<>();
        int sampleLimit = Math.min(states.size(), 25);
        for (int i = 0; i < sampleLimit; i++) {
            State s = states.get(i);
            Map<String, Integer> trans = new HashMap<>();
            for (Map.Entry<Character, Integer> entry : s.next.entrySet()) {
                trans.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            stateViews.add(new StateView(s.id, s.len, s.link, s.isClone, trans));
        }

        long executionTime = System.nanoTime() - startTime;
        String insight = "Suffix Automaton compactly represents all " + (src.length() * (src.length() + 1) / 2) +
                " potential substrings using only " + states.size() + " states and " + totalTransitions +
                " transitions. Querying '" + pat + "' took strictly " + (found ? path.size() - 1 : "early-exit") +
                " state transitions in O(|pattern|) time.";

        return new AutomatonResult(src, pat, found, states.size(), totalTransitions, path, stateViews, executionTime, insight);
    }
}

package com.library.search.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Course Outcome 3 (CO-3):
 * "Apply advanced dynamic-programming patterns — interval DP, bitmask DP, DP on trees, DP on subsets —
 * to design polynomial-time algorithms for combinatorial optimisation."
 *
 * Pattern 3: Dynamic Programming on Trees (Tree DP)
 * Digital Library Application: Hierarchical Knowledge Taxonomy (ACM / Dewey Decimal Classification) Subtree Optimization.
 *
 * Problem: Maximum Weight Independent Set on Knowledge Taxonomy Tree.
 * In digital library curriculum curation and grant accreditation, certain parent-child disciplines overlap in scope.
 * To maximize educational impact without duplicative syllabus credit, we select a non-adjacent subset of taxonomy nodes.
 *
 * Recurrence:
 * DP[u][0] = sum_{v in children(u)} max(DP[v][0], DP[v][1])   (u is NOT chosen: children can be chosen or not)
 * DP[u][1] = weight[u] + sum_{v in children(u)} DP[v][0]     (u IS chosen: children CANNOT be chosen)
 *
 * Time Complexity: O(N) where N is number of taxonomy nodes.
 * Space Complexity: O(N) call stack / memoization table.
 */
@Component
public class TreeDP {

    public static class TaxonomyNode {
        private final int id;
        private final String name;
        private final int weight; // Academic impact / citation value
        private final List<TaxonomyNode> children = new ArrayList<>();

        public TaxonomyNode(int id, String name, int weight) {
            this.id = id;
            this.name = name;
            this.weight = weight;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getWeight() { return weight; }
        public List<TaxonomyNode> getChildren() { return children; }
        public void addChild(TaxonomyNode child) { children.add(child); }
    }

    public static class NodeDecision {
        private final int id;
        private final String name;
        private final int weight;
        private final long dpExclude;
        private final long dpInclude;
        private final boolean selectedInOptimal;

        public NodeDecision(int id, String name, int weight, long dpExclude, long dpInclude, boolean selectedInOptimal) {
            this.id = id;
            this.name = name;
            this.weight = weight;
            this.dpExclude = dpExclude;
            this.dpInclude = dpInclude;
            this.selectedInOptimal = selectedInOptimal;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getWeight() { return weight; }
        public long getDpExclude() { return dpExclude; }
        public long getDpInclude() { return dpInclude; }
        public boolean isSelectedInOptimal() { return selectedInOptimal; }
    }

    public static class TreeDPResult {
        private final long maxImpactScore;
        private final List<NodeDecision> nodeDecisions;
        private final List<String> selectedCategories;
        private final long executionTimeNanos;
        private final String recurrenceExplanation;

        public TreeDPResult(long maxImpactScore, List<NodeDecision> nodeDecisions,
                            List<String> selectedCategories, long executionTimeNanos,
                            String recurrenceExplanation) {
            this.maxImpactScore = maxImpactScore;
            this.nodeDecisions = nodeDecisions;
            this.selectedCategories = selectedCategories;
            this.executionTimeNanos = executionTimeNanos;
            this.recurrenceExplanation = recurrenceExplanation;
        }

        public long getMaxImpactScore() { return maxImpactScore; }
        public List<NodeDecision> getNodeDecisions() { return nodeDecisions; }
        public List<String> getSelectedCategories() { return selectedCategories; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public String getRecurrenceExplanation() { return recurrenceExplanation; }
    }

    public TreeDPResult optimizeTaxonomyTree() {
        long startTime = System.nanoTime();
        TaxonomyNode root = buildDefaultTaxonomyTree();

        Map<Integer, long[]> dp = new HashMap<>(); // id -> [dpExclude, dpInclude]
        postOrderDP(root, dp);

        Set<Integer> selectedNodes = new HashSet<>();
        backtrack(root, false, dp, selectedNodes);

        List<NodeDecision> decisions = new ArrayList<>();
        List<String> selectedNames = new ArrayList<>();
        collectDecisions(root, dp, selectedNodes, decisions, selectedNames);

        long maxScore = Math.max(dp.get(root.getId())[0], dp.get(root.getId())[1]);
        long executionTime = System.nanoTime() - startTime;
        String explanation = "Tree DP computed optimal curriculum impact in strictly O(N) linear time via post-order subtree aggregation. Recurrence: DP[u][0] = sum(max(DP[v][0], DP[v][1])), DP[u][1] = weight[u] + sum(DP[v][0]).";

        return new TreeDPResult(maxScore, decisions, selectedNames, executionTime, explanation);
    }

    private void postOrderDP(TaxonomyNode u, Map<Integer, long[]> dp) {
        long dpExclude = 0;
        long dpInclude = u.getWeight();

        for (TaxonomyNode v : u.getChildren()) {
            postOrderDP(v, dp);
            long[] childDp = dp.get(v.getId());
            dpExclude += Math.max(childDp[0], childDp[1]);
            dpInclude += childDp[0];
        }

        dp.put(u.getId(), new long[]{dpExclude, dpInclude});
    }

    private void backtrack(TaxonomyNode u, boolean parentSelected, Map<Integer, long[]> dp, Set<Integer> selected) {
        long[] val = dp.get(u.getId());
        if (parentSelected) {
            // Cannot select u
            for (TaxonomyNode v : u.getChildren()) {
                backtrack(v, false, dp, selected);
            }
        } else {
            // Choose the better of including u or excluding u
            if (val[1] > val[0]) {
                selected.add(u.getId());
                for (TaxonomyNode v : u.getChildren()) {
                    backtrack(v, true, dp, selected);
                }
            } else {
                for (TaxonomyNode v : u.getChildren()) {
                    backtrack(v, false, dp, selected);
                }
            }
        }
    }

    private void collectDecisions(TaxonomyNode u, Map<Integer, long[]> dp, Set<Integer> selected,
                                  List<NodeDecision> decisions, List<String> selectedNames) {
        long[] val = dp.get(u.getId());
        boolean isSel = selected.contains(u.getId());
        decisions.add(new NodeDecision(u.getId(), u.getName(), u.getWeight(), val[0], val[1], isSel));
        if (isSel) {
            selectedNames.add(u.getName() + " (Impact: " + u.getWeight() + ")");
        }
        for (TaxonomyNode v : u.getChildren()) {
            collectDecisions(v, dp, selected, decisions, selectedNames);
        }
    }

    private TaxonomyNode buildDefaultTaxonomyTree() {
        // Digital Library Hierarchical Knowledge Classification
        TaxonomyNode root = new TaxonomyNode(1, "000: Computer Science & Digital Library Core", 60);

        TaxonomyNode dsa = new TaxonomyNode(2, "004: Data Structures & Algorithms", 95);
        TaxonomyNode stringAlgo = new TaxonomyNode(5, "004.1: Linear String Search & Suffix Structures", 90);
        TaxonomyNode graphFlow = new TaxonomyNode(6, "004.2: Network Flow & Matching Algorithms", 85);
        TaxonomyNode dp = new TaxonomyNode(7, "004.3: Dynamic Programming Patterns", 92);
        dsa.addChild(stringAlgo);
        dsa.addChild(graphFlow);
        dsa.addChild(dp);

        TaxonomyNode db = new TaxonomyNode(3, "005: Database Systems & Information Retrieval", 80);
        TaxonomyNode indexing = new TaxonomyNode(8, "005.1: Suffix Arrays & Inverted Indexing", 78);
        TaxonomyNode vectorSearch = new TaxonomyNode(9, "005.2: TF-IDF Vector Space Models", 82);
        db.addChild(indexing);
        db.addChild(vectorSearch);

        TaxonomyNode ai = new TaxonomyNode(4, "006: Artificial Intelligence & NLP", 88);
        TaxonomyNode deepLearning = new TaxonomyNode(10, "006.1: Deep Learning & Transformers", 94);
        TaxonomyNode bioNLP = new TaxonomyNode(11, "006.2: Sequence Alignment & Bio-Informatics", 75);
        ai.addChild(deepLearning);
        ai.addChild(bioNLP);

        root.addChild(dsa);
        root.addChild(db);
        root.addChild(ai);

        return root;
    }
}

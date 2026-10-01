package com.library.search.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Course Outcome 4 (CO-4):
 * "Dinic's Algorithm (at intuition level) and Max-Flow / Min-Cut Duality"
 *
 * Dinic's Algorithm constructs a Level Graph using BFS, where level[v] = level[u] + 1.
 * It then pushes Blocking Flows exclusively along admissible level graph edges using DFS.
 *
 * Complexity: O(V^2 * E).
 * On unit capacity networks (such as bipartite reservation matching), Dinic runs in O(E * sqrt(V)).
 */
@Component
public class DinicAlgorithm {

    public static class DinicPhase {
        private final int phaseNumber;
        private final Map<Integer, Integer> levels;
        private final int flowPushedInPhase;
        private final int cumulativeFlow;

        public DinicPhase(int phaseNumber, Map<Integer, Integer> levels, int flowPushedInPhase, int cumulativeFlow) {
            this.phaseNumber = phaseNumber;
            this.levels = levels;
            this.flowPushedInPhase = flowPushedInPhase;
            this.cumulativeFlow = cumulativeFlow;
        }

        public int getPhaseNumber() { return phaseNumber; }
        public Map<Integer, Integer> getLevels() { return levels; }
        public int getFlowPushedInPhase() { return flowPushedInPhase; }
        public int getCumulativeFlow() { return cumulativeFlow; }
    }

    public static class DinicResult {
        private final int maxFlow;
        private final List<DinicPhase> phases;
        private final int totalPhases;
        private final long executionTimeNanos;
        private final String theoreticalInsight;

        public DinicResult(int maxFlow, List<DinicPhase> phases, int totalPhases,
                           long executionTimeNanos, String theoreticalInsight) {
            this.maxFlow = maxFlow;
            this.phases = phases;
            this.totalPhases = totalPhases;
            this.executionTimeNanos = executionTimeNanos;
            this.theoreticalInsight = theoreticalInsight;
        }

        public int getMaxFlow() { return maxFlow; }
        public List<DinicPhase> getPhases() { return phases; }
        public int getTotalPhases() { return totalPhases; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public String getTheoreticalInsight() { return theoreticalInsight; }
    }

    public DinicResult computeMaxFlow(FlowNetwork network, int s, int t) {
        long startTime = System.nanoTime();
        int n = network.getVertexCount();
        int[] level = new int[n];
        int maxFlow = 0;
        int phaseCount = 0;
        List<DinicPhase> phases = new ArrayList<>();

        while (bfs(network, s, t, level)) {
            phaseCount++;
            int[] ptr = new int[n]; // Pointer to next edge to explore in DFS
            int pushed = 0;

            while (true) {
                int tr = dfs(network, s, t, Integer.MAX_VALUE, level, ptr);
                if (tr == 0) break;
                pushed += tr;
            }

            maxFlow += pushed;

            Map<Integer, Integer> levelMap = new HashMap<>();
            for (int i = 0; i < n; i++) {
                if (level[i] != -1) levelMap.put(i, level[i]);
            }
            phases.add(new DinicPhase(phaseCount, levelMap, pushed, maxFlow));
        }

        long executionTime = System.nanoTime() - startTime;
        String insight = "Dinic's Algorithm converged in " + phaseCount + " BFS level phases. By pushing blocking flows across layered DAGs, Dinic bounds phase count by O(V) and runs in O(V^2 * E) general, O(E * sqrt(V)) unit.";

        return new DinicResult(maxFlow, phases, phaseCount, executionTime, insight);
    }

    private boolean bfs(FlowNetwork network, int s, int t, int[] level) {
        Arrays.fill(level, -1);
        level[s] = 0;
        Queue<Integer> q = new LinkedList<>();
        q.add(s);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (FlowNetwork.Edge edge : network.getAdj().get(u)) {
                int v = edge.getTo();
                if (level[v] == -1 && edge.remainingCapacity() > 0) {
                    level[v] = level[u] + 1;
                    q.add(v);
                }
            }
        }
        return level[t] != -1;
    }

    private int dfs(FlowNetwork network, int u, int t, int pushed, int[] level, int[] ptr) {
        if (pushed == 0 || u == t) return pushed;

        for (int cid = ptr[u]; cid < network.getAdj().get(u).size(); cid++, ptr[u]++) {
            FlowNetwork.Edge edge = network.getAdj().get(u).get(cid);
            int v = edge.getTo();

            if (level[u] + 1 != level[v] || edge.remainingCapacity() == 0) continue;

            int tr = dfs(network, v, t, Math.min(pushed, edge.remainingCapacity()), level, ptr);
            if (tr == 0) continue;

            edge.setFlow(edge.getFlow() + tr);
            edge.getResidual().setFlow(edge.getResidual().getFlow() - tr);
            return tr;
        }
        return 0;
    }
}

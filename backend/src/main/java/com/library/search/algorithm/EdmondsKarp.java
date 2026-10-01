package com.library.search.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Course Outcome 4 (CO-4):
 * "Apply network-flow algorithms (Ford-Fulkerson with Edmonds-Karp, Dinic at intuition level)
 * and the max-flow / min-cut duality to model and solve assignment, matching, and capacity-constrained problems."
 *
 * Edmonds-Karp Algorithm:
 * Specialization of Ford-Fulkerson that uses Breadth-First Search (BFS) to find the shortest
 * augmenting path (in terms of edge count) from source S to sink T.
 *
 * Guarantees polynomial time complexity: O(V * E^2).
 */
@Component
public class EdmondsKarp {

    public static class AugmentingPathStep {
        private final List<String> pathNodes;
        private final int bottleneckCapacity;
        private final int cumulativeFlow;

        public AugmentingPathStep(List<String> pathNodes, int bottleneckCapacity, int cumulativeFlow) {
            this.pathNodes = pathNodes;
            this.bottleneckCapacity = bottleneckCapacity;
            this.cumulativeFlow = cumulativeFlow;
        }

        public List<String> getPathNodes() { return pathNodes; }
        public int getBottleneckCapacity() { return bottleneckCapacity; }
        public int getCumulativeFlow() { return cumulativeFlow; }
    }

    public static class EdmondsKarpResult {
        private final int maxFlow;
        private final List<AugmentingPathStep> pathHistory;
        private final int totalAugmentations;
        private final long executionTimeNanos;

        public EdmondsKarpResult(int maxFlow, List<AugmentingPathStep> pathHistory,
                                 int totalAugmentations, long executionTimeNanos) {
            this.maxFlow = maxFlow;
            this.pathHistory = pathHistory;
            this.totalAugmentations = totalAugmentations;
            this.executionTimeNanos = executionTimeNanos;
        }

        public int getMaxFlow() { return maxFlow; }
        public List<AugmentingPathStep> getPathHistory() { return pathHistory; }
        public int getTotalAugmentations() { return totalAugmentations; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
    }

    public EdmondsKarpResult computeMaxFlow(FlowNetwork network, int s, int t) {
        long startTime = System.nanoTime();
        int maxFlow = 0;
        List<AugmentingPathStep> history = new ArrayList<>();

        while (true) {
            FlowNetwork.Edge[] edgeTo = new FlowNetwork.Edge[network.getVertexCount()];
            boolean[] visited = new boolean[network.getVertexCount()];
            Queue<Integer> queue = new LinkedList<>();

            queue.add(s);
            visited[s] = true;

            while (!queue.isEmpty()) {
                int u = queue.poll();
                if (u == t) break;

                for (FlowNetwork.Edge edge : network.getAdj().get(u)) {
                    int v = edge.getTo();
                    if (!visited[v] && edge.remainingCapacity() > 0) {
                        visited[v] = true;
                        edgeTo[v] = edge;
                        queue.add(v);
                    }
                }
            }

            // If sink not reached, no more augmenting paths exist
            if (!visited[t]) break;

            // Find bottleneck capacity along path
            int bottleneck = Integer.MAX_VALUE;
            for (FlowNetwork.Edge e = edgeTo[t]; e != null; e = edgeTo[e.getFrom()]) {
                bottleneck = Math.min(bottleneck, e.remainingCapacity());
            }

            // Augment flow
            List<String> pathLabels = new ArrayList<>();
            for (FlowNetwork.Edge e = edgeTo[t]; e != null; e = edgeTo[e.getFrom()]) {
                e.setFlow(e.getFlow() + bottleneck);
                e.getResidual().setFlow(e.getResidual().getFlow() - bottleneck);
                pathLabels.add(network.getNodeLabel(e.getTo()));
            }
            pathLabels.add(network.getNodeLabel(s));
            Collections.reverse(pathLabels);

            maxFlow += bottleneck;
            history.add(new AugmentingPathStep(pathLabels, bottleneck, maxFlow));
        }

        long executionTime = System.nanoTime() - startTime;
        return new EdmondsKarpResult(maxFlow, history, history.size(), executionTime);
    }
}

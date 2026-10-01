package com.library.search.service;

import com.library.search.algorithm.DinicAlgorithm;
import com.library.search.algorithm.EdmondsKarp;
import com.library.search.algorithm.FlowNetwork;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Course Outcome 4 (CO-4):
 * "Apply network-flow algorithms (Ford-Fulkerson with Edmonds-Karp, Dinic at intuition level)
 * and the max-flow / min-cut duality to model and solve assignment, matching, and capacity-constrained problems."
 *
 * Max-Flow / Min-Cut Duality Engine for Digital Library Systems.
 */
@Service
public class MaxFlowMinCutService {

    private final EdmondsKarp edmondsKarp;
    private final DinicAlgorithm dinicAlgorithm;

    public MaxFlowMinCutService(EdmondsKarp edmondsKarp, DinicAlgorithm dinicAlgorithm) {
        this.edmondsKarp = edmondsKarp;
        this.dinicAlgorithm = dinicAlgorithm;
    }

    public static class FlowEdgeView {
        private final String from;
        private final String to;
        private final int capacity;
        private final int flow;
        private final boolean isSaturated;
        private final boolean isMinCutBottleneck;

        public FlowEdgeView(String from, String to, int capacity, int flow,
                            boolean isSaturated, boolean isMinCutBottleneck) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = flow;
            this.isSaturated = isSaturated;
            this.isMinCutBottleneck = isMinCutBottleneck;
        }

        public String getFrom() { return from; }
        public String getTo() { return to; }
        public int getCapacity() { return capacity; }
        public int getFlow() { return flow; }
        public boolean isSaturated() { return isSaturated; }
        public boolean isMinCutBottleneck() { return isMinCutBottleneck; }
    }

    public static class NetworkFlowResponse {
        private final String scenario;
        private final String algorithmUsed;
        private final int maxFlow;
        private final int minCutCapacity;
        private final boolean dualityVerified;
        private final List<String> sourceCutPartition; // Nodes in S_cut
        private final List<String> sinkCutPartition;   // Nodes in T_cut
        private final List<FlowEdgeView> minCutBottleneckEdges;
        private final List<FlowEdgeView> allEdges;
        private final long executionTimeNanos;
        private final String academicExplanation;

        public NetworkFlowResponse(String scenario, String algorithmUsed, int maxFlow, int minCutCapacity,
                                   boolean dualityVerified, List<String> sourceCutPartition,
                                   List<String> sinkCutPartition, List<FlowEdgeView> minCutBottleneckEdges,
                                   List<FlowEdgeView> allEdges, long executionTimeNanos, String academicExplanation) {
            this.scenario = scenario;
            this.algorithmUsed = algorithmUsed;
            this.maxFlow = maxFlow;
            this.minCutCapacity = minCutCapacity;
            this.dualityVerified = dualityVerified;
            this.sourceCutPartition = sourceCutPartition;
            this.sinkCutPartition = sinkCutPartition;
            this.minCutBottleneckEdges = minCutBottleneckEdges;
            this.allEdges = allEdges;
            this.executionTimeNanos = executionTimeNanos;
            this.academicExplanation = academicExplanation;
        }

        public String getScenario() { return scenario; }
        public String getAlgorithmUsed() { return algorithmUsed; }
        public int getMaxFlow() { return maxFlow; }
        public int getMinCutCapacity() { return minCutCapacity; }
        public boolean isDualityVerified() { return dualityVerified; }
        public List<String> getSourceCutPartition() { return sourceCutPartition; }
        public List<String> getSinkCutPartition() { return sinkCutPartition; }
        public List<FlowEdgeView> getMinCutBottleneckEdges() { return minCutBottleneckEdges; }
        public List<FlowEdgeView> getAllEdges() { return allEdges; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }
        public String getAcademicExplanation() { return academicExplanation; }
    }

    /**
     * Scenario 1: Bipartite Student-to-Book Reservation & License Matching
     */
    public NetworkFlowResponse solveReservationMatching(String algorithm) {
        long startTime = System.nanoTime();

        // Nodes:
        // 0: Source (S)
        // 1-4: Students (Alice, Bob, Charlie, Diana)
        // 5-8: Digital Library Books/Licenses (CLRS, Clean Code, AI Modern Approach, Database Internals)
        // 9: Sink (T)
        int vertexCount = 10;
        FlowNetwork network = new FlowNetwork(vertexCount);

        network.setNodeLabel(0, "Source [S]");
        network.setNodeLabel(1, "Student: Alice");
        network.setNodeLabel(2, "Student: Bob");
        network.setNodeLabel(3, "Student: Charlie");
        network.setNodeLabel(4, "Student: Diana");
        network.setNodeLabel(5, "Book: Introduction to Algorithms");
        network.setNodeLabel(6, "Book: Designing Data-Intensive Apps");
        network.setNodeLabel(7, "Book: AI A Modern Approach");
        network.setNodeLabel(8, "Book: Computer Networks");
        network.setNodeLabel(9, "Sink [T]");

        // Student borrowing capacity quotas (Source -> Students)
        network.addEdge(0, 1, 2); // Alice quota = 2
        network.addEdge(0, 2, 1); // Bob quota = 1
        network.addEdge(0, 3, 2); // Charlie quota = 2
        network.addEdge(0, 4, 1); // Diana quota = 1

        // Student preferences / reservation requests (Students -> Books)
        network.addEdge(1, 5, 1);
        network.addEdge(1, 6, 1);
        network.addEdge(2, 5, 1);
        network.addEdge(2, 7, 1);
        network.addEdge(3, 6, 1);
        network.addEdge(3, 8, 1);
        network.addEdge(4, 7, 1);
        network.addEdge(4, 8, 1);

        // Book physical copies / license limits (Books -> Sink)
        network.addEdge(5, 9, 1); // CLRS only 1 copy available
        network.addEdge(6, 9, 2); // DDIA 2 copies available
        network.addEdge(7, 9, 1); // AI 1 copy available
        network.addEdge(8, 9, 2); // Networks 2 copies available

        return solveAndComputeMinCut(network, 0, 9, "Bipartite Student-to-Book Reservation Matching", algorithm, startTime);
    }

    /**
     * Scenario 2: Digital Library CDN & Repository Bandwidth Distribution Network
     */
    public NetworkFlowResponse solveCdnDistribution(String algorithm) {
        long startTime = System.nanoTime();

        // Nodes:
        // 0: Master Repository Server [S]
        // 1: Regional Proxy North
        // 2: Regional Proxy South
        // 3: Campus Gateway Alpha
        // 4: Campus Gateway Beta
        // 5: Engineering Department Lab
        // 6: Science Department Lab
        // 7: Student Digital Reading Room [T]
        int vertexCount = 8;
        FlowNetwork network = new FlowNetwork(vertexCount);

        network.setNodeLabel(0, "Master Repository [S]");
        network.setNodeLabel(1, "Regional Proxy North");
        network.setNodeLabel(2, "Regional Proxy South");
        network.setNodeLabel(3, "Campus Gateway Alpha");
        network.setNodeLabel(4, "Campus Gateway Beta");
        network.setNodeLabel(5, "Engg Research Lab");
        network.setNodeLabel(6, "Science Research Lab");
        network.setNodeLabel(7, "Student Digital Terminal [T]");

        // Backbone transmission links (MB/s capacity)
        network.addEdge(0, 1, 100);
        network.addEdge(0, 2, 80);

        network.addEdge(1, 3, 60);
        network.addEdge(1, 4, 50);
        network.addEdge(2, 3, 40);
        network.addEdge(2, 4, 50);

        network.addEdge(3, 5, 70);
        network.addEdge(4, 6, 80);

        network.addEdge(5, 7, 85);
        network.addEdge(6, 7, 75);

        return solveAndComputeMinCut(network, 0, 7, "Digital Library CDN Distribution & Bandwidth Bottleneck Analysis", algorithm, startTime);
    }

    private NetworkFlowResponse solveAndComputeMinCut(FlowNetwork network, int s, int t,
                                                      String scenario, String algorithm, long startTime) {
        String algo = (algorithm != null && algorithm.equalsIgnoreCase("DINIC")) ? "Dinic" : "Edmonds-Karp";
        int maxFlow;

        if (algo.equals("Dinic")) {
            maxFlow = dinicAlgorithm.computeMaxFlow(network, s, t).getMaxFlow();
        } else {
            maxFlow = edmondsKarp.computeMaxFlow(network, s, t).getMaxFlow();
        }

        // Compute Min-Cut via BFS in residual graph from source S
        boolean[] reachable = new boolean[network.getVertexCount()];
        Queue<Integer> q = new LinkedList<>();
        q.add(s);
        reachable[s] = true;

        while (!q.isEmpty()) {
            int u = q.poll();
            for (FlowNetwork.Edge edge : network.getAdj().get(u)) {
                int v = edge.getTo();
                if (!reachable[v] && edge.remainingCapacity() > 0) {
                    reachable[v] = true;
                    q.add(v);
                }
            }
        }

        List<String> sourceCut = new ArrayList<>();
        List<String> sinkCut = new ArrayList<>();
        for (int i = 0; i < network.getVertexCount(); i++) {
            if (reachable[i]) {
                sourceCut.add(network.getNodeLabel(i));
            } else {
                sinkCut.add(network.getNodeLabel(i));
            }
        }

        List<FlowEdgeView> minCutEdges = new ArrayList<>();
        List<FlowEdgeView> allEdgeViews = new ArrayList<>();
        int minCutCapacity = 0;

        for (FlowNetwork.Edge edge : network.getEdges()) {
            boolean isSaturated = (edge.getFlow() == edge.getCapacity() && edge.getCapacity() > 0);
            boolean isMinCut = (reachable[edge.getFrom()] && !reachable[edge.getTo()] && edge.getCapacity() > 0);

            if (isMinCut) {
                minCutCapacity += edge.getCapacity();
                minCutEdges.add(new FlowEdgeView(
                        network.getNodeLabel(edge.getFrom()),
                        network.getNodeLabel(edge.getTo()),
                        edge.getCapacity(),
                        edge.getFlow(),
                        isSaturated,
                        true
                ));
            }

            allEdgeViews.add(new FlowEdgeView(
                    network.getNodeLabel(edge.getFrom()),
                    network.getNodeLabel(edge.getTo()),
                    edge.getCapacity(),
                    edge.getFlow(),
                    isSaturated,
                    isMinCut
            ));
        }

        boolean dualityVerified = (maxFlow == minCutCapacity);
        long executionTime = System.nanoTime() - startTime;
        String explanation = "Max-Flow / Min-Cut Duality Theorem verified: Maximum Flow = " + maxFlow +
                " units exactly equals Minimum Cut Capacity = " + minCutCapacity +
                " units. Saturated bottleneck edges partition the library network into S-cut (" + sourceCut.size() +
                " nodes) and T-cut (" + sinkCut.size() + " nodes).";

        return new NetworkFlowResponse(scenario, algo, maxFlow, minCutCapacity, dualityVerified,
                sourceCut, sinkCut, minCutEdges, allEdgeViews, executionTime, explanation);
    }
}

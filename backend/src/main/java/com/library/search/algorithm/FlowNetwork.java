package com.library.search.algorithm;

import java.util.*;

/**
 * Course Outcome 4 (CO-4):
 * Flow Network & Residual Graph Representation.
 */
public class FlowNetwork {

    public static class Edge {
        private final int from;
        private final int to;
        private final int capacity;
        private int flow;
        private Edge residual;

        public Edge(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
        }

        public int getFrom() { return from; }
        public int getTo() { return to; }
        public int getCapacity() { return capacity; }
        public int getFlow() { return flow; }
        public void setFlow(int flow) { this.flow = flow; }
        public Edge getResidual() { return residual; }
        public void setResidual(Edge residual) { this.residual = residual; }
        public int remainingCapacity() { return capacity - flow; }
    }

    private final int vertexCount;
    private final List<List<Edge>> adj;
    private final List<Edge> edges;
    private final Map<Integer, String> nodeLabels = new HashMap<>();

    public FlowNetwork(int vertexCount) {
        this.vertexCount = vertexCount;
        this.adj = new ArrayList<>(vertexCount);
        this.edges = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int capacity) {
        Edge forward = new Edge(from, to, capacity);
        Edge backward = new Edge(to, from, 0); // Residual edge with 0 capacity
        forward.setResidual(backward);
        backward.setResidual(forward);

        adj.get(from).add(forward);
        adj.get(to).add(backward);
        edges.add(forward);
    }

    public void setNodeLabel(int id, String label) {
        nodeLabels.put(id, label);
    }

    public String getNodeLabel(int id) {
        return nodeLabels.getOrDefault(id, "Node_" + id);
    }

    public int getVertexCount() { return vertexCount; }
    public List<List<Edge>> getAdj() { return adj; }
    public List<Edge> getEdges() { return edges; }
    public Map<Integer, String> getNodeLabels() { return nodeLabels; }
}

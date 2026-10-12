import java.util.ArrayList;
import java.util.List;

public class FlowGraph {
    private final List<FlowEdge>[] graph;

    @SuppressWarnings("unchecked")
    public FlowGraph(int vertices) {
        graph = new ArrayList[vertices];
        for (int i = 0; i < vertices; i++) graph[i] = new ArrayList<>();
    }

    public FlowEdge addEdge(int from, int to, int capacity) {
        return addEdge(from, to, capacity, 0);
    }

    public FlowEdge addEdge(int from, int to, int capacity, int cost) {
        FlowEdge forward = new FlowEdge(from, to, capacity, cost);
        FlowEdge reverse = new FlowEdge(to, from, 0, -cost);
        forward.reverse = reverse;
        reverse.reverse = forward;
        graph[from].add(forward);
        graph[to].add(reverse);
        return forward;
    }

    public List<FlowEdge> edgesFrom(int u) { return graph[u]; }
    public int size() { return graph.length; }

    public void resetFlow() {
        for (List<FlowEdge> edges : graph) for (FlowEdge e : edges) e.flow = 0;
    }
}

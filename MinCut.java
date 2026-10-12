import java.util.ArrayDeque;
import java.util.Queue;

public class MinCut {
    public static boolean[] sourceSide(FlowGraph graph, int source) {
        boolean[] visited = new boolean[graph.size()];
        Queue<Integer> q = new ArrayDeque<>(); q.add(source); visited[source] = true;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (FlowEdge e : graph.edgesFrom(u)) {
                if (!visited[e.to] && e.residualCapacity() > 0) { visited[e.to] = true; q.add(e.to); }
            }
        }
        return visited;
    }

    public static int cutCapacity(FlowGraph graph, boolean[] side) {
        int total = 0;
        for (int u = 0; u < graph.size(); u++) if (side[u]) {
            for (FlowEdge e : graph.edgesFrom(u)) if (!side[e.to] && e.capacity > 0) total += e.capacity;
        }
        return total;
    }
}

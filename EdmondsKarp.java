import java.util.ArrayDeque;
import java.util.Queue;

public class EdmondsKarp {
    public static int maxFlow(FlowGraph graph, int source, int sink, boolean verbose) {
        int total = 0;
        while (true) {
            FlowEdge[] parent = new FlowEdge[graph.size()];
            boolean[] visited = new boolean[graph.size()];
            Queue<Integer> q = new ArrayDeque<>();
            q.add(source); visited[source] = true;

            while (!q.isEmpty() && !visited[sink]) {
                int u = q.poll();
                for (FlowEdge e : graph.edgesFrom(u)) {
                    if (!visited[e.to] && e.residualCapacity() > 0) {
                        visited[e.to] = true; parent[e.to] = e; q.add(e.to);
                    }
                }
            }
            if (!visited[sink]) break;

            int bottle = Integer.MAX_VALUE;
            StringBuilder path = new StringBuilder();
            for (int v = sink; v != source; v = parent[v].from) {
                bottle = Math.min(bottle, parent[v].residualCapacity());
            }
            for (int v = sink; v != source; v = parent[v].from) {
                FlowEdge e = parent[v]; e.flow += bottle; e.reverse.flow -= bottle;
            }
            total += bottle;

            if (verbose) {
                int[] nodes = new int[graph.size()]; int len = 0;
                for (int v = sink; v != source; v = parent[v].from) nodes[len++] = v;
                System.out.print("Edmonds-Karp path: " + source);
                for (int i = len - 1; i >= 0; i--) System.out.print(" -> " + nodes[i]);
                System.out.println(" | bottleneck = " + bottle + " | current flow = " + total);
            }
        }
        return total;
    }
}

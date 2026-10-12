public class FlowEdge {
    int from;
    int to;
    int capacity;
    int flow;
    int cost;
    FlowEdge reverse;

    public FlowEdge(int from, int to, int capacity) {
        this(from, to, capacity, 0);
    }

    public FlowEdge(int from, int to, int capacity, int cost) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.cost = cost;
    }

    public int residualCapacity() { return capacity - flow; }
}

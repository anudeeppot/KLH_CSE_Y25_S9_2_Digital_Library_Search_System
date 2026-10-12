public class BipartiteMatching {
    public static class MatchingResult {
        public final int maxMatches;
        public final int[] matchedRight; // matchedRight[v] is index of matched left node, or -1

        public MatchingResult(int maxMatches, int[] matchedRight) {
            this.maxMatches = maxMatches;
            this.matchedRight = matchedRight;
        }
    }

    public static MatchingResult computeMatching(boolean[][] compatible) {
        int left = compatible.length;
        if (left == 0) return new MatchingResult(0, new int[0]);
        int right = compatible[0].length;
        int[] matchedRight = new int[right];
        java.util.Arrays.fill(matchedRight, -1);
        int result = 0;
        for (int u = 0; u < left; u++) {
            boolean[] seen = new boolean[right];
            if (dfs(u, compatible, matchedRight, seen)) result++;
        }
        return new MatchingResult(result, matchedRight);
    }

    public static int maximumMatching(boolean[][] compatible) {
        return computeMatching(compatible).maxMatches;
    }

    private static boolean dfs(int u, boolean[][] g, int[] match, boolean[] seen) {
        for (int v = 0; v < g[u].length; v++) {
            if (!g[u][v] || seen[v]) continue;
            seen[v] = true;
            if (match[v] == -1 || dfs(match[v], g, match, seen)) {
                match[v] = u; return true;
            }
        }
        return false;
    }
}

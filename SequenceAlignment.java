public class SequenceAlignment {
    public static class AlignmentResult {
        public final String first, second;
        public final int score;
        public AlignmentResult(String first, String second, int score) { this.first = first; this.second = second; this.score = score; }
    }

    public static AlignmentResult needlemanWunsch(String a, String b,
                                                   int match, int mismatch, int gap) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) dp[i][0] = dp[i - 1][0] + gap;
        for (int j = 1; j <= m; j++) dp[0][j] = dp[0][j - 1] + gap;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int diag = dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? match : mismatch);
                int up = dp[i - 1][j] + gap;
                int left = dp[i][j - 1] + gap;
                dp[i][j] = Math.max(diag, Math.max(up, left));
            }
        }

        StringBuilder x = new StringBuilder(), y = new StringBuilder();
        int i = n, j = m;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0) {
                int score = a.charAt(i - 1) == b.charAt(j - 1) ? match : mismatch;
                if (dp[i][j] == dp[i - 1][j - 1] + score) {
                    x.append(a.charAt(i - 1)); y.append(b.charAt(j - 1)); i--; j--; continue;
                }
            }
            if (i > 0 && dp[i][j] == dp[i - 1][j] + gap) {
                x.append(a.charAt(i - 1)); y.append('-'); i--;
            } else {
                x.append('-'); y.append(b.charAt(j - 1)); j--;
            }
        }
        return new AlignmentResult(x.reverse().toString(), y.reverse().toString(), dp[n][m]);
    }

    public static AlignmentResult smithWaterman(String a, String b,
                                                 int match, int mismatch, int gap) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        int best = 0, bestI = 0, bestJ = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int diag = dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? match : mismatch);
                int up = dp[i - 1][j] + gap;
                int left = dp[i][j - 1] + gap;
                dp[i][j] = Math.max(0, Math.max(diag, Math.max(up, left)));
                if (dp[i][j] > best) { best = dp[i][j]; bestI = i; bestJ = j; }
            }
        }

        StringBuilder x = new StringBuilder(), y = new StringBuilder();
        int i = bestI, j = bestJ;
        while (i > 0 && j > 0 && dp[i][j] > 0) {
            int score = a.charAt(i - 1) == b.charAt(j - 1) ? match : mismatch;
            if (dp[i][j] == dp[i - 1][j - 1] + score) {
                x.append(a.charAt(i - 1)); y.append(b.charAt(j - 1)); i--; j--;
            } else if (dp[i][j] == dp[i - 1][j] + gap) {
                x.append(a.charAt(i - 1)); y.append('-'); i--;
            } else {
                x.append('-'); y.append(b.charAt(j - 1)); j--;
            }
        }
        return new AlignmentResult(x.reverse().toString(), y.reverse().toString(), best);
    }
}

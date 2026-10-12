public class EditDistance {

    public static int levenshteinDistance(String a, String b) {
        if (a == null || b == null) throw new IllegalArgumentException("Strings cannot be null.");
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[n][m];
    }

    public static void printMatrix(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        System.out.print("    ");
        for (int j = 0; j <= m; j++) System.out.printf("%3s", j == 0 ? "-" : b.charAt(j - 1));
        System.out.println();
        for (int i = 0; i <= n; i++) {
            System.out.printf("%3s ", i == 0 ? "-" : a.charAt(i - 1));
            for (int j = 0; j <= m; j++) System.out.printf("%3d", dp[i][j]);
            System.out.println();
        }
    }
}

import java.util.Scanner;

public class KMP {

    static void computeLPS(String pattern, int[] lps) {
        int len = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
    }

    static void search(StringBuilder text, String pattern) {

        int n = text.length();
        int m = pattern.length();

        int[] lps = new int[m];

        // Create LPS array
        computeLPS(pattern, lps);

        int i = 0;
        int j = 0;

        while (i < n) {

            // Compare text and pattern
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            // Pattern found
            if (j == m) {
                System.out.println("Pattern found at index: " + (i - j));
                j = lps[j - 1];
            }

            // Mismatch
            else if (i < n && text.charAt(i) != pattern.charAt(j)) {

                if (j != 0)
                    j = lps[j - 1];
                else
                    i++;
            }
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter content: ");
        StringBuilder content = new StringBuilder(sc.nextLine());

        System.out.print("Enter pattern: ");
        String pattern = sc.nextLine();

        search(content, pattern);

        sc.close();
    }
}
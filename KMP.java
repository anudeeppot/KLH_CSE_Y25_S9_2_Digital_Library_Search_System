import java.util.ArrayList;
import java.util.List;

public class KMP {

    // CO2: LPS = Longest Proper Prefix which is also Suffix.
    public int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else if (length != 0) {
                length = lps[length - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
        return lps;
    }

    // CO2: Actual KMP pattern matching. No built-in search is used.
    public List<Integer> search(String text, String pattern) {
        List<Integer> positions = new ArrayList<>();
        if (text == null || pattern == null || text.isEmpty() || pattern.isEmpty()) {
            return positions;
        }

        String searchText = text.toLowerCase();
        String searchPattern = pattern.toLowerCase();
        if (searchPattern.length() > searchText.length()) return positions;

        int[] lps = buildLPS(searchPattern);
        int i = 0, j = 0;

        while (i < searchText.length()) {
            if (searchText.charAt(i) == searchPattern.charAt(j)) {
                i++;
                j++;
                if (j == searchPattern.length()) {
                    positions.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j != 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return positions;
    }

    public boolean found(String text, String pattern) {
        return !search(text, pattern).isEmpty();
    }

    public void demonstrate(String text, String pattern) {
        System.out.println("\n========== KMP DEMONSTRATION ==========");
        System.out.println("Text    : " + text);
        System.out.println("Pattern : " + pattern);
        int[] lps = buildLPS(pattern.toLowerCase());
        System.out.print("LPS     : ");
        for (int v : lps) System.out.print(v + " ");
        System.out.println();

        String t = text.toLowerCase();
        String p = pattern.toLowerCase();
        if (p.isEmpty()) return;

        int i = 0, j = 0;
        while (i < t.length()) {
            System.out.println("Compare text[" + i + "]='" + t.charAt(i) + "' with pattern[" + j + "]='" + p.charAt(j) + "'");
            if (t.charAt(i) == p.charAt(j)) {
                i++; j++;
                if (j == p.length()) {
                    System.out.println("Match found at index " + (i - j));
                    j = lps[j - 1];
                }
            } else if (j != 0) {
                j = lps[j - 1];
                System.out.println("Mismatch -> jump using LPS to pattern index " + j);
            } else {
                i++;
            }
        }
    }
}

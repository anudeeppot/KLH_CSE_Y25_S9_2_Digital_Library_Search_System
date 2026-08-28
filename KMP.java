import java.util.ArrayList;
import java.util.List;

public class KMP {

    // CO2: Build the LPS (Longest Proper Prefix which is also Suffix) array.
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

    // CO2: Actual KMP pattern matching. No built-in search method is used.
    public List<Integer> search(String text, String pattern) {
        List<Integer> positions = new ArrayList<>();

        if (text == null || pattern == null ||
                text.length() == 0 || pattern.length() == 0) {
            return positions;
        }

        String searchText = text.toLowerCase();
        String searchPattern = pattern.toLowerCase();

        if (searchPattern.length() > searchText.length()) {
            return positions;
        }

        int[] lps = buildLPS(searchPattern);

        int i = 0;
        int j = 0;

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
}

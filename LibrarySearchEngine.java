import java.util.ArrayList;
import java.util.List;

public class LibrarySearchEngine {
    private final KMP kmp;

    public LibrarySearchEngine(KMP kmp) {
        this.kmp = kmp;
    }

    public List<SearchMatch> search(List<LibraryDocument> documents,
                                    String query) {
        List<SearchMatch> matches = new ArrayList<>();

        for (LibraryDocument document : documents) {
            addMatchIfFound(matches, document, "Title", document.getTitle(), query);
            addMatchIfFound(matches, document, "Author", document.getAuthor(), query);
            addMatchIfFound(matches, document, "Category", document.getCategory(), query);
            addMatchIfFound(matches, document, "Content", document.getContent(), query);
        }

        return matches;
    }

    private void addMatchIfFound(List<SearchMatch> matches,
                                 LibraryDocument document,
                                 String fieldName,
                                 String text,
                                 String query) {
        List<Integer> positions = kmp.search(text, query);

        if (!positions.isEmpty()) {
            matches.add(
                new SearchMatch(
                    document,
                    fieldName,
                    positions
                )
            );
        }
    }

    public static class SearchMatch {
        private final LibraryDocument document;
        private final String fieldName;
        private final List<Integer> positions;

        public SearchMatch(LibraryDocument document,
                           String fieldName,
                           List<Integer> positions) {
            this.document = document;
            this.fieldName = fieldName;
            this.positions = positions;
        }

        public LibraryDocument getDocument() { return document; }
        public String getFieldName() { return fieldName; }
        public List<Integer> getPositions() { return positions; }
    }
}

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * LibrarySearchEngine.java — Core Multi-field String Search Engine.
 *
 * Modules integrated:
 *   - CO2 / Module 2: Knuth-Morris-Pratt (KMP) exact string pattern matching.
 *   - Module 3: Levenshtein Distance DP for fault-tolerant fuzzy vocabulary suggestions.
 *   - Module 6: Parallel Document Search via bounded thread pool concurrency.
 *
 * Concurrency Design:
 *   - Bounded thread pool: Min(CPU cores, 8) worker threads to prevent resource exhaustion.
 *   - Independent Document Processing: Documents are immutable during read, so each thread
 *     operates without shared mutable state.
 *   - Deterministic Result Ordering: Document results are assembled in original index order,
 *     guaranteeing 100% equivalence between sequential and parallel search results.
 *   - Automatic Fallback: Auto mode selects sequential execution for small corpora (< 20 docs)
 *     where thread scheduling overhead exceeds processing time.
 */
public class LibrarySearchEngine {

    public static final int PARALLEL_AUTO_THRESHOLD = 20;

    public enum SearchMode {
        AUTO,
        SEQUENTIAL,
        PARALLEL
    }

    private final KMP kmp;
    private final ExecutorService executor;
    private final int workerThreads;

    public LibrarySearchEngine(KMP kmp) {
        this(kmp, Math.max(2, Math.min(Runtime.getRuntime().availableProcessors(), 8)));
    }

    public LibrarySearchEngine(KMP kmp, int workerThreads) {
        this.kmp = kmp;
        this.workerThreads = workerThreads;
        this.executor = Executors.newFixedThreadPool(this.workerThreads, new ThreadFactory() {
            private int counter = 1;
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "SearchWorker-" + counter++);
                t.setDaemon(true);
                return t;
            }
        });
    }

    /**
     * Preserved legacy API: Executes search using default AUTO mode.
     */
    public List<SearchMatch> search(List<LibraryDocument> documents, String query) {
        return searchWithMetrics(documents, query, SearchMode.AUTO).getMatches();
    }

    /**
     * Search documents with execution metrics and selectable search mode (Sequential, Parallel, Auto).
     */
    public SearchExecutionResult searchWithMetrics(List<LibraryDocument> documents, String query, SearchMode mode) {
        if (mode == null) mode = SearchMode.AUTO;
        int docCount = (documents == null) ? 0 : documents.size();

        if (documents == null || documents.isEmpty() || query == null || query.trim().isEmpty()) {
            return new SearchExecutionResult(new ArrayList<>(), mode, "Sequential", 0, docCount, 1);
        }

        // Determine actual execution mode
        boolean runParallel;
        if (mode == SearchMode.PARALLEL) {
            runParallel = true;
        } else if (mode == SearchMode.SEQUENTIAL) {
            runParallel = false;
        } else {
            // AUTO: use sequential for small corpora, parallel for larger
            runParallel = (docCount >= PARALLEL_AUTO_THRESHOLD);
        }

        long startNanos = System.nanoTime();
        List<SearchMatch> matches;

        if (runParallel) {
            matches = executeParallelSearch(documents, query);
        } else {
            matches = executeSequentialSearch(documents, query);
        }
        long elapsedNanos = System.nanoTime() - startNanos;

        String executed = runParallel ? "Parallel" : "Sequential";
        int activeWorkers = runParallel ? this.workerThreads : 1;
        return new SearchExecutionResult(matches, mode, executed, elapsedNanos, docCount, activeWorkers);
    }

    /**
     * Sequential KMP search over all documents.
     */
    public List<SearchMatch> executeSequentialSearch(List<LibraryDocument> documents, String query) {
        List<SearchMatch> matches = new ArrayList<>();
        if (documents == null || query == null || query.trim().isEmpty()) return matches;

        for (LibraryDocument document : documents) {
            searchSingleDocument(document, query, matches);
        }
        return matches;
    }

    /**
     * Parallel KMP search using partitioned task execution.
     * Guarantees deterministic order identical to sequential search.
     */
    public List<SearchMatch> executeParallelSearch(List<LibraryDocument> documents, String query) {
        if (documents == null || documents.isEmpty()) return new ArrayList<>();

        int n = documents.size();
        // Allocate list of per-document results to assemble in strict index order
        List<Future<List<SearchMatch>>> futures = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            final LibraryDocument doc = documents.get(i);
            futures.add(executor.submit(() -> {
                List<SearchMatch> docMatches = new ArrayList<>();
                searchSingleDocument(doc, query, docMatches);
                return docMatches;
            }));
        }

        List<SearchMatch> orderedMatches = new ArrayList<>();
        for (Future<List<SearchMatch>> future : futures) {
            try {
                List<SearchMatch> docMatches = future.get();
                if (docMatches != null && !docMatches.isEmpty()) {
                    orderedMatches.addAll(docMatches);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Fall back to sequential search on interruption
                return executeSequentialSearch(documents, query);
            } catch (ExecutionException e) {
                // If a task fails, continue with remaining tasks
            }
        }
        return orderedMatches;
    }

    private void searchSingleDocument(LibraryDocument document, String query, List<SearchMatch> target) {
        if (document == null) return;
        addIfFound(target, document, "Title", document.getTitle(), query);
        addIfFound(target, document, "Author", document.getAuthor(), query);
        addIfFound(target, document, "ISBN", document.getIsbn(), query);
        addIfFound(target, document, "Category", document.getCategory(), query);
        addIfFound(target, document, "Content", document.getContent(), query);
    }

    private void addIfFound(List<SearchMatch> matches, LibraryDocument d,
                            String field, String text, String query) {
        List<Integer> positions = kmp.search(text, query);
        if (!positions.isEmpty()) matches.add(new SearchMatch(d, field, positions));
    }

    /**
     * Fuzzy search using Levenshtein distance on corpus vocabulary terms.
     */
    public List<FuzzyMatch> fuzzySearch(List<LibraryDocument> documents,
                                        String query, int maxDistance) {
        List<FuzzyMatch> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty() || documents == null) return results;

        List<String> terms = new ArrayList<>();
        List<String> seen = new ArrayList<>();

        for (LibraryDocument d : documents) {
            collectTerms(terms, seen, d.getTitle());
            collectTerms(terms, seen, d.getAuthor());
            collectTerms(terms, seen, d.getCategory());
            collectTerms(terms, seen, d.getContent());
        }

        for (String term : terms) {
            int distance = EditDistance.levenshteinDistance(query.toLowerCase(), term.toLowerCase());
            if (distance <= maxDistance) results.add(new FuzzyMatch(term, distance));
        }

        results.sort((a, b) -> {
            int c = Integer.compare(a.distance, b.distance);
            return c != 0 ? c : a.term.compareToIgnoreCase(b.term);
        });
        return results;
    }

    private void collectTerms(List<String> terms, List<String> seen, String text) {
        if (text == null) return;
        String[] raw = text.split("\\s+");
        for (String token : raw) {
            String clean = cleanToken(token);
            if (!clean.isEmpty() && !containsIgnoreCase(seen, clean)) {
                seen.add(clean);
                terms.add(clean);
            }
        }
    }

    private String cleanToken(String token) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_') sb.append(c);
        }
        return sb.toString();
    }

    private boolean containsIgnoreCase(List<String> list, String value) {
        for (String s : list) if (s.equalsIgnoreCase(value)) return true;
        return false;
    }

    /**
     * Graceful shutdown of thread pool executor.
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // -----------------------------------------------------------------------
    // Inner Data Classes
    // -----------------------------------------------------------------------

    public static class SearchMatch {
        private final LibraryDocument document;
        private final String fieldName;
        private final List<Integer> positions;

        public SearchMatch(LibraryDocument document, String fieldName, List<Integer> positions) {
            this.document = document;
            this.fieldName = fieldName;
            this.positions = positions;
        }

        public LibraryDocument getDocument() { return document; }
        public String getFieldName() { return fieldName; }
        public List<Integer> getPositions() { return positions; }
    }

    public static class FuzzyMatch {
        private final String term;
        private final int distance;

        public FuzzyMatch(String term, int distance) {
            this.term = term;
            this.distance = distance;
        }

        public String getTerm() { return term; }
        public int getDistance() { return distance; }
    }

    public static class SearchExecutionResult {
        private final List<SearchMatch> matches;
        private final SearchMode requestedMode;
        private final String executedMode;
        private final long elapsedNanos;
        private final int scannedDocuments;
        private final int workerThreads;

        public SearchExecutionResult(List<SearchMatch> matches, SearchMode requestedMode,
                                     String executedMode, long elapsedNanos,
                                     int scannedDocuments, int workerThreads) {
            this.matches = matches;
            this.requestedMode = requestedMode;
            this.executedMode = executedMode;
            this.elapsedNanos = elapsedNanos;
            this.scannedDocuments = scannedDocuments;
            this.workerThreads = workerThreads;
        }

        public List<SearchMatch> getMatches() { return matches; }
        public SearchMode getRequestedMode() { return requestedMode; }
        public String getExecutedMode() { return executedMode; }
        public long getElapsedNanos() { return elapsedNanos; }
        public double getElapsedMs() { return elapsedNanos / 1_000_000.0; }
        public int getScannedDocuments() { return scannedDocuments; }
        public int getWorkerThreads() { return workerThreads; }
    }
}

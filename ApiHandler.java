import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ApiHandler.java — Routes all /api/* HTTP requests to the correct algorithm.
 *
 * Endpoints:
 *   GET  /api/status
 *   GET  /api/documents
 *   GET  /api/document?id=<filename>
 *   GET  /api/search?q=<query>&field=<field>
 *   POST /api/fuzzy-search    body: {"query":"...","maxDistance":3}
 *   POST /api/kmp-demo        body: {"docId":"...","pattern":"..."}
 *   POST /api/compare         body: {"docA":"...","docB":"...","match":1,"mismatch":-1,"gap":-2}
 *   POST /api/resource-allocation  (uses fixed graph from Main)
 *   POST /api/matching        body: {"users":[...],"resources":[...],"compatible":[[...],...]}
 *
 * Every endpoint calls the actual Java algorithm class.
 * JSON is built with JsonUtil — no unsafe string concatenation.
 */
public class ApiHandler implements HttpHandler {

    private static final int MAX_ALIGN_CHARS = 200; // NW alignment limit per side
    private final String corpusPath;

    public ApiHandler(String corpusPath) {
        this.corpusPath = corpusPath;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        // CORS pre-flight
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        if ("OPTIONS".equals(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        String path = ex.getRequestURI().getPath();
        try {
            if      (path.equals("/api/status"))              handleStatus(ex);
            else if (path.equals("/api/documents"))           handleDocuments(ex);
            else if (path.equals("/api/document"))            handleDocument(ex);
            else if (path.equals("/api/search"))              handleSearch(ex);
            else if (path.equals("/api/fuzzy-search"))        handleFuzzySearch(ex);
            else if (path.equals("/api/kmp-demo"))            handleKmpDemo(ex);
            else if (path.equals("/api/compare"))             handleCompare(ex);
            else if (path.equals("/api/resource-allocation")) handleResourceAllocation(ex);
            else if (path.equals("/api/matching"))            handleMatching(ex);
            else if (path.equals("/api/random-document"))     handleRandomDocument(ex);
            else if (path.equals("/api/reading-plan"))         handleReadingPlan(ex);
            else json(ex, 404, JsonUtil.error("Unknown API endpoint: " + path));
        } catch (Exception e) {
            // Never expose stack traces
            json(ex, 500, JsonUtil.error("Internal server error: " + safeMessage(e)));
        }
    }

    // -----------------------------------------------------------------------
    // GET /api/status
    // -----------------------------------------------------------------------
    private void handleStatus(HttpExchange ex) throws IOException {
        String body = JsonUtil.obj()
            .put("status", "ok")
            .put("documents", WebServer.documents.size())
            .put("algorithm", "KMP + Levenshtein + Needleman-Wunsch + Edmonds-Karp + BipartiteMatching + Knapsack-Approx + ReservoirSampling + ParallelSearch")
            .put("corpusPath", corpusPath)
            .build();
        json(ex, 200, body);
    }

    // -----------------------------------------------------------------------
    // GET /api/documents
    // -----------------------------------------------------------------------
    private void handleDocuments(HttpExchange ex) throws IOException {
        JsonUtil.ArrayBuilder arr = JsonUtil.arr();
        int idx = 0;
        for (LibraryDocument d : WebServer.documents) {
            String obj = JsonUtil.obj()
                .put("id",                     d.getFileName())
                .put("index",                  idx)
                .put("filename",               d.getFileName())
                .put("title",                  d.getTitle())
                .put("author",                 d.getAuthor())
                .put("isbn",                   d.getIsbn())
                .put("category",               d.getCategory())
                .put("year",                   d.getYear())
                .put("wordCount",              d.getWordCount())
                .put("estimatedReadingMinutes",d.getEstimatedReadingMinutes(200))
                .build();
            arr.addRaw(obj);
            idx++;
        }
        String body = JsonUtil.obj()
            .put("total", WebServer.documents.size())
            .putRaw("documents", arr.build())
            .build();
        json(ex, 200, body);
    }

    // -----------------------------------------------------------------------
    // GET /api/document?id=<filename>
    // -----------------------------------------------------------------------
    private void handleDocument(HttpExchange ex) throws IOException {
        String id = WebServer.getParam(ex.getRequestURI(), "id");
        if (id == null || id.trim().isEmpty()) {
            json(ex, 400, JsonUtil.error("Missing 'id' parameter"));
            return;
        }
        LibraryDocument doc = findDocumentById(id);
        if (doc == null) {
            json(ex, 404, JsonUtil.error("Document not found: " + id));
            return;
        }
        String body = JsonUtil.obj()
            .put("id",       doc.getFileName())
            .put("filename", doc.getFileName())
            .put("title",    doc.getTitle())
            .put("author",   doc.getAuthor())
            .put("isbn",     doc.getIsbn())
            .put("category", doc.getCategory())
            .put("year",     doc.getYear())
            .put("content",  doc.getContent())
            .build();
        json(ex, 200, body);
    }

    // -----------------------------------------------------------------------
    // GET /api/search?q=<query>&field=<field>&mode=<auto|sequential|parallel>
    //   field: all | Title | Author | ISBN | Category | Content
    //   mode:  auto | sequential | parallel
    // -----------------------------------------------------------------------
    private void handleSearch(HttpExchange ex) throws IOException {
        String q = WebServer.getParam(ex.getRequestURI(), "q");
        String field = WebServer.getParam(ex.getRequestURI(), "field");
        String modeParam = WebServer.getParam(ex.getRequestURI(), "mode");

        if (q == null || q.trim().isEmpty()) {
            json(ex, 400, JsonUtil.error("Missing or empty 'q' parameter"));
            return;
        }
        if (field == null || field.trim().isEmpty()) field = "all";

        LibrarySearchEngine.SearchMode searchMode = LibrarySearchEngine.SearchMode.AUTO;
        if ("sequential".equalsIgnoreCase(modeParam)) {
            searchMode = LibrarySearchEngine.SearchMode.SEQUENTIAL;
        } else if ("parallel".equalsIgnoreCase(modeParam)) {
            searchMode = LibrarySearchEngine.SearchMode.PARALLEL;
        }

        // Call LibrarySearchEngine with metrics
        LibrarySearchEngine.SearchExecutionResult execResult =
            WebServer.searchEngine.searchWithMetrics(WebServer.documents, q, searchMode);
        List<LibrarySearchEngine.SearchMatch> allMatches = execResult.getMatches();

        // Filter by field if not "all"
        List<LibrarySearchEngine.SearchMatch> matches;
        if ("all".equalsIgnoreCase(field)) {
            matches = allMatches;
        } else {
            matches = new ArrayList<>();
            String f = normalizeField(field);
            for (LibrarySearchEngine.SearchMatch m : allMatches) {
                if (m.getFieldName().equalsIgnoreCase(f)) matches.add(m);
            }
        }

        JsonUtil.ArrayBuilder arr = JsonUtil.arr();
        for (LibrarySearchEngine.SearchMatch m : matches) {
            LibraryDocument d = m.getDocument();
            JsonUtil.ArrayBuilder posArr = JsonUtil.arr();
            for (int p : m.getPositions()) posArr.add(p);

            String obj = JsonUtil.obj()
                .put("filename",    d.getFileName())
                .put("title",       d.getTitle())
                .put("author",      d.getAuthor())
                .put("category",    d.getCategory())
                .put("year",        d.getYear())
                .put("matchedField",m.getFieldName())
                .put("occurrences", m.getPositions().size())
                .putRaw("positions", posArr.build())
                .build();
            arr.addRaw(obj);
        }

        String metricsObj = JsonUtil.obj()
            .put("requestedMode",    execResult.getRequestedMode().name().toLowerCase())
            .put("executedMode",     execResult.getExecutedMode())
            .put("elapsedNanos",     execResult.getElapsedNanos())
            .put("elapsedMs",        String.format(java.util.Locale.US, "%.3f", execResult.getElapsedMs()))
            .put("scannedDocuments", execResult.getScannedDocuments())
            .put("workerThreads",    execResult.getWorkerThreads())
            .build();

        String body = JsonUtil.obj()
            .put("query",       q)
            .put("field",       field)
            .put("total",       matches.size())
            .put("algorithm",   "KMP (Knuth-Morris-Pratt)")
            .put("complexity",  "Time O(n+m), Space O(m)")
            .putRaw("metrics",  metricsObj)
            .putRaw("results",  arr.build())
            .build();
        json(ex, 200, body);
    }

    // -----------------------------------------------------------------------
    // POST /api/fuzzy-search
    //   body: {"query":"...","maxDistance":3}
    // -----------------------------------------------------------------------
    private void handleFuzzySearch(HttpExchange ex) throws IOException {
        String body = WebServer.readBody(ex);
        String query = jsonString(body, "query");
        int maxDist  = jsonInt(body, "maxDistance", 2);

        if (query == null || query.trim().isEmpty()) {
            json(ex, 400, JsonUtil.error("Missing 'query' field"));
            return;
        }
        if (maxDist < 0 || maxDist > 10) {
            json(ex, 400, JsonUtil.error("'maxDistance' must be between 0 and 10"));
            return;
        }

        // Step 1: exact KMP check
        List<LibrarySearchEngine.SearchMatch> exact =
            WebServer.searchEngine.search(WebServer.documents, query);

        if (!exact.isEmpty()) {
            // Return exact results with a flag
            JsonUtil.ArrayBuilder arr = JsonUtil.arr();
            for (LibrarySearchEngine.SearchMatch m : exact) {
                LibraryDocument d = m.getDocument();
                JsonUtil.ArrayBuilder posArr = JsonUtil.arr();
                for (int p : m.getPositions()) posArr.add(p);
                arr.addRaw(JsonUtil.obj()
                    .put("filename",    d.getFileName())
                    .put("title",       d.getTitle())
                    .put("author",      d.getAuthor())
                    .put("category",    d.getCategory())
                    .put("matchedField",m.getFieldName())
                    .put("occurrences", m.getPositions().size())
                    .putRaw("positions", posArr.build())
                    .build());
            }
            String resp = JsonUtil.obj()
                .put("query",       query)
                .put("exactMatch",  true)
                .put("algorithm",   "KMP exact search")
                .put("total",       exact.size())
                .putRaw("results",  arr.build())
                .putRaw("suggestions", "[]")
                .build();
            json(ex, 200, resp);
            return;
        }

        // Step 2: Levenshtein fuzzy
        List<LibrarySearchEngine.FuzzyMatch> suggestions =
            WebServer.searchEngine.fuzzySearch(WebServer.documents, query, maxDist);

        JsonUtil.ArrayBuilder sugArr = JsonUtil.arr();
        int limit = Math.min(20, suggestions.size());
        for (int i = 0; i < limit; i++) {
            LibrarySearchEngine.FuzzyMatch fm = suggestions.get(i);
            sugArr.addRaw(JsonUtil.obj()
                .put("term",     fm.getTerm())
                .put("distance", fm.getDistance())
                .build());
        }

        String resp = JsonUtil.obj()
            .put("query",           query)
            .put("exactMatch",      false)
            .put("maxDistance",     maxDist)
            .put("algorithm",       "Levenshtein Distance DP")
            .put("complexity",      "Time O(n×m), Space O(n×m)")
            .put("total",           Math.min(20, suggestions.size()))
            .putRaw("suggestions",  sugArr.build())
            .putRaw("results",      "[]")
            .build();
        json(ex, 200, resp);
    }

    // -----------------------------------------------------------------------
    // POST /api/kmp-demo
    //   body: {"docId":"...","pattern":"..."}
    // -----------------------------------------------------------------------
    private void handleKmpDemo(HttpExchange ex) throws IOException {
        String body = WebServer.readBody(ex);
        String docId   = jsonString(body, "docId");
        String pattern = jsonString(body, "pattern");

        if (docId == null || docId.trim().isEmpty()) {
            json(ex, 400, JsonUtil.error("Missing 'docId' field"));
            return;
        }
        if (pattern == null || pattern.trim().isEmpty()) {
            json(ex, 400, JsonUtil.error("Missing 'pattern' field"));
            return;
        }

        LibraryDocument doc = findDocumentById(docId);
        if (doc == null) {
            json(ex, 404, JsonUtil.error("Document not found: " + docId));
            return;
        }

        // Call KMP
        int[] lps = WebServer.kmp.buildLPS(pattern.toLowerCase());
        List<Integer> positions = WebServer.kmp.search(doc.getContent(), pattern);

        JsonUtil.ArrayBuilder lpsArr = JsonUtil.arr();
        for (int v : lps) lpsArr.add(v);
        JsonUtil.ArrayBuilder posArr = JsonUtil.arr();
        for (int p : positions) posArr.add(p);

        String resp = JsonUtil.obj()
            .put("docId",       doc.getFileName())
            .put("title",       doc.getTitle())
            .put("pattern",     pattern)
            .put("occurrences", positions.size())
            .put("algorithm",   "KMP (Knuth-Morris-Pratt)")
            .put("timeComplexity",  "O(n + m)")
            .put("spaceComplexity", "O(m) — LPS array only")
            .putRaw("lpsArray",     lpsArr.build())
            .putRaw("positions",    posArr.build())
            .build();
        json(ex, 200, resp);
    }

    // -----------------------------------------------------------------------
    // POST /api/compare
    //   body: {"docA":"...","docB":"...","match":1,"mismatch":-1,"gap":-2}
    // -----------------------------------------------------------------------
    private void handleCompare(HttpExchange ex) throws IOException {
        String body = WebServer.readBody(ex);
        String docA = jsonString(body, "docA");
        String docB = jsonString(body, "docB");
        int match    = jsonInt(body, "match",    1);
        int mismatch = jsonInt(body, "mismatch", -1);
        int gap      = jsonInt(body, "gap",      -2);

        if (docA == null || docA.trim().isEmpty() || docB == null || docB.trim().isEmpty()) {
            json(ex, 400, JsonUtil.error("Both 'docA' and 'docB' are required"));
            return;
        }
        if (docA.equals(docB)) {
            json(ex, 400, JsonUtil.error("Please select two different documents"));
            return;
        }

        LibraryDocument dA = findDocumentById(docA);
        LibraryDocument dB = findDocumentById(docB);
        if (dA == null) { json(ex, 404, JsonUtil.error("Document A not found: " + docA)); return; }
        if (dB == null) { json(ex, 404, JsonUtil.error("Document B not found: " + docB)); return; }

        // Trim to MAX_ALIGN_CHARS characters each
        String cA = trimContent(dA.getContent(), MAX_ALIGN_CHARS);
        String cB = trimContent(dB.getContent(), MAX_ALIGN_CHARS);

        // Call actual Needleman-Wunsch
        SequenceAlignment.AlignmentResult result =
            SequenceAlignment.needlemanWunsch(cA, cB, match, mismatch, gap);

        String resp = JsonUtil.obj()
            .put("docA",            dA.getFileName())
            .put("titleA",          dA.getTitle())
            .put("docB",            dB.getFileName())
            .put("titleB",          dB.getTitle())
            .put("score",           result.score)
            .put("match",           match)
            .put("mismatch",        mismatch)
            .put("gap",             gap)
            .put("charLimit",       MAX_ALIGN_CHARS)
            .put("alignedA",        result.first)
            .put("alignedB",        result.second)
            .put("algorithm",       "Needleman-Wunsch Global Alignment")
            .put("complexity",      "Time O(n×m), Space O(n×m)")
            .put("note",
                "Needleman-Wunsch performs global sequence alignment. " +
                "It aligns two sequences from beginning to end using dynamic programming.")
            .build();
        json(ex, 200, resp);
    }

    // -----------------------------------------------------------------------
    // POST /api/resource-allocation
    //   Uses the fixed graph from Main.java (Edmonds-Karp + MinCut)
    //   Returns max-flow and bottleneck analysis as JSON.
    // -----------------------------------------------------------------------
    private void handleResourceAllocation(HttpExchange ex) throws IOException {
        final int SOURCE = 0, SINK = 6;
        final String[] LABELS = {
            "Source",
            "Undergrad", "Postgrad", "Faculty",
            "Algorithms-Collection", "AI-Collection",
            "Sink"
        };

        FlowGraph g = new FlowGraph(7);
        g.addEdge(SOURCE, 1, 8);
        g.addEdge(SOURCE, 2, 5);
        g.addEdge(SOURCE, 3, 3);
        g.addEdge(1, 4, 6);
        g.addEdge(1, 5, 5);
        g.addEdge(2, 4, 4);
        g.addEdge(2, 5, 3);
        g.addEdge(3, 4, 2);
        g.addEdge(3, 5, 2);
        g.addEdge(4, SINK, 5);
        g.addEdge(5, SINK, 4);

        int totalRequests = 8 + 5 + 3; // 16
        int maxFlow = EdmondsKarp.maxFlow(g, SOURCE, SINK, false);
        int unfulfilled = totalRequests - maxFlow;

        // Min-Cut analysis
        boolean[] sourceSide = MinCut.sourceSide(g, SOURCE);
        int cutCap = MinCut.cutCapacity(g, sourceSide);

        // Build source-side node list
        JsonUtil.ArrayBuilder srcNodes = JsonUtil.arr();
        JsonUtil.ArrayBuilder sinkNodes = JsonUtil.arr();
        for (int i = 0; i < LABELS.length; i++) {
            if (sourceSide[i]) srcNodes.add(LABELS[i]);
            else sinkNodes.add(LABELS[i]);
        }

        // Build bottleneck edges
        JsonUtil.ArrayBuilder bottlenecks = JsonUtil.arr();
        for (int u = 0; u < LABELS.length; u++) {
            if (!sourceSide[u]) continue;
            for (FlowEdge e : g.edgesFrom(u)) {
                if (!sourceSide[e.to] && e.capacity > 0) {
                    bottlenecks.addRaw(JsonUtil.obj()
                        .put("from",     LABELS[u])
                        .put("to",       LABELS[e.to])
                        .put("capacity", e.capacity)
                        .build());
                }
            }
        }

        // Build user-group summary
        JsonUtil.ArrayBuilder groups = JsonUtil.arr();
        groups.addRaw(JsonUtil.obj().put("group","Undergrad").put("capacity",8).build());
        groups.addRaw(JsonUtil.obj().put("group","Postgrad").put("capacity",5).build());
        groups.addRaw(JsonUtil.obj().put("group","Faculty").put("capacity",3).build());

        JsonUtil.ArrayBuilder resources = JsonUtil.arr();
        resources.addRaw(JsonUtil.obj().put("resource","Algorithms-Collection").put("licences",5).build());
        resources.addRaw(JsonUtil.obj().put("resource","AI-Collection").put("licences",4).build());

        String resp = JsonUtil.obj()
            .put("algorithm",       "Edmonds-Karp Max-Flow")
            .put("complexity",      "Time O(V × E²)")
            .put("totalRequests",   totalRequests)
            .put("maxFlow",         maxFlow)
            .put("unfulfilled",     unfulfilled)
            .put("minCutCapacity",  cutCap)
            .putRaw("userGroups",   groups.build())
            .putRaw("resources",    resources.build())
            .putRaw("sourceSideNodes",  srcNodes.build())
            .putRaw("sinkSideNodes",    sinkNodes.build())
            .putRaw("bottleneckEdges",  bottlenecks.build())
            .put("interpretation",
                "The minimum cut identifies the capacity bottleneck that limits the maximum flow. " +
                "To serve more users, increase the capacity of the bottleneck edges.")
            .build();
        json(ex, 200, resp);
    }

    // -----------------------------------------------------------------------
    // POST /api/matching
    //   body: {
    //     "users": ["A","B","C"],
    //     "resources": ["R1","R2","R3"],
    //     "compatible": [[true,false,true],[false,true,false],[true,true,false]]
    //   }
    // -----------------------------------------------------------------------
    private void handleMatching(HttpExchange ex) throws IOException {
        String body = WebServer.readBody(ex);

        // Parse users array
        String[] users = parseStringArray(body, "users");
        String[] resources = parseStringArray(body, "resources");
        boolean[][] compatible = parseBoolMatrix(body, "compatible",
            users == null ? 0 : users.length,
            resources == null ? 0 : resources.length);

        if (users == null || users.length == 0) {
            json(ex, 400, JsonUtil.error("'users' array is required and cannot be empty"));
            return;
        }
        if (resources == null || resources.length == 0) {
            json(ex, 400, JsonUtil.error("'resources' array is required and cannot be empty"));
            return;
        }
        if (compatible == null) {
            json(ex, 400, JsonUtil.error(
                "'compatible' matrix must have " + users.length + " rows and " + resources.length + " columns"));
            return;
        }

        // Call actual BipartiteMatching
        BipartiteMatching.MatchingResult result = BipartiteMatching.computeMatching(compatible);
        int[] matchedRight = result.matchedRight;  // matchedRight[j] = user index for resource j, or -1

        // Build assignments
        boolean[] userMatched = new boolean[users.length];
        JsonUtil.ArrayBuilder assignments = JsonUtil.arr();
        for (int j = 0; j < resources.length; j++) {
            if (matchedRight[j] != -1) {
                int ui = matchedRight[j];
                userMatched[ui] = true;
                assignments.addRaw(JsonUtil.obj()
                    .put("user",     users[ui])
                    .put("resource", resources[j])
                    .build());
            }
        }

        // Unmatched users
        JsonUtil.ArrayBuilder unmatched = JsonUtil.arr();
        for (int i = 0; i < users.length; i++) {
            if (!userMatched[i]) unmatched.add(users[i]);
        }

        String resp = JsonUtil.obj()
            .put("algorithm",       "Bipartite Matching — Augmenting-path DFS")
            .put("complexity",      "Time O(V × E)")
            .put("maxMatching",     result.maxMatches)
            .put("totalUsers",      users.length)
            .put("totalResources",  resources.length)
            .putRaw("assignments",  assignments.build())
            .putRaw("unmatchedUsers", unmatched.build())
            .build();
        json(ex, 200, resp);
    }

    // -----------------------------------------------------------------------
    // GET /api/random-document
    //   Module 6: Uniform random document discovery via Reservoir Sampling
    // -----------------------------------------------------------------------
    private void handleRandomDocument(HttpExchange ex) throws IOException {
        if (WebServer.documents == null || WebServer.documents.isEmpty()) {
            json(ex, 404, JsonUtil.error("Corpus is empty"));
            return;
        }

        LibraryDocument doc = ReservoirSampler.sample(WebServer.documents);
        if (doc == null) {
            json(ex, 404, JsonUtil.error("Failed to select random document"));
            return;
        }

        String content = doc.getContent();
        String preview = (content != null && content.length() > 280)
            ? content.substring(0, 280).trim() + "..."
            : (content != null ? content : "");

        String body = JsonUtil.obj()
            .put("id",                     doc.getFileName())
            .put("filename",               doc.getFileName())
            .put("title",                  doc.getTitle())
            .put("author",                 doc.getAuthor())
            .put("category",               doc.getCategory())
            .put("year",                   doc.getYear())
            .put("isbn",                   doc.getIsbn())
            .put("wordCount",              doc.getWordCount())
            .put("estimatedReadingMinutes",doc.getEstimatedReadingMinutes(200))
            .put("preview",                preview)
            .put("algorithm",              ReservoirSampler.ALGORITHM)
            .put("complexity",             ReservoirSampler.COMPLEXITY)
            .put("probability",            "1 / " + WebServer.documents.size())
            .put("proofFormula",           ReservoirSampler.PROBABILITY_FORMULA)
            .put("explanation",
                "Reservoir sampling (Algorithm R) maintains a single chosen item in memory. "
                + "When reading document i (1-based), it replaces the current item with probability 1/i. "
                + "Telescoping the non-replacement probabilities guarantees every document has an exact 1/n "
                + "probability of selection, processing the corpus in O(n) time and O(1) auxiliary selection space.")
            .build();
        json(ex, 200, body);
    }

    // -----------------------------------------------------------------------
    // POST /api/reading-plan
    //   Module 5: 1/2-Approximation algorithm for 0/1 Knapsack Reading Plan
    //   body: {
    //     "budget": 30,
    //     "readingSpeed": 200,
    //     "candidates": [ { "id": "11_algorithm_complexity.txt", "priority": 8 }, ... ]
    //   }
    // -----------------------------------------------------------------------
    private void handleReadingPlan(HttpExchange ex) throws IOException {
        String body = WebServer.readBody(ex);
        int budget = jsonInt(body, "budget", 30);
        int speed  = jsonInt(body, "readingSpeed", ReadingPlanGenerator.DEFAULT_READING_SPEED_WPM);
        if (speed <= 0) speed = ReadingPlanGenerator.DEFAULT_READING_SPEED_WPM;

        List<CandidateInput> rawCandidates = parseCandidates(body);
        List<ReadingPlanGenerator.PlanItem> items = new ArrayList<>();

        if (rawCandidates.isEmpty()) {
            for (LibraryDocument doc : WebServer.documents) {
                int words = doc.getWordCount();
                int mins = doc.getEstimatedReadingMinutes(speed);
                items.add(new ReadingPlanGenerator.PlanItem(doc, words, mins, 5));
            }
        } else {
            for (CandidateInput ci : rawCandidates) {
                LibraryDocument doc = findDocumentById(ci.id);
                if (doc != null) {
                    int words = doc.getWordCount();
                    int mins = doc.getEstimatedReadingMinutes(speed);
                    items.add(new ReadingPlanGenerator.PlanItem(doc, words, mins, ci.priority));
                }
            }
        }

        ReadingPlanGenerator.PlanResult result = ReadingPlanGenerator.generatePlan(items, budget, speed);

        JsonUtil.ArrayBuilder docsArr = JsonUtil.arr();
        for (ReadingPlanGenerator.PlanItem it : result.getSelectedItems()) {
            LibraryDocument d = it.getDocument();
            docsArr.addRaw(JsonUtil.obj()
                .put("id",               d.getFileName())
                .put("filename",         d.getFileName())
                .put("title",            d.getTitle())
                .put("author",           d.getAuthor())
                .put("category",         d.getCategory())
                .put("wordCount",        it.getWordCount())
                .put("estimatedMinutes", it.getEstimatedMinutes())
                .put("priority",         it.getPriority())
                .put("density",          String.format(java.util.Locale.US, "%.3f", it.getDensity()))
                .build());
        }

        String resp = JsonUtil.obj()
            .put("budgetMinutes",           result.getBudgetMinutes())
            .put("totalMinutes",            result.getTotalMinutes())
            .put("remainingMinutes",        result.getRemainingMinutes())
            .put("totalPriority",           result.getTotalPriority())
            .put("readingSpeedWpm",         result.getReadingSpeedWpm())
            .put("selectedCount",           result.getSelectedItems().size())
            .put("totalCandidates",         items.size())
            .put("strategy",                result.getStrategy())
            .put("algorithm",               result.getAlgorithm())
            .put("complexity",              result.getComplexity())
            .put("approximationGuarantee",  result.getApproximationGuarantee())
            .put("explanation",             result.getExplanation())
            .put("syllabusTheory",
                "1. Optimization vs Decision: The optimization problem maximizes total document priority subject to sum(time) <= Budget. "
                + "The decision version asks 'Can priority >= K be achieved within budget W?'. The decision problem is NP-complete via reduction from SUBSET-SUM.\n"
                + "2. Why Approximation: While dynamic programming runs in pseudo-polynomial O(n*W) time, large budgets or real-time web responsiveness benefit from polynomial O(n log n) approximation.\n"
                + "3. 1/2-Approximation Proof: Let greedy prefix set be G and first rejected item be item_(k+1). Then V(G) + v_(k+1) >= OPT_fractional >= OPT. "
                + "Comparing V(G) with max single feasible item v_max guarantees max(V(G), v_max) >= 0.5 * OPT.")
            .putRaw("selectedDocuments",    docsArr.build())
            .build();
        json(ex, 200, resp);
    }

    private static class CandidateInput {
        final String id;
        final int priority;
        CandidateInput(String id, int priority) {
            this.id = id;
            this.priority = priority;
        }
    }

    private static List<CandidateInput> parseCandidates(String json) {
        List<CandidateInput> list = new ArrayList<>();
        if (json == null) return list;
        int idx = json.indexOf("\"candidates\"");
        if (idx < 0) return list;
        int arrStart = json.indexOf('[', idx);
        if (arrStart < 0) return list;
        int arrEnd = json.indexOf(']', arrStart);
        if (arrEnd < 0) return list;
        String sub = json.substring(arrStart, arrEnd + 1);

        int pos = 0;
        while ((pos = sub.indexOf('{', pos)) >= 0) {
            int endObj = sub.indexOf('}', pos);
            if (endObj < 0) break;
            String objStr = sub.substring(pos, endObj + 1);
            String id = jsonString(objStr, "id");
            int priority = jsonInt(objStr, "priority", 5);
            if (id != null && !id.trim().isEmpty()) {
                list.add(new CandidateInput(id.trim(), Math.max(1, Math.min(10, priority))));
            }
            pos = endObj + 1;
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void json(HttpExchange ex, int code, String body) throws IOException {
        WebServer.sendResponse(ex, code, "application/json", body);
    }

    private LibraryDocument findDocumentById(String id) {
        for (LibraryDocument d : WebServer.documents) {
            if (d.getFileName().equals(id)) return d;
        }
        return null;
    }

    private static String trimContent(String content, int maxChars) {
        if (content == null || content.isEmpty()) return "";
        return content.length() <= maxChars ? content : content.substring(0, maxChars);
    }

    private static String safeMessage(Exception e) {
        String msg = e.getMessage();
        return (msg != null && msg.length() < 200) ? msg : e.getClass().getSimpleName();
    }

    private static String normalizeField(String field) {
        switch (field.toLowerCase()) {
            case "title":   return "Title";
            case "author":  return "Author";
            case "isbn":    return "ISBN";
            case "category":return "Category";
            case "content": return "Content";
            default:        return field;
        }
    }

    // -----------------------------------------------------------------------
    // Minimal JSON value extractors (no external library)
    // -----------------------------------------------------------------------

    /** Extract a string value for a key from a simple flat JSON object. */
    private static String jsonString(String json, String key) {
        if (json == null) return null;
        String search = "\"" + key + "\"";
        int ki = json.indexOf(search);
        if (ki < 0) return null;
        int colon = json.indexOf(':', ki + search.length());
        if (colon < 0) return null;
        int start = json.indexOf('"', colon + 1);
        if (start < 0) return null;
        StringBuilder sb = new StringBuilder();
        int i = start + 1;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case '"':  sb.append('"');  i += 2; break;
                    case '\\': sb.append('\\'); i += 2; break;
                    case 'n':  sb.append('\n'); i += 2; break;
                    case 'r':  sb.append('\r'); i += 2; break;
                    case 't':  sb.append('\t'); i += 2; break;
                    default:   sb.append(next); i += 2; break;
                }
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    /** Extract an integer value for a key from a simple flat JSON object. */
    private static int jsonInt(String json, String key, int defaultVal) {
        if (json == null) return defaultVal;
        String search = "\"" + key + "\"";
        int ki = json.indexOf(search);
        if (ki < 0) return defaultVal;
        int colon = json.indexOf(':', ki + search.length());
        if (colon < 0) return defaultVal;
        StringBuilder num = new StringBuilder();
        int i = colon + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
        if (i < json.length() && json.charAt(i) == '-') { num.append('-'); i++; }
        while (i < json.length() && Character.isDigit(json.charAt(i))) { num.append(json.charAt(i)); i++; }
        try { return Integer.parseInt(num.toString()); }
        catch (NumberFormatException e) { return defaultVal; }
    }

    /** Parse a JSON string array like ["a","b","c"] at the given key. */
    private static String[] parseStringArray(String json, String key) {
        if (json == null) return null;
        String search = "\"" + key + "\"";
        int ki = json.indexOf(search);
        if (ki < 0) return null;
        int colon = json.indexOf(':', ki + search.length());
        if (colon < 0) return null;
        int open = json.indexOf('[', colon + 1);
        if (open < 0) return null;
        int close = json.indexOf(']', open + 1);
        if (close < 0) return null;
        String inside = json.substring(open + 1, close).trim();
        if (inside.isEmpty()) return new String[0];
        List<String> list = new ArrayList<>();
        int i = 0;
        while (i < inside.length()) {
            if (inside.charAt(i) == '"') {
                StringBuilder sb = new StringBuilder();
                i++;
                while (i < inside.length() && inside.charAt(i) != '"') {
                    if (inside.charAt(i) == '\\' && i + 1 < inside.length()) {
                        i++;
                        sb.append(inside.charAt(i));
                    } else {
                        sb.append(inside.charAt(i));
                    }
                    i++;
                }
                list.add(sb.toString());
                i++; // past closing "
            } else {
                i++;
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * Parse a boolean matrix [[true,false],[false,true]] at the given key.
     * Returns null if dimensions don't match expectedRows x expectedCols.
     */
    private static boolean[][] parseBoolMatrix(String json, String key, int expectedRows, int expectedCols) {
        if (json == null || expectedRows <= 0 || expectedCols <= 0) return null;
        String search = "\"" + key + "\"";
        int ki = json.indexOf(search);
        if (ki < 0) return null;
        int colon = json.indexOf(':', ki + search.length());
        if (colon < 0) return null;
        // Find outer [
        int outerOpen = json.indexOf('[', colon + 1);
        if (outerOpen < 0) return null;

        boolean[][] matrix = new boolean[expectedRows][expectedCols];
        int row = 0;
        int i = outerOpen + 1;
        while (i < json.length() && row < expectedRows) {
            if (json.charAt(i) == '[') {
                int innerClose = json.indexOf(']', i + 1);
                if (innerClose < 0) return null;
                String inner = json.substring(i + 1, innerClose).trim();
                // parse true/false values
                String[] tokens = inner.split(",");
                if (tokens.length != expectedCols) return null;
                for (int c = 0; c < expectedCols; c++) {
                    matrix[row][c] = "true".equals(tokens[c].trim());
                }
                row++;
                i = innerClose + 1;
            } else if (json.charAt(i) == ']') {
                break; // outer close
            } else {
                i++;
            }
        }
        if (row != expectedRows) return null;
        return matrix;
    }
}

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main.java — Digital Library Search System
 *
 * This file is the controller only. It handles menus, user input,
 * and calls the appropriate algorithm classes.
 *
 * Algorithms used:
 *   KMP                — exact pattern search across all document fields
 *   Levenshtein (DP)   — fuzzy search when no exact match is found
 *   Needleman-Wunsch   — global sequence alignment to compare two documents
 *   Edmonds-Karp       — max-flow for library resource allocation
 *   Min-Cut            — bottleneck identification after max-flow
 *   Bipartite Matching — one-to-one user-to-resource assignment
 */
public class Main {

    // -----------------------------------------------------------------------
    // Corpus path and shared objects
    // -----------------------------------------------------------------------
    private static final String CORPUS_PATH = "corpus";

    private static List<LibraryDocument> documents = new ArrayList<>();
    private static final CorpusReader corpusReader = new CorpusReader();
    private static final KMP kmp = new KMP();
    private static final LibrarySearchEngine searchEngine = new LibrarySearchEngine(kmp);

    // -----------------------------------------------------------------------
    // Entry point
    // -----------------------------------------------------------------------
    public static void main(String[] args) {
        loadCorpus();
        Scanner sc = new Scanner(System.in);

        while (true) {
            printMainMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": searchLibraryMenu(sc);     break;
                case "2": fuzzySearch(sc);           break;
                case "3": compareDocuments(sc);      break;
                case "4": resourceAllocation();      break;
                case "5": userResourceMatching();    break;
                case "6": displayAllDocuments();     break;
                case "7": loadCorpus();              break;
                case "8": aboutProject();            break;
                case "9": generateReadingPlanCLI(sc);break;
                case "10": randomDiscoveryCLI();      break;
                case "11": benchmarkSearchCLI(sc);   break;
                case "12":
                    System.out.println("Goodbye.");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Enter 1-12.");
            }
        }
    }

    // -----------------------------------------------------------------------
    // Corpus loading
    // -----------------------------------------------------------------------
    private static void loadCorpus() {
        documents = corpusReader.loadCorpus(CORPUS_PATH);
        System.out.println("Corpus loaded: " + documents.size() + " documents.");
    }

    // -----------------------------------------------------------------------
    // Main menu
    // -----------------------------------------------------------------------
    private static void printMainMenu() {
        System.out.println("\n================================================");
        System.out.println("       DIGITAL LIBRARY SEARCH SYSTEM");
        System.out.println("================================================");
        System.out.println("1.  Search Library");
        System.out.println("2.  Fuzzy Search");
        System.out.println("3.  Compare Documents");
        System.out.println("4.  Resource Allocation");
        System.out.println("5.  User-Resource Matching");
        System.out.println("6.  Display All Documents");
        System.out.println("7.  Reload Corpus");
        System.out.println("8.  About Project");
        System.out.println("9.  Generate Reading Plan (Module 5: 1/2-Approx Knapsack)");
        System.out.println("10. Random Discovery (Module 6: Reservoir Sampling)");
        System.out.println("11. Benchmark Search Execution Modes (Sequential vs. Parallel)");
        System.out.println("12. Exit");
        System.out.print("Enter choice: ");
    }

    // -----------------------------------------------------------------------
    // 1. SEARCH LIBRARY  —  powered by KMP
    // -----------------------------------------------------------------------
    private static void searchLibraryMenu(Scanner sc) {
        while (true) {
            System.out.println("\n===== SEARCH LIBRARY (KMP Exact Search) =====");
            System.out.println("1. KMP Search (all fields)");
            System.out.println("2. Search by Title");
            System.out.println("3. Search by Author");
            System.out.println("4. Search by ISBN");
            System.out.println("5. Search by Category");
            System.out.println("6. Search Document Content");
            System.out.println("7. Demonstrate KMP");
            System.out.println("8. Back");
            System.out.print("Enter choice: ");
            String c = sc.nextLine().trim();

            try {
                switch (c) {
                    case "1": kmpSearchAll(sc);             break;
                    case "2": fieldSearch(sc, "Title");     break;
                    case "3": fieldSearch(sc, "Author");    break;
                    case "4": fieldSearch(sc, "ISBN");      break;
                    case "5": fieldSearch(sc, "Category");  break;
                    case "6": fieldSearch(sc, "Content");   break;
                    case "7": demonstrateKMP(sc);           break;
                    case "8": return;
                    default:  System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void kmpSearchAll(Scanner sc) {
        System.out.print("Enter search query: ");
        String query = sc.nextLine();
        if (query.trim().isEmpty()) { System.out.println("Query cannot be empty."); return; }
        List<LibrarySearchEngine.SearchMatch> results = searchEngine.search(documents, query);
        printResults(results);
    }

    private static void fieldSearch(Scanner sc, String field) {
        System.out.print("Enter " + field + " query: ");
        String query = sc.nextLine();
        if (query.trim().isEmpty()) { System.out.println("Query cannot be empty."); return; }
        List<LibrarySearchEngine.SearchMatch> all = searchEngine.search(documents, query);
        List<LibrarySearchEngine.SearchMatch> filtered = new ArrayList<>();
        for (LibrarySearchEngine.SearchMatch r : all)
            if (r.getFieldName().equals(field)) filtered.add(r);
        printResults(filtered);
    }

    private static void printResults(List<LibrarySearchEngine.SearchMatch> results) {
        System.out.println("\n----- KMP Search Results -----");
        System.out.println("Algorithm : KMP   Time: O(n+m)   Space: O(m)");
        if (results.isEmpty()) {
            System.out.println("No documents found.");
            return;
        }
        System.out.println("Found " + results.size() + " match(es):\n");
        for (LibrarySearchEngine.SearchMatch r : results) {
            LibraryDocument d = r.getDocument();
            System.out.println("  File     : " + d.getFileName());
            System.out.println("  Title    : " + d.getTitle());
            System.out.println("  Author   : " + d.getAuthor());
            System.out.println("  Category : " + d.getCategory());
            System.out.println("  Field    : " + r.getFieldName()
                + "   Positions: " + r.getPositions()
                + "   Occurrences: " + r.getPositions().size());
            System.out.println();
        }
    }

    private static void demonstrateKMP(Scanner sc) {
        displayAllDocuments();
        System.out.print("Enter document number: ");
        try {
            int n = Integer.parseInt(sc.nextLine().trim());
            if (n < 1 || n > documents.size()) { System.out.println("Invalid number."); return; }
            LibraryDocument d = documents.get(n - 1);
            System.out.print("Enter pattern: ");
            String pattern = sc.nextLine();
            if (pattern.trim().isEmpty()) { System.out.println("Pattern cannot be empty."); return; }

            kmp.demonstrate(d.getContent(), pattern);
            List<Integer> pos = kmp.search(d.getContent(), pattern);
            System.out.println("\nAll match positions : " + pos);
            System.out.println("Total occurrences   : " + pos.size());
            System.out.println("Time complexity     : O(n + m)");
            System.out.println("Space complexity    : O(m)  [LPS array only]");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    // -----------------------------------------------------------------------
    // 2. FUZZY SEARCH  —  powered by Levenshtein Distance (DP)
    //
    // Workflow:
    //   1. Run KMP exact search first.
    //   2. If exact match found, show exact results.
    //   3. If no exact match, run Levenshtein on corpus vocabulary.
    //   4. Rank suggestions by edit distance (smallest first).
    // -----------------------------------------------------------------------
    private static void fuzzySearch(Scanner sc) {
        System.out.println("\n===== FUZZY SEARCH (Levenshtein Distance) =====");
        System.out.print("Enter query (possibly misspelled): ");
        String query = sc.nextLine().trim();
        if (query.isEmpty()) { System.out.println("Query cannot be empty."); return; }

        // Step 1: KMP exact check
        List<LibrarySearchEngine.SearchMatch> exact = searchEngine.search(documents, query);
        System.out.println("\n-- Step 1: KMP Exact Search --");
        if (!exact.isEmpty()) {
            System.out.println("KMP found an exact match. Showing exact results:");
            printResults(exact);
            return;
        }
        System.out.println("KMP: No exact match found. Switching to Levenshtein fuzzy search...");

        // Step 2: Levenshtein fuzzy search
        System.out.print("Maximum edit distance (e.g. 2): ");
        int maxDist;
        try {
            maxDist = Integer.parseInt(sc.nextLine().trim());
            if (maxDist < 0) { System.out.println("Distance must be >= 0."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number."); return;
        }

        List<LibrarySearchEngine.FuzzyMatch> suggestions =
            searchEngine.fuzzySearch(documents, query, maxDist);

        System.out.println("\n-- Step 2: Levenshtein Fuzzy Suggestions --");
        System.out.println("Algorithm : Levenshtein DP   Time: O(n×m)   Space: O(n×m)");
        if (suggestions.isEmpty()) {
            System.out.println("No suggestions found within edit distance " + maxDist + ".");
            System.out.println("Try increasing the maximum distance.");
        } else {
            System.out.printf("%-5s %-30s %s%n", "Rank", "Suggestion", "Edit Distance");
            System.out.println("-".repeat(50));
            int limit = Math.min(15, suggestions.size());
            for (int i = 0; i < limit; i++) {
                LibrarySearchEngine.FuzzyMatch m = suggestions.get(i);
                System.out.printf("%-5d %-30s %d%n", i + 1, m.getTerm(), m.getDistance());
            }
        }
    }

    // -----------------------------------------------------------------------
    // 3. COMPARE DOCUMENTS  —  powered by Needleman-Wunsch (Global Alignment)
    //
    // Why Needleman-Wunsch?
    //   It performs global sequence alignment: compares the full content of
    //   two texts and produces a scored alignment showing matches, mismatches,
    //   and gaps. This is meaningful for comparing two research documents.
    //
    // Time: O(n×m)   Space: O(n×m)
    // -----------------------------------------------------------------------
    private static void compareDocuments(Scanner sc) {
        System.out.println("\n===== COMPARE DOCUMENTS (Needleman-Wunsch) =====");
        System.out.println("Algorithm: Needleman-Wunsch Global Alignment");
        System.out.println("Purpose  : Compares the content of two library documents.");
        System.out.println("Time     : O(n x m)   Space: O(n x m)");

        displayAllDocuments();
        try {
            int a = readInt(sc, "Select Document A number: ");
            int b = readInt(sc, "Select Document B number: ");
            if (a < 1 || a > documents.size() || b < 1 || b > documents.size()) {
                System.out.println("Invalid document number."); return;
            }

            // Use first 80 characters of content for a readable demonstration
            String contentA = trimContent(documents.get(a - 1).getContent(), 80);
            String contentB = trimContent(documents.get(b - 1).getContent(), 80);

            System.out.println("\nDocument A: " + documents.get(a - 1).getTitle());
            System.out.println("Document B: " + documents.get(b - 1).getTitle());
            System.out.println("(Comparing first 80 characters of content)");
            System.out.println();

            int match    = readInt(sc, "Match score    (e.g.  1): ");
            int mismatch = readInt(sc, "Mismatch score (e.g. -1): ");
            int gap      = readInt(sc, "Gap penalty    (e.g. -2): ");

            SequenceAlignment.AlignmentResult r =
                SequenceAlignment.needlemanWunsch(contentA, contentB, match, mismatch, gap);

            System.out.println("\n----- Global Alignment Result -----");
            System.out.println("Aligned A : " + r.first);
            System.out.println("Aligned B : " + r.second);
            System.out.println("Score     : " + r.score);
            System.out.println();
            System.out.println("Interpretation:");
            System.out.println("  A higher score indicates greater similarity between the documents.");
            System.out.println("  '-' in the alignment means a gap (insertion/deletion).");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    // -----------------------------------------------------------------------
    // 4. RESOURCE ALLOCATION  —  powered by Edmonds-Karp (Max-Flow)
    //                           + Min-Cut for bottleneck analysis
    //
    // Library resource network:
    //
    //   Source (0)
    //     -> Undergrad users (1)  cap = 8
    //     -> Postgrad users  (2)  cap = 5
    //     -> Faculty users   (3)  cap = 3
    //
    //   User groups -> Resource categories:
    //     Undergrad -> Algorithms collection (4)  cap = 6
    //     Undergrad -> AI collection         (5)  cap = 5
    //     Postgrad  -> Algorithms collection (4)  cap = 4
    //     Postgrad  -> AI collection         (5)  cap = 3
    //     Faculty   -> Algorithms collection (4)  cap = 2
    //     Faculty   -> AI collection         (5)  cap = 2
    //
    //   Categories -> Sink (6):
    //     Algorithms collection -> Sink  cap = 5  (5 licences)
    //     AI collection         -> Sink  cap = 4  (4 licences)
    //
    // Max-flow = maximum concurrent users that can be served.
    // Min-cut  = bottleneck resource/category.
    //
    // Algorithm: Edmonds-Karp  Time: O(V × E²)
    // -----------------------------------------------------------------------
    private static void resourceAllocation() {
        System.out.println("\n===== LIBRARY RESOURCE ALLOCATION (Edmonds-Karp) =====");
        System.out.println("Algorithm : Edmonds-Karp Max-Flow   Time: O(V x E^2)");
        System.out.println("Purpose   : Calculates max concurrent users across resource categories.");

        final int SOURCE = 0, SINK = 6;
        final String[] LABELS = {
            "Source",
            "Undergrad", "Postgrad", "Faculty",
            "Algorithms-Collection", "AI-Collection",
            "Sink"
        };

        FlowGraph g = new FlowGraph(7);

        // Source -> user groups (total demand caps)
        g.addEdge(SOURCE, 1, 8);   // Undergrad: up to 8
        g.addEdge(SOURCE, 2, 5);   // Postgrad : up to 5
        g.addEdge(SOURCE, 3, 3);   // Faculty  : up to 3

        // User groups -> resource categories (access permissions)
        g.addEdge(1, 4, 6);
        g.addEdge(1, 5, 5);
        g.addEdge(2, 4, 4);
        g.addEdge(2, 5, 3);
        g.addEdge(3, 4, 2);
        g.addEdge(3, 5, 2);

        // Resource categories -> Sink (licence limits)
        g.addEdge(4, SINK, 5);   // 5 Algorithms licences
        g.addEdge(5, SINK, 4);   // 4 AI licences

        System.out.println("\nNetwork:");
        System.out.println("  Source -> Undergrad(8), Postgrad(5), Faculty(3)  [total requests: 16]");
        System.out.println("  User groups -> Algorithms-Collection, AI-Collection");
        System.out.println("  Algorithms-Collection -> Sink (5 licences)");
        System.out.println("  AI-Collection         -> Sink (4 licences)");

        System.out.println("\nRunning Edmonds-Karp...");
        int maxFlow = EdmondsKarp.maxFlow(g, SOURCE, SINK, true);

        System.out.println("\n----- Resource Allocation Result -----");
        System.out.println("Total user requests         : " + (8 + 5 + 3));
        System.out.println("Maximum requests satisfied  : " + maxFlow);
        System.out.println("Unfulfilled requests        : " + ((8 + 5 + 3) - maxFlow));

        // Min-Cut: bottleneck analysis
        boolean[] sourceSide = MinCut.sourceSide(g, SOURCE);
        int cutCap = MinCut.cutCapacity(g, sourceSide);

        System.out.println("\n----- Bottleneck Analysis (Min-Cut) -----");
        System.out.println("Min-Cut capacity = " + cutCap
            + "  (equals max-flow — this confirms the theorem)");
        System.out.print("Source-side nodes: ");
        for (int i = 0; i < LABELS.length; i++)
            if (sourceSide[i]) System.out.print(LABELS[i] + "  ");
        System.out.println();
        System.out.println("\nBottleneck edges (the limiting resources):");
        for (int u = 0; u < LABELS.length; u++) {
            if (!sourceSide[u]) continue;
            for (FlowEdge e : g.edgesFrom(u)) {
                if (!sourceSide[e.to] && e.capacity > 0) {
                    System.out.println("  " + LABELS[u] + " -> " + LABELS[e.to]
                        + "  (capacity=" + e.capacity + ")");
                }
            }
        }
        System.out.println("\nTo serve more users, increase capacity on the bottleneck edges.");
    }

    // -----------------------------------------------------------------------
    // 5. USER-RESOURCE MATCHING  —  powered by Bipartite Matching
    //
    // Assigns library users to available resources (one-to-one).
    // Uses augmenting-path DFS (Hopcroft-Karp concept applied directly).
    // Conceptually equivalent to max-flow on a unit-capacity graph.
    //
    // Time: O(V × E)
    // -----------------------------------------------------------------------
    private static void userResourceMatching() {
        System.out.println("\n===== USER-RESOURCE MATCHING (Bipartite Matching) =====");
        System.out.println("Algorithm : Augmenting-path DFS bipartite matching");
        System.out.println("Purpose   : Assigns each user to one available resource.");
        System.out.println("Time      : O(V x E)");

        String[] users     = {"Anudeep", "Sarvesh", "Gautam", "Priya", "Ravi"};
        String[] resources = {"AlgoBook", "AIJournal", "NetworksPaper", "MLDataset", "DBTextbook"};

        // Compatibility table: compatible[user][resource] = true if user can access resource
        boolean[][] compatible = {
            {true,  true,  false, false, true },   // Anudeep
            {false, true,  true,  false, false},   // Sarvesh
            {true,  false, true,  true,  false},   // Gautam
            {false, false, false, true,  true },   // Priya
            {true,  true,  false, false, false}    // Ravi
        };

        // Print compatibility table
        System.out.println("\nCompatibility (YES = user can access resource):");
        System.out.printf("%-12s", "");
        for (String r : resources) System.out.printf("%-16s", r);
        System.out.println();
        for (int i = 0; i < users.length; i++) {
            System.out.printf("%-12s", users[i]);
            for (int j = 0; j < resources.length; j++)
                System.out.printf("%-16s", compatible[i][j] ? "YES" : "-");
            System.out.println();
        }

        // Run bipartite matching
        BipartiteMatching.MatchingResult result = BipartiteMatching.computeMatching(compatible);
        int totalMatched = result.maxMatches;
        int[] matchedResource = result.matchedRight;

        // Print assignment
        System.out.println("\n----- Assignment Result -----");
        for (int j = 0; j < resources.length; j++) {
            if (matchedResource[j] != -1) {
                System.out.println("  " + users[matchedResource[j]]
                    + "  -->  " + resources[j]);
            }
        }

        // Find unmatched users
        boolean[] matched = new boolean[users.length];
        for (int j = 0; j < resources.length; j++)
            if (matchedResource[j] != -1) matched[matchedResource[j]] = true;
        for (int i = 0; i < users.length; i++)
            if (!matched[i]) System.out.println("  " + users[i] + "  --> (unmatched)");

        System.out.println("\nMaximum matching = " + totalMatched + " out of " + users.length + " users.");
    }

    // -----------------------------------------------------------------------
    // 6. DISPLAY ALL DOCUMENTS
    // -----------------------------------------------------------------------
    private static void displayAllDocuments() {
        System.out.println("\n----- Corpus Documents (" + documents.size() + " total) -----");
        for (int i = 0; i < documents.size(); i++) {
            LibraryDocument d = documents.get(i);
            System.out.printf("%3d. %-45s | %s%n", i + 1, d.getFileName(), d.getTitle());
        }
    }

    // -----------------------------------------------------------------------
    // 8. ABOUT PROJECT
    // -----------------------------------------------------------------------
    private static void aboutProject() {
        System.out.println("\n================================================");
        System.out.println("       ABOUT THIS PROJECT");
        System.out.println("================================================");
        System.out.println("Project : Digital Library Search System");
        System.out.println("Language: Java (standard library only)");
        System.out.println("Corpus  : " + documents.size() + " external .txt files in corpus/");
        System.out.println();
        System.out.println("ALGORITHMS USED:");
        System.out.println();
        System.out.println("  KMP (CO2)");
        System.out.println("    Purpose : Exact text search across all document fields");
        System.out.println("    Feature : Search Library");
        System.out.println("    Time    : O(n + m)   Space: O(m)");
        System.out.println();
        System.out.println("  Levenshtein Distance (Module 3)");
        System.out.println("    Purpose : Fuzzy search when the user misspells a query");
        System.out.println("    Feature : Fuzzy Search");
        System.out.println("    Time    : O(n x m)   Space: O(n x m)");
        System.out.println();
        System.out.println("  Needleman-Wunsch (Module 3)");
        System.out.println("    Purpose : Global text alignment to compare two documents");
        System.out.println("    Feature : Compare Documents");
        System.out.println("    Time    : O(n x m)   Space: O(n x m)");
        System.out.println();
        System.out.println("  Edmonds-Karp Max-Flow (Module 4)");
        System.out.println("    Purpose : Calculates maximum concurrent resource access");
        System.out.println("    Feature : Resource Allocation");
        System.out.println("    Time    : O(V x E^2)");
        System.out.println();
        System.out.println("  Min-Cut (Module 4)");
        System.out.println("    Purpose : Identifies resource bottlenecks after max-flow");
        System.out.println("    Feature : Bottleneck Analysis (within Resource Allocation)");
        System.out.println();
        System.out.println("  Bipartite Matching (Module 4)");
        System.out.println("    Purpose : Assigns each user to exactly one resource");
        System.out.println("    Feature : User-Resource Matching");
        System.out.println("    Time    : O(V x E)");
        System.out.println();
        System.out.println("  Knapsack 1/2-Approximation (Module 5)");
        System.out.println("    Purpose : Solves NP-hard 0/1 knapsack for reading plan generation");
        System.out.println("    Feature : Reading Plan / Study Pack Builder");
        System.out.println("    Time    : O(n log n) [sorting by priority density]   Space: O(n)");
        System.out.println();
        System.out.println("  Reservoir Sampling (Module 6)");
        System.out.println("    Purpose : Unbiased O(n) uniform random document discovery (Algorithm R)");
        System.out.println("    Feature : Surprise Me / Random Discovery");
        System.out.println("    Time    : O(n) single pass   Space: O(1) auxiliary selection space");
        System.out.println();
        System.out.println("  Parallel Document Search (Module 6)");
        System.out.println("    Purpose : Multi-threaded document scanning using bounded executor");
        System.out.println("    Feature : Sequential, Parallel, and Auto Search Modes");
        System.out.println("    Order   : Strictly deterministic matching sequential execution");
        System.out.println();
        System.out.println("\"The Digital Library Search System uses KMP for exact text");
        System.out.println(" searching, Dynamic Programming for fuzzy searching and document");
        System.out.println(" comparison, Network Flow for resource allocation, Approximation");
        System.out.println(" for reading plans, and Randomised/Parallel algorithms for discovery and search.\"");
        System.out.println();
        System.out.println("Team: P. Anudeep (2520030087) | S. Sarvesh Reddy (2520030049)");
        System.out.println("      V. Gautam (2520030316)");
        System.out.println("Guide: Dr. S. Vinay Kumar, Dept. of CSE");
    }

    // -----------------------------------------------------------------------
    // 9. GENERATE READING PLAN (Module 5: 1/2-Approximation for 0/1 Knapsack)
    // -----------------------------------------------------------------------
    private static void generateReadingPlanCLI(Scanner sc) {
        System.out.println("\n===== READING PLAN BUILDER (1/2-Approximation for 0/1 Knapsack) =====");
        if (documents.isEmpty()) {
            System.out.println("Corpus is empty. Please load documents first.");
            return;
        }

        System.out.print("Enter your reading time budget in minutes (e.g., 30): ");
        int budget;
        try {
            budget = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid budget. Using default 30 minutes.");
            budget = 30;
        }

        System.out.print("Enter reading speed in words per minute [default 200]: ");
        String speedStr = sc.nextLine().trim();
        int speed = 200;
        if (!speedStr.isEmpty()) {
            try { speed = Math.max(50, Integer.parseInt(speedStr)); }
            catch (NumberFormatException ignored) {}
        }

        // Build candidate list from loaded documents
        List<ReadingPlanGenerator.PlanItem> candidates = new ArrayList<>();
        for (int i = 0; i < documents.size(); i++) {
            LibraryDocument doc = documents.get(i);
            int words = doc.getWordCount();
            int mins = doc.getEstimatedReadingMinutes(speed);
            // Default priority 5; higher for core algorithm docs
            int prio = 5;
            if (doc.getTitle().toLowerCase().contains("algorithm") || doc.getTitle().toLowerCase().contains("kmp")) {
                prio = 9;
            } else if (doc.getCategory().toLowerCase().contains("computer") || doc.getCategory().toLowerCase().contains("science")) {
                prio = 7;
            }
            candidates.add(new ReadingPlanGenerator.PlanItem(doc, words, mins, prio));
        }

        ReadingPlanGenerator.PlanResult res = ReadingPlanGenerator.generatePlan(candidates, budget, speed);

        System.out.println("\n----- Generated Reading Plan -----");
        System.out.println("Strategy Applied  : " + res.getStrategy());
        System.out.println("Budget            : " + res.getBudgetMinutes() + " minutes");
        System.out.println("Total Time Used   : " + res.getTotalMinutes() + " minutes");
        System.out.println("Remaining Budget  : " + res.getRemainingMinutes() + " minutes");
        System.out.println("Total Priority    : " + res.getTotalPriority() + " points");
        System.out.println("Selected Documents: " + res.getSelectedItems().size());
        System.out.println("Guarantee         : " + res.getApproximationGuarantee());
        System.out.println();

        System.out.printf("%-4s | %-36s | %-12s | %-8s | %-8s\n", "#", "Document Title", "Est. Minutes", "Priority", "Density");
        System.out.println("-----------------------------------------------------------------------------------");
        int idx = 1;
        for (ReadingPlanGenerator.PlanItem it : res.getSelectedItems()) {
            String title = it.getDocument().getTitle();
            if (title.length() > 36) title = title.substring(0, 33) + "...";
            System.out.printf("%-4d | %-36s | %-12d | %-8d | %-8.3f\n",
                idx++, title, it.getEstimatedMinutes(), it.getPriority(), it.getDensity());
        }
    }

    // -----------------------------------------------------------------------
    // 10. RANDOM DISCOVERY (Module 6: Reservoir Sampling Algorithm R)
    // -----------------------------------------------------------------------
    private static void randomDiscoveryCLI() {
        System.out.println("\n===== RANDOM DISCOVERY (Reservoir Sampling — Module 6) =====");
        if (documents.isEmpty()) {
            System.out.println("Corpus is empty. Please load documents first.");
            return;
        }

        LibraryDocument chosen = ReservoirSampler.sample(documents);
        if (chosen == null) {
            System.out.println("No document selected.");
            return;
        }

        System.out.println("Algorithm   : " + ReservoirSampler.ALGORITHM);
        System.out.println("Complexity  : " + ReservoirSampler.COMPLEXITY);
        System.out.println("Probability : 1 / " + documents.size() + " (" + ReservoirSampler.PROBABILITY_FORMULA + ")");
        System.out.println();
        System.out.println("Selected Document Details:");
        System.out.println("  File     : " + chosen.getFileName());
        System.out.println("  Title    : " + chosen.getTitle());
        System.out.println("  Author   : " + chosen.getAuthor());
        System.out.println("  Category : " + chosen.getCategory());
        System.out.println("  Year     : " + chosen.getYear());
        System.out.println("  Words    : " + chosen.getWordCount());
        System.out.println("  Est Time : " + chosen.getEstimatedReadingMinutes(200) + " min (at 200 WPM)");
        System.out.println("\nPreview:");
        String text = chosen.getContent();
        if (text.length() > 300) text = text.substring(0, 300) + "...";
        System.out.println(text);
    }

    // -----------------------------------------------------------------------
    // 11. BENCHMARK SEARCH MODES (Sequential vs. Parallel — Module 6)
    // -----------------------------------------------------------------------
    private static void benchmarkSearchCLI(Scanner sc) {
        System.out.println("\n===== BENCHMARK SEARCH MODES (Sequential vs. Parallel) =====");
        System.out.print("Enter query string to benchmark [default: 'algorithm']: ");
        String q = sc.nextLine().trim();
        if (q.isEmpty()) q = "algorithm";

        System.out.println("\nExecuting Sequential KMP Search over " + documents.size() + " documents...");
        LibrarySearchEngine.SearchExecutionResult seqRes =
            searchEngine.searchWithMetrics(documents, q, LibrarySearchEngine.SearchMode.SEQUENTIAL);

        System.out.println("Executing Parallel KMP Search over " + documents.size() + " documents...");
        LibrarySearchEngine.SearchExecutionResult parRes =
            searchEngine.searchWithMetrics(documents, q, LibrarySearchEngine.SearchMode.PARALLEL);

        System.out.println("\n----- Benchmark Results -----");
        System.out.println("Query               : \"" + q + "\"");
        System.out.println("Documents Scanned   : " + seqRes.getScannedDocuments());
        System.out.println("Sequential Matches  : " + seqRes.getMatches().size() + " | Time: " + String.format("%.3f ms (%d ns)", seqRes.getElapsedMs(), seqRes.getElapsedNanos()));
        System.out.println("Parallel Matches    : " + parRes.getMatches().size() + " | Time: " + String.format("%.3f ms (%d ns)", parRes.getElapsedMs(), parRes.getElapsedNanos()) + " [" + parRes.getWorkerThreads() + " worker threads]");

        // Verify result equivalence
        boolean matchCountEqual = (seqRes.getMatches().size() == parRes.getMatches().size());
        boolean orderIdentical = true;
        if (matchCountEqual) {
            for (int i = 0; i < seqRes.getMatches().size(); i++) {
                LibrarySearchEngine.SearchMatch m1 = seqRes.getMatches().get(i);
                LibrarySearchEngine.SearchMatch m2 = parRes.getMatches().get(i);
                if (!m1.getDocument().getFileName().equals(m2.getDocument().getFileName()) ||
                    !m1.getFieldName().equals(m2.getFieldName()) ||
                    !m1.getPositions().equals(m2.getPositions())) {
                    orderIdentical = false;
                    break;
                }
            }
        }

        System.out.println("Result Equivalence  : " + (matchCountEqual && orderIdentical ? "PERFECT 100% MATCH (Order and positions identical)" : "MISMATCH"));
    }

    // -----------------------------------------------------------------------
    // Utility helpers
    // -----------------------------------------------------------------------
    private static int readInt(Scanner sc, String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(sc.nextLine().trim());
    }

    /** Returns the first maxChars characters of content (avoids huge alignment matrices). */
    private static String trimContent(String content, int maxChars) {
        if (content == null || content.isEmpty()) return "";
        return content.length() <= maxChars ? content : content.substring(0, maxChars);
    }
}

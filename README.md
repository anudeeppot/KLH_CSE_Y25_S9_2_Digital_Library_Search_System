# DIGITAL LIBRARY SEARCH SYSTEM

**Course:** Data Structures & Algorithms / Advanced Algorithms Lab  
**Team Number:** 2 | **Section:** 9  

### Team Members:
- **P. Anudeep** (2520030087)
- **S. Sarvesh Reddy** (2520030049)
- **V. Gautam** (2520030316)

**Project Guide:** Dr. S. Vinay Kumar, Associate Professor, Department of Computer Science and Engineering  

---

## 1. Executive Summary

The **Digital Library Search System** is an end-to-end, academic-grade Java application designed to solve information retrieval, text comparison, resource management, and discovery challenges in a modern digital library repository.

The application operates on an external corpus of **100 structured academic documents** stored as plain text files in the `corpus/` directory.

### Core Modules & Algorithmic Foundations:
1. **String Pattern Matching (CO2):** Knuth-Morris-Pratt (KMP) linear-time multi-field exact search ($O(n+m)$).
2. **Dynamic Programming (Module 3):**
   - **Levenshtein Distance:** Fault-tolerant fuzzy search with ranked spelling suggestions ($O(n \times m)$).
   - **Needleman-Wunsch Global Alignment:** Quantitative document similarity scoring and character-level alignment ($O(n \times m)$).
3. **Network Flow & Combinatorial Optimization (Module 4):**
   - **Edmonds-Karp Max-Flow:** Multi-tier resource capacity allocation across student/faculty tiers ($O(V \cdot E^2)$).
   - **Min-Cut Bottleneck Analysis:** Reachability cut identifying limiting institutional assets ($O(V+E)$).
   - **Bipartite Matching:** Optimal 1-to-1 user-to-resource reservation assignment ($O(V \cdot E)$).
4. **NP-Completeness & Approximation (Module 5):**
   - **Reading Plan / Study Pack Builder:** Greedy 1/2-Approximation algorithm for the 0/1 Knapsack problem ($O(n \log n)$ time, $O(n)$ space). Optimizes learning value subject to reading-time budgets.
5. **Randomised & Parallel Algorithms (Module 6):**
   - **Surprise Me / Random Discovery:** Reservoir Sampling (Algorithm R, $k=1$) selecting a random document from a corpus stream with strictly uniform $1/n$ probability in $O(n)$ time and $O(1)$ auxiliary selection space.
   - **Parallel Document Search:** Bounded multi-threaded search engine with deterministic sequential ordering, sub-millisecond nanosecond instrumentation, and adaptive auto-fallback.

---

## 2. Real-World Library Problems & Algorithm Mapping

| Library Challenge | Traditional Problem | Algorithm Applied | Syllabus Unit | Complexity |
|---|---|---|---|---|
| **Multi-field Catalog Search** | Naive $O(n \times m)$ string search causes query latency | **Knuth-Morris-Pratt (KMP)** | CO2 | Time: $O(n + m)$<br>Space: $O(m)$ |
| **Typo & Misspelling Handling** | Exact search returns 0 matches for typos | **Levenshtein Distance (DP)** | Module 3 | Time: $O(n \times m)$<br>Space: $O(n \times m)$ |
| **Document Content Comparison** | Unstructured text diff cannot detect insertions/deletions | **Needleman-Wunsch Alignment** | Module 3 | Time: $O(n \times m)$<br>Space: $O(n \times m)$ |
| **Concurrent Resource Licensing** | Ad-hoc allocation causes resource saturation and starvation | **Edmonds-Karp (Max-Flow)** | Module 4 | Time: $O(V \times E^2)$<br>Space: $O(V + E)$ |
| **Licensing Bottleneck Detection** | Manual inspection fails to identify capacity limits | **Min-Cut (Max-Flow Min-Cut Theorem)** | Module 4 | Time: $O(V + E)$<br>Space: $O(V)$ |
| **Exclusive User Resource Assignment** | Conflicts when multiple users request restricted items | **Bipartite Matching** | Module 4 | Time: $O(V \times E)$<br>Space: $O(V)$ |
| **Time-Budgeted Study Planning** | Selecting highest-value reading subset is NP-hard 0/1 Knapsack | **Greedy 1/2-Approximation Knapsack** | **Module 5** | Time: $O(n \log n)$<br>Space: $O(n)$ |
| **Unbiased Serendipitous Discovery** | Loading all documents for random selection wastes memory or biases order | **Reservoir Sampling (Algorithm R)** | **Module 6** | Time: $O(n)$<br>Aux Space: $O(1)$ |
| **High-Throughput Corpus Searching** | Sequential document scanning cannot utilize multi-core CPUs | **Bounded Parallel Search Engine** | **Module 6** | Work: $O(n(L+m))$<br>Span: $O(\frac{n}{p}(L+m))$ |

---

## 3. Project Architecture & Folder Structure

The project keeps Java source files at the project root so the included Windows CMD launchers can compile the application with the JDK. Runtime assets and supporting documents are grouped separately.

```text
DigitalLibrarySearch/
├── corpus/                         # 100 active TXT library documents
├── web/                            # HTML, CSS, and browser JavaScript
│   ├── index.html
│   ├── style.css
│   └── app.js
├── docs/                           # Reports and project-submission documents
│   ├── CORPUS_EXPANSION_REPORT.md
│   ├── CORPUS_INFO.md
│   ├── Project_Abstract.pdf
│   └── Digital_Library_Search_System.pptx
├── Main.java                       # CLI entry point
├── WebServer.java                  # Built-in Java HTTP server
├── ApiHandler.java                 # API endpoints
├── CorpusReader.java               # TXT corpus loading
├── LibraryDocument.java            # Document model
├── LibrarySearchEngine.java        # Search and sequential/parallel execution
├── KMP.java                        # Exact string search
├── EditDistance.java               # Levenshtein distance
├── SequenceAlignment.java          # Needleman–Wunsch alignment
├── FlowEdge.java / FlowGraph.java  # Flow network structures
├── EdmondsKarp.java / MinCut.java  # Flow and cut algorithms
├── BipartiteMatching.java          # User-resource matching
├── ReadingPlanGenerator.java       # Approximate reading plan
├── ReservoirSampler.java           # Random document discovery
├── JsonUtil.java                   # JSON support
├── AlgorithmTests.java             # Algorithm test suite
├── CorpusVerificationTests.java    # Corpus integration verification
├── run_web.bat                     # Launch web application
├── run_cli.bat                     # Launch CLI application
├── run_tests.bat                   # Compile and run tests
├── README.md
└── .gitignore
```

Compiled Java `.class` files are generated when you run the batch scripts and are intentionally not included in the source package. The original-corpus backup and development scratch/generator files are also excluded from the clean distribution; the active `corpus/` directory remains intact.

---

## 4. Feature Details & Theoretical Foundations

### Feature 1: Search Library (KMP Exact Search)
- **Problem:** Fast, exact phrase searching across document metadata and text without backtracking.
- **Algorithm:** Knuth-Morris-Pratt (KMP) pattern matching with precomputed Longest Proper Prefix-Suffix (LPS) table.
- **Complexity:** Time $O(n + m)$, Space $O(m)$ where $n$ is text length and $m$ is query length.

### Feature 2: Fuzzy Search (Levenshtein Distance)
- **Problem:** Fault-tolerant query resolution for misspelled search terms (e.g., `"algoritm"` $\rightarrow$ `"algorithm"`).
- **Algorithm:** Dynamic Programming edit distance over corpus vocabulary tokens.
- **Complexity:** Time $O(n \times m)$, Space $O(n \times m)$.

### Feature 3: Document Comparison (Needleman-Wunsch Global Alignment)
- **Problem:** Qualitative and quantitative similarity analysis of two research texts with insertion/deletion detection.
- **Algorithm:** Needleman-Wunsch dynamic programming with backtracked character-level alignment trace.
- **Complexity:** Time $O(n \times m)$, Space $O(n \times m)$.

### Feature 4: Resource Allocation & Bottleneck Analysis (Edmonds-Karp + Min-Cut)
- **Problem:** Maximize concurrent student/faculty digital access licenses across competing collection demands.
- **Algorithm:** Edmonds-Karp BFS augmenting-path algorithm ($O(V \cdot E^2)$) and Min-Cut reachability BFS ($O(V+E)$).
- **Verification:** Proves Max-Flow = Min-Cut ($9 = 9$).

### Feature 5: User-Resource Matching (Maximum Bipartite Matching)
- **Problem:** 1-to-1 conflict-free assignment of students to restricted single-copy research assets.
- **Algorithm:** DFS-based alternating augmenting paths in a bipartite graph.
- **Complexity:** Time $O(V \cdot E)$, Space $O(V)$.

---

### Feature 6 (Module 5): Reading Plan & Study Pack Builder (1/2-Approximation Knapsack)

#### Purpose & Context
A student often has a fixed time budget (e.g., 30 or 45 minutes) to prepare for a class or exam. Given $n$ candidate documents, each document has:
- A word count $W_i$.
- An estimated reading time $t_i = \max(1, \lceil W_i / \text{speed} \rceil)$ minutes (default speed: 200 WPM).
- A user-assigned priority/value $v_i \in [1, 10]$.

Selecting the subset of documents that maximizes total priority without exceeding the reading budget $B$ is formally the **0/1 Knapsack Problem**:
$$\max \sum_{i \in S} v_i \quad \text{subject to} \quad \sum_{i \in S} t_i \le B, \quad S \subseteq \{1, \dots, n\}$$

#### Why Approximation is Required (NP-Completeness)
- The decision version of 0/1 Knapsack is **NP-complete** (by polynomial reduction from `SUBSET-SUM`).
- Standard dynamic programming runs in $O(n \cdot B)$ pseudo-polynomial time. If the budget $B$ is measured in fine units or large volumes of documents are queried in interactive web requests, DP can introduce latency and memory bloat.
- The greedy approximation algorithm runs in **strictly polynomial time $O(n \log n)$** and requires only $O(n)$ working space.

#### The 1/2-Approximation Algorithm
1. Discard any candidate whose reading time $t_i > B$.
2. Compute the priority density (efficiency) for each candidate:
   $$r_i = \frac{v_i}{t_i}$$
3. Sort the remaining candidates in descending order of density: $r_1 \ge r_2 \ge \dots \ge r_k$.
4. Greedily pack documents in sorted order into set $G$ until the next document would exceed budget $B$.
5. Identify the single best feasible candidate $m = \arg\max_{i \in \text{candidates}, t_i \le B} v_i$.
6. Return whichever solution yields higher priority:
   $$\text{Result} = \begin{cases} G & \text{if } \sum_{i \in G} v_i \ge v_m \\ \{m\} & \text{otherwise} \end{cases}$$

#### Mathematical Guarantee & Proof
Let $\text{OPT}$ be the optimal 0/1 knapsack score. Let $k$ be the index of the first candidate that could not fit into the greedy set $G$. The continuous (fractional) relaxation optimum $\text{OPT}_{\text{frac}}$ satisfies:
$$\sum_{i \in G} v_i + v_k \ge \text{OPT}_{\text{frac}} \ge \text{OPT}$$
Since both $G$ and $\{k\}$ are feasible solutions, and $v_m \ge v_k$:
$$\sum_{i \in G} v_i + v_m \ge \text{OPT} \implies \max\left(\sum_{i \in G} v_i, v_m\right) \ge \frac{1}{2}\text{OPT}$$

> **Important Caveat for Viva:** This guarantee mathematically bounds the objective score (priority points) to at least $50\%$ of the theoretical maximum knapsack score. It does not imply that the chosen documents are pedagogically or stylistically optimal.

---

### Feature 7 (Module 6): Surprise Me / Random Discovery (Reservoir Sampling)

#### Purpose & Context
Allows students and researchers to discover random documents from the digital library collection without bias. Rather than shuffling an entire in-memory collection or picking an index from an already materialized array, the feature implements **Reservoir Sampling (Algorithm R for $k=1$)**.

#### Algorithm & Stream Operation
Given a stream or iterator of documents $d_1, d_2, \dots, d_n$:
1. Select the first document $d_1$ into the reservoir.
2. For each subsequent document $d_i$ at 1-based index $i = 2, 3, \dots, n$:
   - Generate a random integer $r \in [0, i-1]$.
   - If $r = 0$ (probability $\frac{1}{i}$), replace the current reservoir candidate with $d_i$.
3. When the stream ends, return the current candidate.

#### Mathematical Proof of Uniform Probability
For any document $d_i$ ($1 \le i \le n$), $d_i$ is chosen at step $i$ with probability $\frac{1}{i}$, and it must not be replaced in any subsequent step $j$ ($i+1 \le j \le n$). The probability of survival at step $j$ is $1 - \frac{1}{j} = \frac{j-1}{j}$.

Therefore, the final probability that $d_i$ remains in the reservoir is:
$$P(d_i \text{ selected}) = \frac{1}{i} \times \prod_{j=i+1}^n \left(1 - \frac{1}{j}\right) = \frac{1}{i} \times \left(\frac{i}{i+1} \times \frac{i+1}{i+2} \times \cdots \times \frac{n-1}{n}\right)$$
The product telescopes cleanly:
$$P(d_i \text{ selected}) = \frac{1}{i} \times \frac{i}{n} = \frac{1}{n}$$
Every document has an exact, unbiased probability of $\frac{1}{n}$ of being chosen.

#### Complexity
- **Time Complexity:** $O(n)$ single-pass scan.
- **Auxiliary Selection Space:** $O(1)$ constant memory beyond the document reference itself.

---

### Feature 8 (Module 6): Parallel Document Search Engine

#### Purpose & Context
In a large digital library, searching every document sequentially for a search phrase creates latency. Because text searches across separate documents are independent read-only operations, search can be accelerated using parallel threads.

#### Concurrency Architecture & Safety
- **Bounded Thread Pool:** Uses a bounded `ExecutorService` (`Executors.newFixedThreadPool(workers)`) where worker count is bounded by CPU cores (up to 8). This avoids unbounded thread spawning.
- **Deterministic Result Assembly:** To prevent race conditions and ensure results match sequential search exactly, each worker processes an assigned document and writes to an indexed slot `results[i]`. Results are collected sequentially in document-order, guaranteeing 100% deterministic ordering.
- **Graceful Lifecycle Management:** Explicit `shutdown()` method releases all executor resources during server shutdown.

#### Execution Modes
1. **Sequential Mode:** Searches documents one-by-one on the calling thread. Ideal for small collections or low CPU overhead.
2. **Parallel Mode:** Dispatches document matching across worker threads and collates results deterministically.
3. **Auto Mode (Default):** Evaluates corpus size against an empirical threshold (`PARALLEL_AUTO_THRESHOLD = 20`). If corpus size $< 20$, it executes sequentially to avoid thread context-switch overhead; otherwise, it executes in parallel.

#### Performance Instrumentation
The Java backend measures actual search latency using `System.nanoTime()` and returns execution metrics in the API response:
```json
"metrics": {
  "requestedMode": "auto",
  "executedMode": "Parallel",
  "elapsedNanos": 1086200,
  "elapsedMs": "1.086",
  "scannedDocuments": 100,
  "workerThreads": 8
}
```

---

## 5. Deliberately Excluded Syllabus Topics

To keep the application strictly focused on real-world digital library operations and avoid confusing viva examiners with disconnected code samples, the following syllabus topics were deliberately excluded:

| Topic | Reason for Exclusion from Digital Library Project |
|---|---|
| **SAT / 3-SAT Solvers** | Boolean satisfiability solvers have no natural purpose in text search or library operations. |
| **CLIQUE / VERTEX COVER** | Graph reduction visualizers add pedagogical clutter without serving digital library users. |
| **Polynomial-time Reductions** | Reductions are theoretical proofs, not software application features. |
| **Miller-Rabin Primality Testing** | Cryptographic primality checks are unrelated to library cataloging or document search. |
| **Randomized QuickSort** | Java's built-in Dual-Pivot Quicksort is already optimal; reimplementing quicksort adds redundant code. |
| **Parallel Prefix-Sum / GPU** | GPU programming and scan operations require native C/CUDA libraries and break the zero-dependency Java constraint. |

---

## 6. Compilation, Testing, and Execution Guide

### Prerequisites
- Windows Operating System with Command Prompt (`cmd.exe`) or PowerShell.
- Java Development Kit (JDK 8 or higher; JDK 11+ recommended).
- Zero external libraries, Maven, Gradle, or npm packages required.

### Step 1: Compile the Entire Project (Windows CMD)
Open Windows Command Prompt, navigate to the project directory, and compile:
```cmd
cd /d "c:\Users\Anudeep_Potnuru\Desktop\DigitalLibrarySearch_DSA_Lab_Upgraded_Updated\FinalDigitalLibrarySearch"
javac --add-modules jdk.httpserver *.java
```

### Step 2: Run the Algorithm Test Suite (18/18 Unit Tests)
Run the pure-JDK algorithm test suite to verify Module 5 & Module 6 implementations:
```cmd
java AlgorithmTests
```
Expected output:
```text
=================================================
   DIGITAL LIBRARY SYSTEM — ALGORITHM TEST SUITE
=================================================
[PASS] Reading Plan: Empty list returns 0 items and 0 minutes
[PASS] Reading Plan: Zero and negative budget returns empty selection
[PASS] Reading Plan: Document exceeding total budget is rejected
[PASS] Reading Plan: Single best feasible item chosen when it beats greedy set
[PASS] Reading Plan: Greedy set chosen when its sum exceeds best single item
[PASS] Reading Plan: Handles equal density items correctly up to budget
[PASS] Reading Plan: Total minutes never exceeds budget (Total: 31 <= Budget: 35)
[PASS] Reading Plan: Mathematical 1/2-approximation bound verified against Exact 0/1 Knapsack DP (ratio >= 0.5)
[PASS] Reservoir Sampling: Returns null for empty collection
[PASS] Reservoir Sampling: Single item is selected with 100% certainty
[PASS] Reservoir Sampling: Multi-item returns a valid member of the stream
[PASS] Reservoir Sampling: Injectable Random produces deterministic selection
[PASS] Reservoir Sampling: Statistical uniformity verified over 20,000 trials (empirical P ≈ 0.25 each)
[PASS] Parallel Search: Empty corpus returns 0 matches gracefully
[PASS] Parallel Search: Auto mode falls back to Sequential for small corpus (< threshold)
[PASS] Parallel Search: Handles zero matches and multiple matches correctly
[PASS] Parallel Search: Sequential and Parallel results are 100% identical in items, positions, and order
[PASS] Parallel Search: Thread-safe across 20 rapid consecutive search executions without race conditions

=================================================
   TEST SUMMARY: 18 / 18 PASSED (100.0%)
=================================================
```

### Step 3: Run the Web Application
Start the built-in HTTP server:
```cmd
java WebServer
```
Open your web browser and navigate to:
```
http://localhost:8080
```
- Access **Reading Plan** from the sidebar or dashboard card.
- Click **🎲 Surprise Me** in the Hero header or Search bar to test Reservoir Sampling.
- Toggle between **Auto**, **Sequential**, and **Parallel** modes in KMP Search to inspect thread execution metrics.
- Navigate to **Settings** to toggle between **Light Mode** and **Dark Mode** (or click the quick theme toggle in the sidebar footer / mobile header). Preferences are automatically saved in `localStorage`.

### Step 4: Run the Interactive Console Menu
To run the terminal interface:
```cmd
java Main
```
The console menu includes:
- `Option 1:` KMP Exact Search
- `Option 2:` Fuzzy Search (Levenshtein Distance)
- `Option 3:` Compare Documents (Needleman-Wunsch Alignment)
- `Option 4:` Library Resource Allocation (Edmonds-Karp Max-Flow)
- `Option 5:` User-Resource Matching (Bipartite Matching)
- `Option 9:` Reading Plan Generator (Module 5 Approximation)
- `Option 10:` Random Document Discovery (Module 6 Reservoir Sampling)
- `Option 11:` Benchmark Search Modes (Module 6 Parallel vs. Sequential)

---

## 7. REST API Reference

All algorithm computation occurs strictly in Java; browser JavaScript handles only rendering and input submission.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/status` | System health, document count, active algorithm modules |
| `GET` | `/api/documents` | Returns all 100 documents with word counts & reading times |
| `GET` | `/api/document?id={filename}` | Returns complete content and metadata for a specific document |
| `GET` | `/api/search?q={query}&field={field}&mode={mode}` | KMP search with `mode` (`auto`, `sequential`, `parallel`) and nanosecond performance metrics |
| `POST` | `/api/fuzzy-search` | Levenshtein Distance DP fuzzy search suggestions |
| `POST` | `/api/kmp-demo` | LPS prefix-suffix array table and exact match indexes |
| `POST` | `/api/compare` | Needleman-Wunsch global alignment and score computation |
| `POST` | `/api/resource-allocation` | Edmonds-Karp Max-Flow and Min-Cut bottleneck identification |
| `POST` | `/api/matching` | DFS augmenting path Maximum Bipartite Matching |
| `GET` | `/api/random-document` | **[Module 6]** Reservoir sampling uniform document discovery |
| `POST` | `/api/reading-plan` | **[Module 5]** 1/2-Approximation knapsack study pack generator |

---

## 8. Viva Voce & Academic Defense Questions

### Q1: Why is the Knapsack problem NP-complete, and why does dynamic programming not disprove $P = NP$?
**Answer:** The decision version of 0/1 Knapsack is NP-complete because `SUBSET-SUM` can be reduced to it in polynomial time. The standard DP solution runs in $O(n \cdot W)$ time, which is **pseudo-polynomial**, not polynomial. The input size of capacity $W$ is proportional to $\log_2(W)$ bits. If $W = 2^k$, the runtime is $O(n \cdot 2^k)$, which is exponential in the length of the binary representation of $W$.

### Q2: Why is the greedy density algorithm alone insufficient, and why must we compare it against the single best item?
**Answer:** Pure greedy density packing can perform arbitrarily poorly for 0/1 knapsack. Consider budget $W = 100$, Item 1 with weight 1 and value 2 (density 2), and Item 2 with weight 100 and value 100 (density 1). Greedy chooses Item 1 (value 2) and has no remaining capacity for Item 2, achieving only $\frac{2}{100} = 2\%$ of optimal. Comparing the greedy set with the single best feasible item ($\max(2, 100) = 100$) guarantees at least $50\%$ of the optimal value in all cases.

### Q3: Why does Reservoir Sampling guarantee an unbiased $1/n$ probability without knowing $n$ in advance?
**Answer:** The proof relies on telescoping probabilities. The probability that item $i$ enters the reservoir is $\frac{1}{i}$. The probability that it is NOT evicted by item $j$ (where $j > i$) is $1 - \frac{1}{j} = \frac{j-1}{j}$. Multiplying these independent probabilities from $i+1$ to $n$ yields:
$$P(\text{selected}) = \frac{1}{i} \times \left(\frac{i}{i+1} \times \frac{i+1}{i+2} \times \dots \times \frac{n-1}{n}\right) = \frac{1}{n}$$
Each item has an equal $\frac{1}{n}$ probability regardless of corpus stream size.

### Q4: Why doesn't Parallel Search guarantee faster performance on very small corpora?
**Answer:** Concurrency introduces overhead: creating `Callable` tasks, scheduling workers in the thread pool queue, thread context switching, and gathering `Future.get()` results. On a corpus with fewer than 20 documents, this overhead exceeds the microseconds needed to scan the text sequentially. This is why our search engine implements an adaptive **Auto Mode** that automatically executes sequentially for small corpora.

### Q5: How is thread safety and deterministic ordering maintained in the parallel search engine?
**Answer:** Documents are treated as immutable data structures during search. Each worker task returns its own independent match result. Rather than writing into a shared mutable synchronized list, each task writes to a specific index in an array corresponding to its document number. The engine then iterates through the array in order to build the final list, guaranteeing identical result ordering between sequential and parallel executions.

---

## 9. Known Limitations

1. **In-Memory Corpus:** While the Reservoir Sampling algorithm operates with $O(1)$ auxiliary selection space relative to a stream, the current library application pre-loads document metadata into memory at startup for fast UI browsing.
2. **Reading Speed Assumption:** Reading time is estimated based on an average words-per-minute rate (default: 200 WPM). In practice, technical formulas or complex code may require longer comprehension times.
3. **Approximation Bound:** The 1/2-approximation knapsack guarantee applies strictly to numerical priority scores; it does not account for thematic prerequisites between documents.

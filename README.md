# Digital Library Search System

[![B.Tech CSE DSA-3 Capstone](https://img.shields.io/badge/Course-B.Tech%20CSE%20DSA--3-blue.svg)](https://github.com)
[![Java Spring Boot](https://img.shields.io/badge/Backend-Java%2017%2F21%20%7C%20Spring%20Boot%203-brightgreen.svg)](https://spring.io)
[![React Vite](https://img.shields.io/badge/Frontend-React%20%7C%20Vite%20%7C%20Vanilla%20CSS-61dafb.svg)](https://vitejs.dev)
[![Algorithms](https://img.shields.io/badge/Algorithms-KMP%20%7C%20Rabin--Karp%20%7C%20Boyer--Moore%20%7C%20DP-orange.svg)](https://en.wikipedia.org/wiki/String-searching_algorithm)

A full-stack, enterprise-grade Digital Library Search System engineered specifically for **B.Tech Computer Science & Engineering DSA-3 (Data Structures & Algorithms)** academic demonstration, project reviews, and viva examinations.

The platform demonstrates how classical and advanced Data Structures and Algorithms power high-throughput search, typo-tolerant fuzzy matching, document similarity modeling, and citation network analysis across books, research papers, journals, and magazines.

---

## 1. Key Project Highlights

- **Zero Mock Algorithms:** Every algorithm is natively implemented in Java—no third-party search libraries (e.g. Lucene or Elasticsearch) or built-in regex wrappers are used.
- **Academic Review Ready:** Interactive Algorithm Lab visualizes LPS tables, rolling hash calculations, DP cost matrices, bad-character shifts, and parallel benchmarks.
- **Preserved Existing Code:** Seamlessly integrates the user's original `KMP.java`, `LibraryDocument.java`, `LibrarySearchEngine.java`, `CorpusReader.java`, and `Main.java`.
- **Intelligent Strategy Optimization:** Dynamic "Auto" search mode analyzes query length, alphabet entropy, and typo likelihood to pick the optimal algorithm.
- **Embedded & Production Persistence:** Operates out-of-the-box with embedded in-memory H2 database (with web console) and includes standard MySQL 8.x schema DDL.

---

## 2. Implemented DSA Modules & Complexities

| Algorithm | Category | Best Case | Average Case | Worst Case | Space | Real Usage in Digital Library |
|---|---|---|---|---|---|---|
| **Knuth-Morris-Pratt (KMP)** | String Matching | $O(n)$ | $O(n + m)$ | $O(n + m)$ | $O(m)$ | Exact keyword matching in titles, authors, categories, and content without text rollbacks. |
| **Rabin-Karp** | Rolling Hash | $O(n + m)$ | $O(n + m)$ | $O(n \cdot m)$ | $O(1)$ | High-throughput streaming search, rolling hash window updates, and collision checks. |
| **Boyer-Moore** | Sub-linear Search | $O(n / m)$ | $O(n)$ | $O(n \cdot m)$ | $O(\sigma)$ | Sub-linear natural language text search skipping multiple characters via Bad Character rule. |
| **Levenshtein Distance** | Dynamic Programming | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | $O(n \cdot m)$ | Fuzzy search, typographical error detection, and "Did You Mean" recommendations. |
| **Suffix Array & LCP** | Text Indexing | $O(m + \log n)$ | $O(m \log n)$ | $O(m \log n)$ | $O(n)$ | Amortized indexing over archived document collections with binary search retrieval. |
| **TF-IDF + Cosine** | Vector Space Model | $O(V + D)$ | $O(V + D)$ | $O(V + D)$ | $O(V)$ | Thematic vector similarity scoring to recommend "Related Resources". |
| **Parallel Search Engine** | Concurrent Systems | Ideal $O(N/p)$ | $O(N/p)$ | $O(N)$ | $O(p)$ | Multi-core search across partitioned document shards using Java `ExecutorService`. |
| **Reservoir Sampling** | Randomized Algorithm | $O(N)$ | $O(N)$ | $O(N)$ | $O(k)$ | Unbiased uniform random document sampling from streams (Algorithm R, proof $k/N$). |

---

## 3. Technology Stack

### Frontend
- **Framework:** React 18
- **Build Tool:** Vite
- **Routing:** React Router DOM v6
- **HTTP Client:** Axios (Centralized API service)
- **Icons:** Lucide React
- **Styling:** Vanilla CSS with custom modern design system (Glassmorphism, dark aesthetic, responsive grid)

### Backend
- **Language:** Java 17 / 21 / 25
- **Framework:** Spring Boot 3.3.4 (REST Controllers, Services, JPA Entities)
- **Build Tool:** Apache Maven
- **Database:** Embedded H2 Database (with `/h2-console`) & optional MySQL 8.x DDL
- **Testing:** JUnit 5 (Unit tests for all DSA algorithms)

---

## 4. Project Directory Structure

```text
DSA Project-3/
├── backend/
│   ├── pom.xml                               # Maven build configuration
│   └── src/
│       ├── main/
│       │   ├── java/com/library/search/
│       │   │   ├── LibrarySearchApplication.java # Spring Boot main entry point
│       │   │   ├── Main.java                 # Standalone CLI runner (preserved)
│       │   │   ├── LibrarySearchEngine.java  # Legacy search engine class (preserved)
│       │   │   ├── algorithm/
│       │   │   │   ├── KMP.java              # Knuth-Morris-Pratt implementation
│       │   │   │   ├── RabinKarp.java        # Rabin-Karp rolling hash algorithm
│       │   │   │   ├── BoyerMoore.java       # Boyer-Moore bad character algorithm
│       │   │   │   ├── EditDistance.java     # Levenshtein DP & matrix backtracking
│       │   │   │   ├── SuffixArray.java      # Suffix Array & binary search
│       │   │   │   ├── DocumentSimilarity.java # TF-IDF & Cosine Similarity
│       │   │   │   ├── ParallelSearchEngine.java # Multi-core concurrent search
│       │   │   │   ├── RandomizedSampler.java # Reservoir sampling (Algorithm R)
│       │   │   │   └── SearchStrategyOptimizer.java # Auto selection decision engine
│       │   │   ├── config/
│       │   │   │   ├── CorsConfig.java       # CORS configuration for React frontend
│       │   │   │   └── DataInitializer.java  # Database seeder from corpus folder
│       │   │   ├── controller/
│       │   │   │   ├── SearchController.java # GET & POST /api/search
│       │   │   │   ├── AlgorithmController.java # /api/algorithms/* endpoints
│       │   │   │   └── DocumentController.java  # CRUD & similarity endpoints
│       │   │   ├── dto/                      # Data Transfer Objects
│       │   │   ├── model/
│       │   │   │   └── LibraryDocument.java  # JPA Entity & document model
│       │   │   ├── repository/
│       │   │   │   └── DocumentRepository.java # Spring Data JPA repository
│       │   │   └── service/
│       │   │       ├── SearchService.java    # Search orchestration service
│       │   │       ├── DocumentService.java  # Document management service
│       │   │       └── CorpusReader.java     # Corpus file ingestion (preserved)
│       │   └── resources/
│       │       ├── application.properties    # H2 configuration
│       │       └── application-mysql.properties # MySQL profile
│       └── test/
│           └── java/com/library/search/
│               └── AlgorithmTest.java        # JUnit 5 test suite
├── frontend/
│   ├── package.json                          # Frontend dependencies
│   ├── vite.config.js                        # Vite configuration & API proxy
│   ├── index.html                            # HTML entry point with fonts
│   └── src/
│       ├── api/
│       │   └── apiService.js                 # Centralized Axios client
│       ├── components/
│       │   ├── Navbar.jsx                    # Responsive header navigation
│       │   ├── Footer.jsx                    # Footer with course info
│       │   ├── SearchBar.jsx                 # Search input with algorithm picker
│       │   ├── DocumentCard.jsx              # Document & search result card
│       │   ├── AlgorithmBadge.jsx            # Color-coded algorithm badges
│       │   ├── LoadingSpinner.jsx            # Animated loading indicator
│       │   └── ErrorMessage.jsx              # User-friendly error alert banner
│       ├── pages/
│       │   ├── HomePage.jsx                  # Hero dashboard & statistics
│       │   ├── SearchPage.jsx                # Interactive search engine
│       │   ├── DocumentsPage.jsx             # Document catalog & Add modal
│       │   ├── DocumentDetailPage.jsx        # Document details & related items
│       │   ├── AlgorithmsHubPage.jsx         # Algorithm Lab dashboard
│       │   ├── KmpDemoPage.jsx               # KMP interactive visualizer
│       │   ├── RabinKarpDemoPage.jsx         # Rabin-Karp hash visualizer
│       │   ├── BoyerMooreDemoPage.jsx        # Boyer-Moore jump visualizer
│       │   ├── EditDistanceDemoPage.jsx      # DP matrix interactive table
│       │   ├── SuffixArrayDemoPage.jsx       # Suffix array binary search lab
│       │   ├── SimilarityDemoPage.jsx        # TF-IDF cosine similarity tester
│       │   ├── ComparePage.jsx               # Side-by-side benchmark & charts
│       │   ├── ParallelBenchmarkPage.jsx     # Multi-threaded concurrent search
│       │   ├── RandomizedSamplingPage.jsx    # Reservoir sampling demo
│       │   └── AboutPage.jsx                 # Request flow & viva review Q&A
│       ├── App.jsx                           # Route declarations
│       ├── main.jsx                          # React mount point
│       └── index.css                         # CSS design system
├── database/
│   └── schema.sql                            # SQL DDL schema for MySQL / H2
├── corpus/                                   # 20 rich sample documents (.txt)
└── docs/
    └── architecture.md                       # Architectural design documentation
```

---

## 5. Setup & Run Instructions

### Prerequisites
- **Java:** JDK 17, 21, or 25
- **Maven:** Apache Maven 3.8+
- **Node.js:** v18+ (tested on v26.9.0)
- **npm:** v9+

### Step 1: Start the Backend (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

- Backend REST API will start at: `http://localhost:8080/api`
- Embedded H2 Database console is accessible at: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:librarydb`
  - Username: `sa`
  - Password: *(leave blank)*
- On startup, `DataInitializer` automatically reads all 20 `.txt` documents from `corpus/` and seeds the database.

### Step 2: Start the Frontend (React Vite)

Open a new terminal window:

```bash
cd frontend
npm install
npm run dev
```

- Open your browser at: `http://localhost:5173`

### Step 3: Run the Preserved Standalone CLI (Optional)

To test the original command-line interface without launching the web server:

```bash
cd backend
mvn compile exec:java -Dexec.mainClass="com.library.search.Main"
```

### Step 4: Run the Backend Unit Tests

To run the JUnit 5 DSA test suite:

```bash
cd backend
mvn test
```

---

## 6. Frontend Routing Overview

| Route | Component | Purpose |
|---|---|---|
| `/` | `HomePage` | Digital library dashboard, hero section, live stats, search bar with algorithm picker. |
| `/search` | `SearchPage` | Search results, execution time in ms/ns, matched field badges, Did-You-Mean typo alert. |
| `/documents` | `DocumentsPage` | Full catalog, filters by Type (Book, Paper, Journal, Magazine), Category, and Add Document form. |
| `/documents/:id` | `DocumentDetailPage` | Document details, abstract, content preview, related resources (TF-IDF), and citation links. |
| `/algorithms` | `AlgorithmsHubPage` | Central demonstration hub with cards for all implemented algorithms. |
| `/algorithms/kmp` | `KmpDemoPage` | Interactive KMP tester with LPS array table and step trace. |
| `/algorithms/rabin-karp` | `RabinKarpDemoPage` | Rabin-Karp rolling hash tester, hash values, and collision tracker. |
| `/algorithms/boyer-moore` | `BoyerMooreDemoPage` | Boyer-Moore tester with bad character table and right-to-left scan steps. |
| `/algorithms/edit-distance` | `EditDistanceDemoPage` | Levenshtein Dynamic Programming $(n+1) \times (m+1)$ interactive matrix and alignment operations. |
| `/algorithms/suffix-array` | `SuffixArrayDemoPage` | Suffix Array generator, lexicographical suffixes table, and binary search steps. |
| `/algorithms/similarity` | `SimilarityDemoPage` | Document text comparison with TF-IDF and Cosine Similarity percentage. |
| `/algorithms/parallel` | `ParallelBenchmarkPage` | Multi-core search benchmark comparing sequential vs `ExecutorService` parallel search. |
| `/algorithms/randomized` | `RandomizedSamplingPage` | Reservoir Sampling demonstration with mathematical proof of fairness. |
| `/compare` | `ComparePage` | Side-by-side empirical benchmark of multiple algorithms with execution time bar charts. |
| `/about` | `AboutPage` | System architecture, end-to-end request flow diagram, and Viva Review Q&A. |

---

## 7. Key REST API Endpoints

### 1. Document Search
- **Endpoint:** `GET /api/search?q={query}&algorithm={algo}&category={cat}&documentType={type}`
- **Sample Request:** `GET /api/search?q=machine%20learning&algorithm=KMP`
- **Sample Response:**
```json
{
  "query": "machine learning",
  "algorithmUsed": "KMP",
  "selectionRationale": "User explicitly specified KMP algorithm.",
  "executionTimeNanos": 5301042,
  "executionTimeMs": 5.301,
  "totalResults": 3,
  "didYouMean": null,
  "results": [
    {
      "document": {
        "id": 12,
        "title": "Deep Learning",
        "author": "Ian Goodfellow, Yoshua Bengio, Aaron Courville",
        "documentType": "Book",
        "category": "Machine Learning"
      },
      "matchedField": "Title",
      "positions": [5],
      "matchCount": 8,
      "relevanceScore": 38.0,
      "highlightedSnippet": "...representation learning and machine learning in modern digital libraries..."
    }
  ]
}
```

### 2. KMP Demonstration
- **Endpoint:** `POST /api/algorithms/kmp`
- **Request Body:**
```json
{
  "text": "knuth morris pratt string matching algorithm",
  "pattern": "algorithm"
}
```
- **Response:**
```json
{
  "algorithm": "KMP",
  "found": true,
  "positions": [35],
  "comparisons": 44,
  "executionTime": 524125,
  "executionTimeMs": 0.524,
  "lps": [0, 0, 0, 0, 0, 0, 0, 0, 0],
  "timeComplexity": "Preprocessing: O(m), Searching: O(n), Total: O(n + m)",
  "spaceComplexity": "Auxiliary Space: O(m) for LPS array"
}
```

### 3. Edit Distance Dynamic Programming
- **Endpoint:** `POST /api/algorithms/edit-distance`
- **Request Body:**
```json
{
  "text": "algoritm",
  "target": "algorithm"
}
```
- **Response:**
```json
{
  "source": "algoritm",
  "target": "algorithm",
  "distance": 1,
  "similarity": 88.89,
  "dpMatrix": [[0, 1, 2, ...], [1, 0, 1, ...]],
  "operations": [
    { "type": "MATCH", "sourceChar": "a", "targetChar": "a", "cost": 0 },
    { "type": "INSERT", "sourceChar": "-", "targetChar": "h", "cost": 1 }
  ]
}
```

5. **Concurrent Multi-Core Parallel Search:**
   - Query: `search` | Multiplier: `20x`
   - *Outcome:* Evaluates empirical speedup and thread utilization against single-thread execution.

---

## 9. DSA Viva Examination Defense Guide

- **Q: Why is KMP guaranteed linear $O(n + m)$?**
  *A:* The LPS table ensures that the text pointer $i$ never rolls back. Because $i$ increases up to $n$ times and the pattern index $j$ drops at most $n$ times, total comparisons are strictly bounded by $2n$, yielding $O(n + m)$.
- **Q: Can Boyer-Moore be slower than KMP?**
  *A:* Yes. On small alphabets (e.g., binary strings `01010101`) or pathological repetitive patterns, the bad character heuristic yields shifts of only 1, degrading Boyer-Moore towards $O(n \cdot m)$ without the good suffix rule, whereas KMP maintains guaranteed $O(n + m)$.
- **Q: Why does standard Suffix Array search take $O(m \log n)$ instead of $O(\log n)$?**
  *A:* Binary search performs $\log n$ comparisons. However, each comparison compares two strings of up to length $m$. Thus, standard binary search is $O(m \log n)$. With LCP acceleration, it approaches $O(m + \log n)$.

---

## 10. Contributors & License

- **Course:** B.Tech Computer Science & Engineering (DSA-3)
- **Project Domain:** Full-Stack Digital Library Search System Powered by Data Structures & Algorithms
- **License:** MIT License (Open for educational evaluation)

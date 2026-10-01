# Digital Library Search System - Architecture Documentation

## 1. System Overview

The **Digital Library Search System** is a full-stack, enterprise-grade academic search platform engineered specifically for **B.Tech CSE DSA-3 (Data Structures & Algorithms)** demonstration and project defense. 

The system transitions an existing command-line Java text processing tool into a high-performance, responsive web application backed by Spring Boot REST services, JPA/H2 persistence, and a modern React Vite frontend.

---

## 2. Architectural Layers

The application adheres to a clean, multi-tier decoupled architecture:

```
┌────────────────────────────────────────────────────────────────┐
│                    REACT VITE CLIENT (SPA)                     │
│  - React Router DOM (Declarative Routing)                      │
│  - Centralized Axios API Service (apiService.js)               │
│  - Dynamic Visualizers (DP Table, LPS Grid, Suffix Arrays)     │
└───────────────────────────────┬────────────────────────────────┘
                                │ JSON via HTTP REST / CORS
                                ▼
┌────────────────────────────────────────────────────────────────┐
│                 SPRING BOOT REST CONTROLLERS                   │
│  - SearchController       (/api/search)                        │
│  - DocumentController     (/api/documents)                     │
│  - AlgorithmController    (/api/algorithms/*)                  │
└───────────────────────────────┬────────────────────────────────┘
                                │
                                ▼
┌────────────────────────────────────────────────────────────────┐
│                    CORE SERVICE LAYER                          │
│  - SearchService               - DocumentService               │
│  - CorpusReader                                                │
└───────────────────────────────┬────────────────────────────────┘
                                │
        ┌───────────────────────┴────────────────────────┐
        ▼                                                ▼
┌──────────────────────────────┐       ┌─────────────────────────┐
│     DSA ALGORITHM ENGINE     │       │   DATA PERSISTENCE      │
│  - KMP (buildLPS, search)    │       │  - DocumentRepository   │
│  - RabinKarp (Rolling Hash)  │       │  - H2 Embedded Database │
│  - BoyerMoore (Bad Character)│       │  - MySQL 8.x Compatible │
│  - EditDistance (DP Matrix)  │       │  - Raw Corpus .txt Files│
│  - SuffixArray & Binary Index│       └─────────────────────────┘
│  - TF-IDF Cosine Similarity  │
│  - ParallelSearchEngine      │
│  - RandomizedSampler         │
│  - SearchStrategyOptimizer   │
└──────────────────────────────┘
```

---

## 3. End-to-End Request Flow

When an end-user performs a search (e.g., searching `"machine learning"` with `"KMP"`):

1. **User Action (React UI):** User enters query string in `SearchBar.jsx` and clicks "Search".
2. **Client Routing (React Router DOM):** Navigation triggers `/search?q=machine+learning&algorithm=KMP`.
3. **HTTP Transport (Axios):** `searchAPI.search()` sends a GET request to `http://localhost:8080/api/search?q=machine%20learning&algorithm=KMP`.
4. **Endpoint Resolution (Spring Boot Controller):** `SearchController.searchGet()` receives parameters and constructs a `SearchRequest` DTO.
5. **Business Logic & Timing (SearchService):** 
   - Initiates high-resolution nanosecond timer: `long startTime = System.nanoTime()`.
   - Fetches candidate library documents from `DocumentRepository`.
6. **DSA Algorithm Execution:**
   - Calls `KMP.search(document.getContent(), query)` and `KMP.search(document.getTitle(), query)`.
   - `KMP.buildLPS()` computes the pattern's prefix-suffix array.
   - Exact match positions and comparisons are recorded without regex or native library shortcuts.
7. **Ranking & Snippet Extraction:** Matches are scored by field weighting (Title > Keywords > Author > Content) and surrounding context snippets are extracted.
8. **JSON Serialization:** Spring Boot serializes `SearchResponse` containing document cards, execution time, match positions, and rationale.
9. **UI Re-rendering:** React updates state, rendering `DocumentCard` components with highlighted match spans, execution duration badges, and direct detail links.

---

## 4. DSA Algorithm Mapping to Digital Library Modules

| Module | Core Algorithm | Class in Codebase | Real Usage in Library System |
|---|---|---|---|
| **Module 1 & 2** | Knuth-Morris-Pratt (KMP) | `algorithm/KMP.java` | Guaranteed linear-time exact keyword matching across titles, authors, categories, and content. |
| **Module 2** | Rabin-Karp Rolling Hash | `algorithm/RabinKarp.java` | Constant-time sliding window hash comparisons and multi-pattern dictionary verification. |
| **Module 2** | Boyer-Moore | `algorithm/BoyerMoore.java` | Sub-linear search on large natural language texts using right-to-left scan and bad-character shifts. |
| **Module 3** | Levenshtein Edit Distance | `algorithm/EditDistance.java` | Dynamic programming matrix computing minimal edits; powers Fuzzy Search & "Did You Mean" typo correction. |
| **Module 2** | Suffix Array & LCP | `algorithm/SuffixArray.java` | Preprocessed lexicographical suffix index for fast binary search retrieval over archived corpora. |
| **Module 1** | TF-IDF & Cosine Similarity | `algorithm/DocumentSimilarity.java` | Vector space model measuring term frequencies and cosine angles to recommend "Related Resources". |
| **Module 5** | Strategy Optimization | `algorithm/SearchStrategyOptimizer.java` | Dynamically selects the best search algorithm based on pattern length, alphabet entropy, and typo risk. |
| **Module 6** | Parallel Search Engine | `algorithm/ParallelSearchEngine.java` | Multi-core concurrent document search using `ExecutorService` thread pools, evaluated via Amdahl's Law. |
| **Module 6** | Reservoir Sampling | `algorithm/RandomizedSampler.java` | Uniform randomized document selection from streams using Algorithm R with proof of fairness $k/N$. |

---

## 5. Database Schema & Persistence

The backend operates on a flexible persistence architecture:
- **Default (Zero-Config):** Embedded in-memory **H2 Database** (`jdbc:h2:mem:librarydb`) with H2 web console enabled at `/h2-console`.
- **Corpus File Ingestion:** On initial boot, `DataInitializer.java` detects the `corpus/` folder and parses all `.txt` documents using `CorpusReader.java`, persisting records automatically.
- **Production MySQL 8.x:** Provided in `database/schema.sql` and activated via `--spring.profiles.active=mysql`.

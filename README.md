# Digital Library Search & Resource Optimization System
### B.Tech CSE Data Structures & Algorithms (DSA-3) Capstone Project

[![Course Outcome](https://img.shields.io/badge/Syllabus-CO--1%20to%20CO--4%20Aligned-success.svg)](https://github.com)
[![Java Spring Boot](https://img.shields.io/badge/Backend-Java%2017%2F21%20%7C%20Spring%20Boot%203-brightgreen.svg)](https://spring.io)
[![React Vite](https://img.shields.io/badge/Frontend-React%20%7C%20Vite%20%7C%20Vanilla%20CSS-61dafb.svg)](https://vitejs.dev)
[![Algorithms](https://img.shields.io/badge/Algorithms-KMP%20%7C%20Z--Algo%20%7C%20Suffix%20Automaton%20%7C%20Dinic-orange.svg)](https://en.wikipedia.org/wiki/String-searching_algorithm)

An enterprise-grade, full-stack Digital Library System engineered strictly to fulfill **Course Outcomes CO-1 through CO-4** of the B.Tech CSE Data Structures & Algorithms syllabus.

The system combines linear-time string search, suffix automata, advanced polynomial dynamic programming patterns, and network-flow max-flow/min-cut duality to solve real-world digital library challenges across 30 curated volumes from **IEEE Xplore**, **ACM Digital Library**, **Stanford InfoLab**, **MIT OpenCourseWare**, **Springer Nature**, **Oxford Academic**, and **arXiv**.

---

## 1. Executive Defense: "What Have You Improved?" & "What is the Use?"

### What Was Improved?
1. **From Naive Lookups to Strictly Linear & Suffix Indexing (CO-2):**
   - Replaced $O(N \cdot M)$ text scanning with strictly linear $O(N + M)$ algorithms (**KMP** with $\pi$ / LPS array and **Z-Algorithm** with $Z$-box $[L, R]$) with 0 text rollbacks.
   - Integrated **Kasai's $O(N)$ algorithm** for Longest Common Prefix (LCP) array generation on Suffix Arrays for instant phrase duplicate detection.
   - Implemented an intuitive **Suffix Automaton (DAWG)** bounding states to $\le 2N-1$ and answering substring presence queries in strictly $O(M)$ time regardless of corpus size.
2. **From Heuristics to Polynomial Advanced Dynamic Programming (CO-3):**
   - **Interval DP ($O(N^3)$):** Replaces arbitrary search filter execution with optimal boolean query parenthesization trees.
   - **Bitmask DP ($O(2^N \cdot M)$):** Replaces factorial $O(N!)$ brute-force set cover with bit-manipulated state transitions for syllabus topic book bundling.
   - **Tree DP ($O(N)$):** Implements Maximum Weight Independent Set on Dewey Decimal & ACM taxonomy trees via post-order subtree traversal.
   - **Sequence Alignment ($O(N \cdot M)$):** Global Needleman-Wunsch alignment for historical manuscript diffs and academic revision tracking.
3. **From First-Come Starvation to Network Flow & Min-Cut Duality (CO-4):**
   - Implemented **Edmonds-Karp ($O(V \cdot E^2)$)** and **Dinic's Algorithm ($O(V^2 \cdot E)$)** for bipartite student-to-book reservation matching.
   - Leveraged **Max-Flow / Min-Cut Duality** on Digital Library CDN networks to isolate the exact saturated link bottlenecks throttling campus access.
4. **Adaptive Problem Signature Classification (CO-1):**
   - Formally evaluates Shannon entropy $H(S)$, query length ratio, error tolerances, and graph constraints to dynamically dispatch the optimal algorithm.

### What is the Real Use of This Project in Digital Libraries?
Modern digital libraries (e.g. ACM DL, IEEE Xplore, Google Books, PubMed) manage millions of research documents and concurrent student requests. This project implements the core algorithmic infrastructure for:
- **Plagiarism & Duplicate Research Detection:** Via Rabin-Karp rolling hashes and Kasai LCP arrays.
- **Query Engine Optimization:** Via Interval DP multi-facet join trees.
- **Curator Curriculum Topic Bundling:** Via Bitmask DP minimal cost coverage.
- **Resource Reservation & CDN Bandwidth Allocation:** Via Edmonds-Karp/Dinic max-flow with saturated min-cut diagnostics.

---

## 2. Course Outcomes (CO-1 to CO-4) Syllabus Matrix

| Course Outcome | Theoretical Syllabus Requirement | Project Implementation Class | Time Complexity | Space Complexity |
|---|---|---|---|---|
| **CO-1** | Evaluate problem-class signatures (substring search, sequence alignment, network flow, combinatorial optimization) | `ProblemClassSignatureEvaluator.java` | $O(1)$ decision dispatch | $O(1)$ |
| **CO-2** | Linear-time string algorithms (KMP, Z-function, Rabin-Karp with rolling hash) | `KMP.java`, `ZAlgorithm.java`, `RabinKarp.java` | $O(N + M)$ | $O(M)$ or $O(1)$ |
| **CO-2** | Suffix-based structures (suffix array, LCP array via Kasai, suffix automaton at intuition level) | `SuffixArray.java`, `SuffixAutomaton.java` | $O(M \log N)$ or $O(M)$ query | $O(N)$ states |
| **CO-3** | Advanced DP Pattern 1: Interval DP | `IntervalDP.java` (Query Plan Trees) | $O(N^3)$ | $O(N^2)$ |
| **CO-3** | Advanced DP Pattern 2: Bitmask DP | `BitmaskDP.java` (Curator Topic Cover) | $O(2^N \cdot M)$ | $O(2^N)$ |
| **CO-3** | Advanced DP Pattern 3: DP on Trees | `TreeDP.java` (Taxonomy Hierarchy) | $O(N)$ | $O(N)$ |
| **CO-3** | Advanced DP Pattern 4: Sequence Alignment / DP on Subsets | `SequenceAlignmentDP.java` (Needleman-Wunsch) | $O(N \cdot M)$ | $O(N \cdot M)$ |
| **CO-4** | Network-flow algorithms: Ford-Fulkerson with Edmonds-Karp | `EdmondsKarp.java` (BFS Augmenting Paths) | $O(V \cdot E^2)$ | $O(V + E)$ |
| **CO-4** | Network-flow algorithms: Dinic (at intuition level) | `DinicAlgorithm.java` (Level Graph + DFS) | $O(V^2 \cdot E)$ | $O(V + E)$ |
| **CO-4** | Max-Flow / Min-Cut Duality & Capacity Constraints | `MaxFlowMinCutService.java` (Bottleneck Cut) | $O(V \cdot E^2)$ / $O(V^2 \cdot E)$ | $O(V + E)$ |

> **Note on Syllabus Scope:** Topics from CO-5 (NP-completeness, 3-SAT reductions, approximation ratios) and CO-6 (Randomized algorithms like Las Vegas / Monte Carlo, parallel prefix sums) are strictly excluded per instructor requirements.

---

## 3. Technology Stack

- **Backend:** Java 17 / 21 / 27, Spring Boot 3.3.4 (REST Controllers, Services, JPA Entities), Apache Maven 3.9, JUnit 5.
- **Frontend:** React 18, Vite 5, React Router DOM v6, Axios, Lucide React, Vanilla CSS design system (Dark & Light themes, Glassmorphism, Responsive Grid).
- **Database:** In-memory H2 Database (with `/h2-console` web GUI) & optional MySQL 8.x schema DDL.
- **Corpus:** 30 high-impact academic texts from IEEE, ACM, Stanford, MIT, Springer, Oxford, arXiv, Bell Labs.

---

## 4. Running the System Locally

### Step 1: Start the Spring Boot Backend
```bash
cd backend
mvn spring-boot:run
```
*Backend runs at `http://localhost:8080/api`*
*H2 Console accessible at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:digitallibrarydb`, User: `sa`, Password: empty)*

### Step 2: Start the React + Vite Frontend
```bash
cd frontend
npm install
npm run dev
```
*Frontend runs at `http://localhost:5173/`*

---

## 5. Top Examiner Viva Questions & Defense Answers

1. **Why is KMP superior to naive string search?**
   - Naive matching rolls back the text index upon mismatch, causing worst-case $O(N \cdot M)$ runtime. KMP preprocesses the pattern into an LPS ($\pi$) table in $O(M)$ time, ensuring the text pointer never backtracks, guaranteeing strictly linear $O(N + M)$ performance.
2. **What is the Z-box invariant in the Z-Algorithm?**
   - The Z-box maintains $[L, R]$, the rightmost substring matching a prefix of $S = P + '\$' + T$. When current index $i \le R$, if $Z[i - L] < R - i + 1$, the value is directly copied in $O(1)$ without character comparisons.
3. **What is Kasai's algorithm and why is it needed?**
   - Kasai computes the Longest Common Prefix (LCP) array from a Suffix Array in strictly linear $O(N)$ time by exploiting the property that $height \ge height_{prev} - 1$.
4. **How does a Suffix Automaton achieve $O(M)$ query time?**
   - It represents the minimal DFA accepting all substrings. Following state transitions for $M$ characters requires exactly $M$ operations, independent of text length $N$.
5. **State the Max-Flow / Min-Cut Duality Theorem.**
   - In any capacity network, the maximum flow from source $S$ to sink $T$ is mathematically equal to the sum of capacities of edges in the minimum $S-T$ cut. In the library, this identifies the exact saturated channel bottleneck restricting borrowing or download throughput.

---

## 6. Authors & Course Information
- **Course:** B.Tech Computer Science & Engineering — Data Structures & Algorithms 3 (DSA-3)
- **Institution:** KL University (KLH)
- **Author:** Sarvesh Reddy (`SarveshReddy-07`)

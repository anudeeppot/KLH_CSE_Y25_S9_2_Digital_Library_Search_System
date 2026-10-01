import React from 'react';
import { BookOpen, CheckCircle, Award, Target, HelpCircle, Layers, Network, FileText, ArrowRight, Zap } from 'lucide-react';

function SyllabusVivaPage() {
  const vivaQuestions = [
    {
      q: "1. Why is the Knuth-Morris-Pratt (KMP) algorithm superior to naive string matching in a digital library?",
      a: "Naive string matching has a worst-case time complexity of O(N * M) because whenever a mismatch occurs, the text pointer rolls back. In contrast, KMP preprocesses the pattern into an LPS (π) array in O(M) time, allowing the text pointer to advance monotonically without ever rolling back. This guarantees O(N + M) strictly linear time."
    },
    {
      q: "2. How does the Z-Algorithm differ from KMP, and what is the Z-box invariant?",
      a: "Both achieve O(N + M) linear pattern matching. While KMP tracks the longest proper prefix that is also a suffix of prefixes, Z-Algorithm calculates Z[i], the longest substring starting at i that matches a prefix of S = P + '$' + T. The Z-box maintains the interval [L, R] of the rightmost matched segment, enabling O(1) direct value copies when inside the boundary."
    },
    {
      q: "3. What is Kasai's algorithm and why is it used alongside the Suffix Array?",
      a: "A Suffix Array provides lexicographically sorted suffix indices, allowing binary search pattern queries in O(M log N). Kasai's algorithm computes the Longest Common Prefix (LCP) array in linear O(N) time by exploiting the property that height drops by at most 1 between consecutive suffix ranks (h >= h_prev - 1). This allows instantaneous longest repeated phrase detection across library texts."
    },
    {
      q: "4. What is a Suffix Automaton (DAWG) and what are its space and query bounds?",
      a: "A Suffix Automaton is the minimal deterministic finite state automaton recognizing all substrings of a string. For a text of length N, it bounds states to at most 2N-1 and transitions to at most 3N-4. It can verify whether an arbitrary query of length M is a valid substring in strictly O(M) time, completely independent of the text size N!"
    },
    {
      q: "5. How does Interval DP optimize digital library boolean query plans?",
      a: "In multi-facet searches involving multiple filter joins (e.g. Title ⊗ Author ⊗ Year ⊗ Category), merging adjacent intermediate filter result sets in the wrong sequence creates combinatorial overhead. Interval DP computes the optimal parenthesization tree using recurrence DP[i][j] = min_{i<=k<j} (DP[i][k] + DP[k+1][j] + cost(i, k, j)) in O(N^3) time."
    },
    {
      q: "6. Why is Bitmask DP suited for the Library Curator's syllabus coverage problem?",
      a: "Selecting the minimum cost subset of books covering N required academic competencies is the Set Cover problem (NP-hard). When N <= 20, bitmask DP represents each topic state as an integer bit (0 to 2^N - 1), reducing exponential O(N!) factorial brute force to O(2^N * M) polynomial-exponential time."
    },
    {
      q: "7. Explain the recurrence relation for Dynamic Programming on Trees (Tree DP).",
      a: "Tree DP solves Maximum Weight Independent Set on hierarchical taxonomies (Dewey/ACM categories) in strictly linear O(N) time. Recurrence: DP[u][0] = sum_{v in children(u)} max(DP[v][0], DP[v][1]) (when u is excluded) and DP[u][1] = weight[u] + sum_{v in children(u)} DP[v][0] (when u is included, children cannot be chosen)."
    },
    {
      q: "8. Explain the Max-Flow / Min-Cut Duality Theorem in the context of the Digital Library.",
      a: "The Max-Flow Min-Cut theorem states that in any flow network, the maximum flow from source S to sink T is exactly equal to the total capacity of the minimum cut separating S from T. In our digital library, this proves that the maximum rate of document downloads or student book borrowings is precisely restricted by the capacity of the saturated bottleneck edges."
    },
    {
      q: "9. How does Dinic's algorithm improve upon Edmonds-Karp?",
      a: "Edmonds-Karp finds single shortest augmenting paths via BFS in O(V * E^2) time. Dinic builds a BFS Level Graph (layered DAG) and pushes multiple blocking flows using DFS in a single phase, achieving O(V^2 * E) general complexity. On unit-capacity networks (like bipartite student-book reservation matching), Dinic runs in O(E * sqrt(V)) time."
    },
    {
      q: "10. How does the Problem-Class Signature Evaluator (CO-1) classify incoming workloads?",
      a: "It computes Shannon entropy H(S), pattern-to-text ratio, typo tolerance, bipartite flow constraints, and topological tree structures to automatically select the optimal algorithm: KMP for low-entropy repeating prefixes, Boyer-Moore for high-entropy natural text, Suffix Structures for recurring corpus queries, Sequence Alignment for typos, and Network Flow for capacity constraints."
    }
  ];

  return (
    <div className="container" style={{ padding: '2rem 1rem' }}>
      {/* Header */}
      <div style={{ marginBottom: '2.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
          <span className="badge badge-success">Academic Viva Defense</span>
          <span style={{ color: 'var(--text-muted)' }}>B.Tech CSE DSA-3 Capstone Examination</span>
        </div>
        <h1 style={{ fontSize: '2.3rem', fontWeight: 800, margin: 0 }}>
          Syllabus Alignment, Improvements &amp; Viva Defense Master Guide
        </h1>
        <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem', maxWidth: '850px' }}>
          This master guide provides direct, mathematically rigorous answers to the critical viva examination questions: <strong>"What have you improved?"</strong> and <strong>"What is the practical use of this project in real-world digital libraries?"</strong>
        </p>
      </div>

      {/* SECTION 1: WHAT HAVE YOU IMPROVED? */}
      <div className="card" style={{ padding: '2rem', marginBottom: '2.5rem', borderLeft: '5px solid var(--primary)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
          <Zap size={24} color="var(--primary)" />
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800, margin: 0 }}>
            1. What Have You Improved? (Executive Defense Answer)
          </h2>
        </div>
        <p style={{ color: 'var(--text-secondary)', lineHeight: '1.6', marginBottom: '1.5rem' }}>
          We transformed a basic search prototype into an <strong>enterprise-grade algorithmic Digital Library Engine</strong> strictly aligned with Course Outcomes CO-1 through CO-4. Every naive or heuristic approach was replaced with formal, optimal data structures:
        </p>

        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.92rem' }}>
            <thead>
              <tr style={{ background: 'var(--bg-tertiary)', borderBottom: '2px solid var(--border)' }}>
                <th style={{ padding: '0.85rem' }}>Core Module</th>
                <th style={{ padding: '0.85rem' }}>Old / Naive Approach</th>
                <th style={{ padding: '0.85rem' }}>Our Engineered Improvement</th>
                <th style={{ padding: '0.85rem' }}>Mathematical Speedup</th>
              </tr>
            </thead>
            <tbody>
              <tr style={{ borderBottom: '1px solid var(--border)' }}>
                <td style={{ padding: '0.85rem', fontWeight: 700 }}>Strategy Selection (CO-1)</td>
                <td style={{ padding: '0.85rem', color: 'var(--danger)' }}>Hardcoded trial-and-error</td>
                <td style={{ padding: '0.85rem', color: 'var(--success)', fontWeight: 600 }}>
                  Formal Problem-Class Signature Evaluator analyzing Shannon Entropy, text ratio, and state cardinality
                </td>
                <td style={{ padding: '0.85rem', fontFamily: 'monospace' }}>Adaptive O(1) dispatch</td>
              </tr>
              <tr style={{ borderBottom: '1px solid var(--border)' }}>
                <td style={{ padding: '0.85rem', fontWeight: 700 }}>String Matching (CO-2)</td>
                <td style={{ padding: '0.85rem', color: 'var(--danger)' }}>Naive O(N * M) text scans with rollbacks</td>
                <td style={{ padding: '0.85rem', color: 'var(--success)', fontWeight: 600 }}>
                  KMP (π / LPS array) &amp; Z-Algorithm (Z-box [L, R]) with zero rollback
                </td>
                <td style={{ padding: '0.85rem', fontFamily: 'monospace' }}>O(N + M) strictly linear</td>
              </tr>
              <tr style={{ borderBottom: '1px solid var(--border)' }}>
                <td style={{ padding: '0.85rem', fontWeight: 700 }}>Corpus Indexing (CO-2)</td>
                <td style={{ padding: '0.85rem', color: 'var(--danger)' }}>Full-text scan per query</td>
                <td style={{ padding: '0.85rem', color: 'var(--success)', fontWeight: 600 }}>
                  Suffix Array + Kasai's O(N) LCP Array &amp; Suffix Automaton (DAWG)
                </td>
                <td style={{ padding: '0.85rem', fontFamily: 'monospace' }}>Query in O(M log N) or O(M)</td>
              </tr>
              <tr style={{ borderBottom: '1px solid var(--border)' }}>
                <td style={{ padding: '0.85rem', fontWeight: 700 }}>Combinatorial DP (CO-3)</td>
                <td style={{ padding: '0.85rem', color: 'var(--danger)' }}>Exhaustive brute force or greedy picks</td>
                <td style={{ padding: '0.85rem', color: 'var(--success)', fontWeight: 600 }}>
                  Interval DP (O(N^3) query trees), Bitmask DP (O(2^N * M) topic cover), Tree DP (O(N) taxonomy)
                </td>
                <td style={{ padding: '0.85rem', fontFamily: 'monospace' }}>Polynomial-time bounds</td>
              </tr>
              <tr>
                <td style={{ padding: '0.85rem', fontWeight: 700 }}>Resource Flow (CO-4)</td>
                <td style={{ padding: '0.85rem', color: 'var(--danger)' }}>First-come first-served starvation</td>
                <td style={{ padding: '0.85rem', color: 'var(--success)', fontWeight: 600 }}>
                  Max-Flow (Edmonds-Karp &amp; Dinic) with Min-Cut saturated bottleneck diagnosis
                </td>
                <td style={{ padding: '0.85rem', fontFamily: 'monospace' }}>O(V E^2) / O(V^2 E) optimal</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      {/* SECTION 2: WHAT IS THE USE OF THIS PROJECT? */}
      <div className="card" style={{ padding: '2rem', marginBottom: '2.5rem', borderLeft: '5px solid var(--success)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
          <Target size={24} color="var(--success)" />
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800, margin: 0 }}>
            2. What is the Use of This Project in Real-World Digital Libraries?
          </h2>
        </div>
        <p style={{ color: 'var(--text-secondary)', lineHeight: '1.6', marginBottom: '1.5rem' }}>
          Real-world digital repositories like <strong>IEEE Xplore</strong>, <strong>ACM Digital Library</strong>, <strong>PubMed Central</strong>, and <strong>Stanford InfoLab</strong> process petabytes of text and millions of concurrent student queries. This project implements the core algorithmic infrastructure powering these systems:
        </p>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.25rem' }}>
          <div style={{ background: 'var(--bg-tertiary)', padding: '1.25rem', borderRadius: '8px' }}>
            <h4 style={{ color: 'var(--primary)', margin: '0 0 0.5rem 0', fontWeight: 700 }}>
              1. Plagiarism &amp; Duplicate Research Detection
            </h4>
            <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)', margin: 0 }}>
              Using Rabin-Karp polynomial rolling hash fingerprints and Suffix Array Kasai LCP arrays, the system identifies identical and paraphrased passages across submitted academic papers in linear time.
            </p>
          </div>

          <div style={{ background: 'var(--bg-tertiary)', padding: '1.25rem', borderRadius: '8px' }}>
            <h4 style={{ color: 'var(--primary)', margin: '0 0 0.5rem 0', fontWeight: 700 }}>
              2. Intelligent Query Execution Optimization
            </h4>
            <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)', margin: 0 }}>
              Multi-facet boolean search expressions (keywords, publication dates, author facets) are optimized using Interval DP to find the parenthesization tree that minimizes intermediate database scan cardinality.
            </p>
          </div>

          <div style={{ background: 'var(--bg-tertiary)', padding: '1.25rem', borderRadius: '8px' }}>
            <h4 style={{ color: 'var(--primary)', margin: '0 0 0.5rem 0', fontWeight: 700 }}>
              3. Curriculum &amp; Syllabus Topic Bundling
            </h4>
            <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)', margin: 0 }}>
              Using Bitmask DP, academic curators find the optimal minimal-cost collection of library volumes that covers all required syllabus competencies without purchasing redundant book licenses.
            </p>
          </div>

          <div style={{ background: 'var(--bg-tertiary)', padding: '1.25rem', borderRadius: '8px' }}>
            <h4 style={{ color: 'var(--primary)', margin: '0 0 0.5rem 0', fontWeight: 700 }}>
              4. CDN Bandwidth &amp; Multi-Tenant Allocation
            </h4>
            <p style={{ fontSize: '0.88rem', color: 'var(--primary)', margin: 0 }}>
              Edmonds-Karp and Dinic flow algorithms model multi-tenant student e-book license contention and university CDN bandwidth distribution, isolating the exact network bottlenecks via the Min-Cut theorem.
            </p>
          </div>
        </div>
      </div>

      {/* SECTION 3: SYLLABUS MAPPING */}
      <div className="card" style={{ padding: '2rem', marginBottom: '2.5rem' }}>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '1.25rem' }}>
          3. Syllabus Course Outcomes (CO-1 to CO-4) Mapping
        </h2>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div style={{ padding: '1rem', border: '1px solid var(--border)', borderRadius: '8px' }}>
            <span className="badge badge-primary">CO-1</span>
            <strong style={{ marginLeft: '0.75rem', fontSize: '1.05rem' }}>
              Problem-Class Signatures &amp; Advanced Strategy Selection
            </strong>
            <p style={{ margin: '0.5rem 0 0 0', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Implemented in <code>ProblemClassSignatureEvaluator.java</code>. Inspects Shannon entropy H(S), pattern ratio |P|/|T|, typo tolerance, and constraint graphs to dispatch between Substring Search, Sequence Alignment, Network Flow, or Combinatorial Optimization.
            </p>
          </div>

          <div style={{ padding: '1rem', border: '1px solid var(--border)', borderRadius: '8px' }}>
            <span className="badge badge-primary">CO-2</span>
            <strong style={{ marginLeft: '0.75rem', fontSize: '1.05rem' }}>
              Linear-Time String Algorithms &amp; Suffix-Based Structures
            </strong>
            <p style={{ margin: '0.5rem 0 0 0', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Implemented in <code>KMP.java</code> (LPS π-table, O(N+M)), <code>ZAlgorithm.java</code> (Z-box [L, R], O(N+M)), <code>RabinKarp.java</code> (rolling hash mod 10^9+7), <code>SuffixArray.java</code> (Kasai's O(N) LCP), and <code>SuffixAutomaton.java</code> (DAWG DFA with O(M) queries).
            </p>
          </div>

          <div style={{ padding: '1rem', border: '1px solid var(--border)', borderRadius: '8px' }}>
            <span className="badge badge-primary">CO-3</span>
            <strong style={{ marginLeft: '0.75rem', fontSize: '1.05rem' }}>
              Advanced Dynamic Programming Patterns
            </strong>
            <p style={{ margin: '0.5rem 0 0 0', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Implemented in <code>IntervalDP.java</code> (O(N^3) query parenthesization), <code>BitmaskDP.java</code> (O(2^N * M) syllabus coverage), <code>TreeDP.java</code> (O(N) post-order taxonomy independent set), and <code>SequenceAlignmentDP.java</code> (Needleman-Wunsch manuscript diff).
            </p>
          </div>

          <div style={{ padding: '1rem', border: '1px solid var(--border)', borderRadius: '8px' }}>
            <span className="badge badge-primary">CO-4</span>
            <strong style={{ marginLeft: '0.75rem', fontSize: '1.05rem' }}>
              Network Flow Algorithms &amp; Max-Flow / Min-Cut Duality
            </strong>
            <p style={{ margin: '0.5rem 0 0 0', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Implemented in <code>EdmondsKarp.java</code> (O(V E^2) BFS augmenting paths), <code>DinicAlgorithm.java</code> (O(V^2 E) level graphs + blocking flows), and <code>MaxFlowMinCutService.java</code> (Max-Flow Min-Cut Theorem verification, student reservation matching, CDN distribution).
            </p>
          </div>
        </div>
      </div>

      {/* SECTION 4: TOP 10 VIVA QUESTIONS & ANSWERS */}
      <div className="card" style={{ padding: '2rem' }}>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '1.25rem' }}>
          4. Top Examiner Viva Questions &amp; Model Defense Answers
        </h2>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {vivaQuestions.map((item, idx) => (
            <div key={idx} style={{ padding: '1.25rem', background: 'var(--bg-secondary)', borderRadius: '8px', borderLeft: '4px solid var(--primary)' }}>
              <div style={{ fontWeight: 700, fontSize: '1rem', color: 'var(--text-primary)', marginBottom: '0.5rem' }}>
                {item.q}
              </div>
              <div style={{ fontSize: '0.92rem', color: 'var(--text-secondary)', lineHeight: '1.5' }}>
                {item.a}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default SyllabusVivaPage;

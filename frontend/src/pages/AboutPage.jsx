import React, { useState } from 'react';
import {
  BookOpen,
  Cpu,
  Layers,
  HelpCircle,
  ChevronDown,
  ChevronUp,
  ShieldCheck,
  Code2,
  Terminal,
  Database,
  ArrowRight
} from 'lucide-react';

const AboutPage = () => {
  const [openFaq, setOpenFaq] = useState(0);

  const vivaQuestions = [
    {
      q: 'How does KMP guarantee linear O(n + m) time complexity without backtracking?',
      a: 'In a naive pattern search, every character mismatch forces the text pointer i to reset back to i - j + 1, causing O(n * m) comparisons in the worst case. KMP preprocesses the pattern into a Longest Proper Prefix which is also a Suffix (LPS) array in O(m) time. When a mismatch occurs at pattern index j, the text index i never rolls back; instead, j jumps to lps[j - 1]. Because i monotonically increases from 0 to n and j decreases at most as many times as it increased, the total number of character inspections is strictly bounded by O(n + m).'
    },
    {
      q: 'How is rolling hash computed in Rabin-Karp in constant O(1) time?',
      a: 'Rabin-Karp treats strings as polynomial numbers in base d (e.g. 256) modulo a prime q (e.g. 1000000007). To slide the window from text[i..i+m-1] to text[i+1..i+m], the algorithm subtracts the high-order term (text[i] * d^(m-1)), multiplies the entire remaining sum by d, and adds the new incoming character text[i+m]. In modular arithmetic: hash(i+1) = (d * (hash(i) - text[i]*h) + text[i+m]) % q. This update requires only 3 arithmetic operations, achieving O(1) sliding window hash calculation.'
    },
    {
      q: 'What is the role of Collision Verification in Rabin-Karp?',
      a: 'Because the set of possible strings of length m is vastly larger than the prime modulus q (by the Pigeonhole Principle), two completely different strings can yield identical hash values (called a spurious hit or hash collision). Therefore, whenever textHash == patternHash, the algorithm must verify character-by-character in O(m) time. If collisions are rare (with large q), the average search time is O(n + m). If every window collides, the algorithm degrades to O(n * m).'
    },
    {
      q: 'Why can Boyer-Moore achieve sublinear O(n / m) search speed on natural language?',
      a: 'Boyer-Moore matches characters from right to left within the pattern window. When a mismatch occurs on a text character c that does not appear anywhere in the pattern, the entire pattern can immediately skip forward by its full length m without inspecting any of the intervening characters. On large alphabets (e.g., natural language text), such skips occur frequently, allowing the algorithm to inspect only a fraction of the text characters (approx. n / m comparisons).'
    },
    {
      q: 'How is Dynamic Programming applied to Edit Distance for Fuzzy Search?',
      a: 'Levenshtein Edit Distance constructs a 2D matrix dp[m+1][n+1]. Cell dp[i][j] stores the minimum edits to convert source[0..i-1] into target[0..j-1]. If the current characters match, dp[i][j] = dp[i-1][j-1]. Otherwise, it takes 1 + min(dp[i-1][j] for deletion, dp[i][j-1] for insertion, dp[i-1][j-1] for substitution). In our Digital Library, if a user enters a query with typographical errors (e.g., "machne"), the system tests against library catalog tokens, finds words within edit distance <= 2, and displays "Did you mean: machine learning?".'
    },
    {
      q: 'Why do Suffix Arrays take O(m * log n) time for binary search retrieval?',
      a: 'A Suffix Array stores the starting indices of all n suffixes of a text sorted lexicographically. Finding occurrences of a pattern of length m is reduced to finding the range of suffixes that begin with that pattern. Binary search requires O(log n) probing steps. However, at each step, comparing the pattern with the text suffix requires up to m character comparisons. Hence, the standard binary search takes O(m * log n) comparisons. Using Longest Common Prefix (LCP) information, this can be accelerated towards O(m + log n).'
    },
    {
      q: 'How does Multi-Core Parallel Search improve throughput across document shards?',
      a: 'ParallelSearchEngine utilizes a fixed thread pool managed by Java ExecutorService, partitioning the document corpus into balanced subsets across available CPU cores. Each worker thread runs string matching independently on its assigned shard. Results are aggregated concurrently via thread-safe Futures. Under high document volume, this achieves empirical sublinear speedup approaching Amdahl\'s Law theoretical limit.'
    },
    {
      q: 'How does the "Auto" Algorithm Selection work in SearchService?',
      a: 'SearchStrategyOptimizer evaluates query characteristics: pattern length, alphabet entropy (unique character ratio), total corpus size, and typo probability. For short or highly repetitive patterns, it selects KMP (guaranteed linear time). For longer patterns in natural language with high alphabet entropy, it selects Boyer-Moore (sublinear jump advantage). For queries with suspected typos or multiple words, it activates Edit Distance Fuzzy Search. For static indexed corpora with recurring lookups, it selects Suffix Arrays.'
    },
    {
      q: 'What is Reservoir Sampling and how does it guarantee fairness?',
      a: 'Reservoir Sampling (Algorithm R) chooses k items from a stream of size N in a single O(N) pass. It places the first k items into the reservoir. For each subsequent item i > k, it generates a random index j in [0, i]. If j < k, the item replaces reservoir[j]. By induction, the probability that any specific item is chosen and survives all subsequent replacements telescopes to exactly k / N, guaranteeing an unbiased uniform sample.'
    },
    {
      q: 'How does TF-IDF and Cosine Similarity determine related documents?',
      a: 'Term Frequency (TF) measures the frequency of a word in a document. Inverse Document Frequency (IDF) discounts ubiquitous words across all documents. Each document is represented as a high-dimensional vector of TF-IDF weights. Cosine Similarity calculates the cosine of the angle between two document vectors: cos(θ) = (A · B) / (||A|| * ||B||). A higher cosine value (near 1.0 or 100%) indicates strong conceptual overlap, enabling the Digital Library to recommend closely related resources.'
    }
  ];

  return (
    <div>
      {/* Header */}
      <div style={{ textAlign: 'center', maxWidth: '850px', margin: '0 auto 3rem' }}>
        <div style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '0.5rem',
          background: 'rgba(56, 189, 248, 0.1)',
          border: '1px solid rgba(56, 189, 248, 0.3)',
          borderRadius: '9999px',
          padding: '0.4rem 1rem',
          fontSize: '0.85rem',
          color: 'var(--accent-cyan)',
          fontWeight: 600,
          marginBottom: '1rem',
        }}>
          <ShieldCheck size={16} /> B.Tech CSE DSA-3 Capstone Review Documentation
        </div>
        <h1 style={{ fontSize: '2.5rem', marginBottom: '0.75rem' }}>
          System Architecture <span className="text-gradient">& Viva Review Guide</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.05rem', lineHeight: 1.6 }}>
          Comprehensive architectural breakdown, end-to-end request flow, and professor viva examination answers.
        </p>
      </div>

      {/* Complete Request Flow Diagram Card */}
      <section className="glass-card" style={{ marginBottom: '3rem', borderLeft: '4px solid var(--accent-cyan)' }}>
        <h2 style={{ fontSize: '1.4rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Layers size={22} color="var(--accent-cyan)" /> End-to-End System Request Flow
        </h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.92rem', marginBottom: '1.5rem', lineHeight: 1.6 }}>
          Every query entered on the frontend travels through a disciplined, layered full-stack pipeline from React to Java Spring Boot and the dedicated DSA algorithm modules:
        </p>

        <div className="visualizer-box" style={{ color: '#cbd5e1', lineHeight: 1.8, fontSize: '0.88rem' }}>
{`React UI (User Enters Query "machine learning", Selects Algorithm "KMP")
    ↓
React Router DOM (Routes to /search?q=machine+learning&algorithm=KMP)
    ↓
Centralized Axios Service (searchAPI.search() dispatches HTTP GET to backend)
    ↓
Spring Boot REST Controller (SearchController.java @GetMapping)
    ↓
Search Service (SearchService.java evaluates algorithm & initiates timer System.nanoTime())
    ↓
DSA Algorithm Engine (KMP.java / RabinKarp.java / BoyerMoore.java / EditDistance.java)
    ↓
Corpus / H2 Repository (Scans Title, Author, Category, Keywords, and Content text)
    ↓
Match Extraction & Ranking (Collects exact match positions, relevance score, snippet)
    ↓
JSON Response Payload (Returns matching documents, executionTimeMs, matchCount, rationale)
    ↓
React UI Re-Render (Displays DocumentCards with highlighted match spans, time, badges)`}
        </div>
      </section>

      {/* Project Viva Q&A Accordion */}
      <section style={{ marginBottom: '3.5rem' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h2 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>
            DSA-3 Viva & Project Review Questions
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
            Click each question to view the rigorous theoretical explanation and mathematical defense.
          </p>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {vivaQuestions.map((item, idx) => {
            const isOpen = openFaq === idx;
            return (
              <div
                key={idx}
                className="glass-card"
                style={{
                  cursor: 'pointer',
                  borderColor: isOpen ? 'var(--border-accent)' : undefined,
                  transition: 'all 0.2s ease',
                }}
                onClick={() => setOpenFaq(isOpen ? -1 : idx)}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '1rem' }}>
                  <h3 style={{ fontSize: '1.05rem', color: isOpen ? 'var(--accent-cyan)' : 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                    <HelpCircle size={18} color={isOpen ? 'var(--accent-cyan)' : 'var(--text-muted)'} style={{ flexShrink: 0 }} />
                    {item.q}
                  </h3>
                  {isOpen ? <ChevronUp size={20} color="var(--accent-cyan)" /> : <ChevronDown size={20} color="var(--text-muted)" />}
                </div>

                {isOpen && (
                  <div style={{
                    marginTop: '1rem',
                    paddingTop: '1rem',
                    borderTop: '1px solid var(--border-subtle)',
                    color: 'var(--text-secondary)',
                    fontSize: '0.92rem',
                    lineHeight: 1.7,
                  }}>
                    {item.a}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </section>

      {/* Common Errors & Fixes Guide */}
      <section className="glass-card" style={{ marginBottom: '3rem', borderLeft: '4px solid var(--accent-amber)' }}>
        <h2 style={{ fontSize: '1.4rem', marginBottom: '1rem' }}>
          Common Errors, Debugging & Project Review Fixes
        </h2>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
          <div>
            <strong style={{ color: '#fff' }}>1. CORS Policy Error (Access-Control-Allow-Origin):</strong>
            <p>Fixed via <code>CorsConfig.java</code> registering <code>allowedOriginPatterns("*")</code> on Spring Boot.</p>
          </div>
          <div>
            <strong style={{ color: '#fff' }}>2. Empty Search Query:</strong>
            <p>Backend validates input and gracefully returns a structured empty response with 0 execution time instead of throwing exceptions.</p>
          </div>
          <div>
            <strong style={{ color: '#fff' }}>3. Missing Corpus Files:</strong>
            <p><code>DataInitializer.java</code> detects if corpus directory is relative to <code>../corpus</code> or <code>corpus/</code>, falling back to foundational seeded records if no directory is located.</p>
          </div>
          <div>
            <strong style={{ color: '#fff' }}>4. Single Algorithm Universality Fallacy:</strong>
            <p>During project evaluation, never claim an algorithm is universally fastest. Defend performance differences by citing alphabet size, pattern repetition, and whether indexing preprocessing is permitted.</p>
          </div>
        </div>
      </section>
    </div>
  );
};

export default AboutPage;

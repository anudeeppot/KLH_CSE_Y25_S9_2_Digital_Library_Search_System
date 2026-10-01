import React from 'react';
import { Link } from 'react-router-dom';
import {
  Cpu,
  Layers,
  FileCode,
  Binary,
  BarChart2,
  Zap,
  ArrowRight,
  ShieldCheck,
  Network,
  AlignLeft,
  Sparkles,
  GitBranch
} from 'lucide-react';

const AlgorithmsHubPage = () => {
  const coModules = [
    {
      co: 'CO-1',
      title: 'Problem-Class Signature Evaluator',
      desc: 'Evaluates problem signatures (entropy, text ratio, state cardinality, network flows) and dynamically selects the mathematically optimal algorithm strategy.',
      timeComplexity: 'O(1) Dispatch',
      spaceComplexity: 'O(1)',
      link: '/algorithms/signature',
      badgeClass: 'badge-primary',
      icon: <Sparkles size={20} color="var(--primary)" />,
      tags: ['Shannon Entropy', 'Decision Matrix', 'Problem Signatures'],
    },
    {
      co: 'CO-2',
      title: 'Knuth-Morris-Pratt (KMP)',
      desc: 'Linear-time exact pattern matching preprocessing the pattern into an LPS (π) array with zero text rollback.',
      timeComplexity: 'O(N + M)',
      spaceComplexity: 'O(M)',
      link: '/algorithms/kmp',
      badgeClass: 'badge-kmp',
      icon: <Cpu size={20} color="var(--accent-cyan)" />,
      tags: ['LPS Array', 'Zero Rollback', 'String Matching'],
    },
    {
      co: 'CO-2',
      title: 'Z-Algorithm (Z-Function)',
      desc: 'Computes the Z-box [L, R] array in linear O(N + M) time, finding all occurrences where Z[i] == |P|.',
      timeComplexity: 'O(N + M)',
      spaceComplexity: 'O(N + M)',
      link: '/algorithms/z-algorithm',
      badgeClass: 'badge-kmp',
      icon: <Zap size={20} color="var(--accent-indigo)" />,
      tags: ['Z-box Invariant', 'Prefix Matching', 'Linear Scan'],
    },
    {
      co: 'CO-2',
      title: 'Rabin-Karp Rolling Hash',
      desc: 'Polynomial rolling hash modulo 10^9+7 for multi-pattern document scanning and plagiarism detection.',
      timeComplexity: 'O(N + M)',
      spaceComplexity: 'O(1)',
      link: '/algorithms/rabin-karp',
      badgeClass: 'badge-rabin',
      icon: <FileCode size={20} color="var(--accent-purple)" />,
      tags: ['Rolling Hash', 'Collision Handling', 'Fingerprinting'],
    },
    {
      co: 'CO-2',
      title: 'Suffix Array & Kasai LCP',
      desc: 'Lexicographically sorted suffix index with Kasai linear O(N) LCP computation and binary search retrieval.',
      timeComplexity: 'O(M log N)',
      spaceComplexity: 'O(N)',
      link: '/algorithms/suffix-array',
      badgeClass: 'badge-suffix',
      icon: <Layers size={20} color="var(--accent-amber)" />,
      tags: ['Kasai Algorithm', 'LCP Array', 'Binary Search'],
    },
    {
      co: 'CO-2',
      title: 'Suffix Automaton (DAWG)',
      desc: 'Minimal deterministic finite automaton (DFA) accepting all substrings with <= 2N-1 states in O(M) query time.',
      timeComplexity: 'O(M) Query',
      spaceComplexity: 'O(N) States',
      link: '/algorithms/suffix-automaton',
      badgeClass: 'badge-suffix',
      icon: <Binary size={20} color="var(--accent-emerald)" />,
      tags: ['Minimal DFA', 'Suffix Links', 'O(M) Acceptance'],
    },
    {
      co: 'CO-3',
      title: 'Advanced Dynamic Programming Suite',
      desc: 'All 4 syllabus DP patterns: Interval DP (query trees), Bitmask DP (topic cover), Tree DP (Dewey taxonomy), Sequence Alignment.',
      timeComplexity: 'O(N^3), O(2^N M), O(N)',
      spaceComplexity: 'O(N^2), O(2^N), O(N)',
      link: '/algorithms/advanced-dp',
      badgeClass: 'badge-primary',
      icon: <GitBranch size={20} color="var(--accent-rose)" />,
      tags: ['Interval DP', 'Bitmask DP', 'Tree DP', 'Sequence Alignment'],
    },
    {
      co: 'CO-4',
      title: 'Network Flow & Min-Cut Analyzer',
      desc: 'Edmonds-Karp and Dinic flow algorithms with Max-Flow / Min-Cut Duality for book reservations & CDN bandwidth.',
      timeComplexity: 'O(V E^2) / O(V^2 E)',
      spaceComplexity: 'O(V + E)',
      link: '/algorithms/network-flow',
      badgeClass: 'badge-success',
      icon: <Network size={20} color="var(--success)" />,
      tags: ['Edmonds-Karp', 'Dinic Algorithm', 'Min-Cut Duality'],
    },
  ];

  return (
    <div className="container" style={{ padding: '2rem 1rem' }}>
      {/* Header */}
      <div style={{ textAlign: 'center', maxWidth: '850px', margin: '0 auto 3rem' }}>
        <div style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '0.5rem',
          background: 'rgba(99, 102, 241, 0.1)',
          border: '1px solid rgba(99, 102, 241, 0.3)',
          borderRadius: '9999px',
          padding: '0.4rem 1.25rem',
          fontSize: '0.85rem',
          color: 'var(--primary)',
          fontWeight: 600,
          marginBottom: '1rem',
        }}>
          <ShieldCheck size={16} /> B.Tech CSE DSA-3 Capstone Examination
        </div>
        <h1 style={{ fontSize: '2.5rem', fontWeight: 800, margin: '0 0 0.75rem 0' }}>
          Data Structures &amp; <span className="text-gradient">Algorithms Lab</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.05rem', lineHeight: 1.6 }}>
          Explore interactive visualizations, step-by-step trace executions, mathematical recurrences, and native Java implementations covering Course Outcomes CO-1 through CO-4.
        </p>
      </div>

      {/* Grid of Algorithms */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.5rem', marginBottom: '3.5rem' }}>
        {coModules.map((algo, idx) => (
          <div key={idx} className="card" style={{ display: 'flex', flexDirection: 'column', padding: '1.5rem', border: '1px solid var(--border)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
              <span className={`badge ${algo.badgeClass}`}>{algo.co}</span>
              <span style={{ fontSize: '0.8rem', fontFamily: 'monospace', color: 'var(--primary)' }}>
                {algo.timeComplexity}
              </span>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
              {algo.icon}
              <h3 style={{ fontSize: '1.2rem', fontWeight: 700, margin: 0 }}>
                {algo.title}
              </h3>
            </div>

            <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)', lineHeight: 1.6, marginBottom: '1rem' }}>
              {algo.desc}
            </p>

            <div style={{ display: 'flex', gap: '0.35rem', flexWrap: 'wrap', marginBottom: '1.25rem' }}>
              {algo.tags.map((t, tidx) => (
                <span key={tidx} className="badge badge-secondary" style={{ fontSize: '0.72rem' }}>
                  {t}
                </span>
              ))}
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 'auto', paddingTop: '0.75rem', borderTop: '1px solid var(--border)' }}>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Space: <span style={{ fontFamily: 'monospace', fontWeight: 600 }}>{algo.spaceComplexity}</span>
              </div>
              <Link to={algo.link} className="btn btn-outline" style={{ fontSize: '0.82rem', padding: '0.4rem 0.85rem' }}>
                <span>Launch Lab</span>
                <ArrowRight size={14} />
              </Link>
            </div>
          </div>
        ))}
      </div>

      {/* Cross-Algorithm Benchmark Link */}
      <div className="card" style={{
        background: 'linear-gradient(135deg, rgba(56, 189, 248, 0.08) 0%, rgba(168, 85, 247, 0.08) 100%)',
        border: '1px solid var(--primary)',
        padding: '2rem',
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'space-between',
        alignItems: 'center',
        gap: '1.5rem',
      }}>
        <div>
          <h3 style={{ fontSize: '1.3rem', margin: '0 0 0.4rem 0', fontWeight: 700 }}>
            Run Comparative Benchmark Across All Algorithms
          </h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.92rem', margin: 0 }}>
            Enter custom text and pattern inputs to execute KMP, Z-Algorithm, Rabin-Karp, Boyer-Moore, and Suffix Arrays side-by-side with execution time charts.
          </p>
        </div>
        <Link to="/compare" className="btn btn-primary">
          <BarChart2 size={16} />
          <span>Go to Algorithm Benchmark Arena</span>
        </Link>
      </div>
    </div>
  );
};

export default AlgorithmsHubPage;

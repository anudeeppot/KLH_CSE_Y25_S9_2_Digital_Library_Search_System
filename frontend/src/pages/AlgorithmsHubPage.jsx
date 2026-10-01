import React from 'react';
import { Link } from 'react-router-dom';
import {
  Cpu,
  Layers,
  FileCode,
  Binary,
  BarChart2,
  Zap,
  Activity,
  ArrowRight,
  ShieldCheck,
  Hash,
  Shuffle
} from 'lucide-react';

const AlgorithmsHubPage = () => {
  const algorithms = [
    {
      id: 'kmp',
      name: 'Knuth-Morris-Pratt (KMP)',
      category: 'String Searching',
      solved: 'Locates exact pattern occurrences in linear time without rolling back the text index upon character mismatch.',
      timeComplexity: 'O(n + m)',
      spaceComplexity: 'O(m)',
      link: '/algorithms/kmp',
      badgeClass: 'badge-kmp',
      realUsage: 'Exact keyword and query matching across titles, author names, categories, and full document texts in the digital library.',
    },
    {
      id: 'rabin-karp',
      name: 'Rabin-Karp Rolling Hash',
      category: 'Randomized & Hashing',
      solved: 'Computes polynomial rolling hash values of sliding text windows in O(1) time, verifying character matches on hash equality.',
      timeComplexity: 'Avg: O(n + m), Worst: O(n * m)',
      spaceComplexity: 'O(1)',
      link: '/algorithms/rabin-karp',
      badgeClass: 'badge-rabin',
      realUsage: 'High-throughput document filtering and multi-pattern dictionary verification for plagiarism / citation integrity.',
    },
    {
      id: 'suffix-array',
      name: 'Suffix Array & Binary Search',
      category: 'Text Indexing',
      solved: 'Constructs a compact lexicographically sorted array of all text suffixes, enabling O(m log n) binary search substring queries.',
      timeComplexity: 'Search: O(m * log n), Preprocessing: O(n^2 log n)',
      spaceComplexity: 'O(n)',
      link: '/algorithms/suffix-array',
      badgeClass: 'badge-suffix',
      realUsage: 'Amortized full-text indexing across massive archived journals and book collections for instant recurring queries.',
    },
  ];

  return (
    <div>
      {/* Header */}
      <div style={{ textAlign: 'center', maxWidth: '850px', margin: '0 auto 3rem' }}>
        <div style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '0.5rem',
          background: 'rgba(99, 102, 241, 0.1)',
          border: '1px solid rgba(99, 102, 241, 0.3)',
          borderRadius: '9999px',
          padding: '0.4rem 1rem',
          fontSize: '0.85rem',
          color: 'var(--accent-indigo)',
          fontWeight: 600,
          marginBottom: '1rem',
        }}>
          <ShieldCheck size={16} /> DSA-3 Viva Demonstration Hub
        </div>
        <h1 style={{ fontSize: '2.5rem', marginBottom: '0.75rem' }}>
          Data Structures & <span className="text-gradient">Algorithms Lab</span>
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.05rem', lineHeight: 1.6 }}>
          Explore interactive visualizations, step-by-step trace executions, mathematical complexities, and native Java implementations for every core DSA module.
        </p>
      </div>

      {/* Grid of Algorithms */}
      <div className="grid-cols-3" style={{ marginBottom: '3.5rem' }}>
        {algorithms.map((algo) => (
          <div key={algo.id} className="glass-card" style={{ display: 'flex', flexDirection: 'column' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
              <span className={`badge ${algo.badgeClass}`}>{algo.category}</span>
              <span className="text-mono" style={{ fontSize: '0.75rem', color: 'var(--accent-cyan)' }}>
                {algo.timeComplexity.split(',')[0]}
              </span>
            </div>

            <h3 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>
              {algo.name}
            </h3>

            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', lineHeight: 1.6, marginBottom: '1rem' }}>
              {algo.solved}
            </p>

            <div style={{
              background: 'rgba(0, 0, 0, 0.3)',
              borderRadius: 'var(--radius-sm)',
              padding: '0.65rem 0.85rem',
              fontSize: '0.78rem',
              marginBottom: '1.25rem',
              borderLeft: '3px solid var(--accent-cyan)',
            }}>
              <strong style={{ color: 'var(--text-primary)' }}>Library Usage:</strong> {algo.realUsage}
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 'auto', paddingTop: '0.75rem', borderTop: '1px solid var(--border-subtle)' }}>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Space: <span className="text-mono" style={{ color: '#fff' }}>{algo.spaceComplexity}</span>
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
      <div style={{
        background: 'linear-gradient(135deg, rgba(56, 189, 248, 0.1) 0%, rgba(168, 85, 247, 0.1) 100%)',
        border: '1px solid var(--border-accent)',
        borderRadius: 'var(--radius-xl)',
        padding: '2rem',
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'space-between',
        alignItems: 'center',
        gap: '1.5rem',
      }}>
        <div>
          <h3 style={{ fontSize: '1.3rem', marginBottom: '0.4rem' }}>
            Run Comparative Benchmark Across All Algorithms
          </h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            Enter custom text and pattern inputs to execute KMP, Rabin-Karp, and Suffix Arrays side-by-side with execution time charts.
          </p>
        </div>
        <Link to="/compare" className="btn btn-primary">
          <BarChart2 size={16} />
          <span>Go to Algorithm Benchmark</span>
        </Link>
      </div>
    </div>
  );
};

export default AlgorithmsHubPage;

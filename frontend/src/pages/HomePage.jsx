import React, { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  BookOpen,
  Search,
  Cpu,
  FileCode,
  Zap,
  BarChart2,
  Database,
  Layers,
  ArrowRight,
  ShieldCheck,
  Binary
} from 'lucide-react';
import SearchBar from '../components/SearchBar';
import { searchAPI } from '../api/apiService';

const HomePage = () => {
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    totalDocuments: 20,
    totalBooks: 5,
    totalResearchPapers: 9,
    totalJournals: 3,
    totalMagazines: 3,
    totalCitations: 28,
  });

  useEffect(() => {
    searchAPI.getDocumentStats()
      .then((res) => {
        if (res.data) setStats(res.data);
      })
      .catch((err) => console.log('Stats loaded default fallback: ', err));
  }, []);

  const handleSearch = (searchParams) => {
    const queryStr = new URLSearchParams(searchParams).toString();
    navigate(`/search?${queryStr}`);
  };

  const featureCards = [
    {
      title: 'Fast Pattern Search',
      algorithm: 'KMP & Boyer-Moore',
      desc: 'Linear and sub-linear string pattern matching without backtracking. Preprocesses patterns into LPS tables and bad character skip rules.',
      link: '/algorithms/kmp',
      icon: Zap,
      color: 'var(--accent-cyan)',
    },
    {
      title: 'Fuzzy Search & Typo Detection',
      algorithm: 'Levenshtein DP',
      desc: 'Dynamic programming matrix computing minimal edits (insert, delete, replace). Enables resilient Did-you-mean suggestions.',
      link: '/algorithms/edit-distance',
      icon: Layers,
      color: 'var(--accent-purple)',
    },
    {
      title: 'Document Similarity',
      algorithm: 'TF-IDF & Cosine Similarity',
      desc: 'Vector space modeling to identify thematic relatedness across papers and books using term frequency and inverse document frequency vectors.',
      link: '/algorithms/similarity',
      icon: FileCode,
      color: 'var(--accent-emerald)',
    },
    {
      title: 'Multi-Core Parallel Search',
      algorithm: 'Parallel Thread Engine',
      desc: 'Concurrent document search across partitioned corpus shards using Java ExecutorService, benchmarked against Amdahl’s law.',
      link: '/algorithms/parallel',
      icon: Cpu,
      color: 'var(--accent-indigo)',
    },
    {
      title: 'Suffix Array Indexing',
      algorithm: 'Suffix Array & Binary Search',
      desc: 'Compact full-text indexing structure. Sorts all lexicographical suffixes once, enabling O(m log n) binary search substring queries.',
      link: '/algorithms/suffix-array',
      icon: Binary,
      color: 'var(--accent-amber)',
    },
    {
      title: 'Algorithm Performance Lab',
      algorithm: 'Comparative Benchmark',
      desc: 'Side-by-side empirical benchmarking of execution time, comparison counts, and theoretical complexities across diverse text inputs.',
      link: '/compare',
      icon: BarChart2,
      color: 'var(--accent-rose)',
    },
  ];

  return (
    <div>
      {/* Hero Section */}
      <section style={{ textAlign: 'center', padding: '3.5rem 1rem 3rem', maxWidth: '960px', margin: '0 auto' }}>
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
          marginBottom: '1.5rem',
        }}>
          <ShieldCheck size={16} /> B.Tech CSE DSA-3 Capstone Project
        </div>

        <h1 style={{ fontSize: 'clamp(2.4rem, 5vw, 3.8rem)', lineHeight: 1.15, marginBottom: '1.25rem' }}>
          Digital Library <span className="text-gradient">Search System</span>
        </h1>

        <p style={{
          fontSize: 'clamp(1.05rem, 2vw, 1.25rem)',
          color: 'var(--text-secondary)',
          lineHeight: 1.6,
          marginBottom: '2.5rem',
          maxWidth: '780px',
          margin: '0 auto 2.5rem',
        }}>
          Fast and intelligent document search powered by Data Structures and Algorithms.
          Explore Books, Journals, Magazines, and Research Papers with real-time algorithmic execution.
        </p>

        {/* Hero Search Box */}
        <div style={{ maxWidth: '850px', margin: '0 auto 2rem' }}>
          <SearchBar onSearch={handleSearch} />
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', gap: '0.75rem', flexWrap: 'wrap', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          <span>Try searching:</span>
          {['algorithms', 'machine learning', 'dynamic programming', 'parallel search', 'database internals'].map((tag) => (
            <button
              key={tag}
              onClick={() => handleSearch({ query: tag, algorithm: 'AUTO' })}
              style={{
                background: 'rgba(255, 255, 255, 0.05)',
                border: '1px solid var(--border-subtle)',
                color: 'var(--text-secondary)',
                borderRadius: '6px',
                padding: '0.2rem 0.6rem',
                cursor: 'pointer',
                fontSize: '0.82rem',
              }}
            >
              {tag}
            </button>
          ))}
        </div>
      </section>

      {/* Statistics Strip */}
      <section style={{
        background: 'var(--bg-card)',
        backdropFilter: 'blur(16px)',
        border: '1px solid var(--border-subtle)',
        borderRadius: 'var(--radius-xl)',
        padding: '2rem 1.5rem',
        margin: '2rem auto 4rem',
        boxShadow: 'var(--shadow-md)',
      }}>
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))',
          gap: '1.5rem',
          textAlign: 'center',
        }}>
          <div>
            <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-cyan)', fontFamily: 'var(--font-heading)' }}>
              {stats.totalDocuments || 20}
            </div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Total Documents
            </div>
          </div>
          <div>
            <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-blue)', fontFamily: 'var(--font-heading)' }}>
              {stats.totalBooks || 5}
            </div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Books
            </div>
          </div>
          <div>
            <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-purple)', fontFamily: 'var(--font-heading)' }}>
              {stats.totalResearchPapers || 9}
            </div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Research Papers
            </div>
          </div>
          <div>
            <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-emerald)', fontFamily: 'var(--font-heading)' }}>
              {stats.totalJournals || 3}
            </div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Journals
            </div>
          </div>
          <div>
            <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-amber)', fontFamily: 'var(--font-heading)' }}>
              {stats.totalMagazines || 3}
            </div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Magazines
            </div>
          </div>
          <div>
            <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--accent-rose)', fontFamily: 'var(--font-heading)' }}>
              7+
            </div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              DSA Algorithms
            </div>
          </div>
        </div>
      </section>

      {/* Feature Cards Grid */}
      <section style={{ marginBottom: '4rem' }}>
        <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
          <h2 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>
            Data Structures & <span className="text-gradient">Algorithms Engine</span>
          </h2>
          <p style={{ color: 'var(--text-secondary)', maxWidth: '650px', margin: '0 auto' }}>
            Every interaction in this digital library is driven by explicit Java DSA implementations designed for transparent academic review.
          </p>
        </div>

        <div className="grid-cols-3">
          {featureCards.map((feat, idx) => {
            const Icon = feat.icon;
            return (
              <div key={idx} className="glass-card" style={{ display: 'flex', flexDirection: 'column' }}>
                <div style={{
                  width: '44px',
                  height: '44px',
                  borderRadius: '10px',
                  background: `rgba(255, 255, 255, 0.05)`,
                  border: `1px solid ${feat.color}40`,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginBottom: '1rem',
                  color: feat.color,
                }}>
                  <Icon size={22} />
                </div>

                <div style={{ fontSize: '0.78rem', fontWeight: 600, color: feat.color, textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '0.25rem' }}>
                  {feat.algorithm}
                </div>

                <h3 style={{ fontSize: '1.2rem', marginBottom: '0.75rem' }}>
                  {feat.title}
                </h3>

                <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)', lineHeight: 1.6, marginBottom: '1.5rem', flex: 1 }}>
                  {feat.desc}
                </p>

                <Link
                  to={feat.link}
                  className="btn btn-outline"
                  style={{ alignSelf: 'flex-start', fontSize: '0.82rem', padding: '0.45rem 0.9rem' }}
                >
                  <span>Launch Visualizer</span>
                  <ArrowRight size={14} />
                </Link>
              </div>
            );
          })}
        </div>
      </section>

      {/* College Review CTA Banner */}
      <section style={{
        background: 'linear-gradient(135deg, rgba(56, 189, 248, 0.1) 0%, rgba(99, 102, 241, 0.1) 100%)',
        border: '1px solid var(--border-accent)',
        borderRadius: 'var(--radius-xl)',
        padding: '2.5rem 2rem',
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'space-between',
        alignItems: 'center',
        gap: '1.5rem',
      }}>
        <div style={{ maxWidth: '650px' }}>
          <h3 style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>
            Preparing for your DSA Project Viva & Review?
          </h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.92rem', lineHeight: 1.6 }}>
            Access our comprehensive Viva Guide covering theoretical time and space complexities, mathematical proofs, algorithm trade-offs, and step-by-step request flow explanation.
          </p>
        </div>
        <Link to="/about" className="btn btn-primary" style={{ padding: '0.85rem 1.75rem', fontSize: '0.95rem' }}>
          <span>View Project Viva Guide</span>
          <ArrowRight size={16} />
        </Link>
      </section>
    </div>
  );
};

export default HomePage;
